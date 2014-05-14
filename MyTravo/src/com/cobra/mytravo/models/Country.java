package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Country extends BaseType implements Serializable{
	private int id;
	private String country_name;
	private String brief_information;
	private String detail_information;
	private String crawled_url;
	private String image_path;
	private String last_update_time;
	private String image_url;
	private String cover_url;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCountry_name() {
		return country_name;
	}
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}
	public String getBrief_information() {
		return brief_information;
	}
	public void setBrief_information(String brief_information) {
		this.brief_information = brief_information;
	}
	public String getDetail_information() {
		return detail_information;
	}
	public void setDetail_information(String detail_information) {
		this.detail_information = detail_information;
	}
	public String getCrawled_url() {
		return crawled_url;
	}
	public void setCrawled_url(String crawled_url) {
		this.crawled_url = crawled_url;
	}
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	public String getLast_update_time() {
		return last_update_time;
	}
	public void setLast_update_time(String last_update_time) {
		this.last_update_time = last_update_time;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getCover_url() {
		return cover_url;
	}
	public void setCover_url(String cover_url) {
		this.cover_url = cover_url;
	}
	public static class CountryRequestData{
		private ArrayList<Province> related_province;
		private ArrayList<String> image_url_list;
		public ArrayList<Province> getRelated_province() {
			return related_province;
		}
		public void setRelated_province(ArrayList<Province> related_province) {
			this.related_province = related_province;
		}
		public ArrayList<String> getImage_url_list() {
			return image_url_list;
		}
		public void setImage_url_list(ArrayList<String> image_url_list) {
			this.image_url_list = image_url_list;
		}
		
	}
}
