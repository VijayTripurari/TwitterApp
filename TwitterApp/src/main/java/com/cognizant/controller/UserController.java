package com.cognizant.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.repository.UserRepository;
import com.cognizant.response.Response;
import com.cognizant.service.IUserService;
import com.congnizant.model.FollowUser;
import com.congnizant.model.TweetLike;
import com.congnizant.model.User;
import com.congnizant.model.UserTweet;

@RestController
public class UserController {

	@Autowired
	IUserService userService;
	
	@Autowired
	UserRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<Response> signUpUser(@RequestBody final User user) {
		return userService.signUpUser(user);
	}
	
	@PostMapping("/login")
	public ResponseEntity<Response>  loginUser(@RequestBody final User user) {
	 return	userService.loginUser(user);
	}
	
	@PostMapping("/secure/follow")
	public ResponseEntity<Response>  followUser(@RequestBody final FollowUser followUser, HttpServletRequest request) {
	return userService.followUser(followUser,request);
   }
	
	@PostMapping("/secure/followers")
	public ResponseEntity<Response>  getFollowers(@RequestBody final User user, HttpServletRequest request) {
		return userService.getFollowers(user,request);
	   }
	
	@PostMapping("/secure/tweet")
	public ResponseEntity<Response>  tweet(@RequestBody final UserTweet userTweet, HttpServletRequest request) {
		return userService.tweet(userTweet,request);
	   }
	
	@PostMapping("/secure/like")
	public ResponseEntity<Response>  likeTweet(@RequestBody final TweetLike likeTweet, HttpServletRequest request) {
		return userService.likeTweet(likeTweet,request);
	   }
	@PostMapping("/secure/homeapi")
	public ResponseEntity<Response>  home(@RequestBody final User user, HttpServletRequest request) {
		return userService.home(user,request);
	   }
	
	
}
