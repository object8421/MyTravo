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
import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SearchTravelService extends IntentService
{
	private int progress = 0;
	private List<JSONObject> jsons;
	
	public SearchTravelService()
	{
		super("SearchTravelService");
	}

	public int getProgress() 
	{
		return progress;
	}
	
	public List<JSONObject> getJsons() {
		return jsons;
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("SearchTravelService", "start");
		
		String order = intent.getStringExtra("order");
		if(order == null)
			order = "default";
		int first_idx = intent.getIntExtra("first_idx", 1);
		int max_qty = intent.getIntExtra("max_qty", 20);
		
		String urlstr = AppData.HOST_IP + "travel/search?order=" + order + "&first_idx=" 
				+ String.valueOf(first_idx) + "&max_qty=" + String.valueOf(max_qty) ;
		
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
								Log.i("jbjbjb", "success");
								jsons = new ArrayList<JSONObject>();
								JSONArray jsonArray = response.getJSONArray("travels");
								for(int i = 0 ; i < jsonArray.length() ; i++)
								{
									JSONObject json = jsonArray.getJSONObject(i);
									jsons.add(json);
									if(json.getString("cover_path") != null)
									{
										Intent intent = new Intent(SearchTravelService.this, GetMyTravelsPicture.class);
										intent.putExtra("travel_id", json.getInt("id"));
									}
									
								}
								progress = 100;
								/*List<Travel> travels = new ArrayList<Travel>();
								JSONArray jsonArray = response.getJSONArray("travels");
								for(int i = 0 ; i < jsonArray.length(); i++)
								{
									Gson gson = new Gson();
									Travel travel = gson.fromJson(jsonArray.getString(i), Travel.class);
									if(jsonArray.getJSONObject(i).getString("cover_path") != null)
									{
										Intent intent = new Intent(SearchTravelService.this, GetMyTravelsPicture.class);
										intent.putExtra("travel_id", travel.getId());
										startService(intent);
									}
									Log.i("traveltitle", travel.getTitle());
									travels.add(travel);
								}*/
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
		return new SearchTravelBinder();
	}
	
	public class SearchTravelBinder extends Binder
	{
		public SearchTravelService getService()
		{
			return SearchTravelService.this;
		}
	}
}
