#-*- coding: utf-8 -*-
from django.shortcuts import render

# Create your views here.

import traceback
from django.shortcuts import render, render_to_response,get_object_or_404

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
        des_list = DesCountry.objects.all()
        for des in des_list :
            print des.country_name
        print "共有 %s 条数据。"%len(des_list)

        return render(request,'home.html',{"des_list":des_list})

class GetRelatedDesView(View):
    def get(self,request):
        pass