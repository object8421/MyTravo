# -*- coding:utf-8 -*-
#date:2013/09/06

import base64
from datetime import datetime
from threading import Thread

def strpdate(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d').date()

def strpdatetime(s):
	if s is not None:
		return datetime.strptime(s, '%Y-%m-%d %H:%M:%S')

def check_key(need_keys, keys):
	''' chekc if keys include all needed key'''
	for key in need_keys:
		if key not in keys:
			return False
	return True

def save_image(path, content):
	def __save_image(_path, _content):
		binary = base64.b64decode(_content)
		with open(_path, 'w') as f:
			f.write(binary)
	Thread(target = __save_image, kwargs = {'_path' : path,
		'_content' : content}).start()
