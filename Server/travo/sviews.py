#-*- coding: utf-8 -*-
import traceback
from django.shortcuts import render, render_to_response,get_object_or_404
from django.views.generic.edit import FormView
from django.core.mail import send_mail
from django.views.generic import View
from travo.forms import RegisterForm, ContactForm
from django.http import HttpResponse
from django.template import RequestContext, loader
from django.shortcuts import render
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
import logging
import string
from django.conf import settings
from datetime import datetime
from service import userservice,travelservice,noteservice


from models import User,Travel,Location
from rc import *

from django.core.paginator import Paginator
from django.core.paginator import PageNotAnInteger
from django.core.paginator import EmptyPage

# Create your views here.

#===================user view=====================================
class RegisterView(View):
    def post(self, request):
        nickname = request.POST.get('nickname','')
        password = request.POST.get('password','')
        email = request.POST.get('email','')
        res = userservice.travo_register(nickname, email, password, request.META['REMOTE_ADDR'])
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

class OtherInfoView(View):
    def get(self, request, id):
        template = loader.get_template('website/others.html')
        print '根本没执行？'
        user = userservice.get_user_by_id(id)
        self_token = request.session['token']
        userinfo_result = userservice.get_user_info(self_token, user.id)
        
        if user.token == self_token:
            return HttpResponse('权限受限，访问被拒绝!')
        if userinfo_result[RSP_CODE] == RC_SUCESS:
            followers_list = userservice.follow_list(user.token)['users']
            travels= travelservice.get_travel(user.token,3)
            follow_result = userservice.get_follow_state(self_token, user.token)
            print follow_result
            if follow_result:
                is_followed = 1
            else:
                is_followed = 0
            return render(request,'website/others.html',{"user":user,
                
                "recent_travel_list":travels['travel_list'],
                "followed_list":followers_list,
                "userinfo":userinfo_result['user_info'],
                "is_followed":is_followed,
                "other_id":id
                })
        else:
            return HttpResponse('权限受限，访问被拒绝!')

class MyInfoView(View):
    '''展示个人主页'''
    def get(self, request):
        template = loader.get_template('website/me.html')
        token = request.session['token']
        user = get_object_or_404(User, token=token)
        userinfo = userservice.get_user_info(token, user.id)['user_info']
        followed_list_length = userservice.follow_list(token)['length']
        result = travelservice.get_travel(token,3)   
        basic_travel_path = settings.COVER_PATH
        return render(request,'website/me.html',{"user":user,
            "userinfo":userinfo,
            "recent_travel_list":result['travel_list'],
            "basic_travel_path":basic_travel_path,
            "followed_list_length":followed_list_length,
            })

class RegisterSuccessView(View):
    def get(self,request):
        template = loader.get_template('website/register_successful.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class DetailInfoView(View):
    def get(self, request):
        template = loader.get_template('website/detail_info.html')
        token  = request.session['token']
        user = userservice.get_user(token)
        print user.email
        user_info = userservice.get_user_info(token, user.id)['user_info']
        context = RequestContext(request)
        return render(request,'website/detail_info.html',{"user":user,
            "userinfo":user_info
            })

class LoginView(View):
    def get(self,request):
        template = loader.get_template('website/login_page.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))
    def post(self,request):
        email = request.POST.get('email')
        password = request.POST.get('password')
        print email
        print password
        res = userservice.travo_login(email,password, request.META['REMOTE_ADDR'])
        ret = "0"
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        if res[RSP_CODE] == RC_SUCESS:
            logging.debug('user %s login successful',res['user'].nickname)
            request.session['username'] = res['user'].nickname
            request.session['token'] = res['user'].token
            request.session['userid'] = res['user'].id
            ret = "1"
        else:
            ret = "-1"
        response.write(ret)
        return response   
        

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
        token  = request.session['token']
        user = get_object_or_404(User,token=request.session['token'])
        user_info = userservice.get_user_info(token, user.id)['user_info']
        return render(request,'website/set.html',{"user":user,
            "userinfo":user_info
            })
    def post(self, request):
        attr_dict = request.POST
        is_info_public = request.POST.get('is_info_public','1')
        signature = request.POST.get('signature','')
        user = get_object_or_404(User,token=request.session['token'])
        for key in attr_dict.keys():
            print key
            print attr_dict[key]
        try:
            userservice.update_info(request.session['token'],attr_dict)
            result = userservice.change_self_avatar(user,request.FILES.get('face_path'))
            print result 
            print request.FILES
            userservice.change_signature_authority(request.session['token'],signature,is_info_public)
            
        except:
           print traceback.format_exc()
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        return response
class CommentView(View):
    def post(self,request):
        token = request.session['token']
        travel_id = request.POST.get['travel_id']
        content = request.POST.get['content']
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        result = travelservice.comment(token, travel_id, content)
        ret = "0"
        if result[RSP_CODE] == RC_SUCESS:
            ret = "1"
        else:
            ret = "2"
        print ret
        response.write(ret)
        return response    
class ChangeFollowView(View):
    def post(self,request):
        token = request.session['token']
        other_id = request.POST.get('other_id')
        action = request.POST.get('action')
        print token
        print other_id
        print action
        result = userservice.follow(token,other_id,action)
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        ret = "0"
        if result[RSP_CODE] == RC_SUCESS:
            ret = "1"
        else:
            ret = "2"
        print ret
        response.write(ret)
        return response
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
class ChangeAvatarView(View):
    def post(self,request):
        
        user = get_object_or_404(User,token=request.session['token'])
        result = userservice.change_self_avatar(user,request.FILES)
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        return response;
class ChangeEmailView(View):
    def post(self,request):
        pass

class LoginPageView(View):
    def get(self,request):
        template = loader.get_template('website/login_page.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))
    def post(self,request):
        email = request.POST.get('email','')
        password = request.POST.get('password','')
        print email
        print password
        res = userservice.travo_login(email,password)
        ret = "0"
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        if res[RSP_CODE] == RC_SUCESS:
            logging.debug('user %s login successful',res['user'].nickname)
            request.session['username'] = res['user'].nickname
            request.session['token'] = res['user'].token
            request.session['userid'] = res['user'].id
            return render_to_response('website/welcome.html',context_instance=RequestContext(request))
        else:
            ret = "-1"
            response.write(ret)
            return response
        

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
        me = get_object_or_404(User,token=request.session['token'])
        print me.email
        context = RequestContext(request,{'user':me})
        return render(request,'website/test.html',{
            'user':me,
            })
    def post(self, request):
        pass

class SearchResultView(View):
    def get(self,request):
        template = loader.get_template('website/search_result.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class HotTravelView(View):
    def get(self,request):
        searchtype = "default"
        travel_list = travelservice.search_web(order=searchtype)
        page_size = 10
        paginator = Paginator(travel_list, page_size)
        try:
            page = int(request.GET.get('page','1'))

        except ValueError:
            page = 1
        try:
            travel_results = paginator.page(page)
        except (EmptyPage, InvalidPage):
            travel_results = paginator.page(paginator.num_pages)
        basic_travel_path = settings.COVER_PATH
        return render(request,'website/hot_travel.html',{"travel_list":travel_results,
            })
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
        travel['create_time'] = str(datetime.now())[0:19]
        travel['cover'] = 'cover'
        result = travelservice.upload(token,[travel,],request.FILES)
        return HttpResponse('添加成功!')


class EditTravelView(View):
    def get(self,request):
        template = loader.get_template('website/edit_travel.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))


class EditNoteView(View):
    def get(self,request):
        template = loader.get_template('website/edit_note.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))


class FollowingView(View):
    def get(self,request):
        template = loader.get_template('website/following.html')
        context = RequestContext(request)
        return HttpResponse(template.render(context))

class FollowedView(View):
    def get(self,request):
        token = request.session['token']
        user = get_object_or_404(User, token=token)
        userinfo = userservice.get_user_info(token, user.id)['user_info']
        result = userservice.follow_list(token)
        if result[RSP_CODE] == RC_SUCESS:
            return render(request,'website/followed.html',{"followed_list":result['users'],
            "currentuser":user,
            "currentuserinfo":userinfo})
        else:
            pass

class ShowMyTravel(View):
    def get(self,request):
        template = loader.get_template('website/all_my_travel.html')
        token = request.session['token']
        user = get_object_or_404(User, token=token)
        my_recent_travel_list = travelservice.get_travel(token)['travel_list']
        basic_travel_path = settings.COVER_PATH
        context =  RequestContext(request,{\
            "user":user,
            "recent_travel_list":my_recent_travel_list,
            "basic_travel_path":basic_travel_path,})

        return HttpResponse(template.render(context))
        pass

class DetailTravelView(View):
    def get(self,request,travel_id):
        template = loader.get_template('website/detail_travel.html')
        travel = get_object_or_404(Travel,pk=travel_id)
        comments_result= travelservice.get_comments(travel_id)
        notes_result = noteservice.get_all_in_travel(travel_id)
        if notes_result[RSP_CODE] == RC_SUCESS and comments_result[RSP_CODE] == RC_SUCESS:
            note_list = notes_result['notes']
            comment_list = comments_result['comments']
            
        else:
            return HttpResponse('获取游记出现问题')
        print note_list
        context = RequestContext(request,{\
            'travel':travel,
            'note_list':note_list,
            'comment_list':comment_list
            })
        return HttpResponse(template.render(context))

class CommentTravelView(View):
    def post(self,request):
        ret = "0"
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        token = request.session['token']
        travel_id = request.POST.get('travel_id')
        content = request.POST.get('content')
        comment_result = travelservice.comment(token, travel_id, content)
        if comment_result[RSP_CODE] == RC_SUCESS:
            ret = "1"
        else:
            ret = "2"
        print ret
        response.write(ret)
        return response       
class FavoriteTravelView(View):
    def post(self,request):
        ret = "0"
        response = HttpResponse()
        response['Content-Type']="text/javascript"
        token = request.session['token']
        travel_id = request.POST.get('travel_id')
        favorite_result = travelservice.favorit(token,travel_id)
        if favorite_result[RSP_CODE] == RC_SUCESS:
            ret = "1"
        else:
            ret = "2"
        print ret
        response.write(ret)
        return response  
#======================note view======================================



class NewNoteView(View):
    def get(self, request,travel_id):
        template = loader.get_template('website/new_note.html')
        context = RequestContext(request,{'travel_id':travel_id,})
        return HttpResponse(template.render(context))


class AddNewNoteView(View):
    def post(self,request):
        token = request.session['token']
        note = {}
        note['user_id'] = request.session['userid']
        note['travel_id'] = request.POST['travel_id']
        travel_id = request.POST['travel_id']
        #note_photo = request.FILES.get('note_photo', None)
        note['content'] = request.POST.get('note_description','暂无描述')
        location ={}
        location['address'] = request.POST.get('note_location','')
        print request.POST.get('latitude',0.0)
        print request.POST.get('longitude',0.0)
        location['latitude'] = float(request.POST.get('latitude',0.0))
        location['longitude'] = float(request.POST.get('longitude',0.0))
        note['location'] = location
        note['image'] = 'note_photo'
        
        note['create_time'] = str(datetime.now())[0:19]
        print request.POST.get('create_time',datetime.now())
        result = noteservice.upload(token,[note,],request.FILES)

        print result
        for key in note:
            print '%s:%s'%(key,note[key])
        print 'Latitude is %s'%request.POST.get('latitude',0.0)
        template = loader.get_template('website/detail_travel.html')
        context = RequestContext(request,{'travel_id':request.POST['travel_id'],})
        #return HttpResponse(template.render(context))
        return HttpResponseRedirect(reverse('detail_travel',args=(travel_id,)))
