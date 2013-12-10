#-*- coding:utf-8 -*-
#Date:2013-09-26

import utils

from config import *
from default import *
from service import NoteService

class UploadHandler(BaseHandler):
	def post(self):
		self.handle()
	
	def do(self):
		try:
			return NoteService.upload_note(
					self.get_user_id_by_token(),
					self.get_required_data('notes')
					)
		except MissingDataError:
			return {rsp_code : RC['illegal_data']}

class SyncHandler(BaseHandler):
	def get(self):
		self.handle()
	
	def do(self):
		begin_time = self.get_nullable_argument('begin_time')
		max_qty = self.get_nullable_argument('max_qty')

		try:
			return NoteService.sync_note(
					self.get_user_id_by_token(),
					utils.strpdatetime(begin_time),
					max_qty
					)
		except ValueError:
			return {rsp_code : RC['wrong_arg']}

class GetHandler(BaseHandler):
	def get(self, travel_id):
		self.travel_id = travel_id
		self.handle()

	def do(self):
		self.user_id = 0
		if self.get_nullable_argument('token') is not None:
			self.user_id = self.get_user_id_by_token()

		return NoteService.get_note(self.travel_id, self.user_id)
