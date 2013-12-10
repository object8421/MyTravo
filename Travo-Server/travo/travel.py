#-*- coding:utf-8 -*-
#Date:2013-09-20

from service import TravelService
from default import * 
from config import *
from tornado.web import MissingArgumentError

class UploadHandler(BaseHandler):
	def post(self):
		self.handle()

	def do(self):
		return TravelService.upload_travel(
				self.get_user_id_by_token(),
				self.get_required_data('travels')
				)

class SyncHandler(BaseHandler):
	def get(self):
		self.handle()

	def do(self):
		begin_time = self.get_nullable_argument('begin_time')

		return TravelService.sync_travel(
				self.get_user_id_by_token(),
				begin_time
				)

class GetCoverHandler(BaseHandler):
	def get(self, travel_id):
		self.travel_id = travel_id
		self.handle()

	def do(self):
		self.user_id = 0
		if self.get_nullable_argument('token') is not None:
			self.user_id = self.get_user_id_by_token()
		
		result = TravelService.get_cover(self.travel_id, self.user_id)
		if result[rsp_code] == RC['sucess']:
			self.set_header('Content-Type', 'image/jpeg; charsete=utf-8')
			self.write(open(result['cover_path']).read())
			return {rsp_code : RC['sucess']}
		else:
			return result
