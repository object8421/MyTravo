from django.conf.urls import patterns, url
from travo import mviews 

urlpatterns = patterns('',
		url(r'index', mviews.IndexView, name='welcome'),
		url(r'^sync$', mviews.SyncView.as_view(), name='sync'),

		url(r'^user/login$', mviews.LoginView.as_view(), name='login'),
		url(r'^user/register$', mviews.RegisterView.as_view(), name='register'),

		url(r'^travel/upload$', mviews.UploadTravelView.as_view(), name='upload_travel'),
		url(r'^travel/sync$', mviews.SyncTravelView.as_view(), name='sync_travel'),
		url(r'^travel/(\d+)/cover$', mviews.CoverView.as_view(), name='get_cover'),

		url(r'^note/upload$', mviews.UploadNoteView.as_view(), name='upload_note'),
		url(r'^note/sync$', mviews.SyncNoteView.as_view(), name='sync_note')
	)
