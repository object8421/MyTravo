#-*- coding:utf-8 -*-
#Date:2013-9-24

import utils
import sys

from datetime import datetime
from tornado.web import MissingArgumentError
from service import LocationService
from default import * 
from config import *

class UploadHandler(BaseHandler):
	def post(self):
		self.handle()

	def do(self):
		try:
			return LocationService.upload(
						self.get_user_id_by_token(),
						self.get_required_data('locations')
						)
		except MissingArgumentError:
			return {rsp_code : RC['illegal_data']}

class SyncHandler(BaseHandler):
	def get(self):
		self.handle()
	
	def do(self):
		begin_time = self.get_nullable_argument('begin_time', datetime.min)
		max_qty = self.get_nullable_argument('max_qty', sys.maxint)

		try:
			return LocationService.sync(
						self.get_user_id_by_token(),
						utils.strpdatetime(begin_time),
						max_qty
						)
		except ValueError:
			return {rsp_code : RC['wrong_arg']}
