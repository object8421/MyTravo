package com.cobra.mytravo.data;





import com.cobra.mytravo.helpers.PhotoUtils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

/**
 * app info of Travo
 * stores the basic info like url path and sharedpreference
 * Created by L!ar 2013/12/23
 * 
 */
public class AppData extends Application {
	public static final String TRAVO_PATH    //SD卡TRAVO项目路径
		    = Environment.getExternalStorageDirectory() + "/Travo"; 
	
	public static final String TRAVO_AVATAR_PATH = TRAVO_PATH + "/avatar.jpg";
	public static final String QQ_KEY = "100520860";
	public static final String WEIBO_KEY = "3656582238";
	public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html ";
	public static final String WEIBO_SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            
+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            
+ "follow_app_official_microblog," + "invitation_write";

   
    private static Context sContext;
    public static final String HOST_IP = "http://travo.com.cn/mobile/";
    //public static final String HOST_IP = "http://112.124.70.221:8080/mobile/";
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
     * get sharedpreferences
     */
    public static SharedPreferences getMySharedPreferences(){
    	return sharedPreferences;
    }
    public static Editor getEditor(){
    	return editor;
    }
    /*
     * get idtoken
     */
    public static String getIdToken(){
    	return sharedPreferences.getString("idToken", null);
    }
    /*
     * get current user's login state
     */
    public static boolean getIsLogin(){
    	return sharedPreferences.getBoolean("isLogin", false);
    }
    /*
     * get user's nickname
     */
    public static String getNickname(){
    	return sharedPreferences.getString("nickname", null);
    }
    /*
     * get uri of user's avatar
     */
    public static String getAvatarUri(){
    	return sharedPreferences.getString("avatar", null);
    }
    /*
     * get gender
     */
    public static String getSex(){
    	return sharedPreferences.getString("sex", "男");
    }
    /*
     * get userId
     */
    public static int getUserId(){
    	return sharedPreferences.getInt("user_id", -1);
    }
    
    /*
     * get email
     */
    public static String getEmail()
    {
    	return sharedPreferences.getString("email", "");
    }
    public static String getSignature(){
    	return sharedPreferences.getString("signature", "");
    }
    /*
     * get last time of a travel
     */
    public static String getTravelTime(){
    	return sharedPreferences.getString("travel_time", null);
    }
    /*
     * get QQ token
     */
    public static String getQQIdToken(){
    	return sharedPreferences.getString("QQIdToken", null);
    }
    /*
     * get QQ id
     */
    public static String getQQOpenId(){
    	return sharedPreferences.getString("QQIdToken", null);
    }
    /*
     * get longitude
     */
    public static double getLongitude(){
    	return Double.longBitsToDouble(sharedPreferences.getLong("longitude", 
    			Double.doubleToLongBits(118.09772180000004)));
    }
    /*
     * get latitude
     */
    public static double getLatitude(){
    	return Double.longBitsToDouble(sharedPreferences.getLong("latitude", 
    			Double.doubleToLongBits(24.4390262)));
    }
    /*
     * get location
     */
    public static String getLocation(){
    	return sharedPreferences.getString("location", null);
    }

    public static void setQQIdToken(String QQIdTokenString){
    	editor.putString("QQIdToken", QQIdTokenString);
    	editor.commit();
    }

    public static void setQQOpenId(String QQOpenIdString){
    	editor.putString("QQOpenId", QQOpenIdString);
    	editor.commit();
    }

    public static void setUserId(int user_id){
    	editor.putInt("user_id", user_id);
    	editor.commit();
    }

    public static void setIdToken(String idToken){
    	editor.putString("idToken", idToken);
    	editor.commit();
    }

    public static void setIsLogin(boolean flag){
    	editor.putBoolean("isLogin", flag);
    	editor.commit();
    }

    public static void setNickname(String nickname){
    	editor.putString("nickname", nickname);
    	editor.commit();
    }
    
    public static void setEmail(String email)
    {
    	editor.putString("email", email);
    	editor.commit();
    }
    
    public static void setSignature(String signature){
    	editor.putString("signature", signature);
    	editor.commit();
    }
    
    public static void setAvatarUri(String avatarUri){
    	editor.putString("avatar", avatarUri);
    	editor.commit();
    }

    public static void setSex(String sex){
    	editor.putString("sex", sex);
    	editor.commit();
    }

    public static void setTravel_time(String time){
    	editor.putString("travel_time", time);
    	editor.commit();
    }

    public static void setLongitude(Double value){
    	editor.putLong("longitude", Double.doubleToRawLongBits(value));
    	editor.commit();
    }

    public static void setLatitude(Double value){
    	editor.putLong("latitude", Double.doubleToRawLongBits(value));
    	editor.commit();
    }
    public static void setLocation(String name){
    	editor.putString("location", name);
    	editor.commit();
    }
    public static void setPassword(String password){
    	editor.putString("password", password);
    	editor.commit();
    }
    public static String getPassword(){
    	return sharedPreferences.getString("password", "");
    }
    public static void setFacePath(String facePath){
    	editor.putString("face_path", facePath);
    	editor.commit();
    }
    public static String getFacePath(){
    	return sharedPreferences.getString("face_path", "");
    }
    /*
     * clear data
     */
    public static void clearData(){
    	editor.clear();
    	editor.commit();
    }
    
}
