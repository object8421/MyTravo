package com.cobra.mytravo.models;

import android.database.Cursor;

import com.cobra.mytravo.data.UserSyncDataHelper.UserSyncInfoDB;
import com.google.gson.Gson;

public class UserSync extends BaseType
{
	public UserSync(String token)
	{
		this.token = token;
	}
	
	private String token;
	private String travel;
	private String note;
	
	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public String getTravel()
	{
		return travel;
	}

	public void setTravel(String travel)
	{
		this.travel = travel;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public static final UserSync fromJson(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, UserSync.class);
	}
	
	public static final UserSync fromCursor(Cursor cursor)
	{
		UserSync usersync;
		usersync = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(UserSyncInfoDB.JSON)), UserSync.class);
		return usersync;
	}
}
