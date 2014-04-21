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

from models import DesCountry,DesProvince,DesCity,DesScenerySpot


from django.core.paginator import Paginator
from django.core.paginator import PageNotAnInteger
from django.core.paginator import EmptyPage

import json

# Create your views here.

#===================user view=====================================
class HomeView(View):
    def get(self, request):
        country_list = DesCountry.objects.all()[:5]
        province_list = DesProvince.objects.all()


        return render(request,'home.html',{"country_list":country_list,
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
			return render(request,'country_detail_info.html',{"country":country})
		if des_type == 'province':
			province = get_object_or_404(DesProvince,pk=des_id)
			return render(request,'province_detail_info.html',{"province":province})
		if des_type == 'city':
			city = get_object_or_404(DesCity,pk=des_id)
			return render(request,'city_detail_info.html',{"city":city})
		if des_type == 'scenery_spot':
			scenery_spot = get_object_or_404(DesScenerySpot,pk=des_id)
			return render(request,'spot_detail_info.html',{"spot":scenery_spot})
		return render(request,'des_not_exist.html')

		

class GetRelatedDesView(View):
    def get(self,request):
        pass

