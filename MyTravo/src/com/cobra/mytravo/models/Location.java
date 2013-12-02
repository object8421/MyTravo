package com.cobra.mytravo.models;

import com.google.gson.Gson;

import android.R.string;

public class Location {

	/**
	 * @param args
	 */
	private String time;
	private double latitude;
	private double longitude;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Location fromJson(String json){
		 return new Gson().fromJson(json, Location.class);
	}
}
