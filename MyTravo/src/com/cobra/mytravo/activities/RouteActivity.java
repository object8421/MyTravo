package com.cobra.mytravo.activities;

import java.lang.annotation.Target;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

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
	
	private void setUI()
	{
		LatLng xiamen = new LatLng(24.433, 118.107);
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(xiamen, 14));
	}
	
	private void drawArrow(double x1, double y1, double x2, double y2)
	{
		double arrow_height = 10 / 1E4;
		double bottom_line = 7 / 1E4;
		double x3 = 0;
		double y3 = 0;
		double x4 = 0;
		double y4 = 0;
		double arctangent = Math.atan(bottom_line / arrow_height); // 箭头角度  
		double arrow_len = Math.sqrt(bottom_line * bottom_line + arrow_height * arrow_height);
		double[] endPoint_1 = rotateVec(x2 - x1, y2 - y1, arctangent,   
                arrow_len); 
		double[] endPoint_2 = rotateVec(x2 - x1, y2 - y1, -arctangent,  
                arrow_len);  
		double x_3 = x2 - endPoint_1[0];
		double y_3 = y2 - endPoint_1[1];    
        double x_4 = x2 - endPoint_2[0]; 
        double y_4 = y2 - endPoint_2[1];    
        /*Double X3 = new Double(x_3);  
        x3 = X3.intValue();    
        Double Y3 = new Double(y_3);    
        y3 = Y3.intValue();    
        Double X4 = new Double(x_4);    
        x4 = X4.intValue();    
        Double Y4 = new Double(y_4);    
        y4 = Y4.intValue();  */
        
        DrawOnMap drawOnMap = new DrawOnMap(this, map);
        drawOnMap.addPolyline(new LatLng(x1, y1), new LatLng(x2, y2));
        drawOnMap.addPolyline(new LatLng(x2, y2), new LatLng(x_3, y_3));
        drawOnMap.addPolyline(new LatLng(x2, y2), new LatLng(x_4, y_4));
	}
	
	private double[] rotateVec(double px, double py, double ang, double newlen)
	{
		double rotateResult[] = new double[2];
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		
		double d = Math.sqrt(vx * vx + vy * vy);
		
		vx = vx/d * newlen;
		vy = vy/d * newlen;
		
		rotateResult[0] = vx;
		rotateResult[1] = vy;
		
		Log.i("vx", String.valueOf(vx));
		Log.i("vy", String.valueOf(vy));
		return rotateResult;
	}
	
	private void formRoute()
	{
		setUI();
		List<LatLng> latlngs = new ArrayList<LatLng>();
		for (Note note : notes)
		{ 
			latlngs.add(getLatLngFromNote(note));
		}
	/*	if(latlngs.size() != 0)
		{
			DrawOnMap drawOnMap = new DrawOnMap(this, map);
			drawOnMap.addPolyline((ArrayList<LatLng>) latlngs);
		}*/
		if(latlngs.size() >= 2)
		{
			for(int i = 0 ; i < latlngs.size() - 1 ; i++)
			{
				drawArrow(latlngs.get(i).latitude , latlngs.get(i).longitude, 
						latlngs.get(i+1).latitude , latlngs.get(i+1).longitude);
			}
		}
		for(int i = 0 ; i < latlngs.size() ; i++)
		{
			map.addMarker(new MarkerOptions().position(latlngs.get(i)).title(notes.get(i).getContent()));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.route, menu);
		return true;
	}

}
