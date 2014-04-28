package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.helpers.ActionBarUtils;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.Menu;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "设置");
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit();
	
	}

	 public static class PrefsFragement extends PreferenceFragment implements OnSharedPreferenceChangeListener{  
	        @Override  
	        public void onCreate(Bundle savedInstanceState) {  
	            // TODO Auto-generated method stub  
	            super.onCreate(savedInstanceState);  
	            
	            addPreferencesFromResource(R.xml.preference);  
	        }

	        @Override  
	        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {  
	            // TODO Auto-generated method stub  
	            // Set summary to be the user-description for the selected value  
//	            if(!key.equals(MainActivity.PRF_CHECK))  
//	            {  
//	                Preference connectionPref = findPreference(key);  
//	                connectionPref.setSummary(sharedPreferences.getString(key, ""));  
//	            }  
	        }  
	      
	        @Override  
	        public void onResume() {  
	            super.onResume();  
	            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);  
	      
	        }  
	      
	        @Override  
	        public void onPause() {  
	            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);  
	            super.onPause();  
	        }  
	    }  

}
