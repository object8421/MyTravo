package com.cobra.mytravo.activities;

import java.util.IllegalFormatCodePointException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.internet.SearchTravelService;
import com.cobra.mytravo.internet.user.BindService;
import com.cobra.mytravo.internet.user.GetFollowsService;
import com.cobra.mytravo.internet.user.GetUserInfoService;
import com.cobra.mytravo.internet.user.GetUserPhotoService;
import com.cobra.mytravo.internet.user.UnFollowFriendService;
import com.cobra.mytravo.internet.user.UpdatePasswordService;
import com.cobra.mytravo.internet.user.UpdateUserInfoService;
import com.cobra.mytravo.internet.user.UpdateUserService;
import com.cobra.mytravo.internet.user.FollowFriendService;
import com.cobra.mytravo.models.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity{
	private static final int REQUEST_CODE_NICKNAME = 0;
	private static final int REQUEST_CODE_GENDER = 1;
	private static final int REQUEST_CODE_SIGNATURE = 2;
	
	private View nickname_layout, gender_layout, signature_layout, 
	email_layout, password_layout, bind_layout;
	private TextView nicknameTextView;
	private TextView genderTextView;
	private TextView signatureTextView;
	private Intent editIntent;
	
	private String access_token;
	
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
		bind_layout = findViewById(R.id.layout5);
		
		
		nicknameTextView.setText(AppData.getNickname());
		genderTextView.setText(AppData.getSex());
		signatureTextView.setText(AppData.getSignature());
		
		password_layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/*Intent updatepassword = new Intent(UserInfoActivity.this, UpdatePasswordService.class);
				updatepassword.putExtra("email", "op@qq.com");
				updatepassword.putExtra("oldpassword", "fuckmyself");
				updatepassword.putExtra("newpassword", "w");
				startService(updatepassword);*/
				
				
				/*Intent getUserInfo = new Intent(UserInfoActivity.this, GetUserInfoService.class);
				getUserInfo.putExtra("user_id", 2);
				startService(getUserInfo);*/
				
				/*Intent getUserPhoto = new Intent(UserInfoActivity.this, GetUserPhotoService.class);
				getUserPhoto.putExtra("user_id", 1);
				startService(getUserPhoto);*/
				
				/*Intent followfriend = new Intent(UserInfoActivity.this, FollowFriendService.class);
				followfriend.putExtra("user_id", 1);
				startService(followfriend);*/
				
				/*Intent unfollowfriend = new Intent(UserInfoActivity.this, UnFollowFriendService.class);
				unfollowfriend.putExtra("user_id", 1);
				startService(unfollowfriend);*/
				
				/*Intent follows = new Intent(UserInfoActivity.this, GetFollowsService.class);
				startService(follows);*/
				
				Intent searchtravel = new Intent(UserInfoActivity.this, SearchTravelService.class);
				startService(searchtravel);
			}
		});
		
		nickname_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editIntent = new Intent(UserInfoActivity.this, EditNicknameActivity.class);
				startActivityForResult(editIntent, REQUEST_CODE_NICKNAME);
			}
		});
		
		email_layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent setEmail = new Intent(UserInfoActivity.this, EditEmailActivity.class);
				startActivity(setEmail);
			}
		});
		
		bind_layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Tencent mTencent = Tencent.createInstance(AppData.QQ_KEY, UserInfoActivity.this);
				mTencent.login(UserInfoActivity.this, "get_user_info", new IUiListener()
				{
					@Override
					public void onError(UiError arg0)
					{
						Toast.makeText(UserInfoActivity.this, "error", Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onComplete(Object arg0)
					{
						Toast.makeText(UserInfoActivity.this, "complete", Toast.LENGTH_LONG).show();
						doComplete((JSONObject)arg0);
						AppData.setQQIdToken(access_token);
						
						Intent bindService = new Intent(UserInfoActivity.this, BindService.class);
						startService(bindService);
					}
					
					protected void doComplete(JSONObject values) {
						try
						{
							access_token = (String) values.get("access_token");
							Log.i("access_token", access_token);
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
					
					@Override
					public void onCancel()
					{
						Toast.makeText(UserInfoActivity.this, "cancel", Toast.LENGTH_LONG).show();
					}
				});
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
