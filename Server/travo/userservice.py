import uuid
import re

from django.db import IntegrityError
from models import *
from django.core.exceptions import ObjectDoesNotExist
from rc import * 

def _build_token():
	return uuid.uuid4().hex

def _check_email(email):
	format = '^[\d\w-]+\@[\d\w-]+(\.[\d\w-]+)+$'
	return re.match(format, email) is not None

########	login	###############
def travo_login(email, password):
	result = {}
	try:
		u = User.objects.get(email=email)
	except ObjectDoesNotExist:
		result[RSP_CODE] = RC_NO_SUCH_USER
	else:
		if u.password == password: 
			result[RSP_CODE] = RC_SUCESS
			result['user'] = u
		else:
			result[RSP_CODE] = RC_WRONG_PASSWORD
	return result 

def qq_login():
	pass

def sina_login():
	pass

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
