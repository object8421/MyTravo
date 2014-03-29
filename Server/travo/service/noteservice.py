import utils
import uuid
import userservice
import traceback

from travo.rc import *
from travo.models import Note, Location, Travel
from django.core.exceptions import ValidationError
from datetime import datetime


def _build_image_path(image):
	suffix = cover.name.split('.')[-1]
	return uuid.uuid4().hex[0:16] + '.' + suffix

#########	upload	############
def upload(token, notes, images={}):
	user = userservice.get_user(token)
	rsps = []
	for n in notes:
		if n.get('id') is None or n.get('id') == 0:
			rsps.append(_new(user, n, images.get(n.get('image'))))
		else:
			rsps.append(_update(user, n, images.get(n.get('image'))))
	result = {RSP_CODE : RC_SUCESS}
	result['rsps'] = rsps
	result['lm_time'] = utils.datetimepstr(datetime.now())
	return result

def _update(user, n, image=None):
	'''use dict n to update note'''
	utils.filter_key(n,
			('travel_id', 'create_time', 'user_id', 'image_path', 'lm_time'))
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['id'] = n['id']
	rsp['tag'] = n.get('tag')
	try:
		note = Note.objects.get(pk=n['id'])
	except ObjectDoesNotExist:
			rsp[RSP_CODE] = RC_NO_SUCH_NOTE
	else:
		if note.user != user:
			#not same user
			rsp[RSP_CODE] = RC_PERMISSION_DENIED
		else:
			note.update(n)
			if image is not None: 
				_save_image(note, image)
			try:
				note.save()
			except ValidationError, e:
				print traceback.format_exc()
				rsp[RSP_CODE] = RC_ILLEGAL_DATA
	return rsp

def _new(user, n, image=None):
	'''create a new note for user'''
	if n.has_key('id'):
		n.pop('id')
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['tag'] = n.get('tag')
	if not _check_key(n):
		rsp[RSP_CODE] = RC_ILLEGAL_DATA
		return rsp
	try:
		note = Note.from_dict(n)
		location = None
		if n.has_key('location'):
			location = Location.from_dict(n['location'])
		note.user = user
		note.travel_id = n['travel_id']
	except ValueError, e:
		rsp[RSP_CODE] = RC_ILLEGAL_DATA
	else:
		if not _test_travel_owner(user.id, n['travel_id']): 
			#test the travel wheather belongs to this user
			rsp[RSP_CODE] = RC_PERMISSION_DENIED
		else:
			if not _exists_note(user.id, n['travel_id'], note.create_time):
				#do not found any same note 
				if image is not None: 
					_save_image(note, image)
				try:
					if location is not None:
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

def _save_image(note, image):
	note.image_path = _build_image_path(image)
	note.photo_time = utils.get_photo_time(image)
	if note.photo_time is None:
		note.photo_time = note.create_time
	utils.save_image(note.image_path, image)

def _exists_note(user_id, travel_id, create_time):
	return Note.objects.filter(user_id=user_id, travel_id=travel_id, create_time=create_time).exists()

def _test_travel_owner(user_id, travel_id):
	return Travel.objects.filter(user_id=user_id, id=travel_id).exists()

def _check_key(n):
	return n.has_key('travel_id') and n.has_key('create_time')

########	sync	##########
def sync(token, begin_time):
	user = userservice.get_user(token)
	result = {RSP_CODE : RC_SUCESS}
	if begin_time is None:
		#sync all note 
		result['notes'] = list(Note.objects.filter(user = user))
	else:
		begin_time = utils.strpdatetime(begin_time)
		result['notes'] = list(Note.objects.filter(user=user,lm_time__gt=begin_time))
	return result 

########	image	##########
def get_image(note_id):
	note = None
	try:
		note = Note.objects.get(pk=note_id)
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_NOTE}
	if note.image_path is None:
		return {RSP_CODE : RC_NO_IMAGE}
	result = {RSP_CODE : RC_SUCESS}
	result['image'] = utils.get_image(note.image_path)
	return result

######    get all in travel ######
def get_all_in_travel(travel_id):
	try:
		travel = Travel.objects.get(pk=travel_id)
	except:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	else:
		if not travel.is_public:
			return {RSP_CODE : RC_PERMISSION_DENIED}
		result = {RSP_CODE : RC_SUCESS}
		result['notes'] = list(Note.objects.filter(travel_id=travel_id, is_deleted=False))
		return result

