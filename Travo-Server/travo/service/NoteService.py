#-*- coding:utf-8 -*-
#date:2013-09-08

import uuid
import utils
import traceback

from datetime import datetime
from config import *
from service import * 

def upload_note(user_id, notes):
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
				conn.commit()
			except KeyError:
				#'tag'不存在
				continue
		result['rsps'] = rsps
		return result
	except Exception:
		print('============caught exception===========')
		print(traceback.format_exc())
		return {rsp_code : RC['server_error']}
	finally:
		conn.close()

def sync_note(user_id, begin_time, max_qty):
	if begin_time is None:
		begin_time = datetime.min

	if max_qty is None:
		import sys
		max_qty = sys.maxint

	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_sync_note', (
				user_id,
				begin_time,
				max_qty
				)
			)
		result = {rsp_code : RC['sucess']}
		result['notes'] = []
		for note in cur.stored_results().next().fetchall():	
			result['notes'].append(_note_to_map(note))

		return result
	except:
		print('============caught exception===========')
		print(traceback.format_exc())
		return {rsp_code : RC['server_error']}
	finally:
		cur.close()
		conn.close()

def get_note(travel_id, user_id):
	conn = _get_connect()
	cur = conn.cursor()

	try:
		code = cur.callproc('sp_get_note', (travel_id, user_id, 0))[2]
		if code == -1:
			return {rsp_code : RC['no_such_travel']}
		if code == -2:
			return {rsp_code : RC['permission_denied']}
		
		result = {rsp_code : RC['sucess']}
		result['notes'] = []
		for note in cur.stored_results().next().fetchall():	
			result['notes'].append(_note_to_map(note))

		return result
	except:
		print('============caught exception===========')
		print(traceback.format_exc())
		return {rsp_code : RC['server_error']}
	finally:
		cur.close()
		conn.close()

def _note_to_map(n):
	note = {}
	note['note_id']		= n[0]
	note['user_id']		= n[1]
	note['travel_id']	= n[2]
	note['create_time'] = str(n[3])
	note['content']		= n[4]
	note['comment_qty']	= n[5]
	note['vote_qty']	= n[6]
	if n[7] is None:
		note['has_image'] = 0
	else:
		note['has_image'] = 1
	return note

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
						-1 : RC['no_travel'],
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

