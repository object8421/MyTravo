package com.cobra.mytravo.models;

public class MyLocation {
	private double latitude;
	private double longitude;
	private String nameString;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longtitude) {
		this.longitude = longtitude;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
}
