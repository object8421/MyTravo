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
				travel = _fill_travel(t)
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

def get_cover(travel_id, user_id):
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

def _fill_travel(values):
	travel = {}
	travel['user_id']		= values[0]
	travel['travel_id']		= values[1]
	travel['title']			= values[2]
	travel['destination']	= values[3]
	travel['begin_date']	= str(values[4])
	travel['end_date']		= str(values[5])
	travel['average_spend']	= values[6]
	travel['create_time']	= str(values[7])
	travel['comment_qty']	= values[8]
	travel['vote_qty']		= values[9]
	travel['favorite_qty']	= values[10]
	travel['read_times']	= values[11]
	travel['is_public']		= values[12]
	travel['is_deleted']	= values[13]
	travel['description']	= values[14]
	travel['cover_path']	= values[15]
	return travel

def _build_cover_path():
	return uuid.uuid4().hex[0:16] 
