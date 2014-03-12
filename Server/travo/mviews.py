import uuid
import re
import traceback
import json
import urllib2
import utils

from rc import * 
from django.shortcuts import render
from django.http import HttpResponse
from django.views.generic import View
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.views.generic import View
from django.core.exceptions import ObjectDoesNotExist
from django.conf import settings
from models import * 
from utils import * 

def IndexView(request):
	return HttpResponse('Welcome!')

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
		print('host:' + self._request.META['REMOTE_ADDR'])
		print('arg:' + str(self._request.GET))
		print('post:' + str(self._request.POST))
		print('body:' + self._request.body)
		
		try:
			result = self.do()
			print('==========response==========')
			print(MyJsonEncoder().encode(result))
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

	def get_user(self):
		t = self.get_required_arg('token')
		u = None
		try:
			u = User.objects.get(token=t)
		except ObjectDoesNotExist:
			raise TokenError('no_such_user')
		lr = LoginRecord.objects.filter(user=u).latest('time')
		if (datetime.now() - lr.time).days >= 60:
			raise TokenError('token_overdate')
		return u

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
		return HttpResponse(self.handle())
	
	def do(self):
		result = {
			'travo' : self.travo_login,
			'qq'	: self.qq_login,
			'sina'	: self.sina_login
		}[self.get_required_arg('user_type')]()

		if result[RSP_CODE] == RC_SUCESS:
			#record login
			self.record_login(result['user'])
		return result

	def travo_login(self):
		result = {}
		try:
			u = User.objects.get(email=self.get_required_arg('email'))
		except ObjectDoesNotExist:
			result[RSP_CODE] = RC_NO_SUCH_USER
		else:
			if u.password == self.get_required_arg('password'):
				result[RSP_CODE] = RC_SUCESS
				result['user'] = u
			else:
				result[RSP_CODE] = RC_WRONG_PASSWORD
		return result 

	def qq_login(self):
		pass

	def sina_login(self):
		pass

class RegisterView(UserView):
	def post(self, request):
		self._request = request
		return HttpResponse(self.handle())

	def do(self):
		u = User()
		u.nickname = self.get_required_data('nickname')
		#check must data
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

		#register sucess
		result = {RSP_CODE : RC_SUCESS}
		result['token'] = u.token
		result['user_id'] = u.id

		self.record_login(u)
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

	def update_travel(self, user, t):
		'''use dict t to update travel'''
		travel = None
		rsp = {RSP_CODE : RC_SUCESS}
		try:
			travel = Travel.objects.get(pk=t['id'])
		except ObjectDoesNotExist:
			rsp[RSP_CODE] = RC_NO_SUCH_TRAVEL
		else:
			if travel.user != user:
				#not same user
				rsp[RSP_CODE] = RC_PERMISSION_DENIED
			else:
				travel.update(t)
				if t.has_key('cover'):
					travel.cover_path = self.build_cover_path()
					utils.save_cover(travel.cover_path, t['cover'])
				try:
					travel.save()
				except ValidationError, e:
					rsp[RSP_CODE] = RC_ILLEGAL_DATA
		rsp['id'] = t['id']



	def do(self):
		user = self.get_user_by_token()
		travels = self.get_required_data('travels')
		rsps = []
		for t in travels:
			rsp = {RSP_CODE : RC_SUCESS}
			travel = None
			if t.has_key('id'):
				#update
				try:
					travel = Travel.objects.get(pk=t['id'])
				except ObjectDoesNotExist:
					rsp[RSP_CODE] = RC_NO_SUCH_TRAVEL
				else:
					if travel.user != user:
						#not same user
						rsp[RSP_CODE] = RC_PERMISSION_DENIED
					else:
						travel.update(t)
						if t.has_key('cover'):
							travel.cover_path = self.build_cover_path()
							utils.save_cover(travel.cover_path, t['cover'])
						try:
							travel.save()
						except ValidationError, e:
							rsp[RSP_CODE] = RC_ILLEGAL_DATA
				rsp['id'] = t['id']
			else:
				#upload
				try:
					travel = Travel.from_dict(t)
				except ValueError, e:
					raise IllegalDataError(e)
				travel.user = user
				try:
					#search weahter have same travel
					Travel.objects.get(user_id=user.id, title=travel.title, create_time=travel.create_time)
				except:
					#do not found any same travel
					if t.has_key('cover'):
						#save cover
						travel.cover_path = self.build_cover_path()
						utils.save_cover(travel.cover_path, t['cover'])
					try:
						travel.save()
					except ValidationError, e:
						rsp[RSP_CODE] = RC_ILLEGAL_DATA
					else:
						#sucess upload
						rsp['id'] = travel.id
				else:
					#found same travel
					rsp[RSP_CODE] = RC_DUP_DATA
			rsp['tag'] = t.get('tag')
			rsps.append(rsp)
		result = {RSP_CODE : RC_SUCESS}
		result['rsps'] = rsps
		return result 

	def build_cover_path(self):
		return uuid.uuid4().hex[0:16]

class SyncTravelView(BaseView):
	def get(self, request):
		self._request = request
		return HttpResponse(self.handle())

	def do(self):
		u = self.get_user_by_token()
		begin_time = self.get_arg('begin_time')
		result = {RSP_CODE : RC_SUCESS}
		if begin_time is None:
			#sync all travel
			result['travels'] = list(Travel.objects.filter(user = u))
		else:
			begin_time = utils.strpdatetime(begin_time)
			result['travels'] = list(Travel.objects.filter(user=u,lm_time__gte=begin_time))
		return result 

#############################################
########	Note MOUDLE		#################
#############################################
class UploadNoteView(BaseView):
	def post(self, request):
		self._request = request
		return HttpResponse(self.handle())
'''
	def do(self):
		user = self.get_user_by_token()
		notes = self.get_required_data('notes')
		rsps = []
		for n in notes:
			if n.has_key('id'):
				rsp = self.update_note(user, n)
			else:
				rsp = self.new_note(user, n)
			rsps.append(rsp)
		rsps[RSP_CODE] = RC_SUCESS

				note = Note.objects.get(pk=n['id'])
				if note.user != user:
					rsp[RSP_CODE] = RC_PERMISSION_DENIED
				else:
					note.update(n)
					if n.has_key('image'):
						note.image_path = self.build
					note.save()
						if t.has_key('cover'):
							travel.cover_path = self.build_cover_path()
							utils.save_cover(travel.cover_path, t['cover'])
'''
