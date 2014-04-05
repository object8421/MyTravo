package com.cobra.mytravo.internet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CommentTravelService extends IntentService {

	public CommentTravelService(String name) {
		super(name);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		URL url = null;
		InputStreamReader in = null;
		String result = null;
		int travel_id = intent.getIntExtra("travel_id", 0);
		String content = intent.getStringExtra("content");
		String way = intent.getStringExtra("way");
			
		try
		{
			url = new URL(AppData.HOST_IP + "travel/" + String.valueOf(travel_id) + "/comment?token=" + AppData.getIdToken());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Charset", "utf-8");
			
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write(content.getBytes());
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
				Log.i("成功", "100");
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
