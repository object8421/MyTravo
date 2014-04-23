#-*- coding: utf-8 -*-
from django.shortcuts import render

# Create your views here.

import traceback
from django.shortcuts import render, render_to_response,get_object_or_404
from django.db.models import Q
from django.core.mail import send_mail
from django.views.generic import View
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.shortcuts import render
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
import logging
import string
from django.conf import settings
from datetime import datetime
from jpush import JPushClient
from models import DesCountry,DesProvince,DesCity,DesScenerySpot


from django.core.paginator import Paginator
from django.core.paginator import PageNotAnInteger
from django.core.paginator import EmptyPage

import json

# Create your views here.

#===================user view=====================================
class HomeView(View):
    def get(self, request):
        country_list = DesCountry.objects.all()[:20]
        hottest_country_list = DesCountry.objects.all()[:5]
        province_list = DesProvince.objects.all()
        hottest_province_list = DesProvince.objects.all()[:12]

        return render(request,'home.html',{"country_list":country_list,
        	"hottest_country_list":hottest_country_list,
        	"hottest_province_list":hottest_province_list,

        	"province_list":province_list})

class SearchView(View):
	def get(self,request,keyword):
		country_list = DesCountry.objects.filter(Q(country_name__contains=keyword))
		province_list = DesProvince.objects.filter(Q(province_name__contains=keyword))
		city_list = DesCity.objects.filter(Q(city_name__contains=keyword))
		scenery_spot_list = DesScenerySpot.objects.filter(Q(spot_name__contains=keyword))
		return render(request,'search_result.html',{"country_list":country_list,
			"province_list":province_list,
			"city_list":city_list,
			"spot_list":scenery_spot_list,})

class DesDetailView(View):
	def get(self,request,des_type,des_id):
		print des_type
		if des_type == 'country':
			country = get_object_or_404(DesCountry,pk=des_id)
			image_url_list = country.image_url.split(';')
			image_url_list.remove(image_url_list[-1])
			related_province = DesProvince.objects.filter(related_country = country.country_name)
			return render(request,'country_detail_info.html',{"country":country,
				"related_province":related_province,
				"image_url_list":image_url_list})

		if des_type == 'province':
			province = get_object_or_404(DesProvince,pk=des_id)
			related_city = DesCity.objects.filter(related_province = province.province_name)
			image_url_list = province.image_url.split(';')
			image_url_list.remove(image_url_list[-1])
			return render(request,'province_detail_info.html',{"province":province,
				"related_city":related_city,
				"image_url_list":image_url_list})

		if des_type == 'city':
			city = get_object_or_404(DesCity,pk=des_id)
			image_url_list = city.image_url.split(';')
			related_spot = DesScenerySpot.objects.filter(related_city = city.city_name)
			return render(request,'city_detail_info.html',{"city":city,
				"related_spot":related_spot,
				"image_url_list":image_url_list})

		if des_type == 'scenery_spot':
			scenery_spot = get_object_or_404(DesScenerySpot,pk=des_id)
			image_url_list = scenery_spot.image_path.split(';')
			return render(request,'spot_detail_info.html',{"spot":scenery_spot,
				"related_spot":related_spot,
				"image_url_list":image_url_list})
		return render(request,'des_not_exist.html')

class GetDesPushView(View):
	def get(self,request,des_name):
		city = DesCity.objects.filter(Q(city_name__contains=des_name))
		if city:
			des_city = city[0]
			appkey = '986aa2521dcb7f92092a8848'
			master_secret = 'b6ddbc1f0933845e4fa50c9b'
			jpush_client = JPushClient(master_secret)
			welcome_message = "欢迎您来到:" + des_city.city_name
			des_url = "http://travo.com.cn/destination/" + des_city.city_name + '/' + des_city.id
			jpush_client.send_notification_by_appkey(app_key, sendno, 'des',
                                         welcome_message,
                                         des_url, 'android')


		

class GetRelatedDesView(View):
    def get(self,request):
        pass





