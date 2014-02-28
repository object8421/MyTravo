# -*- coding:utf-8 -*-

import uuid
import traceback
import re
import json
import urllib2 

from mysql.connector import IntegrityError
from threading import Thread
from default import ServerError
from service import *
from config import *

class EmailError(BaseException):
	pass

class AuthError(BaseException):
	pass

def _get_travo_register_arg(args, token):
	if not _check_email(args['email']):
		raise EmailError('illegal email')

	return (args['nickname'],
			token,
			args['email'],
			args['password'],
			None,	#qq_user_id
			None	#sina_user_id
	)

def get_qq_user_id(qq_token):
	r = urllib2.urlopen('https://graph.z.qq.com/moc2/me?access_token=' + qq_token)
	response = r.read()
	if response.startswith('code'):
		raise AuthError('qq auth fail')
	else:
		return response[-32:]

def _get_qq_register_arg(args, token):
	return (args['nickname'],
			token,
			None,	#email
			None,	#password
			get_qq_user_id(args['qq_token']),
			None	#sina_user_id
	)

def get_sina_user_id(sina_token):
	try:
		r = urllib2.urlopen('https://api.weibo.com/2/account/get_uid.json?access_token=' + sina_token)
		return json.loads(r.read())['uid']
	except:
		raise AuthError('sina auth fail')
		
def _get_sina_register_arg(args, token):
	return (args['nickname'],
			token,
			None,	#email
			None,	#password
			None,	#qq_user_id
			get_sina_user_id(args['sina_token'])
	)

def register(args):
	arg_list = None
	token = _build_token()
	try:
		arg_list = {	#调用存储过程时向数据库中发送的数据
			'travo' : _get_travo_register_arg,
			'qq'	: _get_qq_register_arg,
			'sina'	: _get_sina_register_arg
		}[args['user_type']](args, token)
	except EmailError:
		return {rsp_code : RC['illegal_email']}
	except AuthError:
		return {rsp_code : RC['auth_fail']}
	
	conn = _get_connect()
	cur = conn.cursor()
	user_id = 0
	try:
		cur.callproc('sp_register', arg_list)	
		user_id = cur.stored_results().next().fetchone()[0]
		conn.commit()

		#register sucess
		_record_login(args['remote_ip'], user_id)
		result = {rsp_code : RC['sucess']}
		result['user_id'] = user_id
		result['token'] = token 
		return result
	except IntegrityError, e:
		if 'email_UNIQUE' in str(e):
			#email duplicate
			return {rsp_code : RC['dup_email']}
		elif 'nickname_UNIQUE' in str(e):
			#nickname duplicate
			return {rsp_code : RC['dup_nickname']}
		elif 'qq_user_id_UNIQUE' in str(e) or 'sina_user_id_UNIQUE' in str(e):
			return {rsp_code : RC['dup_bind']}
	finally:
		cur.close()
		conn.close()

def login(args):
	token = _build_token()
	code = 0
	conn = _get_connect()
	cur = conn.cursor()
	data = None
	try:
		code = {
				'travo'	: lambda : cur.callproc('sp_travo_login', (
								args['email'], args['password'], token, 0))[3],
				'qq'	: lambda : cur.callproc('sp_qq_login', (
								get_qq_user_id(args['qq_token']), token, 0))[2],
				'sina'	: lambda : cur.callproc('sp_sina_login', (
								get_sina_user_id(args['sina_token']), token, 0))[2],
		}[args['user_type']]()

		if code == -1:
			return {rsp_code : RC['no_such_user']}
		if code == -2:
			return {rsp_code : RC['wrong_password']}

		data = cur.stored_results().next().fetchone()
		conn.commit()
	except AuthError, e:
		return {rsp_code : RC['auth_fail']}
	except Exception, e:
		raise ServerError(e)
		print('===============caught exception=============')
		print(traceback.format_exc())
		return {rsp_code : RC['server_error']}
	finally:
		cur.close()
		conn.close()

	#login sucess
	result = {rsp_code : RC['sucess']}
	result['user_id']		= data[0] 
	result['token']			= data[1]
	result['email']			= data[2]
	result['qq_user_id']	= data[3]
	result['sina_user_id']	= data[4]
	result['nickname']		= data[5]
	result['register_time']	= str(data[6])
	result['signature']		= data[7]
	result['account']		= data[8]
	result['is_location_public'] = data[9]
	result['is_info_public'] = data[10]
	result['travel_qty'] = data[11]
	result['scenic_point_qty'] = data[12]
	result['favorite_travel_qty'] = data[13]
	result['achievement_qty'] = data[14]
	result['follower_qty'] = data[15]

	_record_login(args['remote_ip'], result['user_id'])
	return result

def bind_open_user(user_id, user):
	open_user_id = None
	try:
		open_user_id = {
				'sina'	: lambda : get_sina_user_id(user['token']),
				'qq'	: lambda : get_qq_user_id(user['token'])
				}[user['user_type']]()
	except AuthError:
		return {rsp_code : RC['auth_fail']}

	conn = _get_connect()
	cur = conn.cursor()
	try:
		code = cur.callproc('sp_bind_open_user', (user_id, user['user_type'], open_user_id, 0 ) )[3]
		if code == -1:
			return {rsp_code : RC['dup_bind']}
		conn.commit()
		return {rsp_code : RC['sucess']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def update_email(user_id, email, password):
	if not _check_email(email):
		return {rsp_code : RC['illegal_email']}
	conn = _get_connect()
	cur = conn.cursor()
	try:
		code = cur.callproc('sp_update_email', (user_id, email, password, 0))[3]
		if code == -1:
			return {rsp_code : RC['wrong_password']}
		conn.commit()
		return {rsp_code : RC['sucess']}
	except IntegrityError:
		print('===============caught exception=============')
		print(traceback.format_exc())
		#dup email
		return {rsp_code : RC['dup_email']}
	finally:
		cur.close()
		conn.close()
		
def update_user(user_id, nickname, signature, face):
	if face is not None:
		face_path = _build_face_path()

	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_update_user', (user_id,
			nickname, signature, face_path))
		conn.commit()
	except IntegrityError:
		#only dup nickname may raise this error
		return {rsp_code : RC['dup_nickname']}
	finally:
		cur.close()
		conn.close()

	if face is not None:
		_save_image(FACE_ROOT + face_path, face)

	return {rsp_code : RC['sucess']}

def get_user(user_id):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_get_user', (user_id,))
		datas = cur.stored_results().next().fetchone() 
		
		if datas is None:
			return {rsp_code : RC['no_user']}

		result = {rsp_code : RC['sucess']}
		result['nickname'] = datas[0]
		result['register_time'] = str(datas[1])
		result['signature'] = datas[2]
		result['face_url'] = _build_face_url(datas[3])
		result['account'] = datas[4]
		result['is_location_public'] = datas[5]
		result['travel_qty'] = datas[6]
		result['scenic_point_qty'] = datas[7]
		result['favorite_travel_qty'] = datas[8]
		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def update_password(remote_ip, email, old_pass, new_pass):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		user_id = cur.callproc('sp_update_password', (email,
			old_pass, new_pass, 0))[3]
		if user_id == -1:
			return {rsp_code : RC['no_email']}
		if user_id == -2:
			return {rsp_code : RC['wrong_password']}
		conn.commit()
		_record_login(remote_ip, user_id)
		return {rsp_code : RC['sucess']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def _build_face_url(face_path):
	if face_path is None:
		return None
	return FACE_URL + face_path

def _build_face_path():
	return uuid.uuid4().hex[0:16]

def _record_login(remote_ip, user_id):
	class RecordLoginThread(Thread):
		def __init__(self, remote_ip, user_id):
			super(RecordLoginThread, self).__init__()
			self._remote_ip = remote_ip
			self._user_id = user_id
		def run(self):
			conn = _get_connect()
			cur = conn.cursor()
			try:
				cur.callproc('sp_record_login', (
					self._user_id, self._remote_ip))
				conn.commit()
			except Exception, e:
				raise ServerError(e)
			finally:
				cur.close()
				conn.close()

	RecordLoginThread(remote_ip, user_id).start()

def _check_email(email):
	format = '^[\d\w-]+\@[\d\w-]+(\.[\d\w-]+)+$'
	return re.match(format, email) is not None

def _build_token():
	return uuid.uuid4().hex

