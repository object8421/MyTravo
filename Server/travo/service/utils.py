import copy
import StringIO
from oss.oss_api import *
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

def datetimepstr(d):
	return str(d)[0:-7]

######    get image    ######
'''
def _get_image(path):
	ftp = get_ftp()
	output = StringIO.StringIO()
	ftp.retrbinary('RETR ' + path, output.write)
	ftp.close()
	return output.getvalue()
'''
def _get_image(path):
	with open(path) as f:
		return f.read()

def get_cover(path):
	return _get_image(settings.COVER_PATH + path)

def get_image(path):
	return _get_image(settings.IMAGE_PATH + path)

def get_face(path):
	return _get_image(settings.FACE_PATH + path)

######    save image    ######
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
'''

def save_cover(path, cover):
	__save_image(settings.COVER_PATH + path, cover)

def save_image(path, image):
	__save_image(settings.IMAGE_PATH + path, image)

def save_face(path, face):
	__save_image(settings.FACE_PATH + path, face)

def save_image_snap(path, image):
	return _save_snap(settings.IMAGE_SNAP_PATH + path, image, settings.IMAGE_SNAP_WIDTH, settings.IMAGE_SNAP_HEIGHT)

def save_cover_snap(path, cover):
	return _save_snap(settings.COVER_SNAP_PATH + path, cover, settings.COVER_SNAP_WIDTH, settings.COVER_SNAP_HEIGHT)

def _save_snap(path, image, WIDTH, HEIGHT):
	cpimage = copy.deepcopy(image)
	im = Image.open(cpimage)
	width, height = im.size
	resized = False

	if width > WIDTH: 
		width = WIDTH 
		resized = True
	if height > HEIGHT:
		height = HEIGHT 
		resized = True

	if resized:
		region = im.resize((width, height), Image.ANTIALIAS)
		region.save(path)
		return True
	return False

'''
def __save_image(bucket_name,file_name, image):
	oss = OssAPI('oss.aliyuncs.com','ZtI6J33H7O9dWyP5','6XubAUt6JFWQ7yi9w5MPQkKLHtcFe3')
	res = oss.put_object_from_string(bucket_name,file_name,str(image.read()))
	print '%s \n %s'%(res.status,res.read())

def save_cover(path, cover):
	__save_image('travo-travel-cover', path, cover)

def save_image(path, image):
    __save_image('travo-note-pic', path, image)

def save_face(path, face):
	__save_image('travo-user-avatar', path, face)
'''

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
	cpimage = copy.deepcopy(photo)
	im = Image.open(cpimage)

	exif = im._getexif()
	if exif is None:
		return None
	else:
		print exif[36867]
		return _standard_time_str(exif[36867])		#DateTimeOriginal
