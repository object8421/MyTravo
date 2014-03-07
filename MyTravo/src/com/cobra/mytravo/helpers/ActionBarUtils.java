package com.cobra.mytravo.helpers;

import com.cobra.mytravo.R;

import android.app.ActionBar;
import android.content.Context;
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
				getDrawable(R.color.transparant_black));
		actionBar.setDisplayShowHomeEnabled(false);
	}
}
