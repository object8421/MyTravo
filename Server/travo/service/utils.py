import copy
import StringIO

from datetime import datetime
from threading import Thread
from django.conf import settings
from PIL import Image
from ftplib import FTP

######    datetime    ######
def _standard_time_str(s):
	'''change YYYY:MM:DD to YYYY-MM-DD'''
	return s.replace(':', '-', 2)

def strpdatetime(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d %H:%M:%S')

def strpdate(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d').date()

######    get image    ######
def _get_image(path):
	ftp = get_ftp()
	output = StringIO.StringIO()
	ftp.retrbinary('RETR ' + path, output.write)
	ftp.close()
	return output.getvalue()

def get_cover(path):
	return _get_image(settings.COVER_PATH + path)

def get_image(path):
	return _get_image(settings.IMAGE_PATH + path)

def get_face(path):
	return _get_image(settings.FACE_PATH + path)

######    save image    ######
'''
def __save_image(path, image):
	def do_save(_path, _image):
		with open(_path, 'wb') as f:
				f.write(_image.read())

	Thread(target = do_save, kwargs = {'_path' : path,
		'_image' : image}).start()
'''
def get_ftp():
	ftp = FTP()
	ftp.connect(settings.FTP_SERVER, settings.FTP_PORT)
	ftp.login(settings.FTP_USER)
	return ftp

def __save_image(path, image):
	def do_save(_path, _image):
		ftp = get_ftp()
		ftp.storbinary('STOR ' + path, _image)
		ftp.close()

	Thread(target = do_save, kwargs = {'_path' : path,
		'_image' : image}).start()

def save_cover(path, cover):
	__save_image(settings.COVER_PATH + path, cover)

def save_image(path, image):
	__save_image(settings.IMAGE_PATH + path, image)

def save_face(path, face):
	__save_image(settings.FACE_PATH + path, face)

######    other    ######
def filter_key(d, keys):
	'''filter some unchangeable data'''
	def _remove_key(d, key):
		if d.has_key(key):
			d.pop(key)
	for key in keys:
		_remove_key(d, key)

def get_photo_time(photo):
	'''return photo shoot time'''
	im = Image.open(copy.deepcopy(photo))
	exif = im._getexif()
	if exif is None:
		return None
	else:
		print exif[36867]
		return _standard_time_str(exif[36867])		#DateTimeOriginal

