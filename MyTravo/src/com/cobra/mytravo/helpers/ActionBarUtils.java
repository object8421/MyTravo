package com.cobra.mytravo.helpers;

import com.cobra.mytravo.R;

import android.app.ActionBar;
import android.content.Context;
import android.text.style.SuperscriptSpan;

public class ActionBarUtils {
	
	public static void InitialDarkActionBar(Context context, ActionBar actionBar){
		actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		actionBar.setSplitBackgroundDrawable(context.getResources().
				getDrawable(R.color.black));
		
	}
	public static void InitialDarkActionBar(Context context, ActionBar actionBar,String title){
		InitialDarkActionBar(context, actionBar);
		actionBar.setTitle(title);
		
	}
}
