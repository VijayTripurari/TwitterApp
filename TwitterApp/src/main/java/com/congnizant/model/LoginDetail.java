package com.congnizant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOGIN_DETAILS")
public class LoginDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int loginId;
	private int userId;
	private String email;
	private String token;
	public LoginDetail() {
		super();
	}
	public LoginDetail(int userId, String email, String token) {
		super();
		this.userId = userId;
		this.email = email;
		this.token = token;
	}
	public int getLoginId() {
		return loginId;
	}
	public void setLoginId(int loginId) {
		this.loginId = loginId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int i) {
		this.userId = i;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
		
}
