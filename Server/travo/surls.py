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
		url(r'^user/show_my_travel',sviews.ShowMyTravel.as_view(),name='show_my_travel'),
		url(r'^user/set_account', sviews.PersonalInfoSetView.as_view(),\
            name = 'set_account'),
		url(r'^test', sviews.TestView.as_view(),name='test'),
		url(r'^user/detail_info', sviews.DetailInfoView.as_view(),\
			name = 'detail_info'),

	)

