#-*- coding: utf-8 -*-
from django.conf.urls import patterns, url
from django.conf import settings  
from travo import sviews 
import django.views
urlpatterns = patterns('travo',
		url(r'^user/register$', sviews.RegisterView.as_view(), name='register'),
		url(r'^user/login$',sviews.LoginView.as_view(),name='login'),
		url(r'^user/logout$',sviews.LogoutView.as_view(), name = 'logout'),
		url(r'^user/show_register$', sviews.ShowRegisterView.as_view(), name='show_register'),
		url(r'^index$', sviews.IndexView.as_view(), name ='index'),
		url(r'^$', sviews.IndexView.as_view(), name ='index'),
		url(r'^contact', sviews.ContactView.as_view(), name = 'contact'),
		url(r'^me', sviews.MyInfoView.as_view(),name = 'me'),
		url(r'user/change_password',sviews.ChangePasswordView.as_view(),name='change_password'),
		url(r'^user/register_success', sviews.RegisterSuccessView.as_view(), \
			name = 'register_success'),
		url(r'^user/new_travel',sviews.NewTravelView.as_view(),\
			name = 'new_travel'),
		#显示添加note的表单
		url(r'new_note/(?P<travel_id>\d+)/',sviews.NewNoteView.as_view(),\
			name = 'new_note'),	
		url(r'add_new_note',sviews.AddNewNoteView.as_view(),name = 'add_new_note'),
		url(r'detail_travel/(?P<travel_id>\d+)/',sviews.DetailTravelView.as_view(),\
			name = 'detail_travel'),	

		url(r'^user/show_my_travel',sviews.ShowMyTravel.as_view(),name='show_my_travel'),
		url(r'^user/set_account', sviews.PersonalInfoSetView.as_view(),\
            name = 'set_account'),
		url(r'^test', sviews.TestView.as_view(),name='test'),
		url(r'^user/detail_info', sviews.DetailInfoView.as_view(),\
			name = 'detail_info'),

	)

