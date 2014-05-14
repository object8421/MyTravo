package com.cobra.mytravo.models;

import java.io.Serializable;

public class Spot extends BaseType implements Serializable{
	private int id;
	private String crawled_url;
	private String spot_name;
	private String related_city;
	private String related_province;
	private String brief_information;
	private String image_url;
	private String cover_url;
	private String ticket;
	private String image_path;
	private String last_update_time;
	private String transportation_info;
	private String proper_travel_time;
	private String background_description;
	private String history_introduction;
	private String geograpyic_info;
	private String visa_info;
	private String attention;
	private String travel_advice;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCrawled_url() {
		return crawled_url;
	}
	public void setCrawled_url(String crawled_url) {
		this.crawled_url = crawled_url;
	}
	public String getSpot_name() {
		return spot_name;
	}
	public void setSpot_name(String spot_name) {
		this.spot_name = spot_name;
	}
	public String getRelated_city() {
		return related_city;
	}
	public void setRelated_city(String related_city) {
		this.related_city = related_city;
	}
	public String getRelated_province() {
		return related_province;
	}
	public void setRelated_province(String related_province) {
		this.related_province = related_province;
	}
	public String getBrief_information() {
		return brief_information;
	}
	public void setBrief_information(String brief_information) {
		this.brief_information = brief_information;
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
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
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
	public String getTransportation_info() {
		return transportation_info;
	}
	public void setTransportation_info(String transportation_info) {
		this.transportation_info = transportation_info;
	}
	public String getProper_travel_time() {
		return proper_travel_time;
	}
	public void setProper_travel_time(String proper_travel_time) {
		this.proper_travel_time = proper_travel_time;
	}
	public String getBackground_description() {
		return background_description;
	}
	public void setBackground_description(String background_description) {
		this.background_description = background_description;
	}
	public String getHistory_introduction() {
		return history_introduction;
	}
	public void setHistory_introduction(String history_introduction) {
		this.history_introduction = history_introduction;
	}
	public String getGeograpyic_info() {
		return geograpyic_info;
	}
	public void setGeograpyic_info(String geograpyic_info) {
		this.geograpyic_info = geograpyic_info;
	}
	public String getVisa_info() {
		return visa_info;
	}
	public void setVisa_info(String visa_info) {
		this.visa_info = visa_info;
	}
	public String getAttention() {
		return attention;
	}
	public void setAttention(String attention) {
		this.attention = attention;
	}
	public String getTravel_advice() {
		return travel_advice;
	}
	public void setTravel_advice(String travel_advice) {
		this.travel_advice = travel_advice;
	}
	
}
