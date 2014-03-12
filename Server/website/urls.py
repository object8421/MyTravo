from django.conf.urls import patterns, url
from django.conf.urls import patterns, url
from website import views 

urlpatterns = patterns('website',
		#url(r'^user/login$', views.LoginView.as_view(), name='login'),
		#url(r'^travel/upload$', views.UploadTravelView.as_view(), name='upload_travel')
		url(r'^user/register$', views.RegisterView.as_view(), name='register'),
		url(r'^user/register_successful',views.RegisterSuccessfulView.as_view(),name = 'register_successful'),
		url(r'^index$', views.IndexView.as_view(), name ='index'),
		url(r'^me',views.MyInfoView.as_view(),name = 'me')
	)
