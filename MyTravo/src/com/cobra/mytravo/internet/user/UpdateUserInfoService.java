package com.cobra.mytravo.internet.user;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.models.UserInfo;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UpdateUserInfoService extends IntentService
{

	public UpdateUserInfoService()
	{
		super("updateuserinfoservice");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("UpdateUserInfoService", "start");
		
		URL url;
		InputStreamReader in = null;
		String result = null;
		UserInfo userinfo = (UserInfo) intent.getSerializableExtra("userinfo");
			
		try
		{
			url = new URL(AppData.HOST_IP + "user/info/update?token=" + AppData.getIdToken());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Charset", "utf-8");
			
			DataOutputStream dop = new DataOutputStream(connection.getOutputStream());
			dop.writeBytes(new Gson().toJson(userinfo));
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
			result = strBuffer.toString();
			JSONObject response = new JSONObject(result);
			int rsp_code = response.getInt("rsp_code");
			
			Log.i("rsp_code", String.valueOf(rsp_code));
			
			switch(rsp_code)
			{
			case MyServerMessage.SUCCESS:
				Log.i("更新用户详细信息成功", "100");
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
