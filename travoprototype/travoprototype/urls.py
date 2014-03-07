from django.conf.urls import patterns, include, url
from django.contrib import admin

admin.autodiscover()
urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'travoprototype.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    #url(r'^travo/',include('travo.urls')),
    url(r'^travo/',include('travo.urls'), name='travo'),
    url(r'^admin/', include(admin.site.urls)),
)
