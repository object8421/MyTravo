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
			return NoteService.upload(
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
			return NoteService.sync(
					self.get_user_id_by_token(),
					utils.strpdatetime(begin_time),
					max_qty
					)
		except ValueError:
			return {rsp_code : RC['wrong_arg']}

class GetByTravelHandler(BaseHandler):
	def get(self, travel_id):
		self.travel_id = travel_id
		self.handle()

	def do(self):
		self.user_id = 0
		if self.get_nullable_argument('token') is not None:
			self.user_id = self.get_user_id_by_token()

		return NoteService.get_by_travel(self.user_id, self.travel_id)

class DeleteHandler(BaseHandler):
	def delete(self, note_id):
		self.note_id = note_id
		self.handle()
	
	def do(self):
		return NoteService.delete(
				self.get_user_id_by_token(),
				self.note_id
				)

class UpdateHandler(BaseHandler):
	def put(self, note_id):
		self.note_id = note_id
		self.handle()
		
	def do(self):
		note = self.get_required_data('note')
		if not isinstance(note, dict):
			raise ValueError(note)

		return NoteService.update(
				self.get_user_id_by_token(),
				self.note_id,
				note
				)
