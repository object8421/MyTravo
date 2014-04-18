from django.conf.urls import patterns, include, url

from django.contrib import admin
#import django.views

from des_info_provider import views

urlpatterns = patterns('',
    url(r'^$', views.HomeView.as_view(), name='des_home'),
)
