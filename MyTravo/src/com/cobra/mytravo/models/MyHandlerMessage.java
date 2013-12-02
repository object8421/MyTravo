package com.cobra.mytravo.models;

import android.R.integer;



public final class MyHandlerMessage {
	public final static int GETLOCATION_SUCCESS = 00;
	public final static int GETLOCATION_FAIL = 01;
	
	public final static int WELCOME_USER_EXIST = 10;
	public final static int WELCOME_USER_NOT_EXIST = 11;
	
	public final static int REGISTER_SUCCESS = 20;
	public final static int REGISTER_FAIL_SERVER = 21;
	public final static int REGISTER_FAIL_EMAIL = 22;
	public final static int REGISTER_FAIL_NICKNAME = 23;
	
	public final static int LOGIN_SUCCESS = 30;
	public final static int LOGIN_FAIL_SERVER = 31;
	public final static int LOGIN_FAIL_EMAIL = 32;
	public final static int LOGIN_FAIL_PASSWORD = 33;

	public final static int UPDATE_USER_INFO_SUCCESS = 40;
	public final static int UPDATE_USER_INFO_FAIL_SERVER = 41;
	public final static int UPDATE_USER_INFO_FAIL_TOKEN = 42;
	public final static int UPDATE_USER_INFO_FAIL_NICKNAME = 43;	

	public final static int GET_AVATAR_FROM_LOCAL_SUCCESS = 50;
	public final static int GET_AVATAR_FROM_LOCAL_FAIL = 51;
	
	public final static int GET_AVATAR_FROM_SERVER_SUCCESS = 60;
	public final static int GET_AVATAR_FROM_SERVER_FAIL = 61;
	
	public final static int GET_QQ_AVATAR_SUCCESS = 70;
	public final static int GET_QQ_AVATAR_FAIL = 71;
	
	public final static int BIND_THIRD_PARTY_TO_TRAVO = 80;
	public final static int SERVER_NO_SUCH_QQ_USER = 81;
	public final static int BIND_QQ_TO_TRAVO_SUCCESS = 82;
	public final static int BIND_QQ_TO_TRAVO_FAIL = 83;
	public final static int BIND_SINA_TO_TRAVO_SUCCESS = 84;
	public final static int BIND_SINA_TO_TRAVO_FAIL = 85;
	
	public final static int QQ_ONLY_REGISTER_SUCCESS = 90;
	public final static int QQ_ONLY_REGISTER_FAIL = 91;
	
	public final static int ADD_NEW_TRAVEL_SUCCESS = 100;
	public final static int ADD_NEW_TRAVEL_FAIL = 101;
	
	public final static int GET_LOCATION_SUCCESS = 110;
	public final static int GET_LOCATION_NAME_SUCCESS = 111;
}
