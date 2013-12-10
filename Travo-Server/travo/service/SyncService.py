#-*- coding:utf-8 -*-
#Date:2013-09-26

from config import * 
from service import *

def get_sync_status(user_id):
	time_list = []	
	conn = _get_connect()
	cur = conn.cursor()
	try:
		cur.callproc('sp_get_sync_status', (user_id,))
		for r in cur.stored_results():
			time = r.fetchone()
			if time is None:
				time_list.append('Null')
			else:
				time_list.append(str(time[0]))

		result = {rsp_code : RC['sucess']}
		result['user'] = time_list[0]
		result['user_info'] = time_list[1]
		result['location'] = time_list[2]
		result['note'] = time_list[3]
		result['travel'] = time_list[4]
		result['travel_plan'] = time_list[5]
		return result
	except Exception: 
		print('===============caught exception=============')
		print(traceback.format_exc())
		return {rsp_code : RC['server_error']}
	finally:
		cur.close()
		conn.close()
