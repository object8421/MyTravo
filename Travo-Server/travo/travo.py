# -*- coding:UTF-8 -*-

#travo.py
#Author:Boozer
#Date:2013/08/11

import os
import sys
import tornado.web
import tornado.ioloop
import user
import travel
import location
import note
import default
import sync

from config import *

URL_HANDLER_MAP = [
		(r'/user/register', user.RegisterHandler),
		(r'/user/login', user.LoginHandler),
		(r'/user/bind', user.BindHandler),
		(r'/user/email/update', user.UpdateEmailHandler),

		(r'/travel/upload', travel.UploadHandler),
		(r'/travel/sync', travel.SyncHandler),
		(r'/travel/(\d+)/delete', travel.DeleteHandler),
		(r'/travel/(\d+)/update', travel.UpdateHandler),
		(r'/travel/(\d+)/cover', travel.GetCoverHandler),
		(r'/travel/search', travel.SearchHandler),
		(r'/travel/(\d+)/favorit', travel.FavoritHandler),
		(r'/travel/favorit', travel.GetFavoritHandler),
		(r'/travel/(\d+)/read', travel.ReadHandler),

		(r'/note/upload', note.UploadHandler),
		(r'/note/sync', note.SyncHandler),
		(r'/travel/(\d+)/note', note.GetByTravelHandler),
		(r'/note/(\d+)/delete', note.DeleteHandler),
		(r'/note/(\d+)/update', note.UpdateHandler),

		(r'/location/upload', location.UploadHandler),
		(r'/location/sync', location.SyncHandler),

		(r'/sync', sync.SyncHandler)
		]

def __init():
	sys.path.append('.')

	required_root = (WEB_ROOT, FACE_ROOT, IMAGE_ROOT, COVER_ROOT)
	for root in required_root:
		if not os.path.exists(root):
			os.makedirs(root)

def __start_server():
	application = tornado.web.Application(URL_HANDLER_MAP)
	application.listen(8000)
	tornado.ioloop.IOLoop.instance().start()

if __name__ == '__main__':
	__init()
	__start_server()
