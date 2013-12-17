#-*- coding:utf-8 -*-
#Date:2013-09-14

from service import UserService
from default import * 
from config import *

class RegisterHandler(BaseHandler):
	def post(self):
		self.handle()
	
	def do(self):
		self.args = {}
		self.args['remote_ip'] = self.request.remote_ip
		self.args['user_type'] = self.get_argument('user_type')
                self.args['nickname'] = self.get_argument('nickname')
                self.args['email'] = self.get_argument('email')
                self.args['password'] = self.get_argument('password')
		try:
                        self.args['nickname'] = self.get_argument('nickname')
            
			try:
				{
					'travo' : self.get_travo_arg, 
					'qq'	: self.get_qq_arg, 
					'sina'	: self.get_sina_arg 
				}[self.args['user_type']]()
			except KeyError:
				return {rsp_code : RC['wrong_arg']}
		except MissingDataError, e:
			return {
				'email'		: lambda : {rsp_code : RC['miss_email']},
				'password'	: lambda : {rsp_code : RC['miss_password']},
				'nickname'	: lambda : {rsp_code : RC['miss_nickname']},
				'qq_token'	: lambda : {rsp_code : RC['miss_token']},
				'sina_token': lambda : {rsp_code : RC['miss_token']}
			}[str(e)]()
		
		return UserService.register(self.args)

	def get_travo_arg(self):
		self.args['email'] = self.get_argument('email')
		self.args['password'] = self.get_argument('password')
	def get_qq_arg(self):
		self.args['qq_token'] = self.get_argument('qq_token')
	def get_sina_arg(self):
		self.args['sina_token'] = self.get_argument('sina_token')

class LoginHandler(BaseHandler):
	def get(self):
		self.handle()
	
	def do(self):
		self.args = {}
		self.args['remote_ip'] = self.request.remote_ip
		self.args['user_type'] = self.get_argument('user_type')
		try:
			{
				'travo' : self.get_travo_arg,
				'qq'	: self.get_qq_arg,
				'sina'	: self.get_sina_arg
			}[self.args['user_type']]()
		except KeyError:
			return {rsp_code : RC['wrong_arg']}
		return UserService.login(self.args)
	def get_travo_arg(self):
		self.args['email'] = self.get_argument('email')
		self.args['password'] = self.get_argument('password')
	def get_qq_arg(self):
		self.args['qq_token'] = self.get_argument('qq_token')
	def get_sina_arg(self):
		self.args['sina_token'] = self.get_argument('sina_token')

class BindHandler(BaseHandler):
	def put(self):
		self.handle()
	
	def do(self):
		user = {'user_type' : self.get_argument('user_type')}
		user['token'] = {
				'sina'	: lambda : self.get_argument('sina_token'),
				'qq'	: lambda : self.get_argument('qq_token')
				}[user['user_type']]()
		return UserService.bind_open_user(
				self.get_user_id_by_token(),
				user
				)

class UpdateEmailHandler(BaseHandler):
	def put(self):
		self.handle()
	def do(self):
		return UserService.update_email(
				self.get_user_id_by_token(),
				self.get_argument('email'),
				self.get_argument('password')
				)
