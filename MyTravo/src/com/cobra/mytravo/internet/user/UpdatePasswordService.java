package com.cobra.mytravo.internet.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UpdatePasswordService extends IntentService
{

	public UpdatePasswordService()
	{
		super("updatepasswordService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("UpdatePasswordService", "start");
		
		URL url;
		InputStreamReader in = null;
		String result = null;
		String email = intent.getStringExtra("email");
		String oldpassword = intent.getStringExtra("oldpassword");
		String newpassword = intent.getStringExtra("newpassword");
			
		try
		{
			url = new URL(AppData.HOST_IP + "user/password/update?old_password=" + oldpassword 
					+ "&email=" + email + "&new_password=" + newpassword);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Charset", "utf-8");
			
			in = new InputStreamReader(connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(in);
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while((line = bufferedReader.readLine())!=null)
			{
				strBuffer.append(line);
			}
			result = strBuffer.toString();
			JSONObject response = new JSONObject(result);
			int rsp_code = response.getInt("rsp_code");
			
			Log.i("rsp_code", String.valueOf(rsp_code));
			
			switch(rsp_code)
			{
			case MyServerMessage.SUCCESS:
				AppData.setIdToken(response.getString("token"));
				Log.i("设置密码成功", "100");
				break;
			default:
				break;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
