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

class Achievement(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
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
    id = models.IntegerField(primary_key=True) # AutoField?
    address = models.CharField(max_length=20)
    district = models.ForeignKey('District')
    class Meta:
        managed = False
        db_table = 'address'

class AuthGroup(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(unique=True, max_length=80)
    class Meta:
        managed = False
        db_table = 'auth_group'

class AuthGroupPermissions(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    group = models.ForeignKey(AuthGroup)
    permission = models.ForeignKey('AuthPermission')
    class Meta:
        managed = False
        db_table = 'auth_group_permissions'

class AuthPermission(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(max_length=50)
    content_type = models.ForeignKey('DjangoContentType')
    codename = models.CharField(max_length=100)
    class Meta:
        managed = False
        db_table = 'auth_permission'

class AuthUser(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    password = models.CharField(max_length=128)
    last_login = models.DateTimeField()
    is_superuser = models.IntegerField()
    username = models.CharField(unique=True, max_length=30)
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=30)
    email = models.CharField(max_length=75)
    is_staff = models.IntegerField()
    is_active = models.IntegerField()
    date_joined = models.DateTimeField()
    class Meta:
        managed = False
        db_table = 'auth_user'

class AuthUserGroups(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    user = models.ForeignKey(AuthUser)
    group = models.ForeignKey(AuthGroup)
    class Meta:
        managed = False
        db_table = 'auth_user_groups'

class AuthUserUserPermissions(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    user = models.ForeignKey(AuthUser)
    permission = models.ForeignKey(AuthPermission)
    class Meta:
        managed = False
        db_table = 'auth_user_user_permissions'

class City(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(unique=True, max_length=15)
    area_code = models.CharField(max_length=4)
    province = models.ForeignKey('Province')
    class Meta:
        managed = False
        db_table = 'city'

class CompleteAddress(models.Model):
    id = models.IntegerField()
    province = models.CharField(max_length=3, blank=True)
    city = models.CharField(max_length=15, blank=True)
    district = models.CharField(max_length=15, blank=True)
    address = models.CharField(max_length=20)
    area_code = models.CharField(max_length=4, blank=True)
    zip = models.CharField(max_length=6, blank=True)
    class Meta:
        managed = False
        db_table = 'complete_address'

class DesCity(models.Model):
    id = models.ForeignKey('DesProvence', db_column='id', primary_key=True)
    city_name = models.CharField(max_length=45)
    background_description = models.CharField(max_length=45, blank=True)
    history_introduction = models.CharField(max_length=45, blank=True)
    geograpyic_info = models.CharField(max_length=45, blank=True)
    transportation_info = models.CharField(max_length=45, blank=True)
    proper_travel_time = models.CharField(max_length=45, blank=True)
    visa_info = models.CharField(max_length=45, blank=True)
    attention = models.CharField(max_length=45, blank=True)
    travel_advice = models.CharField(max_length=45, blank=True)
    image_path = models.CharField(max_length=45, blank=True)
    class Meta:
        managed = False
        db_table = 'des_city'

class DesCountry(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)
    image_path = models.TextField(blank=True)
    country_name = models.CharField(max_length=40)
    class Meta:
        managed = False
        db_table = 'des_country'

class DesProvence(models.Model):
    id = models.ForeignKey(DesCountry, db_column='id', primary_key=True)
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)
    image_path = models.TextField(blank=True)
    provence_name = models.CharField(max_length=40)
    class Meta:
        managed = False
        db_table = 'des_provence'

class DesScenerySpot(models.Model):
    id = models.ForeignKey(DesCity, db_column='id', primary_key=True)
    spot_name = models.CharField(max_length=45, blank=True)
    ticket = models.TextField(blank=True)
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)
    image_path = models.TextField(blank=True)
    class Meta:
        managed = False
        db_table = 'des_scenery_spot'

class DestinationCity(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    background_description = models.CharField(max_length=500, blank=True)
    history_introduction = models.CharField(max_length=500, blank=True)
    geograpyic_info = models.CharField(max_length=500, blank=True)
    transportation_info = models.CharField(max_length=1000, blank=True)
    proper_travel_time = models.CharField(max_length=500, blank=True)
    visa_info = models.CharField(max_length=500, blank=True)
    attention = models.CharField(max_length=2000, blank=True)
    travel_advice = models.CharField(max_length=2000, blank=True)
    image_path = models.CharField(max_length=200, blank=True)
    city_name = models.CharField(max_length=30)
    related_provence = models.ForeignKey('DestinationProvence')
    class Meta:
        managed = False
        db_table = 'destination_city'

class DestinationCountry(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    background_description = models.CharField(max_length=500, blank=True)
    history_introduction = models.CharField(max_length=500, blank=True)
    geograpyic_info = models.CharField(max_length=500, blank=True)
    transportation_info = models.CharField(max_length=1000, blank=True)
    proper_travel_time = models.CharField(max_length=500, blank=True)
    visa_info = models.CharField(max_length=500, blank=True)
    attention = models.CharField(max_length=2000, blank=True)
    travel_advice = models.CharField(max_length=2000, blank=True)
    image_path = models.CharField(max_length=200, blank=True)
    country_name = models.CharField(max_length=30)
    class Meta:
        managed = False
        db_table = 'destination_country'

class DestinationInfo(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    scene_name = models.CharField(max_length=100, blank=True)
    city_description = models.TextField(blank=True)
    destination_info = models.CharField(max_length=1000, blank=True)
    proper_time = models.CharField(max_length=1000, blank=True)
    transportation_info = models.CharField(max_length=1000, blank=True)
    travel_tips = models.CharField(max_length=1000, blank=True)
    class Meta:
        managed = False
        db_table = 'destination_info'

class DestinationProvence(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    background_description = models.CharField(max_length=500, blank=True)
    history_introduction = models.CharField(max_length=500, blank=True)
    geograpyic_info = models.CharField(max_length=500, blank=True)
    transportation_info = models.CharField(max_length=1000, blank=True)
    proper_travel_time = models.CharField(max_length=500, blank=True)
    visa_info = models.CharField(max_length=500, blank=True)
    attention = models.CharField(max_length=2000, blank=True)
    travel_advice = models.CharField(max_length=2000, blank=True)
    image_path = models.CharField(max_length=200, blank=True)
    provence_name = models.CharField(max_length=30)
    related_country = models.ForeignKey(DestinationCountry)
    class Meta:
        managed = False
        db_table = 'destination_provence'

class DestinationSceneryspot(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    background_description = models.CharField(max_length=500, blank=True)
    history_introduction = models.CharField(max_length=500, blank=True)
    geograpyic_info = models.CharField(max_length=500, blank=True)
    transportation_info = models.CharField(max_length=1000, blank=True)
    proper_travel_time = models.CharField(max_length=500, blank=True)
    visa_info = models.CharField(max_length=500, blank=True)
    attention = models.CharField(max_length=2000, blank=True)
    travel_advice = models.CharField(max_length=2000, blank=True)
    image_path = models.CharField(max_length=200, blank=True)
    spot_name = models.CharField(max_length=30)
    related_city = models.ForeignKey(DestinationCity)
    ticket = models.CharField(max_length=500, blank=True)
    class Meta:
        managed = False
        db_table = 'destination_sceneryspot'

class District(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(max_length=15)
    zip = models.CharField(max_length=6)
    city = models.ForeignKey(City)
    class Meta:
        managed = False
        db_table = 'district'

class DjangoAdminLog(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    action_time = models.DateTimeField()
    user = models.ForeignKey(AuthUser)
    content_type = models.ForeignKey('DjangoContentType', blank=True, null=True)
    object_id = models.TextField(blank=True)
    object_repr = models.CharField(max_length=200)
    action_flag = models.IntegerField()
    change_message = models.TextField()
    class Meta:
        managed = False
        db_table = 'django_admin_log'

class DjangoContentType(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(max_length=100)
    app_label = models.CharField(max_length=100)
    model = models.CharField(max_length=100)
    class Meta:
        managed = False
        db_table = 'django_content_type'

class DjangoMigrations(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    app = models.CharField(max_length=255)
    name = models.CharField(max_length=255)
    applied = models.DateTimeField()
    class Meta:
        managed = False
        db_table = 'django_migrations'

class DjangoSession(models.Model):
    session_key = models.CharField(primary_key=True, max_length=40)
    session_data = models.TextField()
    expire_date = models.DateTimeField()
    class Meta:
        managed = False
        db_table = 'django_session'

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
    id = models.IntegerField(blank=True, null=True)
    class Meta:
        managed = False
        db_table = 'favorite_travel'

class Follow(models.Model):
    active = models.ForeignKey('User', db_column='active')
    passive = models.ForeignKey('User', db_column='passive')
    time = models.DateTimeField()
    action = models.CharField(max_length=1)
    id = models.IntegerField(blank=True, null=True)
    class Meta:
        managed = False
        db_table = 'follow'

class Location(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
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
    id = models.IntegerField(blank=True, null=True)
    class Meta:
        managed = False
        db_table = 'login_record'

class Note(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    user = models.ForeignKey('User')
    travel = models.ForeignKey('Travel')
    location = models.ForeignKey(Location, blank=True, null=True)
    create_time = models.DateTimeField()
    content = models.CharField(max_length=2048, blank=True)
    image_path = models.CharField(max_length=24, blank=True)
    is_deleted = models.IntegerField()
    lm_time = models.DateTimeField()
    photo_time = models.DateTimeField(blank=True, null=True)
    class Meta:
        managed = False
        db_table = 'note'

class Province(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(unique=True, max_length=3)
    class Meta:
        managed = False
        db_table = 'province'

class ScenicArea(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    name = models.CharField(max_length=45)
    description = models.CharField(max_length=300, blank=True)
    price = models.DecimalField(max_digits=7, decimal_places=2, blank=True, null=True)
    address = models.ForeignKey(Address)
    class Meta:
        managed = False
        db_table = 'scenic_area'

class ScenicPoint(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
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
    id = models.IntegerField(primary_key=True) # AutoField?
    content = models.CharField(max_length=300, blank=True)
    image_path = models.CharField(max_length=25, blank=True)
    image_explain = models.CharField(max_length=300, blank=True)
    scenic_point = models.ForeignKey(ScenicPoint)
    class Meta:
        managed = False
        db_table = 'scenic_point_info'

class Trans(models.Model):
    user = models.ForeignKey('User')
    time = models.DateTimeField()
    ip = models.CharField(max_length=15)
    method = models.CharField(max_length=8)
    path = models.CharField(max_length=25)
    http_agent = models.CharField(max_length=128)
    rsp_code = models.IntegerField()
    class Meta:
        managed = False
        db_table = 'trans'

class Travel(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
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
    id = models.IntegerField(blank=True, null=True)
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
    id = models.IntegerField(blank=True, null=True)
    class Meta:
        managed = False
        db_table = 'travel_read_log'

class TravelVote(models.Model):
    travel = models.ForeignKey(Travel)
    voter = models.ForeignKey('User', db_column='voter')
    time = models.DateTimeField()
    id = models.IntegerField(blank=True, null=True)
    class Meta:
        managed = False
        db_table = 'travel_vote'

class User(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    email = models.CharField(unique=True, max_length=25, blank=True)
    token = models.CharField(unique=True, max_length=32)
    qq_user_id = models.CharField(unique=True, max_length=32, blank=True)
    sina_user_id = models.CharField(unique=True, max_length=20, blank=True)
    password = models.CharField(max_length=16, blank=True)
    register_time = models.DateTimeField()
    nickname = models.CharField(unique=True, max_length=16)
    face_path = models.CharField(max_length=24, blank=True)
    signature = models.CharField(max_length=70, blank=True)
    account = models.IntegerField()
    travel_qty = models.IntegerField()
    scenic_point_qty = models.IntegerField()
    achievement_qty = models.IntegerField()
    follower_qty = models.IntegerField()
    favorite_travel_qty = models.IntegerField()
    is_location_public = models.IntegerField()
    is_info_public = models.IntegerField()
    lm_time = models.DateTimeField()
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
    user = models.ForeignKey(User, primary_key=True)
    phone = models.CharField(max_length=12, blank=True)
    mobile = models.CharField(max_length=11, blank=True)
    qq = models.CharField(max_length=12, blank=True)
    sina_blog = models.CharField(max_length=25, blank=True)
    name = models.CharField(max_length=4, blank=True)
    age = models.IntegerField(blank=True, null=True)
    sex = models.CharField(max_length=1, blank=True)
    address = models.ForeignKey(Address, db_column='address', blank=True, null=True)
    address2 = models.ForeignKey(Address, db_column='address2', blank=True, null=True)
    native_place = models.ForeignKey(Province, db_column='native_place', blank=True, null=True)
    degree = models.CharField(max_length=6, blank=True)
    job = models.CharField(max_length=15, blank=True)
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

