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

from django.db import models
from datetime import datetime

def filter_key(d):
	'''remove some useless key from a model related dict'''
	for key in d.keys():
		if key.startswith('_'):
			d.pop(key)
			continue
		if isinstance(d[key], datetime):
			d[key] = str(d[key]) 
	return d

class Achievement(models.Model):
	achievement_id = models.IntegerField(primary_key=True)
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
    address_id = models.IntegerField(primary_key=True)
    address = models.CharField(max_length=20)
    district = models.ForeignKey('District')
    class Meta:
        managed = False
        db_table = 'address'

class City(models.Model):
    city_id = models.IntegerField(primary_key=True)
    name = models.CharField(unique=True, max_length=15)
    area_code = models.CharField(max_length=4)
    province = models.ForeignKey('Province')
    class Meta:
        managed = False
        db_table = 'city'

class CompleteAddress(models.Model):
    address_id = models.IntegerField()
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
    district_id = models.IntegerField(primary_key=True)
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

class Location(models.Model):
    user = models.ForeignKey('User')
    time = models.DateTimeField()
    address = models.CharField(max_length=45, blank=True)
    longitude = models.FloatField()
    latitude = models.FloatField()
    class Meta:
        managed = False
        db_table = 'location'

class LoginRecord(models.Model):
    user = models.ForeignKey('User')
    time = models.DateTimeField()
    ip = models.CharField(max_length=15)
    class Meta:
        managed = False
        db_table = 'login_record'

class Note(models.Model):
    note_id = models.IntegerField(primary_key=True)
    user = models.ForeignKey('User')
    travel = models.ForeignKey('Travel')
    create_time = models.DateTimeField()
    content = models.CharField(max_length=2048, blank=True)
    image_path = models.CharField(max_length=24, blank=True)
    comment_qty = models.IntegerField()
    vote_qty = models.IntegerField()
    is_public = models.IntegerField()
    is_deleted = models.IntegerField()
    lm_time = models.DateTimeField()
    class Meta:
        managed = False
        db_table = 'note'

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
    province_id = models.IntegerField(primary_key=True)
    name = models.CharField(unique=True, max_length=3)
    class Meta:
        managed = False
        db_table = 'province'

class ScenicArea(models.Model):
    scenic_area_id = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=45)
    description = models.CharField(max_length=300, blank=True)
    price = models.DecimalField(max_digits=7, decimal_places=2, blank=True, null=True)
    address = models.ForeignKey(Address)
    class Meta:
        managed = False
        db_table = 'scenic_area'

class ScenicPoint(models.Model):
    scenic_point_id = models.IntegerField(primary_key=True)
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
    scenic_point_info_id = models.IntegerField(primary_key=True)
    content = models.CharField(max_length=300, blank=True)
    image_path = models.CharField(max_length=25, blank=True)
    image_explain = models.CharField(max_length=300, blank=True)
    scenic_point = models.ForeignKey(ScenicPoint)
    class Meta:
        managed = False
        db_table = 'scenic_point_info'

class Travel(models.Model):
    travel_id = models.IntegerField(primary_key=True)
    user = models.ForeignKey('User')
    title = models.CharField(max_length=45)
    cover_path = models.CharField(max_length=24, blank=True)
    destination = models.CharField(max_length=45)
    begin_date = models.DateField()
    end_date = models.DateField(blank=True, null=True)
    average_spend = models.CharField(max_length=20, blank=True)
    description = models.CharField(max_length=4096, blank=True)
    create_time = models.DateTimeField()
    comment_qty = models.IntegerField()
    vote_qty = models.IntegerField()
    favorite_qty = models.IntegerField()
    read_times = models.IntegerField()
    is_public = models.IntegerField()
    is_deleted = models.IntegerField()
    lm_time = models.DateTimeField()
    class Meta:
        managed = False
        db_table = 'travel'

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

class User(models.Model):
    user_id = models.AutoField(primary_key=True)
    email = models.CharField(unique=True, max_length=25, blank=True)
    token = models.CharField(unique=True, max_length=32)
    qq_user_id = models.CharField(unique=True, max_length=32, blank=True, default=None)
    sina_user_id = models.CharField(unique=True, max_length=20, blank=True, default=None)
    password = models.CharField(max_length=16, blank=True)
    register_time = models.DateTimeField(auto_now=True)
    nickname = models.CharField(unique=True, max_length=16)
    face_path = models.CharField(max_length=24, blank=True, default=None)
    signature = models.CharField(max_length=70, blank=True)
    account = models.IntegerField(default=0)
    travel_qty = models.IntegerField(default=0)
    scenic_point_qty = models.IntegerField(default=0)
    achievement_qty = models.IntegerField(default=0)
    follower_qty = models.IntegerField(default=0)
    favorite_travel_qty = models.IntegerField(default=0)
    is_location_public = models.IntegerField(default=False)
    is_info_public = models.IntegerField(default=True)
    lm_time = models.DateTimeField()

    def dict(self):
        d = self.__dict__
        d.pop('lm_time')
        d.pop('password')
        d.pop('face_path')
        return filter_key(d)

    class Meta:
        managed = False
        db_table = 'user'

class UserAchievement(models.Model):
    user = models.ForeignKey(User)
    achievement = models.ForeignKey(Achievement)
    time = models.DateTimeField()
    class Meta:
        managed = False
        db_table = 'user_achievement'

class UserInfo(models.Model):
    user_info_id = models.IntegerField(primary_key=True)
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
    user = models.ForeignKey(User)
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

