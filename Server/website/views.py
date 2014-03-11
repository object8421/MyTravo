#-*- coding: utf-8 -*-
from django.shortcuts import render
from django.views.generic.edit import FormView
from django.views.generic import View
from website.forms import RegisterForm
from django.http import HttpResponse

# Create your views here.
class RegisterView(FormView):
	template_name = "register.html"
	form_class = "RegisterForm"
	success_url = '/user/register_successful'
	def get(self,request):
		return HttpResponse("注册？")


class RegisterSuccessfulView(View):
    def get(self,request):
    	return HttpResponse("register_successful")

class IndexView(View):
	def get(self,request):
		return HttpResponse("主页")