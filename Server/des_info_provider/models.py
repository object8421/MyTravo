#-*- coding: utf-8 -*-
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

class DesCity(models.Model):
    """包含了城市的相关信息，其中一些信息不可用"""
    id = models.IntegerField(primary_key=True) # AutoField?
    city_name = models.CharField(max_length=45)
    related_province = models.CharField(max_length=45, blank=True)
    crawled_url = models.TextField(blank=True)
    """去哪儿网的原始链接（多个），以；分开，备用"""
    image_url = models.TextField(blank=True)

    """image path 为保存在OSS中的图片文件名，以；为分隔符，读取时需要加上OSS路径并以
    split函数分开读取"""
    image_path = models.TextField(blank=True)
    brief_information = models.TextField(blank=True)
    detail_information = models.TextField(blank=True)
    last_update_time = models.CharField(max_length=60, blank=True)
    #======================以下为不可用信息======================================
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)
    class Meta:
        db_table = 'des_city'

class DesCountry(models.Model):
    """包含了国家的相关信息，注释同上"""
    id = models.IntegerField(primary_key=True) # AutoField?
    country_name = models.CharField(max_length=40)
    brief_information = models.TextField(blank=True)
    detail_information = models.TextField(blank=True)
    crawled_url = models.TextField(blank=True)
    image_path = models.TextField(blank=True)
    last_update_time = models.TextField(blank=True)
    image_url = models.TextField(blank=True)
    #======================以下为不可用信息======================================
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)
    class Meta:
        db_table = 'des_country'

class DesProvince(models.Model):
    """包含了省份的相关信息，注释同上"""
    id = models.IntegerField(primary_key=True) # AutoField?
    province_name = models.CharField(max_length=40)
    related_country = models.CharField(max_length=45, blank=True)
    crawled_url = models.TextField(blank=True)
    image_path = models.TextField(blank=True)
    image_url = models.TextField(blank=True)
    brief_information = models.TextField(blank=True)
    detail_information = models.TextField(blank=True)
    last_update_time = models.CharField(max_length=60, blank=True)
    #======================以下为不可用信息======================================
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)
    
    class Meta:

        db_table = 'des_province'

class DesScenerySpot(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    crawled_url = models.TextField(blank=True)
    spot_name = models.CharField(max_length=45, blank=True)
    related_city = models.CharField(max_length=45, blank=True)
    related_province = models.CharField(max_length=45, blank=True)
    brief_information = models.TextField(blank=True)
    image_url = models.TextField(blank=True)
    ticket = models.TextField(blank=True)
    image_path = models.TextField(blank=True)
    last_update_time = models.CharField(max_length=45, blank=True)
    transportation_info = models.TextField(blank=True)
    proper_travel_time = models.TextField(blank=True)
    #======================以下为不可用信息======================================
    background_description = models.TextField(blank=True)
    history_introduction = models.TextField(blank=True)
    geograpyic_info = models.TextField(blank=True)
    visa_info = models.TextField(blank=True)
    attention = models.TextField(blank=True)
    travel_advice = models.TextField(blank=True)

    class Meta:

        db_table = 'des_scenery_spot'

class DjangoMigrations(models.Model):
    id = models.IntegerField(primary_key=True) # AutoField?
    app = models.CharField(max_length=255)
    name = models.CharField(max_length=255)
    applied = models.DateTimeField()
    class Meta:

        db_table = 'django_migrations'