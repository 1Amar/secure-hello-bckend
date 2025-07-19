package com.example.demo.bean;

import java.util.List;

/**
*
* @author Amar Pattanshetti
* 
*/

public class UserSummary {
	private String username;
	private String email;
	private String name;
	private List<String> roles;

	public UserSummary(String username, String email, String name, List<String> roles) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.roles = roles;
	}

	// Getters and setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}