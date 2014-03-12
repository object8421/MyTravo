from django.conf.urls import patterns, url
from django.conf.urls import patterns, url
from travo import sviews 

urlpatterns = patterns('travo.sviews',
		#url(r'^user/login$', views.LoginView.as_view(), name='login'),
		#url(r'^travel/upload$', views.UploadTravelView.as_view(), name='upload_travel')
		url(r'^user/register$', RegisterView.as_view(), name='register'),
		url(r'^user/show_register$', ShowRegisterView.as_view(), name='show_register'),
		url(r'^user/register_successful',RegisterSuccessfulView.as_view(),name = 'register_successful'),
		url(r'^index$', IndexView.as_view(), name ='index'),
		url(r'^me', MyInfoView.as_view(),name = 'me')
	)
