
package com.cobra.mytravo.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by L!ar 2013/12/23
 */
public class TimeUtils {

    public static CharSequence getListTime(String created_at) {
        
        Date date = null;
        SimpleDateFormat srcDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        SimpleDateFormat dstDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            date = srcDateFormat.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstDateFormat.format(date);
    }
}
