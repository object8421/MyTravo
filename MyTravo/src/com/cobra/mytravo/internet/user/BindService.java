package com.cobra.mytravo.internet.user;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BindService extends IntentService
{

	public BindService()
	{
		super("bindthirdpartyaccount");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("BindService", "start");
		
		URL url;																		
		InputStreamReader in = null;													
		String result = null;															
		try																				
		{
			url = new URL(AppData.HOST_IP + "user/bind?token=" + AppData.getIdToken() 
					+ "&qq_token=" + AppData.getQQIdToken());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");	
			connection.setRequestProperty("Charset", "utf-8");
			
//			DataOutputStream dop = new DataOutputStream(connection.getOutputStream());
//			dop.writeBytes("");
			
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
				Log.i("绑定成功", "100");
//				Toast.makeText(this, "绑定成功", Toast.LENGTH_LONG).show();
				break;
			case MyServerMessage.DUP_BIND:
				Log.i("该账号已被关联", "204");
//				Toast.makeText(this, "该账号已被关联", Toast.LENGTH_LONG).show();
				break;
			default:
//				Toast.makeText(this, "其他情况", Toast.LENGTH_LONG).show();
				break;
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
