package com.cognizant.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import com.cognizant.response.Response;
import com.congnizant.model.FollowUser;
import com.congnizant.model.TweetLike;
import com.congnizant.model.User;
import com.congnizant.model.UserTweet;

public interface IUserService {
	public ResponseEntity<Response> followUser(FollowUser followUser, HttpServletRequest request);

	public ResponseEntity<Response> getFollowers(User user, HttpServletRequest request);

	public ResponseEntity<Response> home(User user, HttpServletRequest request);

	public ResponseEntity<Response> likeTweet(TweetLike likeTweet, HttpServletRequest request);

	public ResponseEntity<Response> loginUser(User user);

	public ResponseEntity<Response> signUpUser(User user);

	public ResponseEntity<Response> tweet(UserTweet userTweet, HttpServletRequest request);
}
