from django.conf.urls import patterns, include, url
from django.conf import settings  
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'Server.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
	(r'^medias/(?P<path>.*)$', 'django.views.static.serve', {'document_root':settings.MEDIA_ROOT }), 

	url(r'^mobile/', include('travo.murls')),
	url(r'', include('travo.surls')),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^destination/',include('des_info_provider.urls'))
    
)
