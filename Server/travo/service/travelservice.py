import uuid
import utils
import userservice

from travo.rc import *
from travo.models import Travel
from django.core.exceptions import ObjectDoesNotExist
from django.core.exceptions import ValidationError
def _build_cover_path():
	return uuid.uuid4().hex[0:16]

########	upload		###########
def upload(token, travels):
	user = userservice.get_user(token)
	rsps = []
	for t in travels:
		if t.has_key('id'):
			rsps.append(_update(user, t))
		else:
			rsps.append(_new(user, t))
	result = {RSP_CODE : RC_SUCESS}
	result['rsps'] = rsps
	return result

def _update(user, t):
	'''use dict t to update travel'''
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['id'] = t['id']
	rsp['tag'] = t.get('tag')
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
				travel.cover_path = _fetch_cover_and_save(t) 
			try:
				travel.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA
	return rsp

def _new(user, t):
	'''create a new travel for user''' 
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['tag'] = t.get('tag')
	try:
		travel = Travel.from_dict(t)
		travel.user = user
	except ValueError, e:
		rsp[RSP_CODE] = RC_ILLEGAL_DATA
	else:
		try:
			#search whether have same travel
			Travel.objects.get(user_id=user.id, title__search=travel.title, create_time=travel.create_time)
		except:
			#do not found any same travel
			if t.has_key('cover'):
				travel.cover_path = _fetch_cover_and_save(t) 
			try:
				travel.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA
			else:
				#sucess new 
				rsp['id'] = travel.id
		else:
			#found same travel
			rsp[RSP_CODE] = RC_DUP_DATA
	return rsp

def _fetch_cover_and_save(t):
	path = _build_cover_path()
	utils.save_cover(path, t['cover'])
	return path

#######		sync		###########
def sync(token, begin_time):
	user = userservice.get_user(token)
	result = {RSP_CODE : RC_SUCESS}
	if begin_time is None:
		#sync all travel
		result['travels'] = list(Travel.objects.filter(user = user))
	else:
		begin_time = utils.strpdatetime(begin_time)
		result['travels'] = list(Travel.objects.filter(user=user,lm_time__gte=begin_time))
	return result 

#######		cover		###########
def get_cover(travel_id, token):
	travel = None
	result = {RSP_CODE : RC_SUCESS}
	try:
		travel = Travel.objects.get(pk=travel_id)
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	if travel.cover_path is None:
		return {RSP_CODE : RC_NO_COVER}
	if travel.is_public is False:
		user = userservice.get_user()
		if travel.user != user:
			return {RSP_CODE : RC_PERMISSION_DENIED}
		else:
			result['cover'] = utils.get_cover(travel.cover_path)
	else:
		result['cover'] = utils.get_cover(travel.cover_path)
	return result
