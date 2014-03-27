package com.cobra.mytravo.internet;

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
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.models.Travel;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GetMyTravelsService extends IntentService
{
	private String begin_time;
	private TravelsDataHelper mDataHelper;
	
	public GetMyTravelsService()
	{
		super("getmytravelsservice");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("GetMyTravelsService", "start");
		mDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		begin_time = intent.getStringExtra("begin_time");
		String urlstr = null;
		if(begin_time != null)
		{
			begin_time = begin_time.replace(" ", "%20");
			urlstr = AppData.HOST_IP + "travel/sync?token=" + AppData.getIdToken() 
					+ "&begin_time=" + begin_time;
		}
		else
		{
			urlstr = AppData.HOST_IP + "travel/sync?token=" + AppData.getIdToken() ;
		}
		RequestQueue queue = Volley.newRequestQueue(this);
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlstr, null, 
				new Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject response)
					{
						try
						{
							if(response.getInt("rsp_code") == MyServerMessage.SUCCESS)
							{
								List<Travel> travels = new ArrayList<Travel>();
								JSONArray jsonArray = response.getJSONArray("travels");
								for(int i = 0 ; i < jsonArray.length(); i++)
								{
									Gson gson = new Gson();
									Travel travel = gson.fromJson(jsonArray.getString(i), Travel.class);
									if(jsonArray.getJSONObject(i).getString("cover_path") != null)
									{
										Intent intent = new Intent(GetMyTravelsService.this, GetMyTravelsPicture.class);
										intent.putExtra("travel_id", travel.getId());
										startService(intent);
									}
									
									travels.add(travel);
								}
								if(travels.size() != 0)
								{
									mDataHelper.bulkInsert(travels);
								}
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

}
