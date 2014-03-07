package com.cobra.mytravo.helpers;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

public class ScreenUtil {
	private static int screenWidth;
	private static int screenHeight;
	private static float density;
	private static float scaleDensity;
	private static float xdpi;
	private static float ydpi;
	private static int densityDpi;
	
	private static boolean init = false;
	
	private static void init(Context context){
		if (null == context || init) {
			return;
		}
		
		init = true;
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		density = dm.density;
		scaleDensity = dm.scaledDensity;
		xdpi = dm.xdpi;
		ydpi = dm.ydpi;
		densityDpi = dm.densityDpi;
	}
	
	public static int getScreenWidth(Context context) {
		init(context);
		return screenWidth;
	}
	
	public static int getScreenHeight(Context context) {
		init(context);
		return screenHeight;
	}
	
	public static int getStatusBarHeight(Context context) {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			int sbar = context.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e) {
			//LogUtil.error("ScreenUtil", "getStatusBarHeight", e);
	        return 0;
		}
	}
	
	public static int getActivityHeight(Activity activity) {
		Rect rect= new Rect();  
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.height();
	}

	public static int dip2px(Context context, float dipValue) { 
		init(context);
		final float scale = ScreenUtil.density;
		return (int)(dipValue * scale + 0.5f); 
	} 
	
	public static int px2dip(Context context, float pxValue){
		init(context);
		final float scale = ScreenUtil.density;
		return (int)((pxValue-0.5)/scale);
	}
	
	public static void hideSoftInput(Activity activity) {
		if(activity != null && activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
			((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
