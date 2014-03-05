# -*- coding:utf-8 -*-
#date:2013/09/06

import base64
from datetime import datetime
from datetime import date
from threading import Thread

def sNone_to_None(s):
	'''Alter 'None' to None'''
	if s == 'None':
		return  None
	return None

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

def list_to_dict(list_data, idx_key_map):
	'''fetch data from a list and fill in a dict accroding 'idx_key_map'''
	m = idx_key_map #redefine a simple name
	d = {}
	for idx in m.keys():
		d[m[idx]] = list_data[idx]
	return d

def datepstr_indict(d):
	'''parse all date in dict to str'''
	for key in d:
		if isinstance(d[key], date):
			d[key] = str(d[key])

