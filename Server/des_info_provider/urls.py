from django.conf.urls import patterns, include, url
from django.contrib import admin
#import django.views

from des_info_provider import views
urlpatterns = patterns('',
    url(r'^$', views.HomeView.as_view(), name='des_home'),
    url(r'^search/(?P<keyword>\w+)/$',views.SearchView.as_view(), name = 'des_search'),
    url(r'^(?P<des_type>\w+)/(?P<des_id>\d+)',views.DesDetailView.as_view(),name = 'des_detail'),
    url(r'^get_des_push_info/(?P<des_name>\w+)',views.GetDesPushView.as_view(),name = 'get_des_push_info'),

    url(r'^mobile$', views.MobileHomeView.as_view(), name='mobile_des_home'),
    url(r'^mobile/search/(?P<keyword>\w+)/$',views.MobileSearchView.as_view(), name = 'mobile_des_search'),
    url(r'^mobile/(?P<des_type>\w+)/(?P<des_id>\d+)',views.MobileDesDetailView.as_view(),name = 'mobile_des_detail'),
)
