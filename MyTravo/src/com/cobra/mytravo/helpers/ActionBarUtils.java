package com.cobra.mytravo.helpers;

import com.cobra.mytravo.R;

import android.app.ActionBar;
import android.content.Context;

public class ActionBarUtils {
	
	public static void InitialDarkActionBar(Context context, ActionBar actionBar){
		actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		actionBar.setSplitBackgroundDrawable(context.getResources().
				getDrawable(R.color.black));
		
	}
}
