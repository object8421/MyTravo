package com.cobra.mytravo.activities;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.models.MyHandlerMessage;
import com.cobra.mytravo.models.MyServerMessage;
import com.cobra.mytravo.models.User.UserLoginResponse;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private RequestQueue mRequestQueue;
	private LoginThread mLoginThread;
	
	private EditText email;
	private EditText password;
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
			case MyHandlerMessage.LOGIN_SUCCESS:
				Toast.makeText(LoginActivity.this, "您已成功登陆", Toast.LENGTH_SHORT).show();
				break;
			case MyHandlerMessage.LOGIN_FAIL_EMAIL:
				Toast.makeText(LoginActivity.this, "邮箱不存在", Toast.LENGTH_SHORT).show();
				break;
			case MyHandlerMessage.LOGIN_FAIL_PASSWORD:
				Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
				break;
			case MyHandlerMessage.LOGIN_FAIL_SERVER:
				Toast.makeText(LoginActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(LoginActivity.this, "Oops", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		email = (EditText)findViewById(R.id.login_email);
		password = (EditText)findViewById(R.id.login_password);
	}
	
	private boolean check()
	{
		if(email.getText().toString().equals("") || email.getText().toString()== null)
		{
			Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(password.getText().toString().equals("") || password.getText().toString() == null)
		{
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void login()
	{
		mRequestQueue = Volley.newRequestQueue(this);
		mLoginThread = new LoginThread();
		mLoginThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case R.id.login_login:
			if(check())
			{
				login();
				Intent intent = new Intent();
	    		intent.setClass(LoginActivity.this, MainActivity.class);
	    		startActivity(intent);
			}
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class LoginThread extends Thread
	{
		@Override
		public void run() {
			String url = "http://192.168.1.104:9000/" + "user/login?user_type=travo&email="
					+ email.getText().toString() + "&password=" + password.getText().toString();
			GsonRequest<UserLoginResponse> loginRequest = new GsonRequest<UserLoginResponse>(url,
					UserLoginResponse.class, null, 
					new Listener<UserLoginResponse>(){
						Message msg = new Message();
						@Override
						public void onResponse(UserLoginResponse response) {
							// TODO Auto-generated method stub
							int status = response.getRsp_code();
							switch(status)
							{
							case MyServerMessage.SUCCESS:
								Log.i("login", "登录成功");
								msg.what = MyHandlerMessage.LOGIN_SUCCESS;
								break;
							case MyServerMessage.WRONG_PASSWORD:
								msg.what = MyHandlerMessage.LOGIN_FAIL_PASSWORD;
								break;
							case MyServerMessage.MISS_EMAIL:
								msg.what = MyHandlerMessage.LOGIN_FAIL_EMAIL;
								break;
							}
							handler.sendMessage(msg);
						}
			},
			new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
				}
			});
			mRequestQueue.add(loginRequest);
			mRequestQueue.start();
		}
	}
}
