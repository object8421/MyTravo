from datetime import datetime
from threading import Thread
from django.conf import settings

def strpdatetime(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d %H:%M:%S')

def strpdate(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d').date()

def save_cover(path, cover):
	__save_image(settings.COVER_PATH + path, cover)

def get_cover(path):
	with open(settings.COVER_PATH + path) as f:
		return f.read()

def save_image(path, image):
	__save_image(settings.IMAGE_PATH + path, image)

def get_image(path):
	with open(settings.IMAGE_PATH + path) as f:
		return f.read()

def save_face(path, face):
	__save_image(settings.FACE_PATH + path, face)

def get_face(path):
	with open(settings.FACE_PATH + path) as f:
		return f.read()

def __save_image(path, image):
	def do_save(_path, _image):
		with open(_path, 'wb') as f:
				f.write(_image.read())

	Thread(target = do_save, kwargs = {'_path' : path,
		'_image' : image}).start()

def filter_key(d, keys):
	'''filter some unchangeable data'''
	def _remove_key(d, key):
		if d.has_key(key):
			d.pop(key)
	for key in keys:
		_remove_key(d, key)
