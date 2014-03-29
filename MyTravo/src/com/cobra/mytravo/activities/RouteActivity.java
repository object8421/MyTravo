package com.cobra.mytravo.activities;

import java.util.ArrayList;
import java.util.List;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.models.MyLocation;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.util.DrawOnMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

public class RouteActivity extends FragmentActivity
{
	private GoogleMap map;
	private NotesDataHelper notesHelper;
	private List<Note> notes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_route);
		
		map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.routemap)).getMap();
		Intent intent = this.getIntent();
		String travel_created_time = intent.getStringExtra("travel_created_time");
		notesHelper = new NotesDataHelper(this, AppData.getUserId());
		notes = notesHelper.getNotesByTravel(travel_created_time);
		formRoute();
	}
	
	private LatLng getLatLngFromNote(Note note)
	{
		MyLocation mylocation = note.getLocation();
		//Log.i("latlng", mylocation.getLatitude() + mylocation.getLongitude());
		return new LatLng(mylocation.getLatitude(), 
				mylocation.getLongitude());
	}
	
	private void formRoute()
	{
		List<LatLng> latlngs = new ArrayList<LatLng>();
		for (Note note : notes)
		{
			latlngs.add(getLatLngFromNote(note));
		}
		if(latlngs.size() != 0)
		{
			DrawOnMap drawOnMap = new DrawOnMap(this, map);
			drawOnMap.addPolyline((ArrayList<LatLng>) latlngs);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.route, menu);
		return true;
	}

}
