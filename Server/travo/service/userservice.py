import uuid
import re
import urllib2

from travo.exceptions import TokenError
from django.db import IntegrityError
from travo.models import User, LoginRecord
from django.core.exceptions import ObjectDoesNotExist
from travo.rc import * 
from datetime import datetime

def _build_token():
	return uuid.uuid4().hex

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
	'''
	lr = LoginRecord.objects.filter(user=u).latest('time')
	if (datetime.now() - lr.time).days >= 60:
		raise TokenError('token_overdate')
	'''
	return u

########	login	###############
def travo_login(email, password):
	result = {}
	try:
		u = User.objects.get(email=email)
	except ObjectDoesNotExist:
		result[RSP_CODE] = RC_NO_SUCH_USER
	else:
		if u.password == password: 
			#_update_token(u)
			result[RSP_CODE] = RC_SUCESS
			result['user'] = u
		else:
			result[RSP_CODE] = RC_WRONG_PASSWORD
	return result 

def qq_login():
	pass

def sina_login():
	pass

def _update_token(u):
	u.token = _build_token()
	u.save()

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

##########	register ################
def travo_register(nickname, email, password):
	if not _check_email(email):
		return {RSP_CODE : RC_ILLEGAL_EMAIL}
	u = User(nickname=nickname, email=email, password=password)
	u.token = _build_token()
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
	result['user_id'] = u.id
	return result

def qq_register():
	pass

def sina_register():
	pass

def change_self_info(token,attr_dict):
	#didn't handle portrait.
	user = User.objects.get(token=token)
	for attr in attr_dict.keys():
		if attr in user.keys():
			user['attr'] = attr_dict['attr']
	user.save()
	result = {RSP_CODE:RC_SUCESS}
	return result

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

