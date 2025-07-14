package com.example.demo.bean;

public class UserInfo {
	private String username;
	private String email;
	private String name;
	private String picture;
	private String provider;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public UserInfo() {
	}

	public UserInfo(String username, String email, String name, String picture, String provider) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.picture = picture;
		this.provider = provider;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
}
