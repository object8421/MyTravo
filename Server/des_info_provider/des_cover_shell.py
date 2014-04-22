#-*- coding: utf-8 -*-

import os,sys
sys.path.append("../")

import Server.settings


from des_info_provider.models import DesCountry,DesCity,DesProvince,DesScenerySpot


country_list = DesCountry.objects.all()
for country in country_list:
	country.cover_url = country.image_url.split(';')[0]
	country.save()

province_list = DesProvince.objects.all()
for province in province_list:
	try:
		province.cover_url = province.image_url.split(';')[0]
	except Exception, e:
		print '%s 无图'%province.country_name
	province.save()

city_list = DesCity.objects.all()
for city in city_list:
	try:
		city.cover_url = city.image_url.split(';')[0]
	except Exception, e:
		print '%s 无图'%city.country_name
	city.save()

spot_list = DesScenerySpot.objects.all()
for spot in spot_list:
	try:
		spot.cover_url = spot.image_url.split(';')[0]
	except Exception, e:
		print '%s 无图'%spot.country_name
	spot.save()

