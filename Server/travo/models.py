# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Remove `managed = False` lines for those models you wish to give write DB access
# Feel free to rename the models, but don't rename db_table values or field names.
#
# Also note: You'll have to insert the output of 'django-admin.py sqlcustom [appname]'
# into your database.
from __future__ import unicode_literals
import sys

from django.db import models
from datetime import datetime
from datetime import date
from django.core.exceptions import ObjectDoesNotExist

def _first_upper(s):
	return s[0].upper() + s[1:]

class MyModel():
	@classmethod
	def from_dict(cls, d):
		o = cls()
		for key in o.__dict__:
			if not key.startswith('_'):
				if d.has_key(key):
					setattr(o, key, d[key])
				'''
				if key.endswith('_id'):
					#it's a model field 
					field = key[0:-3]	#user_id[0:-3] = user
					Field = _first_upper(field)
					mmod = sys.modules['travo.models']
					mcls = getattr(mmod, Field) 
					if d.has_key(field):
						setattr(o, field, mcls.from_dict(d[field]))
				else:
					if d.has_key(key):
						setattr(o, key, d[key])
				'''
		return o

	def dict(self):
		'''parse this model to dict'''
		return self._filter_key(self.__dict__)

	def update(self, d):
		'''use new data in dict to update model'''
		for key in d:
			if hasattr(self, key):
				setattr(self, key, d[key])

	def _filter_key(self, d):
		'''remove some useless key from a model related dict'''
		newd = dict(d)
		for key in d:
			if key.startswith('_'):
				newd.pop(key)
				continue
			if isinstance(d[key], datetime) or isinstance(d[key], date):
				newd[key] = str(d[key])
		return newd
	class meta:
		abstract = True

class SyncModel(MyModel):
	@classmethod
	def lm_time(cls, user):
		try:
			return str(cls.objects.filter(user=user).latest('lm_time').lm_time)
		except ObjectDoesNotExist:
			return None

class User(models.Model, MyModel):
	id = models.AutoField(primary_key=True)
	email = models.CharField(unique=True, max_length=25)
	token = models.CharField(unique=True, max_length=32)
	qq_user_id = models.CharField(unique=True, max_length=32, default=None)
	sina_user_id = models.CharField(unique=True, max_length=20, default=None)
	password = models.CharField(max_length=16, blank=True)
	register_time = models.DateTimeField(auto_now=True)
	nickname = models.CharField(unique=True, max_length=16)
	face_path = models.CharField(max_length=24, default=None)
	signature = models.CharField(max_length=70)
	account = models.IntegerField(default=0)
	travel_qty = models.IntegerField(default=0)
	scenic_point_qty = models.IntegerField(default=0)
	achievement_qty = models.IntegerField(default=0)
	follower_qty = models.IntegerField(default=0)
	favorite_travel_qty = models.IntegerField(default=0)
	is_location_public = models.IntegerField(default=False)
	is_info_public = models.IntegerField(default=True)
	lm_time = models.DateTimeField()

	class Meta:
		managed = False
		db_table = 'user'

class Travel(models.Model, SyncModel):
	id = models.AutoField(primary_key=True)
	user = models.ForeignKey('User')
	title = models.CharField(max_length=45)
	cover_path = models.CharField(max_length=24,null=True)
	destination = models.CharField(max_length=45)
	begin_date = models.DateField()
	end_date = models.DateField(blank=True, null=True)
	average_spend = models.CharField(max_length=20, null=True)
	description = models.CharField(max_length=4096, null=True)
	create_time = models.DateTimeField()
	comment_qty = models.IntegerField(default=0)
	vote_qty = models.IntegerField(default=0)
	favorite_qty = models.IntegerField(default=0)
	read_times = models.IntegerField(default=0)
	is_public = models.IntegerField(default=True)
	is_deleted = models.IntegerField(default=False)
	lm_time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'travel'

class Note(models.Model, SyncModel):
	id = models.AutoField(primary_key=True)
	user = models.ForeignKey('User')
	travel = models.ForeignKey('Travel')
	location = models.ForeignKey('Location', null=True)
	create_time = models.DateTimeField()
	content = models.CharField(max_length=2048, blank=True)
	image_path = models.CharField(max_length=24, blank=True)
	is_deleted = models.IntegerField(default=False)
	lm_time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'note'

class Location(models.Model, MyModel):
	id = models.AutoField(primary_key=True)
	address = models.CharField(max_length=45, null=True)
	longitude = models.FloatField()
	latitude = models.FloatField()
	class Meta:
		managed = False
		db_table = 'location'

class Achievement(models.Model):
	id = models.IntegerField(primary_key=True)
	name = models.CharField(max_length=20)
	scenic_point_qty = models.IntegerField()
	class Meta:
		managed = False
		db_table = 'achievement'

class AchievementScenicPoint(models.Model):
	achievement = models.ForeignKey(Achievement)
	scenic_point = models.ForeignKey('ScenicPoint')
	class Meta:
		managed = False
		db_table = 'achievement_scenic_point'

class Address(models.Model):
	id = models.IntegerField(primary_key=True)
	address = models.CharField(max_length=20)
	district = models.ForeignKey('District')
	class Meta:
		managed = False
		db_table = 'address'

class City(models.Model):
	id = models.IntegerField(primary_key=True)
	name = models.CharField(unique=True, max_length=15)
	area_code = models.CharField(max_length=4)
	province = models.ForeignKey('Province')
	class Meta:
		managed = False
		db_table = 'city'

class CompleteAddress(models.Model):
	id = models.IntegerField(primary_key=True)
	province = models.CharField(max_length=3, blank=True)
	city = models.CharField(max_length=15, blank=True)
	district = models.CharField(max_length=15, blank=True)
	address = models.CharField(max_length=20)
	area_code = models.CharField(max_length=4, blank=True)
	zip = models.CharField(max_length=6, blank=True)
	class Meta:
		managed = False
		db_table = 'complete_address'

class District(models.Model):
	id = models.IntegerField(primary_key=True)
	name = models.CharField(max_length=15)
	zip = models.CharField(max_length=6)
	city = models.ForeignKey(City)
	class Meta:
		managed = False
		db_table = 'district'

class ErrorLog(models.Model):
	time = models.DateTimeField(primary_key=True)
	position = models.CharField(max_length=45)
	message = models.CharField(max_length=45)
	class Meta:
		managed = False
		db_table = 'error_log'

class FavoriteTravel(models.Model):
	user = models.ForeignKey('User')
	travel = models.ForeignKey('Travel')
	time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'favorite_travel'

class Follow(models.Model):
	active = models.ForeignKey('User', db_column='active', related_name='active_follow')
	passive = models.ForeignKey('User', db_column='passive', related_name='passive_follow')
	time = models.DateTimeField()
	action = models.CharField(max_length=1)
	class Meta:
		managed = False
		db_table = 'follow'


class LoginRecord(models.Model, MyModel):
	user = models.ForeignKey('User')
	time = models.DateTimeField()
	ip = models.CharField(max_length=15)

	'''
	def __init__(self, u, t, i):
		super(LoginRecord, self).__init__()
		self.user = u
		self.time = t
		self.ip = i
	'''
	class Meta:
		managed = False
		db_table = 'login_record'
		unique_together=('user', 'time')


class NoteComment(models.Model):
	note = models.ForeignKey(Note)
	time = models.DateTimeField()
	commenter = models.ForeignKey('User', db_column='commenter')
	content = models.CharField(max_length=200)
	class Meta:
		managed = False
		db_table = 'note_comment'

class NoteVote(models.Model):
	note = models.ForeignKey(Note)
	time = models.DateTimeField()
	voter = models.ForeignKey('User', db_column='voter')
	class Meta:
		managed = False
		db_table = 'note_vote'

class Province(models.Model):
	id = models.IntegerField(primary_key=True)
	name = models.CharField(unique=True, max_length=3)
	class Meta:
		managed = False
		db_table = 'province'

class ScenicArea(models.Model):
	id = models.IntegerField(primary_key=True)
	name = models.CharField(max_length=45)
	description = models.CharField(max_length=300, blank=True)
	price = models.DecimalField(max_digits=7, decimal_places=2, blank=True, null=True)
	address = models.ForeignKey(Address)
	class Meta:
		managed = False
		db_table = 'scenic_area'

class ScenicPoint(models.Model):
	id = models.IntegerField(primary_key=True)
	name = models.CharField(max_length=45)
	longitude = models.FloatField()
	latitude = models.FloatField()
	price = models.DecimalField(max_digits=7, decimal_places=2, blank=True, null=True)
	description = models.CharField(max_length=300, blank=True)
	scenic_area = models.ForeignKey(ScenicArea)
	class Meta:
		managed = False
		db_table = 'scenic_point'

class ScenicPointInfo(models.Model):
	id = models.IntegerField(primary_key=True)
	content = models.CharField(max_length=300, blank=True)
	image_path = models.CharField(max_length=25, blank=True)
	image_explain = models.CharField(max_length=300, blank=True)
	scenic_point = models.ForeignKey(ScenicPoint)
	class Meta:
		managed = False
		db_table = 'scenic_point_info'

class TravelComment(models.Model):
	travel = models.ForeignKey(Travel)
	time = models.DateTimeField()
	commenter = models.ForeignKey('User', db_column='commenter')
	content = models.CharField(max_length=200)
	class Meta:
		managed = False
		db_table = 'travel_comment'

class TravelPlan(models.Model):
	user = models.ForeignKey('User')
	publish_time = models.DateTimeField()
	title = models.CharField(max_length=70)
	begin_date = models.DateField()
	end_date = models.DateField()
	destination = models.CharField(max_length=45)
	transport = models.CharField(max_length=45, blank=True)
	publisher_name = models.CharField(max_length=10, blank=True)
	publisher_sex = models.CharField(max_length=1, blank=True)
	publisher_age = models.IntegerField(blank=True, null=True)
	publisher_member = models.CharField(max_length=12, blank=True)
	hope_sex = models.CharField(max_length=1, blank=True)
	hope_age = models.CharField(max_length=12, blank=True)
	hope_member = models.CharField(max_length=12, blank=True)
	mobile = models.CharField(max_length=11, blank=True)
	qq = models.CharField(max_length=12, blank=True)
	conment = models.CharField(max_length=200, blank=True)
	lm_time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'travel_plan'

class TravelReadLog(models.Model):
	travel = models.ForeignKey(Travel)
	reader = models.ForeignKey('User', db_column='reader')
	time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'travel_read_log'

class TravelVote(models.Model):
	travel = models.ForeignKey(Travel)
	time = models.DateTimeField()
	voter = models.ForeignKey('User', db_column='voter')
	class Meta:
		managed = False
		db_table = 'travel_vote'

class UserAchievement(models.Model):
	user = models.ForeignKey(User)
	achievement = models.ForeignKey(Achievement)
	time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'user_achievement'

class UserInfo(models.Model, SyncModel):
	#id = models.IntegerField(primary_key=True)
	phone = models.CharField(max_length=12, blank=True)
	mobile = models.CharField(max_length=11, blank=True)
	qq = models.CharField(max_length=12, blank=True)
	sina_blog = models.CharField(max_length=25, blank=True)
	name = models.CharField(max_length=4, blank=True)
	age = models.IntegerField(blank=True, null=True)
	sex = models.CharField(max_length=1, blank=True)
	address = models.ForeignKey(Address, db_column='address', blank=True, null=True, related_name='+')
	address2 = models.ForeignKey(Address, db_column='address2', blank=True, null=True, related_name='+')
	native_place = models.ForeignKey(Province, db_column='native_place', blank=True, null=True)
	degree = models.CharField(max_length=6, blank=True)
	job = models.CharField(max_length=15, blank=True)
	user = models.ForeignKey(User, primary_key=True)
	lm_time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'user_info'

class UserScenicPoint(models.Model):
	user = models.ForeignKey(User)
	scenic_point = models.ForeignKey(ScenicPoint)
	time = models.DateTimeField()
	class Meta:
		managed = False
		db_table = 'user_scenic_point'

