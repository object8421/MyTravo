package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Province extends BaseType implements Serializable{
	private int id;
	private String province_name;
	private String related_country;
	private String crawled_url;
	private String image_path;
	private String image_url;
	private String brief_information;
	private String detail_information;
	private String last_update_time;
	private String cover_url;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProvince_name() {
		return province_name;
	}
	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}
	public String getRelated_country() {
		return related_country;
	}
	public void setRelated_country(String related_country) {
		this.related_country = related_country;
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
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
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
	public String getLast_update_time() {
		return last_update_time;
	}
	public void setLast_update_time(String last_update_time) {
		this.last_update_time = last_update_time;
	}
	public String getCover_url() {
		return cover_url;
	}
	public void setCover_url(String cover_url) {
		this.cover_url = cover_url;
	}
	public static class ProvinceRequestData{
		private ArrayList<City> related_city;
		private ArrayList<String> image_url_list;
		public ArrayList<City> getRelated_city() {
			return related_city;
		}
		public void setRelated_city(ArrayList<City> related_city) {
			this.related_city = related_city;
		}
		public ArrayList<String> getImage_url_list() {
			return image_url_list;
		}
		public void setImage_url_list(ArrayList<String> image_url_list) {
			this.image_url_list = image_url_list;
		}
		
	}
}
