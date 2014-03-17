#-*- coding: utf-8 -*-
from django.shortcuts import render, render_to_response,get_object_or_404
from django.views.generic.edit import FormView
from django.views.generic import View
from travo.forms import RegisterForm, ContactForm
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.shortcuts import render
from django.http import HttpResponseRedirect
import logging
from datetime import datetime
from service import userservice,travelservice
from models import User
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

class LoginView(View):

    def get(self,request):
        pass
    def post(self,request):
        email = request.POST.get('email','')
        password = request.POST.get('password','')
        res = userservice.travo_login(email,password)
        if res[RSP_CODE] == RC_SUCESS:
            logging.debug('user %s login successful',res['user'].nickname)
            request.session['username'] = res['user'].nickname
            request.session['token'] = res['user'].token
            return render_to_response('website/welcome.html',context_instance=RequestContext(request))
        else:
            return render(request,'website/login_fail.html')

        pass

class LogoutView(View):
    def get(self,request):
        try:
            del request.session['token']
        except KeyError, e:
             pass

        logging.debug('user %s logout successfully.',request.session['username'])
        return render(request,'website/welcome.html')


class ContactView(View):
    def get(self,request):
        form = ContactForm()
        return render(request,'website/contact.html', {'form':form})
    def post(self,request):
        return render(request,'website/contact_thanks.html')


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
    def post(self,request):
        token = request.session['token']
        travel = {}
        travel['title'] = request.POST.get('travel_name','')
        travel['begin_date'] = request.POST.get('start_time','')
        travel['description'] = request.POST.get('travel_description','')
        cover_original = request.FILES.get('cover', None)
        cover_name = cover_original.name
        print cover_name
        travel['cover'] = content = cover_original.read()
        travel['create_time'] = datetime.now()
        result = travelservice.upload(token,[travel,])
        print result

        return HttpResponse('添加成功!')

class MyInfoView(View):
    def get(self, request):
        template = loader.get_template('website/me.html')
        context =  RequestContext(request)
        token = request.session['token']
        user = get_object_or_404(User, token=token)
        return HttpResponse(template.render(context),{"user":user})

class TestView(View):
    def get(self,request):
        template = loader.get_template('website/test.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))
    def post(self, request):
        buf = request.FILES.get('cover',None)
        content = buf.name
        print content
        with open('haha.jpg','wb') as image:
            image.write(buf.read())
        return HttpResponse('添加成功!')




class AuthoritySetView(View):
    def get(self, request):
        template = loader.get_template('website/set.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class DetailInfoView(View):
    def get(self, request):
        template = loader.get_template('website/detail_info.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

