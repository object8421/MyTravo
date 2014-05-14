package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.ArrayList;

public class City extends BaseType implements Serializable{
	private int id;
	private String city_name;
	private String related_province;
	private String crawled_url;
	private String image_url;
	private String image_path;
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
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getRelated_province() {
		return related_province;
	}
	public void setRelated_province(String related_province) {
		this.related_province = related_province;
	}
	public String getCrawled_url() {
		return crawled_url;
	}
	public void setCrawled_url(String crawled_url) {
		this.crawled_url = crawled_url;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
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
	public static class CityRequestData{
		private ArrayList<Spot> related_spot;
		private ArrayList<String> image_url_list;
		public ArrayList<Spot> getRelated_spot() {
			return related_spot;
		}
		public void setRelated_spot(ArrayList<Spot> related_spot) {
			this.related_spot = related_spot;
		}
		public ArrayList<String> getImage_url_list() {
			return image_url_list;
		}
		public void setImage_url_list(ArrayList<String> image_url_list) {
			this.image_url_list = image_url_list;
		}
	}
}
