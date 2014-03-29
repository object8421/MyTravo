package com.cobra.mytravo.internet;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.UserSyncDataHelper;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.UserSync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SyncService extends IntentService
{
	private String token;
	private UserSyncDataHelper usersyncDataHelper;
	private UserSync usersync;

	public SyncService()
	{
		super("sync");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("SyncService", "start");
		token = intent.getStringExtra("token");
		usersyncDataHelper = new UserSyncDataHelper(this);
		usersync = new UserSync(token);

		String urlstr = AppData.HOST_IP + "sync?token=" + token;
		RequestQueue queue = Volley.newRequestQueue(this);
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlstr,
				null, new Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject response)
					{
						try
						{
							if (response.getInt("rsp_code") == MyServerMessage.SUCCESS)
							{
								usersync.setTravel(response.getString("travel"));
								usersync.setNote(response.getString("note"));

								//客户端同步对象
								UserSync clientsync = usersyncDataHelper
										.getByToken(token);

								/*clientsync为null说明该用户在该移动设备中第一次同步
								 * 首先从服务器中获取该用户的所有数据，再把该设备中新的travel，note
								 * 上传至服务器，完成同步
								 */
								if (clientsync == null)
								{
									//获取travel
									Intent gettravel = new Intent(
											SyncService.this,
											GetMyTravelsService.class);
									startService(gettravel);
									
									//上传新travel
									Intent uploadnewtravel = new Intent(
											SyncService.this,
											UploadTravelCover.class);
									uploadnewtravel.putExtra("type", "new");
									startService(uploadnewtravel);
									
									//获取note
									Intent getnote = new Intent(SyncService.this,
											GetMyNotesService.class);
									startService(getnote);
									
									//上传新note
									Intent uploadnewnote = new Intent(
											SyncService.this,
											UploadNoteService.class);
									uploadnewnote.putExtra("type", "new");
									startService(uploadnewnote);

									//插入本地数据库内该用户的UserSync实体
									clientsync = new UserSync(token);
									clientsync.setTravel(usersync.getTravel());
									clientsync.setNote(usersync.getNote());
									usersyncDataHelper.insert(clientsync);
								} 
								else	//该用户曾在该设备上与服务器同步过
								{	
									//服务器中有更新的travel版本
									if ( usersync.getTravel().compareTo(
													clientsync.getTravel()) > 0)
									{
										Intent gettravel = new Intent(
												SyncService.this,
												GetMyTravelsService.class);
										gettravel.putExtra("begin_time",
												clientsync.getTravel());
										startService(gettravel);
										
										Intent uploadnewtravel = new Intent(
												SyncService.this,
												UploadTravelCover.class);
										uploadnewtravel.putExtra("type", "new");
										startService(uploadnewtravel);
										
										clientsync.setTravel(usersync.getTravel());
										usersyncDataHelper.update(clientsync);
									}
									else//客户端中有更新的版本或同一版本
									{
										Intent uploaddirtytravel = new Intent(
												SyncService.this,
												UploadTravelCover.class);
										uploaddirtytravel.putExtra("type", "dirty");
										startService(uploaddirtytravel);
									}
									
									//服务器中有更新的note版本
									if(usersync.getNote().compareTo(clientsync.getNote())>0)
									{
										Intent getnote = new Intent(
												SyncService.this,
												GetMyNotesService.class);
										getnote.putExtra("begin_time",
												clientsync.getTravel());
										startService(getnote);
										
										Intent uploadnewnote = new Intent(
												SyncService.this,
												UploadNoteService.class);
										uploadnewnote.putExtra("type", "new");
										startService(uploadnewnote);
										
										clientsync.setNote(usersync.getNote());
										usersyncDataHelper.update(clientsync);
									}
									else
									{
										Intent uploaddirtynote = new Intent(
												SyncService.this,
												UploadNoteService.class);
										uploaddirtynote.putExtra("type", "dirty");
										startService(uploaddirtynote);
									}
									/*
									//客户端中有更新的travel版本
									if(usersync.getTravel().compareTo(clientsync.getTravel()) <= 0)
									{
										Intent uploaddirtytravel = new Intent(SyncService.this,
												UploadTravelCover.class);
										uploaddirtytravel.putExtra("type", "dirty");
										startService(uploaddirtytravel);
										
										clientsync.setTravel(TimeUtils.getTime().toString());
										usersyncDataHelper.update(clientsync);
									}
									
									//客户端中有更新的note版本
									if(usersync.getNote().compareTo(clientsync.getNote()) <= 0)
									{
										Intent uploaddirtynote = new Intent(SyncService.this,
												UploadNoteService.class);
										uploaddirtynote.putExtra("type", "dirty");
										startService(uploaddirtynote);
										
										clientsync.setNote(TimeUtils.getTime().toString());
										usersyncDataHelper.update(clientsync);
									}*/
								}
							}
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
				}, new ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError arg0)
					{
						Log.i("syncrequesterror", arg0.getMessage());
					}
				});
		queue.add(request);
	}

}
