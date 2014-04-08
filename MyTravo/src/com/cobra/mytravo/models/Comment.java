package com.cobra.mytravo.models;

import java.io.Serializable;
import java.util.ArrayList;



public class Comment extends BaseType implements Serializable{
	private int id;
	private int travel_id;
	private String content;
	private String time;
	private User commenter;
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public User getCommenter() {
		return commenter;
	}
	public void setCommenter(User commenter) {
		this.commenter = commenter;
	}
	public static class CommentRequestData{
		private int rsp_code;
		private ArrayList<Comment> comments;
		public int getRsp_code() {
			return rsp_code;
		}
		public void setRsp_code(int rsp_code) {
			this.rsp_code = rsp_code;
		}
		public ArrayList<Comment> getComments() {
			return comments;
		}
		public void setComments(ArrayList<Comment> comments) {
			this.comments = comments;
		}
		
	}
}
