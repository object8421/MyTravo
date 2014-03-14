import traceback
import json
import service

from service import userservice, travelservice, noteservice
from rc import *
from django.shortcuts import render
from django.http import HttpResponse
from django.views.generic import View
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.views.generic import View
from django.conf import settings
from exceptions import IllegalDataError, MissingArgumentError, TokenError
from models import * 
from utils import MyJsonEncoder 

class JsonResponse(HttpResponse):
	def __init__(self, data, **kwargs):
		content = MyJsonEncoder().encode(data)
		kwargs['content_type'] = 'application/json'
		super(JsonResponse, self).__init__(content, kwargs)

def IndexView(request):
	return HttpResponse('Welcome!')

class BaseView(View):
	def __init__(self):
		super(BaseView, self).__init__()
		self._data = None

	def handle(self):
		print('===========request==========')
		print('host:' + self._request.META['REMOTE_ADDR'])
		print('arg:' + str(self._request.GET))
		print('post:' + str(self._request.POST))
		print('body:' + self._request.body)
		
		try:
			result = self.do()
			print('==========response==========')
			#print(MyJsonEncoder().encode(result))
			return result
		except IllegalDataError:
			print('======caught exception======')
			print(traceback.format_exc())
			return {RSP_CODE : RC_ILLEGAL_DATA}
		except MissingArgumentError:
			print('======caught exception======')
			print(traceback.format_exc())
			return {RSP_CODE : RC_WRONG_ARG}
		except Exception, e:
			print('======caught exception======')
			print(traceback.format_exc())
			return {RSP_CODE : RC_SERVER_ERROR}
		except TokenError, e:
			print('======caught exception======')
			print(traceback.format_exc())
			rsp_code = {
					'no_such_user' : RC_NO_SUCH_USER,
					'token_overdate': RC_TOKEN_OVERDATE
					}[e.args[0]]
			return {RSP_CODE : rsp_code}

	def do(self):
		pass

	def get_required_arg(self, key):
		arg = self.get_arg(key)
		if arg is None:
			raise MissingArgumentError(key)
		return arg

	def get_arg(self, key, default = None):
		try:
			return self._request.GET[key]
		except MultiValueDictKeyError:
			return default

	def get_required_data(self, key):
		data = self.get_data(key)
		if data is None:
			raise IllegalDataError(key)
		return data

	def get_data(self, key, default = None):
		try:
			if self._data is None:
				self._data = json.loads(self._request.body)
		except ValueError:
			raise IllegalDataError('can not parse json string to object')
		try:
			data = self._data[key]
		except Exception:
			return default
		else:
			return data	

###############################################
########	USER MOUDLE		###################
###############################################
class UserView(BaseView):
	_request = None
	def record_login(self, u):
		lr = LoginRecord(user=u, time=datetime.now(), ip=self._request.META['REMOTE_ADDR'])
		print lr.dict()
		lr.save()

class LoginView(UserView):
	def get(self, request, *args):
		self._request = request
		return JsonResponse(self.handle())
	
	def do(self):
		result = {
			'travo' : userservice.travo_login(self.get_required_arg('email'), self.get_required_arg('password')),
			'qq'	: userservice.qq_login(),
			'sina'	: userservice.sina_login() 
		}[self.get_required_arg('user_type')]

		if result[RSP_CODE] == RC_SUCESS:
			#record login
			pass
			#self.record_login(result['user'])
		return result

class RegisterView(UserView):
	def post(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return {
			'travo' : userservice.travo_register(
						self.get_required_data('nickname'),
						self.get_required_data('email'),
						self.get_required_data('password')
					),
			'qq'	: userservice.qq_register(), 
			'sina'	: userservice.sina_register() 
		}[self.get_required_arg('user_type')]

##############################################
########	TRAVEL MOUDLE	##################
##############################################
class UploadTravelView(BaseView):
	def post(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return travelservice.upload(
				self.get_required_arg('token'),
				self.get_required_data('travels')
				)

class SyncTravelView(BaseView):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return travelservice.sync(
				self.get_required_arg('token'),
				self.get_arg('begin_time')
				)

class CoverView(BaseView):
	def get(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		result = self.handle()
		if result[RSP_CODE] == RC_SUCESS:
			response = HttpResponse(result.pop('cover'), 'image/jpeg; charsete=utf-8')
			response.write(MyJsonEncoder().encode(result))
			return response
		else:
			return HttpResponse(MyJsonEncoder().encode(result))

	def do(self):
		return travelservice.get_cover(
				self._travel_id,
				self.get_arg('token')
				)

#############################################
########	Note MOUDLE		#################
#############################################
class UploadNoteView(BaseView):
	def post(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return noteservice.upload(
				self.get_required_arg('token'),
				self.get_required_data('notes')
				)
	pass

class SyncNoteView(BaseView):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return noteservice.sync(
				self.get_required_arg('token'),
				self.get_arg('begin_time')
				)

#############################################
########	SYNC MOUDLE		#################
#############################################
class SyncView(BaseView):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())
	def do(self):
		return service.sync_state(self.get_required_arg('token'))
