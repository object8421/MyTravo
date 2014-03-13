#-*- coding: utf-8 -*-
from django.shortcuts import render
from django.views.generic.edit import FormView
from django.views.generic import View
from travo.forms import RegisterForm, ContactForm
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.shortcuts import render
from django.http import HttpResponseRedirect
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
        if res[RSP_CODE] == 100:
            return render(request,'website/register_successful.html')
        else :
            return render(request,'website/register_fail.html')

class ShowRegisterView(View):
    def get(self,request):
        template = loader.get_template('website/register_simple.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))


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
class LoginView(View):
    def get(self,request):
        pass
    def post(self,request):
        email = request.POST.get('email','')
        password = request.POST.get('password','')
        res = userservice.travo_login(email,password)
        if res[RSP_CODE] == RC_SUCESS:
            return render(request,'website/welcome.html',{'user':res['user'],
                'logged_in':'True'})
        else:
            return render(request,'website/login_fail.html')

        pass
class ContactView(View):
    def get(self,request):
        form = ContactForm()
        return render(request,'website/contact.html', {'form':form})
    def post(self,request):
        return render(request,'website/contact_thanks.html')
        pass

class RegisterSuccessView(View):
    def get(self,request):
        template = loader.get_template('website/register_successful.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class NewTravelView(View):
    def get(self,request):
        template = loader.get_template('website/new_travel.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

