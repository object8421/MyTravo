#-*- coding:utf-8 -*-
#Date:2013-09-17

__all__ = [
		'WEB_ROOT',
		'FACE_ROOT',
		'IMAGE_ROOT',
		'COVER_ROOT',
		'TOKEN_VALID_DAY',
		'rsp_code',
		'RC'
]

WEB_ROOT = '/usr/Travo/ImageData/'	#文件的根目录
FACE_ROOT = WEB_ROOT + 'face/'
IMAGE_ROOT = WEB_ROOT + 'image/'
COVER_ROOT = WEB_ROOT + 'cover/'

TOKEN_VALID_DAY = 60

#define some useful key:
rsp_code = 'rsp_code'

#define rsponse code
RC = {
	'sucess'			: 100,
	'wrong_arg'			: 200,
	'wrong_password'	: 201,
	'auth_fail'			: 202,
	'data_incomplete'	: 203,
	'dup_bind'			: 204,
	'dup_data'			: 205,
	'dup_email'			: 206,
	'dup_nickname'		: 207,
	'illegal_data'		: 208,
	'illegal_email'		: 209,
	'miss_email'		: 210,
	'miss_nickname'		: 211,
	'miss_password'		: 212,
	'miss_token'		: 213,
	'no_such_user'		: 214,
	'no_such_travel'	: 215,
	'no_such_note'		: 216,
	'no_cover'			: 217,
	'no_image'			: 218,
	'token_overdate'	: 219,
	'permission_denied'	: 220,
	'server_error'		: 300
}
