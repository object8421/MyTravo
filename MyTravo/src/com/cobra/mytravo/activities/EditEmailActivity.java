package com.cobra.mytravo.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.MyServerMessage;
import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditEmailActivity extends Activity
{
	private EditText email;
	private EditText password;
	private Button save;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case MyHandlerMessage.UPDATE_USER_INFO_SUCCESS:
				AppData.setEmail(email.getText().toString());
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("email", email.getText().toString());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				EditEmailActivity.this.finish();
			}
				
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_email);
		email = (EditText) findViewById(R.id.edit_email_email);
		password = (EditText) findViewById(R.id.edit_email_password);
		save = (Button) findViewById(R.id.edit_email_save);
		save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				new EditEmailThread().start();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.edit_email, menu);
		return true;
	}

	private class EditEmailThread extends Thread
	{
		@Override
		public void run()
		{
			int rsp_code;
//			String jsonToPut = "token=" + AppData.getIdToken() + ", email=" 
//								+ email.getText().toString() 
//								+ ", password=" + password.getText().toString() + "}";
			Map<String, String> map = new HashMap<String, String>();
			map.put("email", email.getText().toString());
			map.put("password", password.getText().toString());
			JSONObject jsonObject = new JSONObject(map);
			String jsonToPut = jsonObject.toString();
			
			String urlstr = AppData.HOST_IP + "user/email/update?token=" + AppData.getIdToken();
			Message msg = new Message();
			HttpURLConnection connection = null;
			InputStreamReader in = null;
			
			URL url;
			try
			{
				url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Charset", "utf-8");
				DataOutputStream dop = new DataOutputStream(connection.getOutputStream());
				dop.writeBytes(jsonToPut);
				dop.flush();
				dop.close();
				
				in = new InputStreamReader(connection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(in);
				StringBuffer strBuffer = new StringBuffer();
				String line = null;
				while((line = bufferedReader.readLine())!=null)
				{
					strBuffer.append(line);
				}
				JSONObject obj = new JSONObject(line);
				rsp_code = obj.getInt("rsp_code");
				if(rsp_code == MyServerMessage.SUCCESS)
				{
					msg.what = MyHandlerMessage.UPDATE_USER_INFO_SUCCESS;
				}
				handler.sendMessage(msg);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}finally{
				if(connection != null)
				{
					connection.disconnect();
				}
				if(in!=null)
				{
					try
					{
						in.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
