package com.cobra.mytravo.data;





import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

/**
 * Created by L!ar 
 * 提供该app的基本信息，包括HOST IP和 SHAREDPREFERENCES等信息
 */
public class AppData extends Application {
	public static final String TRAVO_PATH    //SD卡TRAVO项目路径
		    = Environment.getExternalStorageDirectory() + "/Travo";  
	public static final String TRAVO_AVATAR_PATH = TRAVO_PATH + "/avatar.jpg";
	public static final String QQ_KEY = "100520860";
	public static final String WEIBO_KEY = "3656582238";
	public static final String WEIBO_REDIRECT_URL = "http://www.cobra.travo.com";
	public static final String WEIBO_SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            
+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            
+ "follow_app_official_microblog," + "invitation_write";

   
    private static Context sContext;
    public static final String HOST_IP = "http://172.16.12.26:8000/";
    private static SharedPreferences sharedPreferences; 
    private static Editor editor;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        sharedPreferences = sContext.getSharedPreferences("share", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static Context getContext() {
        return sContext;
    }
    /*
     * 获取该程序sharedpreferences
     */
    public static SharedPreferences getMySharedPreferences(){
    	return sharedPreferences;
    }
    public static Editor getEditor(){
    	return editor;
    }
    /*
     * 获取idtoken
     */
    public static String getIdToken(){
    	return sharedPreferences.getString("idToken", null);
    }
    /*
     * 获取当前是否已经有用户登陆
     */
    public static boolean getIsLogin(){
    	return sharedPreferences.getBoolean("isLogin", false);
    }
    /*
     * 获取当前用户昵称
     */
    public static String getNickname(){
    	return sharedPreferences.getString("nickname", null);
    }
    /*
     * 获取当前用户头像的Uri
     */
    public static String getAvatarUri(){
    	return sharedPreferences.getString("avatar", null);
    }
    /*
     * 获取性别
     */
    public static String getSex(){
    	return sharedPreferences.getString("sex", "男");
    }
    /*
     * 获取user_id(Travo服务器生成)
     */
    public static int getUserId(){
    	return sharedPreferences.getInt("user_id", -1);
    }
    /*
     * 获取最后一次游记的生成时间（用以区别各个游记）
     */
    public static String getTravelTime(){
    	return sharedPreferences.getString("travel_time", null);
    }
    /*
     * 获取QQ TOKEN
     */
    public static String getQQIdToken(){
    	return sharedPreferences.getString("QQIdToken", null);
    }
    /*
     * 获取QQ open Id
     */
    public static String getQQOpenId(){
    	return sharedPreferences.getString("QQIdToken", null);
    }
    /*
     * 获取经度
     */
    public static double getLongitude(){
    	return Double.longBitsToDouble(sharedPreferences.getLong("longitude", 
    			Double.doubleToLongBits(118.09772180000004)));
    }
    /*
     * 获取纬度
     */
    public static double getLatitude(){
    	return Double.longBitsToDouble(sharedPreferences.getLong("latitude", 
    			Double.doubleToLongBits(24.4390262)));
    }
    /*
     * 获取位置名称
     */
    public static String getLocation(){
    	return sharedPreferences.getString("location", null);
    }
    /*
     * 设置用户QQ token
     */
    public static void setQQIdToken(String QQIdTokenString){
    	editor.putString("QQIdToken", QQIdTokenString);
    	editor.commit();
    }
    /*
     * 设置用户QQ openid
     */
    public static void setQQOpenId(String QQOpenIdString){
    	editor.putString("QQOpenId", QQOpenIdString);
    	editor.commit();
    }
    /*
     * 设置用户id
     */
    public static void setUserId(int user_id){
    	editor.putInt("user_id", user_id);
    	editor.commit();
    }
    /*
     * 设置token(该token为TRAVO服务器生成)
     */
    public static void setIdToken(String idToken){
    	editor.putString("idToken", idToken);
    	editor.commit();
    }
    /*
     * 设置是否已登录
     */
    public static void setIsLogin(boolean flag){
    	editor.putBoolean("isLogin", flag);
    	editor.commit();
    }
    /*
     * 设置昵称
     */
    public static void setNickname(String nickname){
    	editor.putString("nickname", nickname);
    	editor.commit();
    }
    /*
     * 设置头像路径
     */
    public static void setAvatarUri(String avatarUri){
    	editor.putString("avatar", avatarUri);
    	editor.commit();
    }
    /*
     * 设置性别
     */
    public static void setSex(String sex){
    	editor.putString("sex", sex);
    	editor.commit();
    }
    /*
     * 设置最近一次游记时间
     */
    public static void setTravel_time(String time){
    	editor.putString("travel_time", time);
    	editor.commit();
    }
    /*
     * 设置经度
     */
    public static void setLongitude(Double value){
    	editor.putLong("longitude", Double.doubleToRawLongBits(value));
    	editor.commit();
    }
    /*
     * 设置纬度
     */
    public static void setLatitude(Double value){
    	editor.putLong("latitude", Double.doubleToRawLongBits(value));
    	editor.commit();
    }
    public static void setLocation(String name){
    	editor.putString("location", name);
    	editor.commit();
    }
    /*
     * 清楚所有sharedpreferences数据
     */
    public static void clearData(){
    	editor.clear();
    }
    
}
