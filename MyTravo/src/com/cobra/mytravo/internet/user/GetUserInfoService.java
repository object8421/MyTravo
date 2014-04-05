package com.cobra.mytravo.internet.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.internet.GetMyNotePicture;
import com.cobra.mytravo.internet.GetMyNotesService;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.UserInfo;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GetUserInfoService extends IntentService
{

	public GetUserInfoService()
	{
		super("GetUserInfoService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("GetUserInfoService", "start");
		
		int user_id = intent.getIntExtra("user_id", 0);
		
		String urlStr = AppData.HOST_IP + "user/" + String.valueOf(user_id) + "/info?token=" + AppData.getIdToken();
		
		RequestQueue queue = Volley.newRequestQueue(this);
		JsonObjectRequest request =  new JsonObjectRequest(Method.GET, urlStr, null, 
				new Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response)
					{
						try
						{
							if(response.getInt("rsp_code") == MyServerMessage.SUCCESS)
							{
								UserInfo userinfo = new Gson().fromJson(response.getJSONObject("user_info").toString()
										,UserInfo.class);
								Log.i("name", userinfo.getName());
							}
						}catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
		}, new ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error)
			{
				error.printStackTrace();
				Log.i("GetMyNotesService", "error");
			}
		});
		queue.add(request);
	}

}
