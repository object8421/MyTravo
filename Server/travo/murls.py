from django.conf.urls import patterns, url
from travo import mviews 

urlpatterns = patterns('',
		url(r'index', mviews.IndexView, name='welcome'),
		url(r'^sync$', mviews.SyncView.as_view(), name='sync'),

		url(r'^user/login$', mviews.LoginView.as_view(), name='login'),
		url(r'^user/register$', mviews.RegisterView.as_view(), name='register'),
		url(r'^user/update$', mviews.UpdateUserView.as_view(), name='update_user'),
		url(r'^user/(\d+)/face$', mviews.GetFaceView.as_view(), name='get_face'),
		url(r'^user/info/update$', mviews.UpdateUserInfoView.as_view(), name='update_user_info'),
		url(r'^user/(\d+)/follow$', mviews.FollowUserView.as_view(), name='follow_user'),
		url(r'^user/(\d+)/unfollow$', mviews.UnfollowUserView.as_view(), name='unfollow_user'),
		url(r'^user/follow/list$', mviews.FollowListView.as_view(), name='follow_list'),
		url(r'^user/(\d+)/info$', mviews.UserInfoView.as_view(), name='user_info'),

		url(r'^travel/upload$', mviews.UploadTravelView.as_view(), name='upload_travel'),
		url(r'^travel/sync$', mviews.SyncTravelView.as_view(), name='sync_travel'),
		url(r'^travel/(\d+)/cover$', mviews.CoverView.as_view(), name='get_cover'),
		url(r'^travel/search/$', mviews.SearchTravelView.as_view(), name='search_travel'),
		url(r'^travel/(\d+)/favorit$', mviews.FavoritTravelView.as_view(), name='favoirt_travel'),
		url(r'^travel/favorit$', mviews.GetFavoritTravelView.as_view(), name='get_favorit_travel'),
		url(r'^travel/(\d+)/read$', mviews.ReadTravelView.as_view(), name='read_travel'),
		url(r'^travel/(\d+)/vote$', mviews.VoteTravelView.as_view(), name='vote_travel'),
		url(r'^travel/(\d+)/comment$', mviews.CommentTravelView.as_view(), name='comment_travel'),
		url(r'^travel/(\d+)/comments$', mviews.GetCommentsView.as_view(), name='get_comments'),
		url(r'^friend/(\d+)/travels$', mviews.FriendTravelsView.as_view(), name='friend_travel'),

		url(r'^note/upload$', mviews.UploadNoteView.as_view(), name='upload_note'),
		url(r'^note/sync$', mviews.SyncNoteView.as_view(), name='sync_note'),
		url(r'^note/(\d+)/image$', mviews.ImageView.as_view(), name='get_image'),
		url(r'^travel/(\d+)/note$', mviews.GetNoteInTravelView.as_view(), name='get_note_in_travel')
	)
