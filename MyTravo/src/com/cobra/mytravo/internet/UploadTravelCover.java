package com.cobra.mytravo.internet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.data.UserSyncDataHelper;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.UserSync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UploadTravelCover extends IntentService
{
	private List<Travel> travels;
	private TravelsDataHelper mDataHelper;
	private NotesDataHelper notesDataHelper;
	private UserSyncDataHelper usersyncDataHelper;
	private String type;

	public UploadTravelCover()
	{
		super("uploadtravelcover");
	}
	
	private JSONObject travsferTravel(Travel travel)
	{
		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject.put("id", travel.getId());
			jsonObject.put("title", travel.getTitle());
			jsonObject.put("destination", travel.getDestination());
			jsonObject.put("begin_date", travel.getBegin_date());
			jsonObject.put("end_date", travel.getEnd_date());
			jsonObject.put("average_spend", String.valueOf(travel.getAverage_spend()));
			jsonObject.put("description", travel.getDescription());
			jsonObject.put("create_time",travel.getCreate_time());
			jsonObject.put("is_public", travel.getIs_public());
			jsonObject.put("cover", "cover" + String.valueOf(travel.getTAG()));
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
		Log.i("UploadTravelCover", "start");
		type = intent.getStringExtra("type");
		mDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		notesDataHelper = new NotesDataHelper(this, AppData.getUserId());
		usersyncDataHelper = new UserSyncDataHelper(this);
		if("dirty".equals(type))
		{
			this.travels = mDataHelper.getDirtyTravels();
		}
		if("new".equals(type))
		{
			this.travels = mDataHelper.getNewTravels();
		}
		
		String token = AppData.getIdToken();
		
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "-----WebKitFormBoundaryp7MA4YWxkTrZu0gW";
		
		try
		{
			URL url = new URL(AppData.HOST_IP + "travel/upload?token=" + token);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url  
			          .openConnection();
			httpURLConnection.setDoInput(true);  
		    httpURLConnection.setDoOutput(true);  
		    httpURLConnection.setUseCaches(false);  
		    httpURLConnection.setRequestMethod("POST");  
//		    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
//		    httpURLConnection.setRequestProperty("Charset", "UTF-8");  
		    httpURLConnection.setRequestProperty("Content-Type",  
		          "multipart/form-data;boundary=" + boundary); 	
		    
		    DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
		    
		    dos.write((twoHyphens + boundary + end).getBytes());
		    dos.write(("Content-Disposition:form-data;name=\"travels\"" + end + end).getBytes());
		    dos.write((getJsonObject().toString() + end).getBytes());
		
		    for(int i = 0 ; i < travels.size() ; i++)
		    {
		    	
		    	String uploadFile = travels.get(i).getCover_url();
		    	if(uploadFile != null)
		    	{
		    		dos.write((twoHyphens + boundary + end).getBytes());
		    		dos.write(("Content-Disposition: form-data; name=\"cover" + String.valueOf(travels.get(i).getTAG()) +
		    				"\"; filename=\""  
		    				+ uploadFile.substring(uploadFile.lastIndexOf("/") + 1)  
		    				+ "\"" + end).getBytes()); 
		    		dos.write(end.getBytes());
		    		FileInputStream fis = new FileInputStream(uploadFile);  
		    		byte[] buffer = new byte[8192]; // 8k  
		    		int count = 0;  
		    		while ((count = fis.read(buffer)) != -1)  
		    		{  
		    			dos.write(buffer, 0, count);  
		    		}  
		    		fis.close();  
		    		System.out.println("file send to server............");  
		    		dos.write(end.getBytes()); 
		    	}
		    }	
		    dos.write((twoHyphens + boundary + twoHyphens + end).getBytes()); 
		    
		    dos.flush();  
		    InputStream is = httpURLConnection.getInputStream();  
		    InputStreamReader isr = new InputStreamReader(is, "utf-8");  
		    BufferedReader br = new BufferedReader(isr);  
		    String result = br.readLine();
		    JSONObject response = new JSONObject(result);
		    int rsp_code = response.getInt("rsp_code");
		    String lm_time;
		    if(rsp_code == 100)
		    {
		    	lm_time = response.getString("lm_time");
		    	if(lm_time != null)
		    	{
		    		UserSync clientsync = usersyncDataHelper.getByToken(AppData.getIdToken());
			    	clientsync.setTravel(lm_time);
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
							for(int j = 0 ; j < travels.size() ; j++)
							{
								Travel nt = travels.get(j);
								if(nt.getTAG() == tag)
								{
									nt.setId(id);
									nt.setIs_sync(0);
									mDataHelper.update(nt);
									List<Note> notes = notesDataHelper.getNotesByTravel(nt.getCreate_time());
									for (Note note : notes)
									{
										note.setTravel_id(nt.getId());
										notesDataHelper.update(note);
									}
								}
							}
						}
					}
				}
//				Intent uploadnoteservice = new Intent(UploadTravelCover.this, UploadNoteService.class);
//				uploadnoteservice.putExtra("type", "dirty");
//				startService(uploadnoteservice);
		    }
		    dos.close();
		    is.close();
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
