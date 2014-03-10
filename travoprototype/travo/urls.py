from django.conf.urls import patterns, url
from travo import views
from django.shortcuts import get_object_or_404, render

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'^homepage', views.home_page, name='homepage')

)