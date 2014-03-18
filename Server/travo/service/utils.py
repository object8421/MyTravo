from datetime import datetime
from threading import Thread
from django.conf import settings

def strpdatetime(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d %H:%M:%S')

def strpdate(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d').date()

def save_cover(path, image_data):
	__save_image(settings.COVER_PATH + path, image_data)

def get_cover(path):
	with open(settings.COVER_PATH + path) as f:
		return f.read()

def save_image(path, image_data):
	__save_image(settings.IMAGE_PATH + path, image_data)

def get_image(path):
	with open(settings.IMAGE_PATH + path) as f:
		return f.read()
def save_face(path, image_data):
	__save_image(settings.FACE_PATH + path, image_data)

def get_face(path):
	with open(settings.FACE_PATH + path) as f:
		return f.read()

def __save_image(path, image_data):
	def do_save(_path, _image_data):
		with open(_path, 'wb') as f:
			f.write(_image_data)

	Thread(target = do_save, kwargs = {'_path' : path,
		'_image_data' : image_data}).start()
