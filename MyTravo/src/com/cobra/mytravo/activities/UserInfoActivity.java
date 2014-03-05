package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UserInfoActivity extends Activity
{
	private TextView nickname;
	private TextView email;
	private Button infoEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		nickname = (TextView)findViewById(R.id.info_username);
		email = (TextView)findViewById(R.id.info_email);
		infoEdit = (Button)findViewById(R.id.info_editEmail);
		infoEdit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(UserInfoActivity.this, EditEmailActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
		if(AppData.getIsLogin())
		{
			nickname.setText(AppData.getNickname());
			email.setText(AppData.getEmail());
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK)
			return;
		if(requestCode == 1)
		{
			this.email.setText(data.getStringExtra("email"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.user_info, menu);
		return true;
	}

}
