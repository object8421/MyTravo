package com.cobra.mytravo.util;

import java.io.IOException;

import org.json.JSONException;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.activities.AddNoteActivity;
import com.cobra.mytravo.models.MyLocation;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AroundPlaceActivity extends Activity
{
	String resultString;
	
	private ListView aroundList = null;
	private EditText input;
	
	public boolean near = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around_place);
		
		near = true;
		aroundList = (ListView)findViewById(R.id.around_list);
		aroundList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				try
				{
					Place place = null;
					if(near)
						place = Tools.fromJsonToAroundPlaces(resultString).get(position);
					else
						place = Tools.fromJsonToSearchPlaces(resultString).get(position);
					String name = place.getName();
					String lat = place.getLatitude();
					String lng = place.getLongitude();
					MyLocation mylocation = new MyLocation();
					mylocation.setLatitude(lat);
					mylocation.setLongitude(lng);
					mylocation.setNameString(name);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();  
					bundle.putSerializable("location", mylocation);
					intent.putExtras(bundle);   
					setResult(RESULT_OK, intent);
					finish();
				} catch (JSONException e)				
				{			
					e.printStackTrace();			
				}					
			}						
		});		
		input = (EditText) findViewById(R.id.place_input);
		Button search = (Button) findViewById(R.id.place_search);
		search.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				near = false;
				search(input.getText().toString());
			}
		});
		startService(new Intent(this, GpsService.class));
		nearbyPlaces();	
	}								
	
	@Override						
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.around_place, menu);
		return true;
	}
	
	@Override
	protected void onDestroy()
	{
		stopService(new Intent(this, GpsService.class));
		super.onDestroy();
	}
	
	public void nearbyPlaces()
	{
		new GetMessageFromServer().execute("2000");
	}
	
	public void search(String text)
	{
		new GetTextSearchResultFromServer().execute(text);
	}
	
	private class GetMessageFromServer extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... parameters)
		{
			try
			{
				resultString = MapsHttpUtil.getAroundPlace(
						Tools.getLocation(getApplicationContext()),
						parameters[0]);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			Log.i("resultString", resultString);
			return resultString;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			AroundListAdapter aroundListAdapter;
			try
			{
				aroundListAdapter = new AroundListAdapter(AroundPlaceActivity.this, Tools.fromJsonToAroundPlaces(result));
				aroundList.setAdapter(aroundListAdapter);
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
	}
	
	
	private class GetTextSearchResultFromServer extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... parameters)
		{
			try
			{
				resultString = MapsHttpUtil.getTextSearchPlaces(Tools.getLocation(getApplicationContext()), "10000" ,parameters[0]);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			Log.i("resultString", resultString);
			return resultString;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			AroundListAdapter aroundListAdapter;
			try
			{
				aroundListAdapter = new AroundListAdapter(AroundPlaceActivity.this, Tools.fromJsonToSearchPlaces(result));
				aroundList.setAdapter(aroundListAdapter);
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
	}
	
}
