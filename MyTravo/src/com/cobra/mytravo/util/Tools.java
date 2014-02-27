package com.cobra.mytravo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.models.MyLocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class Tools
{

	public static final String PLACE_REFRANCE = "refranceString";
	
	/**获得latitude,longtitude形式的数据*/
	public static String getLocation(Context context)
	{
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null)
		{
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location == null)
			return "";
		// 经度
		double longitude = location.getLongitude();
		// 维度
		double latitude = location.getLatitude();
		Log.i("tag", "location is " + longitude + "," + latitude);
		return latitude + "," + longitude;
	}
	
	/** 获得周边地理位置信息的json数据*/
	public static List<Place> fromJsonToAroundPlaces(String resultString)
			throws JSONException
	{
		List<Place> places = new ArrayList<Place>();
		JSONObject clientResponseJsonObject = new JSONObject(resultString);
		JSONArray jsonArray = clientResponseJsonObject.getJSONArray("results");
		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject geometry = jsonObject.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");

			String name = jsonObject.getString("name");
			String address = jsonObject.getString("vicinity");
			String reference = jsonObject.getString("reference");
			String lat = location.getString("lat");
			String lng = location.getString("lng");

			Place place = new Place(name, address, reference);
			place.setLatitude(lat);
			place.setLongitude(lng);
			places.add(place);
		}
		return places;
	}

	/** 获得文本搜索地理位置信息的json数据*/
	public static List<Place> fromJsonToSearchPlaces(String resultString)
			throws JSONException
	{
		List<Place> places = new ArrayList<Place>();
		JSONObject clientResponseJsonObject = new JSONObject(resultString);
		JSONArray jsonArray = clientResponseJsonObject.getJSONArray("results");
		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject geometry = jsonObject.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");

			String name = jsonObject.getString("name");
			String address = jsonObject.getString("formatted_address");
			String reference = jsonObject.getString("reference");
			String lat = location.getString("lat");
			String lng = location.getString("lng");

			Place place = new Place(name, address, reference);
			place.setLatitude(lat);
			place.setLongitude(lng);
			places.add(place);
		}
		return places;
	}

	/** 获得当前位置*/
	public static Location getMyLocation(Context context)
	{
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null)
		{
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location == null)
			return null;
		return location;
	}
}
