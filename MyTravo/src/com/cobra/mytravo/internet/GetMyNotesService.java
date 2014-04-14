package com.cobra.mytravo.internet;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GetMyNotesService extends IntentService
{
	private String begin_time;
	private NotesDataHelper notesDataHelper;
	private TravelsDataHelper travelsDataHelper;
	
	public GetMyNotesService()
	{
		super("getMyNotesService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("getMyNotesService", "start");
		notesDataHelper = new NotesDataHelper(this, AppData.getUserId());
		travelsDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		
		begin_time = intent.getStringExtra("begin_time");
		String urlStr = null;
		if(begin_time != null)
		{
			begin_time = begin_time.replace(" ", "%20");
			urlStr = AppData.HOST_IP + "note/sync?token=" + AppData.getIdToken() 
					+ "&begin_time=" + begin_time;
		}else 
		{
			urlStr = AppData.HOST_IP + "note/sync?token=" + AppData.getIdToken();
		}
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
								List<Note> notes = new ArrayList<Note>();
								JSONArray jsonArray = response.getJSONArray("notes");
								for(int i = 0 ; i < jsonArray.length(); i++)
								{
									Gson gson = new Gson();
									Note note = gson.fromJson(jsonArray.getString(i), Note.class);
									
									if(jsonArray.getJSONObject(i).getString("image_path") != null)
									{
										Intent intent = new Intent(GetMyNotesService.this, GetMyNotePicture.class);
										intent.putExtra("note_id", note.getId());
										intent.putExtra("image_path", note.getImage_path());
										startService(intent);
									}
									
									notes.add(note);
								}
								if(notes.size() != 0)
								{
									notesDataHelper.bulkInsert(notes);
								}
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
