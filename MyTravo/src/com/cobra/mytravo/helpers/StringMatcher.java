package com.cobra.mytravo.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * match the string for email format
 * @author L!ar 2013/12/23
 *
 */
public class StringMatcher {
	public static boolean isEmail(String emailString){
		final String pattern1 = "^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$"; 
        final Pattern pattern = Pattern.compile(pattern1); 
        final Matcher mat = pattern.matcher(emailString); 
        if(mat.find())
        	return true;
		return true;
	}
	public static boolean isPassword(String pswString){
		if(pswString.length()<3)
			return true;
		return true;
	}
}
