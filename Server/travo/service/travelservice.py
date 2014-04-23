#-*- coding: utf-8 -*-
import uuid
import utils
import time
import userservice
from jpush import JPushClient
from django.db.models import Q
from travo.rc import *
from travo.models import Travel,FavoriteTravel,TravelReadLog,TravelVote,TravelComment,Follow
from django.core.exceptions import ObjectDoesNotExist
from django.core.exceptions import ValidationError
from django.db import IntegrityError
from datetime import datetime

def _build_cover_path(cover):
	suffix = cover.name.split('.')[-1]
	return uuid.uuid4().hex[0:16] + '.' + suffix

def _build_snap_path(cover):
	return _build_cover_path(cover)

########	upload		###########
def upload(token, travels, covers={}):
	user = userservice.get_user(token)
	rsps = []
	for t in travels:
		if t.get('id') is None or t.get('id') == 0:
			rsps.append(_new(user, t, covers.get(t.get('cover'))))
		else:
			rsps.append(_update(user, t, covers.get(t.get('cover'))))
	result = {RSP_CODE : RC_SUCESS}
	result['rsps'] = rsps
	result['lm_time'] = utils.datetimepstr(datetime.now())
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
				_save_cover(travel, cover)
			try:
				travel.save()
			except ValidationError, e:
				rsp[RSP_CODE] = RC_ILLEGAL_DATA

	return rsp

def _new(user, t, cover=None):
	'''create a new travel for user'''
	if t.has_key('id'):
		t.pop('id')
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
				_save_cover(travel, cover)
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

def _save_cover(travel, cover):
	travel.cover_path = _build_cover_path(cover) 
	travel.snap_path = _build_snap_path(cover)
	if not utils.save_cover_snap(travel.snap_path, cover):
		travel.snap_path = travel.cover_path

	utils.save_cover(travel.cover_path, cover)

def _exists_travel(user_id, create_time):
	return Travel.objects.filter(user_id=user_id, create_time=create_time).exists()

def _check_key(t):
	'''check required key'''
	return t.has_key('title') and t.has_key('create_time') and t.has_key('begin_date')

#######		sync		###########
def sync(token, begin_time):
	user = userservice.get_user(token)
	result = {RSP_CODE : RC_SUCESS}
	if begin_time is None:
		#sync all travel
		result['travels'] = list(Travel.objects.filter(user = user))
	else:
		begin_time = utils.strpdatetime(begin_time)
		result['travels'] = list(Travel.objects.filter(user=user,lm_time__gt=begin_time))
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
		travel_list = Travel.objects.filter(user=user_id).order_by('-create_time')
		
		if recent == False:
			result['travel_list'] = travel_list
			return result
		else:
			result['travel_list'] = travel_list[:recent]
			return result
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}

#######		search		###########
SO_DEFAULT = 'default'
SO_READ_TIMES = 'read_times'
SO_VOTE_QTY = 'vote_qty'
SO_NEWEST = 'newest'

FAVORIT_SCORE = 6
COMMENT_SCORE = 3
VOTE_SCORE = 4
READ_SCORE = 1

def search(token, order=SO_DEFAULT, first_idx=1, max_qty=20):
	first_idx = int(first_idx)
	user = None
	if token is not None:
		user = userservice.get_user(token)
	return {
			SO_DEFAULT	: _search_default,
			SO_READ_TIMES: _search_read_times,
			SO_VOTE_QTY	: _search_vote_qty,
			SO_NEWEST	: _search_newest,
			}[order](first_idx - 1, max_qty, user)

def _search_default(first_idx, max_qty, user):
	if user is None:
		return _search_newest(first_idx, max_qty, None)
	#search travels which related with user
	relate_T = relate_travel(user)	

	exclude_id = _exclude_id(relate_T)
	prelate_T = clear(relate_T)		#positive related travel

	order_T = sorted(prelate_T.keys(), key=lambda x : prelate_T[x], reverse=True)

	result_T = set() 
	for t in order_T:
		if len(result_T) >= first_idx + max_qty:	#get enough
			break
		result_T |= set(Travel.objects.filter(destination__contains=t.destination).exclude(user=user).exclude(id__in=exclude_id))
	
	return list(result_T)[first_idx : max_qty]

def relate_travel(user):
	relate_T = {}
	add_favorite(user, relate_T)
	add_comment(user, relate_T)
	add_vote(user, relate_T)
	add_read(user, relate_T)
	return relate_T

def _exclude_id(relate_T):
	l = []
	for t in relate_T.keys():
		l.append(t.id)
	return l

def _search_newest(first_idx, max_qty, user):
	if user is None:
		return list(Travel.objects.order_by('create_time').reverse()[first_idx: max_qty])
	else:
		return list(Travel.objects.order_by('create_time').exclude(user=user).reverse()[first_idx: max_qty])

def _search_read_times(first_idx, max_qty, user):
	if user is None:
		return list(Travel.objects.order_by('read_times').reverse()[first_idx: max_qty])
	else:
		return list(Travel.objects.order_by('read_times').exclude(user=user).reverse()[first_idx: max_qty])

def _search_vote_qty(first_idx, max_qty, user):
	if user is None:
		return list(Travel.objects.order_by('vote_qty').reverse()[first_idx: max_qty])
	else:
		return list(Travel.objects.order_by('vote_qty').exclude(user=user).reverse()[first_idx: max_qty])

def clear(tls):
	travels = {} 
	for t in tls:
		if tls[t] > 0 :
			travels[t] = tls[t]
	return travels 
	
def merge(d, t, s):
	if t in d:
		d[t] += s
	else:
		d[t] = s

def positive(content):
	'''judge weather a  comment is positive'''
	return True

def add_favorite(u, relate_T):
	ftls = FavoriteTravel.objects.filter(user=u)
	for ft in ftls:
		merge(relate_T, ft.travel, FAVORIT_SCORE)

def add_comment(u, relate_T):
	tcls = TravelComment.objects.filter(commenter=u)
	for tc in tcls:
		if positive(tc.content):
			merge(relate_T, tc.travel, COMMENT_SCORE)
		else:
			merge(relate_T, tc.travel, -COMMENT_SCORE)


def add_vote(u, relate_T):
	tvls = TravelVote.objects.filter(voter=u)
	for tv in tvls:
		merge(relate_T, tv.travel, VOTE_SCORE)

def add_read(u, relate_T):
	trlls = TravelReadLog.objects.filter(reader=u)
	for trl in trlls:
		merge(relate_T, trl.travel, READ_SCORE)

###add by L!ar for website travel list search
def search_web(order=SO_DEFAULT, first_idx=1, max_qty=20):
	first_idx = int(first_idx)
	return {
			SO_DEFAULT	: _search_default_web,
			SO_READ_TIMES: _search_read_times_web,
			SO_VOTE_QTY	: _search_vote_qty_web,
			SO_NEWEST	: _search_newest_web,
			}[order](first_idx - 1, max_qty)

def _search_default_web(first_idx, max_qty):
	return _search_newest_web(first_idx, max_qty)

def _search_newest_web(first_idx, max_qty):
	return list(Travel.objects.order_by('-create_time'))

def _search_read_times_web(first_idx, max_qty):
	return list(Travel.objects.order_by('-read_times'))

def _search_vote_qty_web(first_idx, max_qty):
	return list(Travel.objects.order_by('-vote_qty'))
###add end

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
				return {RSP_CODE : RC_DUP_ACTION}
		else:
			return {RSP_CODE : RC_FUCK_SELF}

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
###add by L!ar for web get_favorite 'cause we don't need pagination by ourself, just use auto pagination from django provision
def get_favorite_web(token):
	user = userservice.get_user(token)
	fts = FavoriteTravel.objects.filter(user=user).order_by('time').reverse()
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
		else:
			return {RSP_CODE : RC_FUCK_SELF}
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
				return {RSP_CODE : RC_DUP_ACTION}
		else:
			return {RSP_CODE : RC_FUCK_SELF}
	return {RSP_CODE : RC_SUCESS}

######    comment    ######
def comment(token, travel_id, content):
	user = userservice.get_user(token)
	tc = TravelComment()
	#游记的拥有者
	travel_name = ''
	related_user_name = ''
	try:
		travel = Travel.objects.get(pk=travel_id)
		travel_name = travel.title
		related_user_name = travel.user.nickname
		print travel.title
		print related_user_name
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_NO_SUCH_TRAVEL}
	else:
		if travel.user == user:
			return {RSP_CODE : RC_FUCK_SELF}
	tc.travel_id = travel_id
	tc.commenter = user
	tc.content = content
	tc.save()
	APPKEY = '986aa2521dcb7f92092a8848'
	MASTER_SECRET = 'b6ddbc1f0933845e4fa50c9b'
	jpush_client = JPushClient(MASTER_SECRET)

	welcome_message = "您的游记"
	#welcome_message += travel_name
	welcome_message += "有一条新评论，点击查看"
	print welcome_message
	sendno = int(time.time())
	print sendno
	jpush_client.send_notification_by_alias(related_user_name, APPKEY, sendno, 'travo',
	                                         welcome_message,
	                                         content, 'android')


	return {RSP_CODE : RC_SUCESS}

######    get comments    ######
def get_comments(travel_id):
	result = {RSP_CODE : RC_SUCESS}
	result['comments'] = list(TravelComment.objects.filter(travel_id = travel_id))
	return result

######    friend travels    ######
def friend_travels(token, friend_id):
	user = userservice.get_user(token)
	friend = userservice.get_user_by_id(friend_id)
	result = {RSP_CODE : RC_SUCESS}
	result['travels'] = list(Travel.objects.filter(user=friend, is_public=True, is_deleted=False))
	return result
	'''
	try:
		f = Follow.objects.filter(active=user, passive=friend).latest('time')
		if f.action != '1':
			return {RSP_CODE : RC_PERMISSION_DENIED}
	except ObjectDoesNotExist:
		return {RSP_CODE : RC_PERMISSION_DENIED}
	else:
	'''

####################### search travel ########################################
def search_travel(key_word):
	travel_list = Travel.objects.filter(Q(title__contains=key_word)|Q(destination__contains=key_word))
	if travel_list:
		result = {RSP_CODE:RC_SUCESS}
		result['travel_list'] = list(travel_list)
	else:
		result = {RSP_CODE:RC_FUCK_SELF}
		result['travel_list'] = list(travel_list)
	return result
