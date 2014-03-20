import uuid
import utils
import userservice

from travo.rc import *
from travo.models import Travel,FavoriteTravel,TravelReadLog,TravelVote,TravelComment
from django.core.exceptions import ObjectDoesNotExist
from django.core.exceptions import ValidationError
from django.db import IntegrityError

def _build_cover_path():
	return uuid.uuid4().hex[0:16]

########	upload		###########
def upload(token, travels, covers={}):
	user = userservice.get_user(token)
	rsps = []
	for t in travels:
		if t.has_key('id'):
			rsps.append(_update(user, t, covers.get(t.get('cover'))))
		else:
			rsps.append(_new(user, t, covers.get(t.get('cover'))))
	result = {RSP_CODE : RC_SUCESS}
	result['rsps'] = rsps
	return result

def _update(user, t, cover=None):
	'''use dict t to update travel'''
	utils.filter_key(t, 
			('user_id', 'cover_path', 'create_time', 'comment_qty', 'vote_qty', 'favorite_qty', 'read_times', 'lm_time'))
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
			if cover is not None: 
				travel.cover_path = _build_cover_path() 
				utils.save_cover(travel.cover_path, cover)
			try:
				travel.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA

	return rsp

def _new(user, t, cover=None):
	'''create a new travel for user'''
	rsp = {RSP_CODE : RC_SUCESS}
	rsp['tag'] = t.get('tag')
	if not _check_key(t):
		rsp[RSP_CODE] = RC_ILLEGAL_DATA
		return rsp
	try:
		travel = Travel.from_dict(t)
		travel.user = user
	except ValueError, e:
		rsp[RSP_CODE] = RC_ILLEGAL_DATA
	else:
		if not _exists_travel(user.id, travel.create_time):
			if cover is not None:
				travel.cover_path = _build_cover_path() 
				utils.save_cover(travel.cover_path, cover)
			try:
				travel.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA
			else:
				#sucess new 
				rsp['id'] = travel.id
		else:
			rsp[RSP_CODE] = RC_DUP_DATA
	return rsp

def _exists_travel(user_id, create_time):
	return Travel.objects.filter(user_id=user_id, create_time=create_time).exists()

def _check_key(t):
	'''check required key'''
	return t.has_key('title') and t.has_key('create_time')

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

#######	    get  travel		###########
def get_travel(token,recent = False):
	user = userservice.get_user(token)
	user_id = user.id
	result = {RSP_CODE : RC_SUCESS}
	try:
		if recent == False:
			travel_list = Travel.objects.filter(user=user_id).order_by('-create_time')
			result['travel_list'] = travel_list
			return result
		else:
			travel_list = Travel.objects.filter(user=user_id).order_by('-create_time')[:recent]
			result['travel_list'] = travel_list
			return result
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}

#######		search		###########
SO_DEFAULT = 'default'
SO_READ_TIME = 'read_time'
SO_VOTE_QTY = 'vote_qty'
SO_NEWEST = 'newest'

def search(order=SO_DEFAULT, first_idx=1, max_qty=20):
	first_idx = int(first_idx)
	return {
			SO_DEFAULT	: _search_default,
			SO_READ_TIME: _search_read_time,
			SO_VOTE_QTY	: _search_vote_qty,
			SO_NEWEST	: _search_newest,
			}[order](first_idx - 1, max_qty)

def _search_default(first_idx, max_qty):
	return _search_newest(first_idx, max_qty)

def _search_newest(first_idx, max_qty):
	return list(Travel.objects.order_by('create_time').reverse()[first_idx:max_qty])

def _search_read_time(first_idx, max_qty):
	pass

def _search_vote_qty(first_idx, max_qty):
	pass

#######		favorit ############
def favorit(token, travel_id):
	user = userservice.get_user(token)
	try:
		travel = Travel.objects.get(pk=travel_id, is_public=True, is_deleted=False)
	except:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	else:
		if travel.user != user:		#can not favorit self's travel
			ft = FavoriteTravel()
			ft.user = user
			ft.travel_id = travel_id
			try:
				ft.save()
			except IntegrityError:
				#dup favorit
				pass

	return {RSP_CODE : RC_SUCESS}

######    get favorit ######
def get_favorit(token, first_idx=1, max_qty=20):
	user = userservice.get_user(token)
	fts = FavoriteTravel.objects.filter(user=user).order_by('time').reverse()[first_idx - 1:max_qty]
	travels = []
	for ft in fts:
		travels.append(ft.travel)
	result = {RSP_CODE : RC_SUCESS}
	result['travels'] = travels
	return result

######    read    ######
def read(token, travel_id):
	user = userservice.get_user(token)
	try:
		travel = Travel.objects.get(pk=travel_id, is_public=True)
	except:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	else:
		if travel.user != user:	#can not read self's travel
			trl = TravelReadLog()
			trl.reader = user
			trl.travel_id = travel_id
			trl.save()
	return {RSP_CODE : RC_SUCESS}

######    vote    ######
def vote(token, travel_id):
	user = userservice.get_user(token)
	try:
		travel = Travel.objects.get(pk=travel_id)
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	else:
		if travel.user != user:
			tv = TravelVote()
			tv.voter = user
			tv.travel = travel
			try:
				tv.save()
			except:
				#dup vote
				pass
	return {RSP_CODE : RC_SUCESS}

######    comment    ######
def comment(token, travel_id, content):
	user = userservice.get_user(token)
	tc = TravelComment()
	tc.travel_id = travel_id
	tc.commenter = user
	tc.content = content
	try:
		tc.save()
	except IntegrityError:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	return {RSP_CODE : RC_SUCESS}

######    get comments    ######
def get_comments(travel_id):
	result = {RSP_CODE : RC_SUCESS}
	result['comments'] = list(TravelComment.objects.filter(travel_id = travel_id))
	return result

