package com.cobra.mytravo.models;

import android.database.Cursor;


import com.cobra.mytravo.data.UsersDataHelper.UserInfoDB;
import com.google.gson.Gson;

public class User extends BaseType{
	private int id;
	private String token;
	private String qq_user_id;
	private String sina_user_id;
	private String email;
	private String nickname;
	private String password;
	private String signature;
	private String avatar_url;
	private int account;
	private int travel_note_qty;
	private int achievement_qty;
	
	private String last_travel_motified;
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String user_token) {
		this.token = user_token;
	}
	
	public String getQq_user_id() {
		return qq_user_id;
	}
	public void setQq_user_id(String qq_user_id) {
		this.qq_user_id = qq_user_id;
	}
	public String getSina_user_id() {
		return sina_user_id;
	}
	public void setSina_user_id(String sina_user_id) {
		this.sina_user_id = sina_user_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public int getAccount() {
		return account;
	}
	public void setAccount(int account) {
		this.account = account;
	}
	public int getTravel_note_qty() {
		return travel_note_qty;
	}
	public void setTravel_note_qty(int travel_note_qty) {
		this.travel_note_qty = travel_note_qty;
	}
	public int getAchievement_qty() {
		return achievement_qty;
	}
	public void setAchievement_qty(int achievement_qty) {
		this.achievement_qty = achievement_qty;
	}
	public String getLast_travel_motified()
	{
		return last_travel_motified;
	}
	public void setLast_travel_motified(String last_travel_motified)
	{
		this.last_travel_motified = last_travel_motified;
	}
	
	
	public static User fromCursor(Cursor cursor)
	{
		User user;
		user = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(UserInfoDB.JSON)), User.class);
		return user;
	}

	public class UserRegisterResponse
	{
		private int rsp_code;
		
		private int user_id;

		public int getRsp_code() {
			return rsp_code;
		}

		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}

		public int getUser_id() {
			return user_id;
		}

		public void setUser_id(int user_id) {
			this.user_id = user_id;
		}
	}
	
	public class UserLoginResponse
	{
		private int rsp_code;
		private User user;
		public int getRsp_code() {
			return rsp_code;
		}
		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
	}
	public class UserInfoResponse{
		private int rsp_code;
		public int getRsp_code() {
			return rsp_code;
		}
		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}
	}
}
