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
		travels = self.get_required_data('travels')
		if not isinstance(travels, list):
			raise ValueError('travels')
		
		return TravelService.upload(
				self.get_user_id_by_token(),
				travels
				)

class SyncHandler(BaseHandler):
	def get(self):
		self.handle()

	def do(self):
		begin_time = self.get_argument('begin_time', None)

		return TravelService.sync(
				self.get_user_id_by_token(),
				begin_time
				)

class DeleteHandler(BaseHandler):
	def delete(self, travel_id):
		self.travel_id = travel_id
		self.handle()

	def do(self):
		return TravelService.delete(
				self.get_user_id_by_token(),
				self.travel_id
				)

class UpdateHandler(BaseHandler):
	def put(self, travel_id):
		self.travel_id = travel_id
		self.handle()

	def do(self):
		travel = self.get_required_data('travel')
		if not isinstance(travel, dict):
			raise ValueError('travel')

		return TravelService.update(
				self.get_user_id_by_token(),
				self.travel_id,
				travel
				)

class GetCoverHandler(BaseHandler):
	def get(self, travel_id):
		self.travel_id = travel_id
		result = self.handle()

	def do(self):
		self.user_id = 0
		if self.get_nullable_argument('token', None) is not None:
			self.user_id = self.get_user_id_by_token()
		
		result = TravelService.get_cover(self.user_id, self.travel_id)
		if result[rsp_code] == RC['sucess']:
			self.set_header('Content-Type', 'image/jpeg; charsete=utf-8')
			self.write(open(result['cover_path']).read())
			return {rsp_code : RC['sucess']}
		else:
			return result

class SearchHandler(BaseHandler):
	def get(self):
		self.handle()

	def do(self):
		order = self.get_nullable_argument('order', 'default')
		if not order in TravelService.SEARCH_ORDER:
			return RC['wrong_arg']

		first_idx = self.get_nullable_argument('first_idx', 1)
		max_qty = self.get_nullable_argument('max_qty', 20)

		return TravelService.search(order, first_idx, max_qty)

class FavoritHandler(BaseHandler):
	def post(self, travel_id):
		self.travel_id = travel_id
		self.handle()
	
	def do(self):
		return TravelService.favorit(
				self.get_user_id_by_token(),
				self.travel_id
				)

class GetFavoritHandler(BaseHandler):
	def get(self):
		self.handle()
	
	def do(self):
		return TravelService.get_favorit(
				self.get_user_id_by_token(),
				self.get_nullable_argument('first_idx', 1),
				self.get_nullable_argument('max_qty', 20)
				)

class ReadHandler(BaseHandler):
	def post(self, travel_id):
		self.travel_id = travel_id
		self.handle()

	def do(self):
		return TravelService.read(
				self.get_user_id_by_token(),
				self.travel_id
				)

