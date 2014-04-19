package com.cobra.mytravo.helpers;

import java.lang.reflect.Method;

import com.cobra.mytravo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.text.style.SuperscriptSpan;

public class ActionBarUtils {
	public static void ShowActionBarLogo(Context context, ActionBar actionBar){
		actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		actionBar.setSplitBackgroundDrawable(context.getResources().
				getDrawable(R.color.black));
	}
	public static void InitialDarkActionBar(Context context, ActionBar actionBar){
		actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		actionBar.setSplitBackgroundDrawable(context.getResources().
				getDrawable(R.color.black));
		//getActionBar().setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
	}
	public static void InitialDarkActionBar(Context context, ActionBar actionBar,String title){
		InitialDarkActionBar(context, actionBar);
		actionBar.setTitle(title);
		

	}
	public static void InitialActionBarWithBackAndTitle(Context context, ActionBar actionBar, String title){
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setSplitBackgroundDrawable(context.getResources().
				getDrawable(R.color.black));
//        actionBar.setSplitBackgroundDrawable(context.getResources().
//				getDrawable(R.color.transparant_black));
		actionBar.setDisplayShowHomeEnabled(false);
		

	}
	public static boolean hasSmartBar() { 
		 try { 
		 // 新型号可用反射调用Build.hasSmartBar() 
		 Method method = Class.forName("android.os.Build").getMethod("hasSmartBar"); 
		 return ((Boolean) method.invoke(null)).booleanValue(); 
		 } catch (Exception e) { 
		 } 
		 
		 // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断 
		 if (Build.DEVICE.equals("mx2")) { 
		 return true; 
		 } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) { 
		 
		 return false; 
		 } 
		 return false; 
		 } 

}
