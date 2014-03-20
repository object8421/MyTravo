#-*- coding: utf-8 -*-
from django.shortcuts import render, render_to_response,get_object_or_404
from django.views.generic.edit import FormView
from django.core.mail import send_mail
from django.views.generic import View
from travo.forms import RegisterForm, ContactForm
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.shortcuts import render
from django.http import HttpResponseRedirect
import logging
from django.conf import settings
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
        res = userservice.travo_register(nickname, email, password)
        for key in request.POST:
            print "%s : %s"%(key,request.POST.get(key))

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
        try:
            cover_original = request.FILES.get('cover', None)
            cover_name = cover_original.name
            print cover_name
            travel['cover'] = content = cover_original.read()
            travel['create_time'] = datetime.now()
        result = travelservice.upload(token,[travel,])
        print result

        return HttpResponse('添加成功!')

class MyInfoView(View):
    '''展示个人主页'''
    def get(self, request):
        template = loader.get_template('website/me.html')
        token = request.session['token']
        user = get_object_or_404(User, token=token)
        my_recent_travel_list = travelservice.get_travel(token,3)['travel_list']
        basic_travel_path = settings.COVER_PATH
        context =  RequestContext(request,{\
            "user":user,
            "recent_travel_list":my_recent_travel_list,
            "basic_travel_path":basic_travel_path,})

        return HttpResponse(template.render(context))

class ShowMyTravel(View):
    def get(self,request):
        pass

class TestView(View):
    def get(self,request):
        template = loader.get_template('website/test.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))
    def post(self, request):
        buf = request.FILES.get('cover',None)
        title = request.POST.get('title',None)
        content = buf.name
        print title
        for attr in request.POST:

            print "%s : %s"%(attr,request.POST.get('attr'))
        print content
        with open('haha.jpg','wb') as image:
            image.write(buf.read())
        return HttpResponse('添加成功!')


class PersonalInfoSetView(View):
    '''设置个人信息/未完成图片处理'''
    def get(self, request):
        template = loader.get_template('website/set.html')
        context = RequestContext(request)
        user = get_object_or_404(User,token=request.session['token'])
        return HttpResponse(template.render(context),{'user':user})
    def post(self, request):
        attr_dict = request.POST
        userservice.change_self_info(request.session['token'],attr_dict)
        template = loader.get_template('website/me.html')
        context = RequestContext(request)
        user = request.get_object_or_404(User,token=token)
        return HttpResponse(template.render(context),{'user':user})

class ChangePasswordView(View):
    def post(self,request):
        token = request.session['token']
        original_password = request.POST.get('original_password')
        new_password = request.POST.get('new_password')
        print original_password
        print new_password
        result = userservice.change_password(token,original_password,new_password)

        if result[RSP_CODE] == RC_SUCESS:
            return HttpResponse('修改成功！')
        else:
            return HttpResponse('对不起，您的原密码有误。')

class ChangeEmailView(View):
    def post(self,request):
        pass

class DetailInfoView(View):
    def get(self, request):
        template = loader.get_template('website/detail_info.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class NewNoteView(View):
    def get(self, request):
        template = loader.get_template('website/new_note.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class DetailTravelView(View):
    def get(self,request):
        template = loader.get_template('website/detail_travel.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

