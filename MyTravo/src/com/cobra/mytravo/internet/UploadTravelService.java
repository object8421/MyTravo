package com.cobra.mytravo.internet;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Travel;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

public class UploadTravelService extends IntentService
{
	private List<Travel> travels;
	private TravelsDataHelper mDataHelper;
	private String type;
	
	public UploadTravelService()
	{
		super("uploadTravelService");
	}
	
	private JSONObject travsferTravel(Travel travel)
	{
		Bitmap bitmapOrg = BitmapFactory.decodeFile(AppData.TRAVO_PATH + "/" + travel.getCover_url() + ".jpg");
		String encodeImage = null;
		if(bitmapOrg != null){
			ByteArrayOutputStream bao = new ByteArrayOutputStream();  
			bitmapOrg.compress(Bitmap.CompressFormat.PNG, 90, bao); 
			byte[] ba = bao.toByteArray();  
			String encodeImgage = Base64.encodeToString(ba, Base64.DEFAULT);
		}
		
		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject.put("title", travel.getTitle());
			jsonObject.put("destination", travel.getDestination());
			jsonObject.put("begin_date", travel.getBegin_date());
			jsonObject.put("end_date", travel.getEnd_date());
			jsonObject.put("average_spend", String.valueOf(travel.getAverage_spend()));
			jsonObject.put("description", travel.getDescription());
			jsonObject.put("create_time",travel.getCreate_time());
			jsonObject.put("is_public", travel.getIs_public());
			jsonObject.put("cover", encodeImage);
			jsonObject.put("tag", travel.getTAG());
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	private JSONObject getJsonObject()
	{
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (Travel travel : travels)
		{
			jsonArray.put(travsferTravel(travel));
		}
		try
		{
			jsonObject.put("travels", jsonArray);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("UploadTravelService", "start");
		type = intent.getStringExtra("type");
		mDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		if("dirty".equals(type))
		{
			this.travels = mDataHelper.getDirtyTravels();
		}
		if("new".equals(type))
		{
			this.travels = mDataHelper.getNewTravels();
		}
		
		JSONObject jsonRequest;
		String urlstr = AppData.HOST_IP + "travel/upload?token=" + AppData.getIdToken();
		RequestQueue queue = Volley.newRequestQueue(this);
		JsonObjectRequest request = new JsonObjectRequest(Method.POST, urlstr, getJsonObject(), 
				new Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject response)
					{
						try
						{
							int rsp_code = response.getInt("rsp_code");
							if(rsp_code == 100)
							{
								JSONArray jsonArray = response.getJSONArray("rsps");
								if(jsonArray != null && jsonArray.length() > 0)
								{
									for(int i = 0 ; i < jsonArray.length() ; i++)
									{
										JSONObject obj = jsonArray.getJSONObject(i);
										if(obj.getInt("rsp_code") == MyServerMessage.SUCCESS)
										{
											int tag,id;
											tag = obj.getInt("tag");
											id = obj.getInt("id");
											for(int j = 0 ; j < travels.size() ; j++)
											{
												Travel nt = travels.get(j);
												if(nt.getTAG() == tag)
												{
													nt.setId(id);
													nt.setIs_sync(0);
													mDataHelper.update(nt);
												}
											}
										}
									}
								}
							}
							else
							{
								Log.i("UploadTravelService", "fail");
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
					}
				}){
			@Override
			public byte[] getBody() throws AuthFailureError
			{
				return getJsonObject().toString().getBytes();
			}
		};
		queue.add(request);
	}
}
