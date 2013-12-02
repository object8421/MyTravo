package com.cobra.mytravo.models;

import android.R.bool;
import android.R.string;

public class Note {

	/**
	 * Created by L!ar on 9/4/13
	 */
	private int note_id;
	private int user_id;
	private int travel_id;
	private String travel_created_time;
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getTravel_created_time() {
		return travel_created_time;
	}
	public void setTravel_created_time(String travel_created_time) {
		this.travel_created_time = travel_created_time;
	}
	private String image_url;
	private String content;
	private String time;
	private int comment_qty;
	private int vote_qty;
	private boolean is_public;
	private boolean is_deleted;
	public int getNote_id() {
		return note_id;
	}
	public void setNote_id(int note_id) {
		this.note_id = note_id;
	}
	
	public int getTravel_id() {
		return travel_id;
	}
	public void setTravel_id(int travel_id) {
		this.travel_id = travel_id;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getComment_qty() {
		return comment_qty;
	}
	public void setComment_qty(int comment_qty) {
		this.comment_qty = comment_qty;
	}
	public int getVote_qty() {
		return vote_qty;
	}
	public void setVote_qty(int vote_qty) {
		this.vote_qty = vote_qty;
	}
	public boolean isIs_public() {
		return is_public;
	}
	public void setIs_public(boolean is_public) {
		this.is_public = is_public;
	}
	public boolean isIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
}
