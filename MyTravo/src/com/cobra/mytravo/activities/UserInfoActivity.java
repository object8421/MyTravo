package com.cobra.mytravo.activities;

import java.util.IllegalFormatCodePointException;

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

public class UserInfoActivity extends Activity{
	private static final int REQUEST_CODE_NICKNAME = 0;
	private static final int REQUEST_CODE_GENDER = 1;
	private static final int REQUEST_CODE_SIGNATURE = 2;
	
	private View nickname_layout, gender_layout, signature_layout, 
	email_layout, password_layout;
	private TextView nicknameTextView;
	private TextView genderTextView;
	private TextView signatureTextView;
	private Intent editIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		nicknameTextView = (TextView)findViewById(R.id.tv_nickname);
		genderTextView = (TextView)findViewById(R.id.tv_gender);
		signatureTextView = (TextView)findViewById(R.id.tv_signature);
		
		nickname_layout = findViewById(R.id.layout_nickname);
		gender_layout = findViewById(R.id.layout_gender);
		signature_layout = findViewById(R.id.layout2);
		email_layout = findViewById(R.id.layout3);
		password_layout = findViewById(R.id.layout4);
		
		nicknameTextView.setText(AppData.getNickname());
		genderTextView.setText(AppData.getSex());
		signatureTextView.setText(AppData.getSignature());
		
		nickname_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editIntent = new Intent(UserInfoActivity.this, EditNicknameActivity.class);
				startActivityForResult(editIntent, REQUEST_CODE_NICKNAME);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK)
			return;
		if(requestCode == REQUEST_CODE_NICKNAME){
			nicknameTextView.setText(AppData.getNickname());
		}
		else if(requestCode == REQUEST_CODE_GENDER){
			genderTextView.setText(AppData.getSex());
		}
		else if(requestCode == REQUEST_CODE_SIGNATURE){
			signatureTextView.setText(AppData.getSignature());
		}
	}



}
