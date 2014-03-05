#-*- coding:utf-8 -*-
#Date:2013-09-17

import json
import traceback
import service

from tornado.web import RequestHandler
from tornado.web import MissingArgumentError
from config import *

__all__ = [
		'TokenError',
		'BaseHandler',
		'MissingDataError',
		'ServerError'
		]

class TokenError(BaseException):
	pass

class MissingDataError(BaseException):
	pass

class ServerError(BaseException):
	pass

class BaseHandler(RequestHandler):
	def initialize(self):
		self._data = None

	def handle(self):
		print('===========request==========')
		print('uri:' + self.request.uri)
		print('arg:' + str(self.request.arguments))
		print('body:' + self.request.body)
		try:
			result = self.do()
			print('==========response==========')
			print(result)
			self.write(result)
		except MissingArgumentError:
			print('======caught exception======')
			print(traceback.format_exc())
			self.write({rsp_code : RC['wrong_arg']})
		except MissingDataError:
			print('======caught exception======')
			print(traceback.format_exc())
			self.write({rsp_code : RC['illegal_data']})
		except ValueError:
			print('======caught exception======')
			print(traceback.format_exc())
			self.write({rsp_code : RC['illegal_data']})
		except TokenError, e:
			print('======caught exception======')
			print(traceback.format_exc())
			self.write({rsp_code : RC[e.args[0]]})
		except ServerError:
			print('======caught exception======')
			print(traceback.format_exc())
			self.write({rsp_code : RC['server_error']})
		except Exception:
			print('======unhandled exception===')
			print(traceback.format_exc())
			self.write({rsp_code : RC['server_error']})
		finally:
			self.flush()
			self.finish()

	def do(self):
		pass

	def get_data(self, key, default):
		if self._data is None:
			self._data = json.loads(self.request.body)
		return self._data.get(key, default)

	def get_required_data(self, key):
		d = self.get_data(key, None)
		if d is None:
			raise MissingDataError(key)
		return d


	def get_nullable_argument(self, key, default = None):
		try:
			return RequestHandler.get_argument(self, key)
		except MissingArgumentError:
			return default 

	def get_user_id_by_token(self):
		user_id = service.verify_token(self.get_argument('token'))
		if user_id == 0:
			raise TokenError('no_such_user')
		if user_id == -1:
			raise TokenError('token_overdate')
		return user_id

