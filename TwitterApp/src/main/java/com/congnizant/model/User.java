package com.congnizant.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USER_DETAILS")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userId;
	private String email;
	private String password;
	private String name;
	private String gender;

	@OneToMany(cascade =CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Tweet> tweet;
	
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private List<User> followerList;
	
	public User() {
		super();

	}

	public User(String emailAddress, String password, String name, String gender) {
		super();
		this.email = emailAddress;
		this.password = password;
		this.name = name;
		this.gender = gender;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	

	public void setEmail(String emailAddress) {
		this.email = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Tweet> getTweet() {
		return tweet;
	}

	public void setTweet(List<Tweet> tweet) {
		this.tweet = tweet;
	}

	public List<User> getFollowerList() {
		return followerList;
	}

	public void setFollowerList(List<User> followerList) {
		this.followerList = followerList;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", emailAddress=" + email + ", password=" + password + ", name=" + name
				+ ", gender=" + gender + ", tweet=" + tweet + ", followerList=" + followerList + "]";
	}

}
