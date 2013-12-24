package com.cobra.mytravo.activities;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.models.MyHandlerMessage;
import com.cobra.mytravo.models.MyServerMessage;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.models.User.UserRegisterResponse;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private EditText nickname;
	private EditText email;
	private EditText password;
	private EditText repassword;
	
	private RequestQueue mRequestQueue;
	private RegisterThread registerThread;
	public User user;
	private Map<String, String> map;
	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case MyHandlerMessage.REGISTER_SUCCESS:
				Toast.makeText(RegisterActivity.this, "恭喜您， 注册成功！", Toast.LENGTH_SHORT).show();
				break;
			case MyHandlerMessage.REGISTER_FAIL_SERVER:
				
				Toast.makeText(RegisterActivity.this, "Oops!服务器出错啦!", 
		        		   Toast.LENGTH_SHORT).show();
				break;
			case MyHandlerMessage.REGISTER_FAIL_EMAIL:
				
				Toast.makeText(RegisterActivity.this, "该邮箱已被注册，请重新输入邮箱!", 
		        		   Toast.LENGTH_SHORT).show();
				break;
			case MyHandlerMessage.REGISTER_FAIL_NICKNAME:
				
				Toast.makeText(RegisterActivity.this, "该昵称已被注册，请重新输入昵称!", 
		        		   Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(RegisterActivity.this, "Oops!服务器出错啦!", 
	        		   Toast.LENGTH_SHORT).show();
			break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		nickname = (EditText)findViewById(R.id.register_nickname);
		email = (EditText)findViewById(R.id.register_email);
		password = (EditText)findViewById(R.id.register_password);
		repassword = (EditText)findViewById(R.id.register_pwdconfirm);
	}
	
	private boolean check()
	{
		
		if(email.getText().toString().equals("") || email.getText().toString()== null)
		{
			Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(nickname.getText().toString().equals("") || nickname.getText().toString()== null)
		{
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(password.getText().toString().equals("") || password.getText().toString() == null)
		{
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!password.getText().toString().equals(repassword.getText().toString()))
		{
			Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void register()
	{
		user = new User();
		user.setEmail(email.getText().toString());
		user.setNickname(nickname.getText().toString());
		user.setPassword(password.getText().toString());
		map = new HashMap<String, String>();
		map.put("nickname", user.getNickname());
		map.put("password", user.getPassword());
		map.put("email", user.getEmail());
		mRequestQueue = Volley.newRequestQueue(this);
		registerThread = new RegisterThread();
		registerThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
    	case R.id.register_register:
    		if(check())
    		{
    			register();
        		Intent intent = new Intent();
        		intent.setClass(RegisterActivity.this, LoginActivity.class);
        		startActivity(intent);
    		}
    		break;
    	}
		
		return super.onOptionsItemSelected(item);
	}

	private class RegisterThread extends Thread {
		@Override
		public void run() {
			String uri = "http://192.168.1.104:9000/" + "user/register?user_type=travo";
			mRequestQueue.add(new GsonRequest<UserRegisterResponse>(uri,
					UserRegisterResponse.class, null,
					new Listener<UserRegisterResponse>() {
						Message msg = new Message();
						@Override
						public void onResponse(UserRegisterResponse response) {
							Log.i("register_response12", String.valueOf(response.getUser_id()));
							int status = response.getRsp_code();
							switch(status)
							{
							case MyServerMessage.SUCCESS:
								msg.what = MyHandlerMessage.REGISTER_SUCCESS;
								break;
							case MyServerMessage.WRONG_ARG:
								msg.what = MyHandlerMessage.REGISTER_FAIL_SERVER;
								break;
							case MyServerMessage.DUP_EMAIL:
								msg.what = MyHandlerMessage.REGISTER_FAIL_EMAIL;
								break;
							case MyServerMessage.DUP_NICKNAME:
								msg.what = MyHandlerMessage.REGISTER_FAIL_NICKNAME;
								break;
							default:
								msg.what = MyHandlerMessage.REGISTER_FAIL_SERVER;
								break;
							}
							handler.sendMessage(msg);
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i("register_response13", error.getMessage());
						}
					}, map));
			mRequestQueue.start();
		}
	}
}
