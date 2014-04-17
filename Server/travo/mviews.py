import traceback
import json
import service

from service import userservice, travelservice, noteservice
from rc import *
from django.http import HttpResponse
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.views.generic import View
from django.http import Http404
from exceptions import IllegalDataError, MissingArgumentError, TokenError
from models import * 
from utils import MyJsonEncoder 

class JsonResponse(HttpResponse):
	def __init__(self, data, **kwargs):
		content = MyJsonEncoder().encode(data)
		kwargs['content_type'] = 'application/json'
		super(JsonResponse, self).__init__(content, kwargs)

class ImageResponse(HttpResponse):
	def __init__(self, data, **kwargs):
		kwargs['content_type'] = 'image/jpeg; charsete=utf-8'
		super(ImageResponse, self).__init__(data, kwargs)

def IndexView(request):
	return HttpResponse('Welcome!')

class BaseView(View):
	def __init__(self):
		super(BaseView, self).__init__()
		self._data = None

	def handle(self):
		print
		print('************request************')
		print('host:' + self._request.META['REMOTE_ADDR'])
		print('arg:' + str(self._request.GET))

		print('body:' + self._request.body)
		'''
		print('post:' + str(self._request.POST))
		print('FILES:' + str(self._request.FILES))
		'''

		try:
			result = self.do()
			print('==========response==========')
			print result
			return result
		except IllegalDataError:
			print('======caught exception======')
			print(traceback.format_exc())
			return {RSP_CODE : RC_ILLEGAL_DATA}
		except MissingArgumentError:
			print('======caught exception======')
			print(traceback.format_exc())
			return {RSP_CODE : RC_WRONG_ARG}
		except TokenError, e:
			print('======caught exception======')
			print(traceback.format_exc())
			rsp_code = {
					'no_such_user' : RC_NO_SUCH_USER,
					'token_overdate': RC_TOKEN_OVERDATE
					}[e.args[0]]
			return {RSP_CODE : rsp_code}
		except Exception, e:
			print('======caught exception======')
			print(traceback.format_exc())
			return {RSP_CODE : RC_SERVER_ERROR}

	def do(self):
		pass

	def get_token(self):
		return self.get_required_arg('token')

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

	def get_form_data(self, key):
		try:
			data = self._request.POST[key]
		except MultiValueDictKeyError:
			raise IllegalDataError(key)
		try:
			return json.loads(data)[key]
		except ValueError:
			raise IllegalDataError('can not parse json string to object')
		except KeyError:
			raise IllegalDataError('json object key must be same as form key')

###############################################
########	USER MOUDLE		###################
###############################################
class LoginView(BaseView):
	def get(self, request, *args):
		self._request = request
		return JsonResponse(self.handle())
	
	def do(self):
		result = {
			'travo' : self.travo_login,
			'qq'	: self.qq_login
		}[self.get_required_arg('user_type')]()

		return result
	
	def travo_login(self):
		return userservice.travo_login(
				self.get_required_arg('email'),
				self.get_required_arg('password'),
				self._request.META['REMOTE_ADDR'])

	def qq_login(self):
		return userservice.qq_login(
				self.get_required_arg('qq_token'),
				self._request.META['REMOTE_ADDR'])

class RegisterView(BaseView):
	def post(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		result = {
			'travo' : self.travo_register,
			'qq'	: self.qq_register
		}[self.get_required_arg('user_type')]()

		return result


	def travo_register(self):
		return userservice.travo_register(
						self.get_required_data('nickname'),
						self.get_required_data('email'),
						self.get_required_data('password'),
						self._request.META['REMOTE_ADDR']
						)

	def qq_register(self):
		return userservice.qq_register(
						self.get_required_data('nickname'),
						self.get_required_data('qq_token'),
						self._request.META['REMOTE_ADDR']
						)

class UpdateUserView(BaseView):
	def post(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return userservice.update(
				self.get_token(),
				nickname = self.get_arg('nickname'),
				signature = self.get_arg('signature'),
				is_info_public = self.get_arg('is_info_public'),
				face = self._request.FILES.get('face') 
				)

class UpdateUserInfoView(BaseView):
	def put(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return userservice.update_info(
				self.get_token(),
				json.loads(self._request.body)
				)

class GetFaceView(BaseView):
	def get(self, request, user_id):
		self._request = request
		self._user_id = user_id
		result = self.handle()
		if result[RSP_CODE] == RC_SUCESS:
			return ImageResponse(result.pop('face'))
		else:
			raise Http404

	def do(self):
		return userservice.get_face(self._user_id)

class FollowView(BaseView):
	def put(self, request, user_id):
		self. _request = request
		self._user_id = user_id
		return JsonResponse(self.handle())

	def do(self):
		return userservice.follow(
				self.get_token(),
				self._user_id,
				self.action
				)

class FollowUserView(FollowView):
	def put(self, request, user_id):
		self.action = 1
		return super(FollowUserView, self).put(request, user_id)
	
class UnfollowUserView(FollowView):
	def put(self, request, user_id):
		self.action = 0
		return super(UnfollowUserView, self).put(request, user_id)

class FollowListView(BaseView):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return userservice.follow_list(self.get_token())

class UserInfoView(BaseView):
	def get(self, request, user_id):
		self._request = request
		self._user_id = user_id
		return JsonResponse(self.handle())
	def do(self):
		return userservice.get_user_info(
				self.get_arg('token'),
				self._user_id
				)

class UpdateEmailView(BaseView):
	def put(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return userservice.update_email(
				self.get_token(),
				self.get_required_arg('email'),
				self.get_required_arg('password')
				)

class BindView(BaseView):
	def put(self, request):
		self._request = request
		return JsonResponse(self.handle())
	def do(self):
		return userservice.bind(
				self.get_token(),
				self.get_required_arg('qq_token')
				)

class UpdatePasswordView(BaseView):
	def put(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return userservice.update_pass(
				self.get_required_arg('email'),
				self.get_required_arg('old_password'),
				self.get_required_arg('new_password')
				)

class GetSimilarUserView(BaseView):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		return userservice.similar(self.get_token())

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
				self.get_form_data('travels'),
				self._request.FILES
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
			return ImageResponse(result.pop('cover'))
		else:
			raise Http404

	def do(self):
		return travelservice.get_cover(
				self._travel_id,
				self.get_arg('token')
				)

class UserAppender():
	def append_user(self, travels):
		'''append user info into travels'''
		d_travels = []
		for t in travels:
			travel = t.dict()
			travel['user'] = t.user.public_dict()
			travel.pop('user_id')
			d_travels.append(travel)
		return d_travels 

class SearchTravelView(BaseView,UserAppender):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		travels = travelservice.search(
				self.get_arg('token'),
				self.get_arg('order'),
				self.get_arg('first_idx', 1),
				self.get_arg('max_qty', 20)
				)
		result = {RSP_CODE : RC_SUCESS}
		result['travels'] = self.append_user(travels)
		return result

class FavoritTravelView(BaseView):
	def post(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		return JsonResponse(self.handle())
	def do(self):
		return travelservice.favorit(
				self.get_required_arg('token'),
				self._travel_id
				)

class GetFavoritTravelView(BaseView, UserAppender):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		result = travelservice.get_favorit(
				self.get_required_arg('token'),
				int(self.get_arg('first_index', 1)),
				int(self.get_arg('max_qty', 20))
				)
		result['travels'] = self.append_user(result['travels'])
		return result

class ReadTravelView(BaseView):
	def post(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		return JsonResponse(self.handle())
	def do(self):
		return travelservice.read(
				self.get_required_arg('token'),
				self._travel_id
				)

class VoteTravelView(BaseView):
	def post(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		return JsonResponse(self.handle())
	def do(self):
		return travelservice.vote(
				self.get_required_arg('token'),
				self._travel_id
				)

class CommentTravelView(BaseView):
	def post(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		return JsonResponse(self.handle())

	def do(self):
		return travelservice.comment(
				self.get_token(),
				self._travel_id,
				self.get_required_data('content')
				)

class GetCommentsView(BaseView):
	def get(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		return JsonResponse(self.handle())

	def do(self):
		result = travelservice.get_comments(self._travel_id)
		comments = []
		for c in result['comments']:
			comment = c.dict()
			comment['commenter'] = c.commenter.public_dict()
			comment.pop('commenter_id')
			comments.append(comment)
		result['comments'] = comments
		return result

class FriendTravelsView(BaseView):
	def get(self, request, user_id):
		self._request = request
		self._user_id = user_id
		return JsonResponse(self.handle())

	def do(self):
		return travelservice.friend_travels(
				self.get_token(),
				self._user_id
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
				self.get_form_data('notes'),
				self._request.FILES
				)

class LocationAppender():
	'''append location info to note'''
	def append_location(self, notes):
		d_notes = []
		for n in notes:
			note = n.dict()
			if n.location is None:
				note['location'] = None
			else:
				note['location'] = n.location.dict()
			d_notes.append(note)
		return d_notes

class SyncNoteView(BaseView, LocationAppender):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())

	def do(self):
		result = noteservice.sync(
				self.get_required_arg('token'),
				self.get_arg('begin_time')
				)
		result['notes'] = self.append_location(result['notes'])
		return result

class ImageView(BaseView):
	def get(self, request, note_id):
		self._request = request
		self._note_id = note_id
		result = self.handle()
		if result[RSP_CODE] == RC_SUCESS:
			return ImageResponse(result.pop('image'))
		else:
			raise Http404
	
	def do(self):
		return noteservice.get_image(
				self._note_id)

class GetNoteInTravelView(BaseView, LocationAppender):
	def get(self, request, travel_id):
		self._request = request
		self._travel_id = travel_id
		return JsonResponse(self.handle())

	def do(self):
		result = noteservice.get_all_in_travel(self._travel_id)
		if result[RSP_CODE] == RC_SUCESS:
			result['notes'] = self.append_location(result['notes'])
		return result

#############################################
########	SYNC MOUDLE		#################
#############################################
class SyncView(BaseView):
	def get(self, request):
		self._request = request
		return JsonResponse(self.handle())
	def do(self):
		return service.sync_state(self.get_required_arg('token'))

