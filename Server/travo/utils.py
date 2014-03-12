import base64
from json import JSONEncoder
from django.db import models
from datetime import datetime
from threading import Thread
from django.conf import settings

class MyJsonEncoder(JSONEncoder):
	def default(self, o):
		if isinstance(o, models.Model):
			return o.dict()
		return JSONEncoder.default(self, o)

def strpdatetime(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d %H:%M:%S')

def strpdate(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d').date()

def save_cover(path, image_data):
	__save_image(settings.COVER_PATH + path, image_data)

def __save_image(path, image_data):
	def do_save(_path, _image_data):
		binary = base64.b64decode(_image_data)
		with open(_path, 'w') as f:
			f.write(binary)

	Thread(target = do_save, kwargs = {'_path' : path,
		'_image_data' : image_data}).start()

