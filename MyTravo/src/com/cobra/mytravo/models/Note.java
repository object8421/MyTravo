package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.HashMap;

import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.google.gson.Gson;

import android.R.bool;
import android.R.string;
import android.database.Cursor;

public class Note extends BaseType implements Serializable{

	/**
	 * Created by L!ar on 9/4/13
	 */
	private static final HashMap<String, Note> CACHE = new HashMap<String, Note>();
	private int note_id;
	private int user_id;
	private int travel_id;
	private int is_deleted;
	private String travel_created_time;
	private static void addToCache(Note note) {
        CACHE.put(note.getTime(), note);
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
	private String image_url;
	private String description;
	private String time;
	private int comment_qty;
	private int vote_qty;
	private int is_public;
	private MyLocation location;
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
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
}
