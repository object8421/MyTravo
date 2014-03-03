package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class UserInfoActivity extends Activity
{
	private TextView nickname;
	private TextView email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		nickname = (TextView)findViewById(R.id.info_username);
		email = (TextView)findViewById(R.id.info_email);
		
		if(AppData.getIsLogin())
		{
			nickname.setText(AppData.getNickname());
			email.setText(AppData.getEmail());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.user_info, menu);
		return true;
	}

}
