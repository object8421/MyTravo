"""
Django settings for Server project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))

FTP_SERVER = '172.16.12.125'
FTP_PORT = 21
FTP_USER = 'rew'

IMAGE_PATH = '/tmp/travo/' 
FACE_PATH = IMAGE_PATH + 'face/'
COVER_PATH = IMAGE_PATH + 'cover/'
IMAGE_SNAP_PATH = IMAGE_PATH + 'snap/'
COVER_SNAP_PATH = IMAGE_SNAP_PATH + 'cover/'

MEDIA_ROOT = 'ftp://rew@172.16.12.125/image'
MEDIA_URL = '/media/'

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '6(r+_%23s=ho7zoy$u#o@+p%$%ykayj%egdq9mb@7yirhv#)@e'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True 

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []
		#'*',]

TOKEN_VALID_DAY = 30

HORZ_SNAP_WIDTH = 960 
HORZ_SNAP_HEIGHT = 640 
VERT_SNAP_WIDTH = 960 
VERT_SNAP_HEIGHT = 1400 
# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
	'travo',
    'des_info_provider',
)

TEMPLATE_CONTEXT_PROCESSORS = (  
#"django.core.context_processors.auth",  

"django.core.context_processors.debug",  
"django.core.context_processors.i18n",  
"django.core.context_processors.media",  
"django.core.context_processors.request",  
"django.contrib.auth.context_processors.auth",


)  

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
#    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'Server.urls'

WSGI_APPLICATION = 'Server.wsgi.application'


# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'travo',
		'USER': 'travo',
		'PASSWORD':'travo',
		'HOST' : '172.16.12.26',
		'PORT': '3306'
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = None 

USE_I18N = True

#USE_L10N = True

USE_TZ = False 

ADMINS = (
		('Boozer', 'mingyuan1240@gmail.com'),
		('rew', '1135172071@qq.com'),
		('Ethon' 'wlj0427@gmail.com')
		)
EMAIL_HOST = 'smtp.gmail.com'
EMAIL_HOST_USER = 'cobra.travo@gmail.com'
EMAIL_HOST_PASSWORD = 'www.cobra.com'
EMAIL_USE_TLS = True

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'
