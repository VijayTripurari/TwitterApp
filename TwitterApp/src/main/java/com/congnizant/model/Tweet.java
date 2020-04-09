package com.congnizant.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Tweet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	 private int tweetId;
     private String tweetText; 
     @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
     private List<User> likeTweet;
     private Date createdDate;
     @ManyToOne
     private User user;
     
	public Tweet() {
		super();
		
	}
	public Tweet(String tweetText) {
		super();
		this.tweetText = tweetText;
	}
	public int getTweetId() {
		return tweetId;
	}
	public void setTweetId(int tweetId) {
		this.tweetId = tweetId;
	}
	public String getTweetText() {
		return tweetText;
	}
	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}
			
	public List<User> getLikeTweet() {
		return likeTweet;
	}
	public void setLikeTweet(List<User> likeTweet) {
		this.likeTweet = likeTweet;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	@Override
	public String toString() {
		return "Tweet [tweetId=" + tweetId + ", tweetText=" + tweetText + ", likeTweet=" + likeTweet + ", createdDate="
				+ createdDate + "]";
	}
	
	
	
	
}
