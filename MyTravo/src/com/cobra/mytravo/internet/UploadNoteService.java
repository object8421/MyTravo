package com.cobra.mytravo.internet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.data.UserSyncDataHelper;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.UserSync;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UploadNoteService extends IntentService
{
	private List<Note> notes;
	private NotesDataHelper mDataHelper;
	private TravelsDataHelper travelsDataHelper;
	private UserSyncDataHelper usersyncDataHelper;
	private String type;

	public UploadNoteService()
	{
		super("uploadnoteservice");
	}
	
	private JSONObject travsferNote(Note note)
	{
		JSONObject jsonObject = new JSONObject();
		Travel travel = travelsDataHelper.queryByTime(note.getTravel_created_time());
		note.setTravel_id(travel.getId());
		mDataHelper.update(note);
		try
		{
			jsonObject.put("id", note.getId());
			jsonObject.put("content", note.getDescription());
			jsonObject.put("create_time",note.getCreate_time());
			jsonObject.put("travel_id", note.getTravel_id());
			jsonObject.put("tag", note.getTAG());
			jsonObject.put("location", new JSONObject(new Gson().toJson(note.getLocation())));
			jsonObject.put("image", "image" + String.valueOf(note.getTAG()));
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
		for (Note note : notes)
		{
			jsonArray.put(travsferNote(note));
		}
		try
		{
			jsonObject.put("notes", jsonArray);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject;
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("UploadNoteService", "start");
		type = intent.getStringExtra("type");
		mDataHelper = new NotesDataHelper(this, AppData.getUserId());
		travelsDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		usersyncDataHelper = new UserSyncDataHelper(this);
		if("dirty".equals(type))
		{
			this.notes = mDataHelper.getDirtyNotes();
		}
		if("new".equals(type))
		{
			this.notes = mDataHelper.getNewNotes();
		}
		
		JSONObject jsonRequest;
		
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "-----WebKitFormBoundaryp7MA4YWxkTrZu0gW";
		
		try
		{
			URL url = new URL(AppData.HOST_IP + "note/upload?token=" + AppData.getIdToken());
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
//			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
//			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			
			dos.write((twoHyphens + boundary + end).getBytes());
			dos.write(("Content-Disposition:form-data;name=\"notes\"" + end + end).getBytes());
			dos.write(getJsonObject().toString().getBytes());
			
			for(int i = 0 ; i < notes.size() ; i++)
			{
				String uploadfile = notes.get(i).getImage_url();
				if(uploadfile != null)
				{
					dos.write((twoHyphens + boundary + end).getBytes());
					dos.write(("Content-Disposition:form-data;name=\"image" + 
							String.valueOf(notes.get(i).getTAG()) + "\"; filename=\"" + 
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
				}
			}
			dos.write((twoHyphens + boundary + twoHyphens + end).getBytes());
			dos.flush();
			    
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			JSONObject response = new JSONObject(result);
			int rsp_code = response.getInt("rsp_code");
			String lm_time ;
			if(rsp_code == 100)
			{
				lm_time = response.getString("lm_time");
				if(lm_time != null)
				{
					UserSync clientsync = usersyncDataHelper.getByToken(AppData.getIdToken());
			    	clientsync.setNote(lm_time);
			    	usersyncDataHelper.update(clientsync);
				}
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
							for(int j = 0 ; j < notes.size() ; j++)
							{
								Note nt = notes.get(j);
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
