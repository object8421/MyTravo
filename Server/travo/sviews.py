#-*- coding: utf-8 -*-
from django.shortcuts import render
from django.views.generic.edit import FormView
from django.views.generic import View
from travo.forms import RegisterForm
from django.http import HttpResponse
from django.template import RequestContext, loader
from travo import userservice
from rc import *

# Create your views here.
class RegisterView(View):
    def post(self, request):
        nickname = request.POST.get('nickname','')
        password = request.POST.get('password','')
        email = request.POST.get('email','')
        print nickname
        print password
        print email
        res = userservice.travo_register(nickname, email, password)
        return HttpResponse(res[RSP_CODE])

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