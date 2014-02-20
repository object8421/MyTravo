package com.cobra.mytravo.data;

public final class MyServerMessage {
	public final static int SUCCESS = 100;//成功
	public final static int WRONG_ARG = 200;//参数不存在
	public final static int WRONG_PASSWORD = 201;//密码错误
	public final static int AUTH_FAIL = 202;//无法通过第三方进行验证
	public final static int DATA_INCOMPLETE = 203;//数据不完整
	public final static int DUP_BIND = 204;//第三方账户已绑定到其他账户
	public final static int DUP_DATA = 205;//数据重复
	public final static int DUP_EMAIL = 206;//邮箱重复
	public final static int DUP_NICKNAME = 207;//昵称重复
	public final static int ILLEGAL_DATA = 208;//数据不存在或者不合法
	public final static int ILLEGAL_EMAIL = 209;//不合法的邮箱
	public final static int MISS_EMAIL = 210;//邮箱重复
	public final static int MISS_NICKNAME = 211;//缺少昵称
	public final static int MISS_PASSWORD = 212;//缺少密码
	public final static int MISS_TOKEN = 213;//缺少token
	public final static int NO_SUCH_USER = 214;//该用户不存在
	public final static int NO_TRAVEL = 215;//找不到travel
	public final static int OVERDATE_TOKEN = 216;//token已过期
	public final static int SERVER_ERROR = 300;//服务器错误
}
