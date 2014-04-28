import copy
import StringIO
from oss.oss_api import *
from datetime import datetime
from threading import Thread
from django.conf import settings
import Image
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
'''
def __save_image(path, image):
	def do_save(_path, _image):
		with open(_path, 'wb') as f:
				f.write(_image.read())

	Thread(target = do_save, kwargs = {'_path' : path,
		'_image' : image}).start()

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

def save_image_snap(path, image):
	return _save_snap(settings.IMAGE_SNAP_PATH + path, image)

def save_cover_snap(path, cover):
	return _save_snap(settings.COVER_SNAP_PATH + path, cover)
'''
def save_image_snap(path, image):
	image.open()
	return _save_snap('travo-note-pic-snap', path, image)

def save_cover_snap(path, cover):
	cover.open()
	return _save_snap('travo-travel-cover-snap', path, cover)

def horz_image(image):
	width, height = image.size
	return width >= height

def min(a, b):
	if a < b:
		return a
	return b

def _save_snap(bucket, path, image):
	im = Image.open(image)
	width, height = im.size
	resized = False

	if horz_image(im):
		width = min(width, settings.HORZ_SNAP_WIDTH)
		height = min(height, settings.HORZ_SNAP_HEIGHT)
	else:
		width = min(width, settings.VERT_SNAP_WIDTH)
		height = min(height, settings.VERT_SNAP_HEIGHT)

	if (width, height) != im.size:
		resized = True

	if resized:
		region = im.resize((width, height), Image.ANTIALIAS)
		output = StringIO.StringIO()
		region.save(output, im.format)
		__save_image(bucket, path, output.getvalue())
		output.close()
		return True
	return False

def __save_image(bucket, path, image):
	def do_save(_bucket, _path, _image):
		oss = OssAPI('oss.aliyuncs.com','ZtI6J33H7O9dWyP5','6XubAUt6JFWQ7yi9w5MPQkKLHtcFe3')
		res = oss.put_object_from_string(_bucket, _path, _image)
	
	Thread(target = do_save, kwargs = {'_bucket' : bucket, '_path' : path,
		'_image' : image}).start()

def save_cover(path, cover):
	cover.open()
	__save_image('travo-travel-cover', path, str(cover.read()))

def save_image(path, image):
	image.open()
	__save_image('travo-note-pic', path, str(image.read()))

def save_face(path, face):
	face.open()
	__save_image('travo-user-avatar', path, str(face.read()))

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
	#cpimage = copy.deepcopy(photo)
	photo.open()
	im = Image.open(photo)

	exif = im._getexif()
	if exif is None:
		return None
	else:
		print exif[36867]
		return _standard_time_str(exif[36867])		#DateTimeOriginal
