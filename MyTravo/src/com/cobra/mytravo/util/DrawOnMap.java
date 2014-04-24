package com.cobra.mytravo.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cobra.mytravo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawOnMap
{
	private GoogleMap map;
	private Activity activity;
	
	public DrawOnMap(Activity activity, GoogleMap map)
	{
		this.activity = activity;
//		map.setInfoWindowAdapter(new MyInfoWindowAdapter());
		map.setOnInfoWindowClickListener(null);
		this.map = map;
	}
	
	public Marker addMarker(LatLng latlng, String title)
	{
		return map.addMarker(new MarkerOptions().position(latlng).title(title));
	}
	
	public Marker addMarker(LatLng latlng, String title, String snippet, int icon)
	{
		return map.addMarker(new MarkerOptions().position(latlng).title(title)
				.snippet(snippet).icon(BitmapDescriptorFactory.fromResource(icon)));
	}
	
	public Polyline addPolyline(ArrayList<LatLng> list)
	{
		PolylineOptions options = new PolylineOptions().addAll(list);
		Polyline polyline = map.addPolyline(options);
		return polyline;
	}
	
	public Polyline addPolyline(LatLng ...latLngs)
	{
		PolylineOptions options = new PolylineOptions().add(latLngs);
		Polyline polyline = map.addPolyline(options);
		polyline.setColor(Color.YELLOW);
		polyline.setWidth(7);
		return polyline;
	}
	
	public Polygon addPolygon(ArrayList<LatLng> list)
	{
		PolygonOptions options = new PolygonOptions().addAll(list);
		Polygon polygon = map.addPolygon(options);
		return polygon;
	}
	
	public Polygon addPolygon(LatLng ...latLngs)
	{
		PolygonOptions options = new PolygonOptions().add(latLngs);
		Polygon polygon = map.addPolygon(options);
		return polygon;
	}
	
	class MyInfoWindowAdapter implements InfoWindowAdapter
	{
		private View infoWindow;
		@Override
		public View getInfoContents(Marker arg0)
		{
			infoWindow = activity.getLayoutInflater().inflate(R.layout.custom_info_content, null);
			displayView(arg0);
			return infoWindow;
		}

		@Override
		public View getInfoWindow(Marker arg0)
		{
			return null;
		}
		
		private void displayView(Marker marker)
		{
		}
	}
}