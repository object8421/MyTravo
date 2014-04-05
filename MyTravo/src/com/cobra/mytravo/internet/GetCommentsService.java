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
import com.cobra.mytravo.internet.GetFavoritTravelService.GetFavoritTravelBinder;
import com.cobra.mytravo.internet.SearchTravelService.SearchTravelBinder;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class GetCommentsService extends IntentService {

	private int progress = 0;
	private List<JSONObject> jsons;
	
	public int getProgress() 
	{
		return progress;
	}
	
	public List<JSONObject> getJsons() {
		return jsons;
	}
	
	public GetCommentsService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		int travel_id = intent.getIntExtra("travel_id", 0);
		String urlStr = AppData.HOST_IP + "travel/" + String.valueOf(travel_id) + "/comments";
		
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
								Log.i("jbjbjb", "success");
								jsons = new ArrayList<JSONObject>();
								JSONArray jsonArray = response.getJSONArray("comments");
								for(int i = 0 ; i < jsonArray.length() ; i++)
								{
									JSONObject json = jsonArray.getJSONObject(i);
									jsons.add(json);
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
		return new GetCommentsBinder();
	}
	
	public class GetCommentsBinder extends Binder
	{
		public GetCommentsService getService()
		{
			return GetCommentsService.this;
		}
	}

}
