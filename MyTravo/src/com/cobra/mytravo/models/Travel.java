package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


import com.cobra.mytravo.data.TravelsDataHelper;
import com.google.gson.Gson;

import android.R.integer;
import android.database.Cursor;

public class Travel extends BaseType implements Serializable{

	/**
	 * @param args
	 */
	private static final HashMap<String, Travel> CACHE = new HashMap<String, Travel>();
	private int id;
	private int user_id;
	
	private String title;
	private String destination;
	private String begin_date;
	private String end_date;
	private String create_time;
	private String cover_url;
	private String description;
	private double average_spend;
	private int view_qty;
	private int comment_qty;
	private int vote_qty;
	private int favorite_qty;
	private int is_public;
	private int is_deleted;
	private User user;
	
	private int is_sync;//判断客户端中游记的同步状态，1：需要上传到服务器， 0：客户端中为最新版本
	private int TAG;//与服务器交互是辨别travel
	
	
	private String cover_path;
	private String snap_path;
	
	public String getSnap_path() {
		return snap_path;
	}

	public void setSnap_path(String snap_path) {
		this.snap_path = snap_path;
	}

	public String getCover_path() {
		return cover_path;
	}

	public void setCover_path(String cover_path) {
		this.cover_path = cover_path;
	}

	public int getIs_sync()
	{
		return is_sync;
	}

	public void setIs_sync(int is_sync)
	{
		this.is_sync = is_sync;
	}

	public int getTAG()
	{
		return TAG;
	}

	public void setTAG(int tAG)
	{
		TAG = tAG;
	}

	private static void addToCache(Travel travel) {
        CACHE.put(travel.getCreate_time(), travel);
    }

    private static Travel getFromCache(String time) {
        return CACHE.get(time);
    }
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBegin_date() {
		return begin_date;
	}
	public void setBegin_date(String begin_date) {
		this.begin_date = begin_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	
	public String getCreate_time()
	{
		return create_time;
	}

	public void setCreate_time(String create_time)
	{
		this.create_time = create_time;
	}

	public String getCover_url() {
		return cover_url;
	}
	public void setCover_url(String cover_url) {
		this.cover_url = cover_url;
	}
	public double getAverage_spend() {
		return average_spend;
	}
	public void setAverage_spend(double average_spend) {
		this.average_spend = average_spend;
	}
	public int getView_qty() {
		return view_qty;
	}
	public void setView_qty(int view_qty) {
		this.view_qty = view_qty;
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
	public int getFavorite_qty() {
		return favorite_qty;
	}
	public void setFavorite_qty(int favorite_qty) {
		this.favorite_qty = favorite_qty;
	}
	
	public int getIs_public() {
		return is_public;
	}

	public void setIs_public(int is_public) {
		this.is_public = is_public;
	}

	public int getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(int is_deleted) {
		this.is_deleted = is_deleted;
	}

	public static Travel fromJson(String json) {
        return new Gson().fromJson(json, Travel.class);
    }
	public static Travel fromCursor(Cursor cursor) {
        String time = cursor.getString(cursor.getColumnIndex(TravelsDataHelper.TravelsDBInfo.TIME));
        Travel travel = getFromCache(time);
        if (travel != null) {
            return travel;
        }
        travel = new Gson().fromJson(
                cursor.getString(cursor.getColumnIndex(TravelsDataHelper.TravelsDBInfo.JSON)),
                Travel.class);
        addToCache(travel);
        return travel;
    }
	public static void clearCache(){
		CACHE.clear();
	}
	
	public static class TravelsRequestData{
		private int rsp_code;
		private ArrayList<Travel> travels;
		public int getRsp_code() {
			return rsp_code;
		}
		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}
		public ArrayList<Travel> getTravels() {
			return travels;
		}
		public void setTravels(ArrayList<Travel> travels) {
			this.travels = travels;
		}
		
	}
	public static class SearchRequestData{
		private int rsp_code;
		private ArrayList<Travel> travel_list;
		public int getRsp_code() {
			return rsp_code;
		}
		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}
		public ArrayList<Travel> getTravel_list() {
			return travel_list;
		}
		public void setTravel_list(ArrayList<Travel> travel_list) {
			this.travel_list = travel_list;
		}
		
	}
}
