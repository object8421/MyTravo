package com.cobra.mytravo.models;

import java.util.ArrayList;

public class DestinationRequest {
	private ArrayList<Country> countrys;
	private ArrayList<Country> hottest_countrys;
	private ArrayList<Province> hottest_provinces;
	private ArrayList<Province> provinces;
	public ArrayList<Country> getCountrys() {
		return countrys;
	}
	public void setCountrys(ArrayList<Country> countrys) {
		this.countrys = countrys;
	}
	public ArrayList<Country> getHottest_countrys() {
		return hottest_countrys;
	}
	public void setHottest_countrys(ArrayList<Country> hottest_countrys) {
		this.hottest_countrys = hottest_countrys;
	}
	public ArrayList<Province> getHottest_provinces() {
		return hottest_provinces;
	}
	public void setHottest_provinces(ArrayList<Province> hottest_provinces) {
		this.hottest_provinces = hottest_provinces;
	}
	public ArrayList<Province> getProvinces() {
		return provinces;
	}
	public void setProvinces(ArrayList<Province> provinces) {
		this.provinces = provinces;
	}
	
}
