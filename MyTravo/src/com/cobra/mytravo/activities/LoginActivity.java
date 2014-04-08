package com.cobra.mytravo.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.UserSyncDataHelper;
import com.cobra.mytravo.data.UsersDataHelper;
import com.cobra.mytravo.internet.SyncService;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.models.UserSync;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	public static final String APP_ID = "100520856";
	
	private RequestQueue mRequestQueue;
	private LoginThread mLoginThread;
	
	private TextView registerTextView;
	private EditText email;
	private EditText password;
	private Button loginbyqq;
	private Button loginbysina;
	
	private String user_type = "travo";
	private User user;
	
	String access_token = null;
	
	SsoHandler mSsoHandler = null;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case MyHandlerMessage.LOGIN_SUCCESS:
				Intent syncService = new Intent(LoginActivity.this, SyncService.class);
				syncService.putExtra("token", user.getToken());
				startService(syncService);
				Toast.makeText(LoginActivity.this, "您已成功登陆", Toast.LENGTH_SHORT).show();
				AppData.setIsLogin(true);
				Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent1);
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
			case MyHandlerMessage.LOGIN_NO_SUCH_USRE:
				Toast.makeText(LoginActivity.this, "no such user", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				intent.putExtra("user_type", user_type);
				intent.putExtra("access_token", access_token);
				startActivity(intent);
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
		registerTextView = (TextView) findViewById(R.id.login_register);
		registerTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
				registerIntent.putExtra("user_type", "travo");
				startActivity(registerIntent);
			}
		});
		email = (EditText)findViewById(R.id.login_email);
		password = (EditText)findViewById(R.id.login_password);
		loginbyqq = (Button)findViewById(R.id.login_by_qq);
		loginbyqq.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Tencent mTencent = Tencent.createInstance(AppData.QQ_KEY, LoginActivity.this);
				mTencent.login(LoginActivity.this,"get_user_info" , new BaseUiListener());
			}
		});
		loginbysina = (Button)findViewById(R.id.login_by_sina);
		loginbysina.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				WeiboAuth mWeiboAuth = new WeiboAuth(LoginActivity.this, 
						AppData.WEIBO_KEY, AppData.WEIBO_REDIRECT_URL, AppData.WEIBO_SCOPE);
//				mWeiboAuth.anthorize(new MyWeiboAuthListener());
				mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
				mSsoHandler.authorize(new MyWeiboAuthListener());
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(mSsoHandler != null)
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
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
	
	private void save_user(User user)
	{
		AppData.setIdToken(user.getToken());
		AppData.setIsLogin(true);
		AppData.setNickname(user.getNickname());
		AppData.setUserId(user.getId());
		AppData.setEmail(user.getEmail());
	}
	
	private void login()
	{
		mRequestQueue = Volley.newRequestQueue(this);
		mLoginThread = new LoginThread();
		mLoginThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.login_login:
			if(check())
			{
				login();
				
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class LoginThread extends Thread
	{
		@Override
		public void run() {
			Message msg = new Message();
			String url = null;
			if("travo".equals(user_type))
			{
				url = AppData.HOST_IP + "user/login?user_type=travo&email="
						+ email.getText().toString() + "&password=" + password.getText().toString();
			}
			else if("qq".equals(user_type))
			{
				url = AppData.HOST_IP + "user/login?user_type=qq&qq_token=" + access_token;
			}
			else if("sina".equals(user_type))
			{
				url = AppData.HOST_IP + "user/login?user_type=sina&sina_token=" + access_token;
			}
			
			BufferedReader reader = null;
			String result;
			try
			{
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(url));
				HttpResponse response = client.execute(request);
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer strBuffer = new StringBuffer("");
				String line = null;
				while((line = reader.readLine()) != null)
				{
					strBuffer.append(line);
				}
				result = strBuffer.toString();
				JSONObject jsonObject = new JSONObject(result);
				int rsp_code = jsonObject.getInt("rsp_code");
				switch(rsp_code)
				{
				case MyServerMessage.SUCCESS:
					AppData.setIsLogin(true);
					Gson gson = new Gson();
					user = gson.fromJson(jsonObject.getJSONObject("user").toString() , User.class);
					save_user(user);
					msg.what = MyHandlerMessage.LOGIN_SUCCESS;
					break;
				case MyServerMessage.NO_SUCH_USER:
					msg.what = MyHandlerMessage.LOGIN_NO_SUCH_USRE;
					break;
				}
				handler.sendMessage(msg);
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally{
				if(reader != null)
				{
					try
					{
						reader.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					reader = null;
				}
			}
			
		}
	}
	
	private class MyWeiboAuthListener implements WeiboAuthListener
	{

		@Override
		public void onCancel()
		{
			Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Bundle values)
		{
			Toast.makeText(LoginActivity.this, "complete", Toast.LENGTH_LONG).show();
			Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			
			if(mAccessToken.isSessionValid())
			{
				access_token = mAccessToken.getToken();
				Log.i("qq_token", access_token);
				login();
			}
			else
			{
				access_token = "";
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0)
		{
			Toast.makeText(LoginActivity.this, "exception", Toast.LENGTH_LONG).show();
		}
	}
	
	private class BaseUiListener implements IUiListener
	{
		@Override
		public void onCancel()
		{
			Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_LONG).show();
		}
		
		protected void doComplete(JSONObject values) {
			try
			{
				access_token = (String) values.get("access_token");
				user_type = "qq";
				Log.i("access_token", access_token);
				login();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		@Override
		public void onError(UiError arg0)
		{
			Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Object arg0)
		{
			Toast.makeText(LoginActivity.this, "complete", Toast.LENGTH_LONG).show();
			doComplete((JSONObject)arg0);
		}
	}
}
