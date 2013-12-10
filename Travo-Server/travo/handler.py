# -*- coding:UTF-8 -*-

import tornado.web
import json
import traceback
import service
import utils

from service import UserService
from service import LocationService
from service import TravelService
from tornado.web import MissingArgumentError
from tornado.web import RequestHandler
from travo import * 

class BaseHandler(RequestHandler):
	def initialize(self):
		self.datas = None

	def _get_data(self, key):
		if self.datas is None:
			self.datas = json.loads(self.request.body)
		return self.datas.get(key)

	def _handle_request(self):
		print('================request==================')
		print('args:%s' % self.request.arguments)
		print('body:%s' % self.request.body)

		try:
			req_type = self.get_argument('req_type')
			#from req_type map to handler
			handler = self.req_handler_map.get(req_type)
			if handler is None:
				self.write({rsp_code : RC['invalid_req']})
				return
			
			result = handler()
			print('================response=================')
			print(result)
			self.write(result)
		except MissingArgumentError, e:
			print('============caught exception===========')
			print traceback.format_exc()
			self.write({rsp_code : RC['no_req_code']})
		except TokenError, e:
			print('============caught exception===========')
			print traceback.format_exc()
			self.write({rsp_code : RC[e.args[0]]})
		except ValueError, e:
			print('============caught exception===========')
			print traceback.format_exc()
			self.write({rsp_code : RC['wrong_arg']})
		except Exception, e:
			#unhandler exception
			print('============caught exception===========')
			print traceback.format_exc()
			self.write({rsp_code : RC['server_error']})
		finally:
			self.flush()
			self.finish()
	
	def _get_user_id_by_token(self):
		user_id = _verify_token(self.get_argument('token'))
		if user_id == 0:
			raise TokenError('no_token')
		if user_id == -1:
			raise TokenError('token_overdate')
		return user_id

class DefaultHandler(BaseHandler):
	def get(self):
		self.write('Welcome to TraVo')
		self.flush()
	
class UserHandler(BaseHandler):
	def initialize(self):
		super(UserHandler, self).initialize()
		self.req_handler_map = {#requst dispatcher
				'login' : self._login,
				'update_user' : self._update_user,
				'update_password' : self._update_password,
				}
		
	def get(self, user_id = None):
		if user_id is not None:
			#get-user request
			self.write(UserService.get_user(int(user_id)))
		else:
			self.write({rsp_code : RC['invalid_req']})
		self.flush()
		self.finish()

	def post(self, user_id = None):
		'''register request'''
		if user_id is None:
			return self._register()
		else:
			return {rsp_code : RC['invalid_req']}
		
	def put(self, user_id = None):
		'''update_password or update_user request'''
		self._handle_request()

	def _register(self):
		email = self._get_data('email')
		password = self._get_data('password')
		nickname = self._get_data('nickname')
		if email is None:
			return {rsp_code : RC['miss_email']}
		if password is None:
			return {rsp_code : RC['miss_password']}
		if nickname is None:
			return {rsp_code : RC['miss_nickname']}

		return UserService.register(self.request.remote_ip, email, password, nickname)

	def _login(self):
		email = self._get_data('email')
		password = self._get_data('password')

		if email is None:
			return {rsp_code : RC['miss_email']}
		if password is None:
			return {rsp_code : RC['miss_password']}
		
		return UserService.login(self.request.remote_ip, email, password)

	def _update_user(self):
		nickname = self._get_data('nickname')
		signature = self._get_data('signature')
		face = self._get_data('face')

		return UserService.update_user(
				self._get_user_id_by_token(),
				nickname, signature, face
				)

	def _update_password(self):
		email = self._get_data('email')
		old_pass = self._get_data('old_password')
		new_pass = self._get_data('new_password')

		if email is None:
			return {rsp_code : RC['miss_email']}
		if old_pass is None or new_pass is None:
			return {rsp_code : RC['miss_password']}
		
		return UserService.update_password(self.request.remote_ip,
				email, old_pass, new_pass)

class SyncHandler(BaseHandler):
	def initialize(self):
		super(SyncHandler, self).initialize()
		self.req_handler_map = {
				'get_sync_status' : self._get_sync_status
				}

	def get(self):
		self._handle_request()

	def _get_sync_status(self):
		return service.get_sync_status(
				self._get_user_id_by_token()
				)

class LocationHandler(BaseHandler):
	def initialize(self):
		super(LocationHandler, self).initialize()
		self.req_handler_map = {
				'upload_location' : self._upload_location,
				'sync_location' : self._sync_location
				}

	def get(self):
		self._handle_request()
	def post(self):
		self._handle_request()

	def _upload_location(self):
		locations = self._get_data('locations') 
		if locations is None:
			return {rsp_code : RC['no_data']}

		return LocationService.upload_location(
				self._get_user_id_by_token(),
				locations
				) 

	def _sync_location(self):
		return LocationService.sync_location(
				self._get_user_id_by_token(),
				utils.strpdatetime(self.get_argument('begin_time')),
				self.get_argument('max_qty')
				)

class TravelHandler(BaseHandler):
	def initialize(self):
		super(TravelHandler, self).initialize()
		self.req_handler_map = {
				'upload_travel' : self._upload_travel,
				'sync_travel' : self._sync_travel
				}
	def get(self, travel_id = None):
		'''get_travel or sync_travel'''
		if travel_id is not None:
			if travel_id <= 0:
				self.write({rsp_code : RC['wrong_arg']})
			else:
				self.write(TravelService.get_travel(travel_id))
			self.flush()
			self.finish()
		else:
			self._handle_request()

	def post(self):
		'''upload_travel'''
		self._handle_request()

	def put(self, travel_id):
		'''update_travel'''
		pass

	def _upload_travel(self):
		travels = self._get_data('travels')
		if travels is None:
			return {rsp_code : RC['data_incomplete']}
		return TravelService.upload_travel(
				self._get_user_id_by_token(),
				self._get_data('travels')
				)

	def _sync_travel(self):
		return TravelService.sync_travel(
				self._get_user_id_by_token(),
				self.get_argument('begin_time'),
				self.get_argument('max_qty')
				)

class UserTravelHandler(BaseHandler):
	'''获得一个其他用户的游记'''
	def get(self, user_id):
		pass

class MostTravelHandler(BaseHandler):
	'''根据某种排序规则获得游记'''
	def get(self, arg):
		pass

class NoteHandler(BaseHandler):
	def initialize(self):
		super(NoteHandler, self).initialize()
		self.req_handler_map = {#requst dispatcher
				'upload_note' : self._upload_note
				}

	def get(self):
		pass

	def post(self):
		self.handle_request()
	
	def _upload_note(self):
		notes = self._get_data('notes')
		if notes is None:
			return {rsp_code : RC['data_incomplete']}
		return NoteService.upload_note(
				self._get_user_id_by_token(self.get_argument('token')),
				notes
				)

class ImageHandler(RequestHandler):
	def get(self, image_path):
		self._set_header()

	def _write_image(self, file_path):
		try:
			with open(file_path) as f:
				self.write(f.read())
		except IOError:
			return {rsp_code : RC['no_resoruce']}
	
	def _set_header(self):
		self.set_header('Content-Type', 'image/png')
		self.set_header('Content-Disposition',
					'attachment; filename="face.png"')

class FaceHandler(ImageHandler):
	def get(self, face_path):
		self._set_header()
		self._write_image(FACE_ROOT + face_path)
		self.flush()

