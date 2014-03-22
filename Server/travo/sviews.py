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


from models import User,Travel,Location
from rc import *

# Create your views here.

#===================user view=====================================
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

class RegisterSuccessView(View):
    def get(self,request):
        template = loader.get_template('website/register_successful.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class DetailInfoView(View):
    def get(self, request):
        template = loader.get_template('website/detail_info.html')
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
            request.session['userid'] = res['user'].id
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
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        ret = "0"
        if result[RSP_CODE] == RC_SUCESS:
            ret = "1"
        else:
            ret = "2"
        response.write(ret)
        return response

class ChangeEmailView(View):
    def post(self,request):
        pass

#===================generic view=====================================

class IndexView(View):
    def get(self,request):
        template = loader.get_template('website/welcome.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class ContactView(View):
    def get(self,request):
        form = ContactForm()
        return render(request,'website/contact.html', {'form':form})
    def post(self,request):
        return render(request,'website/contact_thanks.html')

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


#===================travel view=====================================

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
            cover_original = request.FILES
            cover_name = cover_original.name
            print cover_name
            travel['cover'] = 'cover'


            travel['create_time'] = datetime.now()
            result = travelservice.upload(token,[travel,],cover_original)
        except:
            print '添加异常'
        print result

        return HttpResponse('添加成功!')



class ShowMyTravel(View):
    def get(self,request):

        pass

class DetailTravelView(View):
    def get(self,request,travel_id):
        template = loader.get_template('website/detail_travel.html')
        travel = get_object_or_404(Travel,pk=travel_id)
        context = RequestContext(request,{'travel':travel,})
        return HttpResponse(template.render(context))

#======================note view======================================
class ShowNoteView(View):
    def post(self,request):
        pass

    def get(self,request):
        token = request.session['token']

        pass



class NewNoteView(View):
    def get(self, request,travel_id):
        template = loader.get_template('website/new_note.html')
        context = RequestContext(request,{'travel_id':travel_id,})
        return HttpResponse(template.render(context))
class AddNewNoteView(View):
    def post(self,request):
        token = request.session['token']
        note = {}
        note['user'] = request.session['userid']
        note['travel'] = request.POST['travel_id']

        try:
            note_photo = request.FILES.get('note_photo', None)
            note['content'] = request.POST.get('description','暂无描述')
            location ={}
            location['address'] = request.POST.get('location','')
            location['longitude'] = 0.0
            location['latitude'] = 0.0
            note['location'] = location
            note['image'] = request.POST.get('note_photo','')

            note['create_time'] = request.POST.get('create_time',datetime.now())
            result = noteservice.upload(token,[note,],request.FILES)

        except:
            pass
        template = loader.get_template('website/detail_travel.html')
        context = RequestContext(request,{'travel_id':travel_id,})
        return HttpResponse(template.render(context))

