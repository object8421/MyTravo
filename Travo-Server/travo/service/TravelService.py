# -*- coding:utf-8 -*-

import traceback
import utils
import uuid

from mysql.connector import IntegrityError
from datetime import datetime
from default import ServerError
from service import *
from config import *

def upload(user_id, travels):
	required_key = ('title', 'destination', 'begin_date', 'create_time', 'is_public')

	if len(travels) == 1:
		if not utils.check_key(required_key, travels[0].keys()):
			return {rsp_code : RC['data_incomplete']}
		return _new_travel(user_id, travels[0])
	
	#有多个travel，要对每一个travel返回结果
	result = {rsp_code : RC['sucess']}
	rsps = []
	rsp = {} 
	for travel in travels:
		try:
			tag = travel['tag']
			if not utils.check_key(required_key, travel.keys()):
				rsp['tag'] = tag 
				rsp[rsp_code] = RC['data_incomplete']
				rsps.append(rsp)
				continue
			rsp = _new_travel(user_id, travel)
			rsp['tag'] = tag
			rsps.append(rsp)
		except KeyError:
			#如果一个travel没有'tag'属性，则直接无视
			continue
	
	result['rsps'] = rsps
	return result

def _new_travel(user_id, travel):
	conn = _get_connect()
	cur = conn.cursor()
	cover_path = None
	if travel.get('cover') is not None:
		cover_path = _build_cover_path()
	try:
		travel_id = cur.callproc('sp_new_travel', (
			user_id, 
			travel['title'],
			travel['destination'],
			utils.strpdate(travel['begin_date']),
			utils.strpdatetime(travel['create_time']),
			utils.strpdate(travel.get('end_date')),
			cover_path,
			travel.get('avg_spend'), 
			travel.get('description'), 
			travel['is_public'], 
			0)
			)[10]
		if travel_id == -1:
			return {rsp_code : RC['dup_data']}
		
		if cover_path is not None:
			utils.save_image(COVER_ROOT + cover_path, travel['cover'])
		conn.commit()
		result = {rsp_code : RC['sucess']}
		result['travel_id'] = travel_id
		return result;
	except ValueError:
		#date format error
		return {rsp_code : RC['illegal_data']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def sync(user_id, begin_time):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_sync_travel', (
			user_id,
			utils.strpdatetime(begin_time)
			)
		)
		travels = []
		for r in cur.stored_results():
			for t in r.fetchall():
				travel = utils.list_to_dict(t, LIST_INDEX.SYNC['TRAVEL'])
				utils.datepstr_indict(travel)
				if travel['cover_path'] is None:
					travel['has_cover'] = 0
				else:
					travel['has_cover'] = 1
				travel.pop('cover_path')
				travels.append(travel)
		result = {rsp_code : RC['sucess']}
		result['travels'] = travels
		return result
	except ValueError:
		#date format error
		print('============caught exception===========')
		print(traceback.format_exc())
		return {rsp_code : RC['wrong_arg']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def delete(user_id, travel_id):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		code = cur.callproc('sp_delete_travel', (
					user_id,
					travel_id,
					0	
					)
				)[2]
		conn.commit()
		return {rsp_code : {
							0  : RC['sucess'],
							-1 : RC['no_such_travel'],
							-2 : RC['permission_denied']
							}[code]
			}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def update(user_id, travel_id, new_travel):
	old_travel = _get_travel(travel_id)
	if old_travel is None:
		return {rsp_code : RC['no_such_travel']}
	if old_travel['user_id'] != user_id:
		return {rsp_code : RC['permission_denied']}

	#update travel
	if new_travel.has_key('cover'):
		old_travel['cover_path'] = _build_cover_path()
		utils.save_image(COVER_ROOT + old_travel['cover_path'], 
				new_travel['cover'])

	for key in new_travel.keys():
		if key != 'cover':
			old_travel[key] = new_travel[key]
			
	#commit to db
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_update_travel', (
			travel_id,
			old_travel['title'],
			old_travel['destination'],
			utils.strpdate(utils.sNone_to_None(old_travel['begin_date'])),
			utils.strpdate(utils.sNone_to_None(old_travel['end_date'])),
			old_travel['average_spend'],
			old_travel['description'],
			old_travel['is_public'],
			old_travel['cover_path']
			)
			)
		conn.commit()
		return {rsp_code : RC['sucess']}
	except ValueError:
		#date format error
		print('============caught exception===========')
		print(traceback.format_exc())
		return {rsp_code : RC['illegal_data']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def _get_travel(travel_id):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_get_travel', (travel_id,))
		r = cur.stored_results().next().fetchone()
		if r is None:
			return None 
		return _fill_travel(r)
	except Exception:
		print('============caught exception===========')
		print(traceback.format_exc())
		return None
	finally:
		cur.close()
		conn.close()

def get_cover(user_id, travel_id):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		r = cur.callproc('sp_get_cover', (travel_id, user_id, '', 0))
		if r[3] == -1:
			return {rsp_code : RC['no_such_travel']}
		if r[3] == -2:
			return {rsp_code : RC['permission_denied']}
		result = {rsp_code : RC['sucess']}
		result['cover_path'] = COVER_ROOT + r[2]
		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def favorit(user_id, travel_id):
	conn = _get_connect()
	cur = conn.cursor()

	try:
		code = cur.callproc('sp_favorit_travel', (user_id, travel_id, 0))[2]
		if code == -1:
			return {rsp_code : RC['no_such_travel']}
		if code == -2:
			return {rsp_code : RC['permission_denied']}
		conn.commit()
		return {rsp_code : RC['sucess']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

SEARCH_ORDER = (
		'default',
		'read_times',
		'vote_qty',
		'comment_qty',
		'newest'
		)

def search(order, first_idx, max_qty):
	conn = _get_connect()
	cur = conn.cursor()

	travels = []

	try:
		cur.callproc('sp_default_search', (first_idx, max_qty))
		for line in cur.stored_results().next():
			travel = utils.list_to_dict(line, LIST_INDEX.SEARCH['TRAVEL'])
			travel['user'] = utils.list_to_dict(line, LIST_INDEX.SEARCH['USER'])
			utils.datepstr_indict(travel)
			if travel['cover_path'] is None:
				travel['has_cover'] = 0
			else:
				travel['has_cover'] = 1
			travel.pop('cover_path')
			travels.append(travel)

		result = {rsp_code : RC['sucess']}
		result['travels'] = travels
		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def get_favorit(user_id, first_idx, max_qty):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_get_favorit', (user_id, first_idx, max_qty))
		travels = []
		for line in cur.stored_results().next():
			travel = utils.list_to_dict(line, LIST_INDEX.FAVORIT['TRAVEL'])
			travel['user'] = utils.list_to_dict(line, LIST_INDEX.FAVORIT['USER'])
			utils.datepstr_indict(travel)
			if travel['cover_path'] is None:
				travel['has_cover'] = 0
			else:
				travel['has_cover'] = 1
			travel.pop('cover_path')
			travels.append(travel)
		result = {rsp_code : RC['sucess']}
		result['travels'] = travels
		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def read(user_id, travel_id):
	conn = _get_connect()
	cur = conn.cursor()

	try:
		code = cur.callproc('sp_read_travel', (user_id, travel_id, 0))[2]
		if code == -1:
			return {rsp_code : RC['no_such_travel']}
		if code == -2:
			return {rsp_code : RC['permission_denied']}
		conn.commit()
		return {rsp_code : RC['sucess']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def _build_cover_path():
	return uuid.uuid4().hex[0:16] 

class LIST_INDEX:
	'''datas fetch from database is a list,
	this class define some map which can assign a key for every item in the list
	'''
	SEARCH = {
		#useful map in 'search' function
		'TRAVEL' : {
				7 : 'travel_id',
				8 : 'title',
				9 : 'average_spend',
				10: 'begin_date',
				11: 'end_date',
				12: 'create_time',
				13: 'description',
				14: 'destination',
				15: 'favorite_qty',
				16: 'comment_qty',
				17: 'read_times',
				18: 'vote_qty',
				19: 'cover_path'
			},
		'USER' : {
				0 : 'user_id',
				1 : 'nickname',
				2 : 'signature',
				3 : 'travel_qty',
				4 : 'scenic_point_qty',
				5 : 'achievement_qty',
				6 : 'follower_qty'
			}
		}

	FAVORIT = {
		#useful map in 'get_favorit' function
			'TRAVEL' : SEARCH['TRAVEL'],
			'USER'	: SEARCH['USER']
			}

	SYNC = {
		#useful map in 'sync' function
			'TRAVEL' : { 
				0 : 'user_id',
				1 : 'travel_id',
				2 : 'title',
				3 : 'destination',
				4 : 'begin_date',
				5 : 'end_date',
				6 : 'average_spend',
				7 : 'create_time',
				8 : 'comment_qty',
				9 : 'vote_qty',
				10: 'favorite_qty',
				11: 'read_times',
				12: 'is_public',
				13: 'is_delete',
				14: 'description',
				15: 'cover_path'
			}
		}
