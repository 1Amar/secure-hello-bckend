package com.example.demo.bean;

import java.util.List;

/**
*
* @author Amar Pattanshetti
* 
*/

public class AdminDashboard {
	private String title;
	private String description;
	private long timestamp;
	private List<UserSummary> users;

	public AdminDashboard(String title, String description, long timestamp, List<UserSummary> users) {
		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
		this.users = users;
	}

	// Getters and setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<UserSummary> getUsers() {
		return users;
	}

	public void setUsers(List<UserSummary> users) {
		this.users = users;
	}
}
