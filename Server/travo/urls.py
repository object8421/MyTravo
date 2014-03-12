from django.conf.urls import patterns, url
from mobile import views 

urlpatterns = patterns('',
		url(r'index', views.IndexView, name='welcome'),
		url(r'^user/login$', views.LoginView.as_view(), name='login'),
		url(r'^user/register$', views.RegisterView.as_view(), name='register'),

		url(r'^travel/upload$', views.UploadTravelView.as_view(), name='upload_travel'),
		url(r'^travel/sync$', views.SyncTravelView.as_view(), name='sync_travel')
	)
