# -*- coding : utf-8 -*-
#date:2013/09/06

import traceback
import utils

from mysql.connector import IntegrityError
from datetime import datetime
from default import ServerError
from service import *
from config import *

def upload(user_id, locations):
	location_list = None 
	try:
		location_list = _format_location(user_id, locations)
	except KeyError:
		return {rsp_code : RC['data_incomplete']}
	except ValueError:
		return {rsp_code : RC['illegal_data']}
	except Exception, e:
		raise ServerError(e)

	sql = 'INSERT INTO location(user_id, address, time, longitude,\
			latitude) VALUES(%s, %s, %s, %s, %s)'
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.executemany(sql, location_list)
		conn.commit()
		return {rsp_code : RC['sucess']}
	except IntegrityError, e:
		return {rsp_code : RC['dup_data']}
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def sync(user_id, begin_time, max_qty):
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_sync_location', (
			user_id,
			begin_time,
			max_qty)
			)
		locs = []
		for r in cur.stored_results():
			for l in r.fetchall():
				loc = {}
				loc['time'] = str(l[0])
				loc['longtiude'] = l[1]
				loc['latitude'] = l[2]
				loc['address'] = l[3]
				locs.append(loc)
		result = {rsp_code : RC['sucess']}
		result['locations'] = locs
		return result
	except Exception, e:
		raise ServerError(e)
	finally:
		cur.close()
		conn.close()

def _format_location(user_id, locations):
	'''format location in dict list to tuple list which
		could inserted in db suitable'''
	def _check_coord(c):
		if c < -180 or c > 180:
			raise ValueError(c)
		return c

	return [
		(
		user_id,
		l.get('address'),
		utils.strpdatetime(l['time']),
		_check_coord(l['longitude']),
		_check_coord(l['latitude'])
		) for l in locations]
