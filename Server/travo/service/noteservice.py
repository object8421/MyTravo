import utils
import uuid
import userservice

from travo.rc import *
from travo.models import Note, Location
from django.core.exceptions import ValidationError


def _build_image_path():
	return uuid.uuid4().hex[0:16]

#########	upload	############
def upload(token, notes):
	user = userservice.get_user(token)
	rsps = []
	for n in notes:
		if n.has_key('id'):
			rsps.append(_update(user, n))
		else:
			rsps.append(_new(user, n))
	result = {RSP_CODE : RC_SUCESS}
	result['rsps'] = rsps
	return result

def _update(user, n):
	'''use dict n to update note'''
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['id'] = t['id']
	rsp['tag'] = t.get('tag')
	try:
		note = Note.objects.get(pk=t['id'])
	except ObjectDoesNotExist:
			rsp[RSP_CODE] = RC_NO_SUCH_NOTE
	else:
		if note.user != user:
			#not same user
			rsp[RSP_CODE] = RC_PERMISSION_DENIED
		else:
			note.update(t)
			if n.has_key('image'):
				note.image_path = _fetch_image_and_save(n['image']) 
			try:
				note.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA
	return rsp

def _new(user, n):
	'''create a new note for user'''
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['tag'] = n.get('tag')
	try:
		note = Note.from_dict(n)
		location = Location.from_dict(n['location'])
		note.user = user
		note.travel_id = n['travel_id']
	except ValueError, e:
		rsp[RSP_CODE] = RC_ILLEGAL_DATA
	else:
		try:
			#search whether have same note 
			Note.objects.get(user_id=user.id, travel_id=n['travel_id'], create_time=note.create_time)
		except:
			#do not found any same travel
			if n.has_key('image'):
				note.image_path = _fetch_image_and_save(n['image']) 
			try:
				location.save()
				note.location = location
				note.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA
			else:
				#sucess new 
				rsp['id'] = note.id
		else:
			#found same travel
			rsp[RSP_CODE] = RC_DUP_DATA
	return rsp

def _fetch_image_and_save(n):
	path = _build_image_path()
	utils.save_image(path, n['image'])
	return path

########	sync	##########
def sync(token, begin_time):
	user = userservice.get_user(token)
	result = {RSP_CODE : RC_SUCESS}
	if begin_time is None:
		#sync all note 
		result['travels'] = list(Note.objects.filter(user = user))
	else:
		begin_time = utils.strpdatetime(begin_time)
		result['travels'] = list(Note.objects.filter(user=user,lm_time__gte=begin_time))
	return result 
