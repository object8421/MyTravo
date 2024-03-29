
package com.cobra.mytravo.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * Created by L!ar 2013/12/23
 */
public class TimeUtils {
	private final static String TAG = "TimeUtils";
	public static CharSequence getTime(){
		Date date = null;
//        SimpleDateFormat srcDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy",Locale.ENGLISH);
		SimpleDateFormat dstDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try {
        	date = new Date(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstDateFormat.format(date);
	}
    public static CharSequence getListTime(String created_at) {
        
        Date date = null;
        SimpleDateFormat srcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dstDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            date = srcDateFormat.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstDateFormat.format(date);
    }
    public static CharSequence getPhotoTime(long  created_at) {
        Log.i(TAG, String.valueOf(created_at));
    	Date date = null;
        SimpleDateFormat srcDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy",Locale.ENGLISH);
        SimpleDateFormat dstDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        try {
//        	date = srcDateFormat.parse(new Date(created_at).toString());
//        	if(date == null){
        		date = new Date(created_at);
        		
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstDateFormat.format(date);
    }
    public static String getCalendarTime(Calendar c){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	String formattedDate = df.format(c.getTime());
    	return formattedDate;
    }
    public static Date CalendarStringToDate(String s){
    	Date date = null;
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	try {
        	date = df.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return date;
    }
}
