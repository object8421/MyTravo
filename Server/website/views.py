#-*- coding: utf-8 -*-
from django.shortcuts import render
from django.views.generic.edit import FormView
from django.views.generic import View
from website.forms import RegisterForm
from django.http import HttpResponse
from django.template import RequestContext, loader
import logging
from mobile.views import RegisterView

# Create your views here.
class RegisterViewWebsite(RegisterView):
    template_name = "register.html"
    form_class = "RegisterForm"
    success_url = '/user/register_successful'
    def get(self,request):
        email = request.GET['inputEmail']
        print email
        #logging.DEBUG('get request from register view')
        return HttpResponse("注册成功")

class ShowRegisterView(View):
    def get(self,request):
        template = loader.get_template('website/register_simple.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))
class RegisterSuccessfulView(View):
    def get(self,request):
    	return HttpResponse("register_successful")

class IndexView(View):
    def get(self,request):
        template = loader.get_template('website/welcome.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class MyInfoView(View):
    def get(self,request):
        template = loader.get_template('website/me.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))
