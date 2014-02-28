# -*- coding:UTF-8 -*-
#$/Service/__init__.py
#Date:2013-09-09

import base64
import traceback

from config import *
from threading import Thread
from datetime import datetime
from mysql import connector
from DBUtils.PooledDB import PooledDB

__all__ = [
		'_get_connect',
		'verify_token'
		]

def _get_connect():
	return _pool.connection() 

def verify_token(token):
	'''verify token weather still valid
	return (0)no_token or (-1)token_overdate or user_id
	'''
	conn = _get_connect()
	cur = conn.cursor()
	try:
		return cur.callproc('sp_verify_token', (token,
			TOKEN_VALID_DAY, 0))[2]
	except Exception:
		print('===============caught exception=============')
		print(traceback.format_exc())
	finally:
		cur.close()
		conn.close()

def _build_insert_sql(table, columns):
	c = len(columns)
	sql = 'INSERT INTO ' + table + '(' + ('%s,' * c)[0:-1] + ')'
	sql %= columns
	sql += 'VALUES(' + ('%s,' * c)[0:-1] + ')'
	return sql

#init db connection pool
_pool = PooledDB(connector, mincached=1, maxusage=5,
	host='localhost', user='travo',
	passwd='travo',db='travo')
