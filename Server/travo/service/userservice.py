import uuid
import re
import urllib2
import utils
import travelservice

from travo.exceptions import TokenError, IllegalDataError,AuthError
from django.db import IntegrityError
from travo.models import User, LoginRecord,UserInfo,Follow,FavoriteTravel
from django.core.exceptions import ObjectDoesNotExist
from django.core.exceptions import ValidationError
from django.db.utils import OperationalError
from django.conf import settings
from travo.rc import * 
from datetime import datetime

def _build_token():
	return uuid.uuid4().hex

def _build_face_path(face):
	suffix = face.name.split('.')[-1]
	return uuid.uuid4().hex[0:16] + '.' + suffix

def _check_email(email):
	format = '^[\d\w-]+\@[\d\w-]+(\.[\d\w-]+)+$'
	return re.match(format, email) is not None

def get_user(t):
	'''use token to get user'''
	u = None
	try:
		u = User.objects.get(token=t)
	except ObjectDoesNotExist:
		raise TokenError('no_such_user')
	lr = LoginRecord.objects.filter(user=u).latest('time')
	if (datetime.now() - lr.time).days >= settings.TOKEN_VALID_DAY:
		raise TokenError('token_overdate')
	return u

def get_user_by_id(user_id):
	try:
		return User.objects.get(pk=user_id)
	except ObjectDoesNotExist:
		raise TokenError('no_such_user')

def record_login(user, ip):
	lr = LoginRecord(user=user, time=datetime.now(), ip=ip)
	lr.save()

########	login	###############
def travo_login(email, password, ip):
	result = {}
	try:
		u = User.objects.get(email=email)
	except ObjectDoesNotExist:
		result[RSP_CODE] = RC_NO_SUCH_USER
	else:
		if u.password == password: 
			_update_token(u)
			result[RSP_CODE] = RC_SUCESS
			result['user'] = u
			record_login(u, ip)
		else:
			result[RSP_CODE] = RC_WRONG_PASSWORD
	return result 

def qq_login(qq_token, ip):
	try:
		qq_id = _get_qq_id(qq_token)
	except AuthError:
		return {RSP_CODE : RC_AUTH_FAIL}
	else:
		try:
			u = User.objects.get(qq_user_id=qq_id)
		except ObjectDoesNotExist:
			return {RSP_CODE : RC_NO_SUCH_USER}
		else:
			result = {RSP_CODE : RC_SUCESS}
			_update_token(u)
			result['user'] = u
			record_login(u, ip)
			return result

def _update_token(u):
	u.token = _build_token()
	u.save()

def _get_qq_id(qq_token):
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

##########	register ################
def travo_register(nickname, email, password, ip):
	if not _check_email(email):
		return {RSP_CODE : RC_ILLEGAL_EMAIL}
	u = User(nickname=nickname, email=email, password=password)
	u.token = _build_token()
	return register(u, ip)

def qq_register(nickname, qq_token, ip):
	try:
		qq_id = _get_qq_id(qq_token)
	except AuthError:
		return {RSP_CODE : RC_AUTH_FAIL}
	else:
		u = User(qq_user_id=qq_id, token=_build_token())
		return register(u, ip)

def register(u, ip):
	try:
		u.save()
	except IntegrityError, e:
		if 'email_UNIQUE' in str(e):
			#email duplicate
			return {RSP_CODE : RC_DUP_EMAIL}
		elif 'nickname_UNIQUE' in str(e):
			#nickname duplicate
			return {RSP_CODE : RC_DUP_NICKNAME}
		elif 'qq_user_id_UNIQUE' in str(e): 
			return {RSP_CODE : RC_DUP_BIND}

	result = {RSP_CODE : RC_SUCESS}
	result['token'] = u.token
	result['user_id'] = u.id
	record_login(u, ip)
	return result

#########	update	################
def update(token, **kwargs):
	user = get_user(token)
	if kwargs['nickname'] is not None:
		user.nickname = kwargs['nickname']
	if kwargs['signature'] is not None:
		user.signature = kwargs['signature']
	if kwargs['is_info_public'] is not None:
		user.is_info_public = kwargs['is_info_public']

	if kwargs['face'] != None:
		face_path = _build_face_path(kwargs['face'])
		user.face_path = face_path
		utils.save_face(face_path, kwargs['face'])
	try:
		user.save()
	except IntegrityError:
		return {RSP_CODE : RC_DUP_NICKNAME}

	return {RSP_CODE : RC_SUCESS}

######    get face    ######
def get_face(user_id):
	user = get_user_by_id(user_id)
	result = {}
	if user.face_path is None:
		result[RSP_CODE] = RC_NO_FACE
	else:
		result[RSP_CODE] = RC_SUCESS
		result['face'] = utils.get_face(user.face_path)
	return result

######   update info    ######
def update_info(token, info):
	user = get_user(token)
	ui = UserInfo.from_dict(info)
	

	ui.user = user
	ui.save()
	return {RSP_CODE : RC_SUCESS}

def change_self_info(token,attr_dict):
	#didn't handle portrait.
	user = User.objects.get(token=token)
	for attr in attr_dict.keys():
		if attr in user.keys():
			user['attr'] = attr_dict['attr']
	user.save()
	result = {RSP_CODE:RC_SUCESS}
	return result

##add by L!ar for changing avatar or adding avatar if there's no one #####
def change_self_avatar(user,image=None):
	if image is not None: 
		_save_image(user, image)
def _save_image(user, image):
	user.face_path = _build_face_path(image)
	user.save()
	utils.save_image(user.face_path, image)	
##add end ###
##add by L!ar for changing signature
def change_signature_authority(token, signature, is_info_public):
	user = User.objects.get(token=token)
	user.signature = signature
	user.is_info_public = is_info_public
	user.save()
	result = {RSP_CODE:RC_SUCESS}
	return result
##end add
def change_authority(token, authority):
	user = User.objects.get(token=token)
	user.is_info_public = authority
	user.save()
	result = {RSP_CODE:RC_SUCESS}
	return result
##end add
def change_password(token, original_password, new_password):
	user = User.objects.get(token=token)
	if user.password == original_password:
		user.password = new_password
		user.save()
		result = {RSP_CODE:RC_SUCESS}
		return result
	else:
		result = result = {RSP_CODE:RC_WRONG_PASSWORD}
		return result

######    follow      ######
def follow(token, passive_id, action):
	activer = get_user(token)
	if activer.id == int(passive_id):
		return {RSP_CODE : RC_FUCK_SELF}
	passiver = get_user_by_id(passive_id)
	f = Follow()
	f.active = activer
	f.passive = passiver
	f.action = action
	try:
		f.save()
	except OperationalError,e:
		if e.args[1] == 'can not insert same action continuously':
			return {RSP_CODE : RC_DUP_ACTION}
		else:
			raise IllegalDataError
	return {RSP_CODE : RC_SUCESS}

######    follow list    ######
def follow_list(token):
	user = get_user(token)
	fl = list(Follow.objects.filter(active=user).order_by('time').reverse())
	unique_passive = []
	result = {RSP_CODE : RC_SUCESS}
	result['users'] = []
	for f in fl:
		if not f.passive in unique_passive:
			unique_passive.append(f.passive)
			result['users'].append(f.passive.public_dict())
	return result

######    get user info    ########
def get_user_info(token, friend_id):
	try:
		u = User.objects.get(pk=friend_id)
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_USER}
	else:
		if u.is_info_public == 0 and u.token != token:
			return {RSP_CODE : RC_PERMISSION_DENIED}
		else:
			result = {RSP_CODE : RC_SUCESS}
			result['user_info'] = UserInfo.objects.get(pk=u.id)
			return result

######    update email    ######
def update_email(token, email, password):
	if not _check_email(email):
		return {RSP_CODE : RC_ILLEGAL_EMAIL}
	user = get_user(token)
	if user.email is None:
		#add email
		user.email = email
		user.password = password
	else:
		#change email
		if user.password != password:
			return {RSP_CODE : RC_WRONG_PASSWORD}
		else:
			user.email = email
	try:
		user.save()
	except IntegrityError, e:
		if 'email_UNIQUE' in str(e):
			#email duplicate
			return {RSP_CODE : RC_DUP_EMAIL}

	return {RSP_CODE : RC_SUCESS}

######    bind QQ    ######
def bind(token, qq_token):
	user = get_user(token)
	try:
		qq_id = _get_qq_id(qq_token)
	except AuthError:
		return {RSP_CODE : RC_AUTH_FAIL}
	else:
		user.qq_user_id = qq_id
		try:
			user.save()
		except IntegrityError, e:
			if 'qq_user_id_UNIQUE' in str(e): 
				return {RSP_CODE : RC_DUP_BIND}
	return {RSP_CODE : RC_SUCESS}

######    update pass    ######
def update_pass(email, old_pass, new_pass):
	try:
		u = User.objects.get(email=email)
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_USER}
	else:
		if u.password != old_pass:
			return {RSP_CODE : RC_WRONG_PASSWORD}
		else:
			u.password = new_pass
			u.token = _build_token()
			u.save()
			result = {RSP_CODE : RC_SUCESS}
			result['token'] = u.token
			return result

######    similar user    ######
def similar(token):
	user = get_user(token)

	relate_T = travelservice.relate_travel(user)
	prelate_T = travelservice.clear(relate_T)		#positive related travel
	order_T = sorted(prelate_T.keys(), key=lambda x : prelate_T[x], reverse=True)
	
	followed_id = _followed_id(user)
	similar_user = set() 
	for t in order_T:
		if len(similar_user) >= 5:
			break
		if not t.user_id in followed_id:
			similar_user.add(t.user)
		for ft in FavoriteTravel.objects.filter(travel=t).exclude(user_id__in=followed_id):
			similar_user.add(ft.user)

	result = {RSP_CODE : RC_SUCESS}
	result['users'] = list(similar_user)
	return result

def _followed_id(user):
	follows = follow_list(user.token)['users']
	ids = []
	for f in follows:
		ids.append(f['id'])
	return ids 



