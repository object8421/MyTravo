package com.cobra.mytravo.internet.user;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UpdateUserService extends IntentService
{

	public UpdateUserService()
	{
		super("UpdateUserInfoService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("UpdateUserService", "start");
		
		URL url;
		InputStreamReader in = null;
		String result = null;
		String nickname = intent.getStringExtra("nickname");
		if(nickname != null)
		{
			nickname = nickname.replace(" ", "%20");
		}
		String signature = intent.getStringExtra("signature");
		if(signature != null)
		{
			signature = signature.replace(" ", "%20");
		}
				
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "-----WebKitFormBoundaryp7MA4YWxkTrZu0gW";
		try
		{
			url = new URL(AppData.HOST_IP + "user/update?token=" + AppData.getIdToken() 
					+ "&nickname=" + nickname + "&signature=" + signature);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			connection.setRequestProperty("Charset", "utf-8");
			
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write((twoHyphens + boundary + end).getBytes());
			String uploadfile = AppData.TRAVO_PATH + "/headshot.jpg";
			dos.write(("Content-Disposition:form-data;name=\"face\"; filename=\"" + 
					uploadfile.substring(uploadfile.lastIndexOf("/") + 1) + "\"" 
					+ end).getBytes());
			dos.write(end.getBytes());
			
			FileInputStream fis = new FileInputStream(uploadfile);
			byte[] buf = new byte[8192];
			int count = 0;
			while((count = fis.read(buf))!=-1)
			{
				dos.write(buf, 0, count);
			}
			fis.close();
			System.out.println("file send to server............");  
		    dos.write(end.getBytes()); 
			
		    dos.write((twoHyphens + boundary + twoHyphens + end).getBytes());
			dos.flush();
			
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
				Log.i("修改用户信息成功", "100");
				break;
			default:
				Log.i("有情况", String.valueOf(rsp_code));
				break;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
