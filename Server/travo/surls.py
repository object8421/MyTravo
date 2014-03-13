from django.conf.urls import patterns, url
from django.conf.urls import patterns, url
from travo import sviews 

urlpatterns = patterns('travo',
		#url(r'^user/login$', views.LoginView.as_view(), name='login'),
		#url(r'^travel/upload$', views.UploadTravelView.as_view(), name='upload_travel')
		url(r'^user/register$', sviews.RegisterView.as_view(), name='register'),
		url(r'^user/login',sviews.LoginView.as_view(),name='login'),
		url(r'^user/show_register$', sviews.ShowRegisterView.as_view(), name='show_register'),
		url(r'^index$', sviews.IndexView.as_view(), name ='index'),
		url(r'^$', sviews.IndexView.as_view(), name ='index'),
		url(r'^contact', sviews.ContactView.as_view(), name = 'contact')
		#url(r'^me', sviews.MyInfoView.as_view(),name = 'me')
	)
