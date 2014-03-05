#-*- coding:utf-8 -*-
#date:2013-09-08

import uuid
import utils
import traceback

from datetime import datetime
from default import ServerError
from config import *
from service import * 

def upload(user_id, notes):
	conn = _get_connect()
	if len(notes) == 1:
		note = notes[0]
		if not utils.check_key(('travel_id', 'create_time'), note.keys()) or \
				note.get('content') is None and note.get('image_file') is None:
			return {rsp_code : RC['data_incomplete']}
		return _new_note(conn, user_id, note)

	result = {rsp_code : RC['sucess']} 
	rsps = []	#response for every note
	rsp = None	#response for one note 
	try:
		for note in notes:
			try:
				rsp = {'tag' : note['tag']} 
				#check required key
				if not utils.check_key(('travel_id', 'create_time'), note.keys()) or \
						note.get('content') is None and note.get('image_file') is None:
					rsp[rsp_code] = RC['data_incomplete']
					rsps.append(rsp)
					continue

				#insert into db
				rsp = dict(rsp, **_new_note(conn, user_id, note))
				rsps.append(rsp)
				#conn.commit()
			except KeyError:
				#'tag'不存在
				continue
		result['rsps'] = rsps
		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		conn.close()

def sync(user_id, begin_time, max_qty):
	NOTE_LIST_INDEX = {
			0 : 'note_id',
			1 : 'user_id',
			2 : 'travel_id',
			3 : 'create_time',
			4 : 'content',
			5 : 'comment_qty',
			6 : 'vote_qty',
			7 : 'image_path'
			}
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_sync_note', (
				user_id,
				begin_time,
				max_qty
				)
			)
		notes = []
		for line in cur.stored_results().next().fetchall():	
			note = utils.list_to_dict(line, NOTE_LIST_INDEX)
			note['create_time'] = str(note['create_time'])
			if note['image_path'] is None:
				note['has_image'] = 1
			else:
				note['has_image'] = 0
			note.pop('image_path')
			notes.append(note)

		result = {rsp_code : RC['sucess']}
		result['notes'] = notes

		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def get_by_travel(user_id, travel_id):
	conn = _get_connect()
	cur = conn.cursor()

	try:
		code = cur.callproc('sp_get_note', (user_id, travel_id, 0))[2]
		if code == -1:
			return {rsp_code : RC['no_such_travel']}
		if code == -2:
			return {rsp_code : RC['permission_denied']}
		
		result = {rsp_code : RC['sucess']}
		result['notes'] = []
		for note in cur.stored_results().next().fetchall():	
			result['notes'].append(_note_to_map(note))

		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def delete(user_id, note_id):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		code = cur.callproc('sp_delete_note', (
					user_id,
					note_id,
					0	
					)
				)[2]
		conn.commit()
		return {rsp_code : {
							0  : RC['sucess'],
							-1 : RC['no_such_note'],
							-2 : RC['permission_denied']
							}[code]
			}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def update(user_id, note_id, new_note):
	if len(new_note) < 1:
		raise ValueError('new_note')

	image_path = None 
	if new_note.has_key('image'):
		image_path = _build_image_path()
		utils.save_image(IMAGE_ROOT + image_path,
				new_note['image'])

	content = new_note.get('content')

	conn = _get_connect()
	cur = conn.cursor()
	try:
		code = cur.callproc('sp_update_note', (
				user_id, note_id, content, image_path, 0))[4]
		conn.commit()
		return {rsp_code : {
							0  : RC['sucess'],
							-1 : RC['no_such_note'],
							-2 : RC['permission_denied']
							}[code]
			}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def _new_note(conn, user_id, note):
	cur = conn.cursor()
	try:
		image_path = None
		if note.get('image_file') is not None:
			image_path = _build_image_path()
			utils.save_image(IMAGE_ROOT + image_path, note['image_file'])

		note_id = cur.callproc('sp_new_note' ,(
				user_id,
				note['travel_id'],
				note.get('content'),
				utils.strpdatetime(note['create_time']),
				image_path,
				0	#note_id
				)
			)[5]
		result = None
		if note_id > 0:
			result = {rsp_code : RC['sucess']}
			result['note_id'] = note_id
			conn.commit()
		else:
			result = {rsp_code : {
						-1 : RC['no_such_travel'],
						-2 : RC['permission_denied'],
						-3 : RC['dup_data']
						}[note_id]
					}

		return result
	except ValueError:
		#date format error
		return {rsp_code : RC['illegal_data']}
	finally:
		cur.close()

def _build_image_path():
	return uuid.uuid4().hex[0:16]

