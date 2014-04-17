package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.google.gson.Gson;

import android.R.bool;
import android.R.string;
import android.database.Cursor;

public class Note extends BaseType implements Serializable{

	private static final HashMap<String, Note> CACHE = new HashMap<String, Note>();
	private int id;
	private int user_id;
	private int travel_id;	//
	private int is_deleted;
	private String travel_created_time;
	private String image_url;//
	private String content;//
	private String create_time;
	private int comment_qty;
	private int vote_qty;
	private int is_public;
	private MyLocation location;  //
	
	private int is_sync;//判断客户端中游记的同步状态，1：需要上传到服务器， 0：客户端中为最新版本
	private int TAG;//
	private String image_path;
	
	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public int getIs_sync()
	{
		return is_sync;
	}

	public void setIs_sync(int is_sync)
	{
		this.is_sync = is_sync;
	}

	private static void addToCache(Note note) {
        CACHE.put(note.getCreate_time(), note);
    }
	
    private static Note getFromCache(String time) {
        return CACHE.get(time);
    }
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
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
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
	public String getCreate_time()
	{
		return create_time;
	}
	public void setCreate_time(String create_time)
	{
		this.create_time = create_time;
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
	public int getIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(int is_deleted) {
		this.is_deleted = is_deleted;
	}
	public int getIs_public() {
		return is_public;
	}
	public void setIs_public(int is_public) {
		this.is_public = is_public;
	}
	public int getTAG()
	{
		return TAG;
	}

	public void setTAG(int tAG)
	{
		TAG = tAG;
	}

	public MyLocation getLocation() {
		return location;
	}

	public void setLocation(MyLocation location) {
		this.location = location;
	}

	public static Note fromJson(String json) {
        return new Gson().fromJson(json, Note.class);
    }
	public static Note fromCursor(Cursor cursor) {
        String time = cursor.getString(cursor.getColumnIndex(NotesDataHelper.NotesDBInfo.TIME));
        Note note = getFromCache(time);
        if (note != null) {
            return note;
        }
        note = new Gson().fromJson(
                cursor.getString(cursor.getColumnIndex(NotesDataHelper.NotesDBInfo.JSON)),
                Note.class);
        addToCache(note);
        return note;
    }
	public static void clearCache(){
		CACHE.clear();
	}
	public static class NotesRequestData{
		private int rsp_code;
		private ArrayList<Note> notes;
		public int getRsp_code() {
			return rsp_code;
		}
		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}
		public ArrayList<Note> getNotes() {
			return notes;
		}
		public void setNotes(ArrayList<Note> notes) {
			this.notes = notes;
		}
	}
}
