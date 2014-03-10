import uuid
import re
import traceback
import json
import urllib2

from rc import * 
from django.shortcuts import render
from django.http import HttpResponse
from django.views.generic import View
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.views.generic import View
from django.core.exceptions import ObjectDoesNotExist
from models import * 
from utils import * 


class EmailError(BaseException):
	pass

class AuthError(BaseException):
	pass

class IllegalDataError(BaseException):
	pass

class MissingArgumentError(BaseException):
	pass

class TokenError(BaseException):
	pass

class BaseView(View):
	def __init__(self):
		super(BaseView, self).__init__()
		self._data = None

	def handle(self):
		print('===========request==========')
		print('arg:' + str(self._request.GET))
		print('body:' + self._request.body)
		
		try:
			result = self.do()
			print('==========response==========')
			print(result)
			return MyJsonEncoder().encode(result)
		except IllegalDataError:
			print('======caught exception======')
			print(traceback.format_exc())
			return MyJsonEncoder().encode({RSP_CODE : RC_ILLEGAL_DATA})
		except MissingArgumentError:
			print('======caught exception======')
			print(traceback.format_exc())
			return MyJsonEncoder().encode({RSP_CODE : RC_WRONG_ARG})
		except Exception, e:
			print('======caught exception======')
			print(traceback.format_exc())
			return MyJsonEncoder().encode({RSP_CODE : RC_SERVER_ERROR})
		except TokenError, e:
			print('======caught exception======')
			print(traceback.format_exc())
			rsp_code = {
					'no_such_user' : RC_NO_SUCH_USER,
					'token_overdate': RC_TOKEN_OVERDATE
					}[e.args[0]]
			return MyJsonEncoder().encode({RSP_CODE : rsp_code}) 

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

	def get_user_by_token(self):
		t = self.get_required_arg('token')
		u = None
		try:
			u = User.objects.get(token=t)
		except ObjectDoesNotExist:
			raise TokenError('no_such_user')

		return u


###############################################
########	USER MOUDLE		###################
###############################################
class LoginView(BaseView):
	def get(self, request, *args):
		self._request = request
		return HttpResponse(self.handle())
	
	def do(self):
		result = {
			'travo' : self.travo_login,
			'qq'	: self.qq_login,
			'sina'	: self.sina_login
		}[self.get_required_arg('user_type')]()

		if result[RSP_CODE] == RC_SUCESS:
			#record login
			pass
		return result

	def travo_login(self):
		result = {}
		u = User.objects.get(email=self.get_required_arg('email'))
		if u is None:
			result[RSP_CODE] = RC_NO_SUCH_USER
		if u.password == self.get_required_arg('password'):
			result[RSP_CODE] = RC_SUCESS
		else:
			return RC_WRONG_PASSWORD
		result['user'] = u
		return result

	def qq_login(self):
		pass

	def sina_login(self):
		pass

class RegisterView(BaseView):
	def post(self, request):
		self._request = request
		return HttpResponse(self.handle())

	def do(self):
		u = User()
		u.nickname = self.get_required_data('nickname')
		try:
			{
				'travo' : self.check_travo_data,
				'qq'	: self.check_qq_data,
				'sina'	: self.check_sina_data
			}[self.get_required_arg('user_type')](u)
		except AuthError:
			return {RSP_CODE : RC_AUTH_FAIL}
		except EmailError:
			return {RSP_CODE : RC_ILLEGAL_EMAIL}

		u.token = self._build_token()
		try:
			u.save()
		except IntegrityError, e:
			if 'email_UNIQUE' in str(e):
				#email duplicate
				return {RSP_CODE : RC_DUP_EMAIL}
			elif 'nickname_UNIQUE' in str(e):
				#nickname duplicate
				return {RSP_CODE : RC_DUP_NICKNAME}
			elif 'qq_user_id_UNIQUE' in str(e) or 'sina_user_id_UNIQUE' in str(e):
				return {RSP_CODE : RC_DUP_BIND}

		result = {RSP_CODE : RC_SUCESS}
		result['token'] = u.token
		result['user_id'] = u.user_id
		return result

	def check_travo_data(self, u):
		u.email = self.get_required_data('email')
		u.password = self.get_required_data('password')

		if not self._check_email(u.email):
			raise EmailError(u.email)

	def check_qq_data(self, u):
		qq_token = self.get_required_data('qq_token')
		u.qq_user_id = self._get_qq_user_id(qq_token)

	def check_sina_data(self):
		sina_token = self.get_required_data('sina_token')
		u.sina_user_id = self._get_sina_user_id(sina_token)

	def _get_qq_user_id(self, qq_token):
		r = urllib2.urlopen('https://graph.z.qq.com/moc2/me?access_token=' + qq_token)
		response = r.read()
		if response.startswith('code'):
			raise AuthError('qq auth fail')
		else:
			return response[-32:]

	def _get_sina_user_id(self, sina_token):
		try:
			r = urllib2.urlopen('https://api.weibo.com/2/account/get_uid.json?access_token=' + sina_token)
			return json.loads(r.read())['uid']
		except:
			raise AuthError('sina auth fail')

	def _build_token(self):
		return uuid.uuid4().hex

	def _check_email(self, email):
		format = '^[\d\w-]+\@[\d\w-]+(\.[\d\w-]+)+$'
		return re.match(format, email) is not None

##############################################
########	TRAVEL MOUDLE	##################
##############################################
class UploadTravelView(BaseView):
	def post(self, request):
		self._request = request
		return HttpResponse(self.handle())

	def do(self):
		user = self.get_user_by_token()
		travels = self.get_required_data('travels')
		#for t in travels:
		#if 
		return 'done'

