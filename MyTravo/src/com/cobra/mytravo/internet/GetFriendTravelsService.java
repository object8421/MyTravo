package com.cobra.mytravo.internet;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.models.Travel;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class GetFriendTravelsService extends IntentService {
	
	private int progress = 0;
	private List<Travel> travels;
	
	public int getProgress() {
		return progress;
	}

	public List<Travel> getTravels() {
		return travels;
	}

	public GetFriendTravelsService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("GetFriendTravelsService", "start");
		
		int user_id = intent.getIntExtra("user_id", 0);
		
		String urlStr = AppData.HOST_IP + "friend/" + String.valueOf(user_id) + "/travel?token=" 
				+ AppData.getIdToken();	
		RequestQueue queue = Volley.newRequestQueue(this);
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlStr, null, 
				new Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject response)
					{
						try
						{
							if(response.getInt("rsp_code") == MyServerMessage.SUCCESS)
							{
								travels = new ArrayList<Travel>();
								JSONArray jsonArray = response.getJSONArray("travels");
								for(int i = 0 ; i < jsonArray.length(); i++)
								{
									Gson gson = new Gson();
									Travel travel = gson.fromJson(jsonArray.getString(i), Travel.class);
									if(jsonArray.getJSONObject(i).getString("cover_path") != null)
									{
										Intent intent = new Intent(GetFriendTravelsService.this, GetMyTravelsPicture.class);
										intent.putExtra("travel_id", travel.getId());
										startService(intent);
									}
									travels.add(travel);
								}
								progress = 100;
							}
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
				}, 
				new ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError arg0)
					{
						Log.i("error", "error");
					}
				});
		queue.add(request);
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return new GetFriendTravelsBinder();
	}
	
	public class GetFriendTravelsBinder extends Binder
	{
		public GetFriendTravelsService getService()
		{
			return GetFriendTravelsService.this;
		}
	}
}
