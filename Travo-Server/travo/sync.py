#-*- coding:utf-8 -*-
#Date:2014-02-27

from default import BaseHandler
from service import SyncService

class SyncHandler(BaseHandler):
	def get(self):
		self.handle()
	
	def do(self):
		return SyncService.get_sync_status(
				self.get_user_id_by_token()
				)
