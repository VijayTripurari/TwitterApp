package com.cognizant.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.cognizant.repository.LoginRepository;
import com.cognizant.repository.TweetRepository;
import com.cognizant.repository.UserRepository;
import com.cognizant.response.Response;
import com.cognizant.service.IUserService;
import com.cognizant.util.TokenGenerator;
import com.congnizant.model.FollowUser;
import com.congnizant.model.LoginDetail;
import com.congnizant.model.Tweet;
import com.congnizant.model.TweetLike;
import com.congnizant.model.User;
import com.congnizant.model.UserTweet;

@Service
public class UserService implements IUserService {
	private static final Logger LOGGER = Logger.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;
	@Autowired
	TweetRepository tweetRepository;
	@Autowired
	LoginRepository loginRepository;

	/**
	 * This method is used for registering a new User so that the user can login
	 * then onwards
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param user
	 * @return
	 */

	public ResponseEntity<Response> signUpUser(User user) {
		Response response = new Response();
		try {
			LOGGER.debug("save method will persist the user object in the USER_DETAILS table");
			String encodedPassword = TokenGenerator.encode(user.getPassword());
			user.setPassword(encodedPassword);
			User savedUser = userRepository.save(user);
			if (savedUser != null) {
				response = new Response();
				response.setStatus("success");
				response.setMessage(savedUser);
			}
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Database error!");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	/**
	 * This method is used for login after login it returns a access token to be
	 * used for other requests in the applicaiton a token in prepared by Token
	 * Generator with email of the user and stored in a separate table called
	 * LOGIN_DETAILS table to track the each login of user with the token so that
	 * next sub sequest user request can be traced with token of that user. if wrong
	 * token is given or token not given invalid Credentials ! Message will be
	 * returned.
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param user
	 * @return
	 */
	public ResponseEntity<Response> loginUser(User user) {
		Response response = new Response();
		boolean isExists = false;
		User loginUser = null;
		LOGGER.debug("in the login method finding whether the user exists in the USER_DETAILS table or not");
		try {
			List<User> userList = userRepository.findAll();
			for (User currentUser : userList) {
				if (currentUser.getEmail().equals(user.getEmail())
						&& currentUser.getPassword().equals(TokenGenerator.encode(user.getPassword()))) {
					isExists = true;
					loginUser = currentUser;
				}
			}
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Database error");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		if (isExists == true) {
			response = new Response();
			response.setStatus("success");
			String encodedString = TokenGenerator.encode(loginUser.getEmail());
			LoginDetail loginDetail = new LoginDetail();
			loginDetail.setUserId(loginUser.getUserId());
			loginDetail.setEmail(loginUser.getEmail());
			loginDetail.setToken(encodedString);
			LoginDetail detail = loginRepository.save(loginDetail);
			response.setMessage(detail.getToken());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setStatus("error");
			response.setMessage("Invalid Credentials !");
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * This method is used for a user to follow the other user. provided the user ID
	 * so that the user can login then onwards
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param followUser
	 * @param request
	 * @return
	 */

	public ResponseEntity<Response> followUser(FollowUser followUser, HttpServletRequest request) {
		Response response = new Response();
		try {
			LOGGER.debug(
					"below statement checks first whether the follower user id user exists in USER_dETAILS table or not");
			List<User> followerList = userRepository.findByUserId(followUser.getFollowerId());
			User follower = followerList.get(0);
			User user = userRepository.findByEmail(followUser.getUser().getEmail()).get(0);
			ResponseEntity<Response> responseEntity = validateToken(user,request);
			if(! responseEntity.getStatusCode().equals(HttpStatus.OK))
				return responseEntity;
			LOGGER.debug("updating the follower user id with user");
			if (follower != null) {
				response = new Response();
				if (null != user.getFollowerList()) {
					user.getFollowerList().add(follower);

				} else {
					List<User> followersList = new ArrayList<>();
					followersList.add(follower);
					user.setFollowerList(followersList);
				}
				LOGGER.debug("data will be updated in the reference table mapping USER_DETAILS_FOLLOWER_LIST");
				userRepository.save(user);
				response.setStatus("success");
				response.setMessage("Following User is Successful");

			} else {
				response.setStatus("error");
				response.setMessage("Invalid follwer Id !");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Database error!");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * This method is used for retrieving list of users who are followers of a
	 * particular user
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param user
	 * @param request
	 * @return
	 */

	public ResponseEntity<Response> getFollowers(User user, HttpServletRequest request) {
		Response response = new Response();
		User currentUser = userRepository.findByEmail(user.getEmail()).get(0);
		ResponseEntity<Response> responseEntity = validateToken(user, request);
		if (!responseEntity.getStatusCode().equals(HttpStatus.OK))
			return responseEntity;
		List<User> followerList = currentUser.getFollowerList();
		LOGGER.debug(
				"retrieving the follower list of user from SER_DETAILS_FOLLOWER_LIST table with userId reference key");
		if (!followerList.isEmpty()) {
			response.setStatus("success");
			response.setMessage(followerList.toString());

		} else {

			response.setStatus("error");
			response.setMessage("Can not get the followers list");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	/**
	 * This method is used for tweeting a post to applicaiton
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param userTweet
	 * @param request
	 * @return
	 */
	public ResponseEntity<Response> tweet(UserTweet userTweet, HttpServletRequest request) {
		List<User> userList = userRepository.findByEmail(userTweet.getUser().getEmail());
		Response response = new Response();
		ResponseEntity<Response> responseEntity = validateToken(userList.get(0), request);
		if (!responseEntity.getStatusCode().equals(HttpStatus.OK))
			return responseEntity;
		LOGGER.debug("tweet post limited to 100 characters");
		if (userTweet.getTweetText().length() > 100) {
			response.setStatus("error");
			response.setMessage("Tweet characters can not be more than 100");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		LOGGER.debug("updating the tweet details of a user");
		LOGGER.debug("date will be updated inthe TWEET , USER_DETAILS , USER_DETAILS_TWEET tables.");
		if (!userList.isEmpty()) {
			User currentUser = userList.get(0);
			Tweet tweet = new Tweet();
			tweet.setTweetText(userTweet.getTweetText());
			currentUser.getTweet().add(tweet);
			tweet.setUser(currentUser);
			userRepository.save(currentUser);
			response.setStatus("success");
			response.setMessage("Tweet successful");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} else {
			response.setStatus("error");
			response.setMessage("Invalid user can not tweet");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * This method is used for liking a tweet by a user provided tweet id
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param likeTweet
	 * @param request
	 * @return
	 */

	public ResponseEntity<Response> likeTweet(TweetLike likeTweet, HttpServletRequest request) {
		List<User> userList = userRepository.findByEmail(likeTweet.getUser().getEmail());
		List<Tweet> tweetList = tweetRepository.findByTweetId(likeTweet.getTweetId());
		Response response = new Response();
		ResponseEntity<Response> responseEntity = validateToken(userList.get(0), request);
		if (!responseEntity.getStatusCode().equals(HttpStatus.OK))
			return responseEntity;
		LOGGER.debug("ensuring the valid user and valid tweet object submitted from request.");
		if (!userList.isEmpty() && !tweetList.isEmpty()) {

			Tweet tweet = tweetList.get(0);
			User user = userList.get(0);
			tweet.getLikeTweet().add(user);
			LOGGER.debug(
					"data gets updated in the TWEET_LIKE_TWEET table with columns tweet_tweetid , like_tweet_tweet_id");
			tweetRepository.save(tweet);
			response.setStatus("success");
			response.setMessage("Like a Tweet successful");
		} else {
			response.setStatus("error");
			response.setMessage("Invalid user or tweet");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	/**
	 * 
	 * This method is used for retrieving the top 10 tweets of a user.
	 * 
	 * @author Vijay Kumar Tripurari
	 * @Version 1.0
	 * @since 2020-04-08
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseEntity<Response> home(User user, HttpServletRequest request) {
		Response response = new Response();
		ResponseEntity<Response> responseEntity = validateToken(user, request);
		if (!responseEntity.getStatusCode().equals(HttpStatus.OK))
			return responseEntity;
		user = userRepository.findByEmail(user.getEmail()).get(0);
		/* Stream<Tweet> tweetLimit = user.getTweet().stream().limit(10); */
		Stream<Tweet> tweetLimit = user.getTweet().stream();
		List<Tweet> tweetList = tweetLimit.collect(Collectors.toList());
		response.setStatus("success");
		response.setMessage(tweetList.toString());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	private ResponseEntity<Response> validateToken(User user,HttpServletRequest request)
	{
		Response response = new Response();
		String token = request.getHeader("token");
		List<LoginDetail> tokenUser = loginRepository.findByToken(token);
		LOGGER.debug("token validation");
		if (!tokenUser.isEmpty()) {
			LoginDetail loginDetail = tokenUser.get(0);
			if (!loginDetail.getEmail().equals(user.getEmail())) {
				response.setStatus("error");
				response.setMessage("Invalid Token");
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
			}
		} else {
			response.setStatus("error");
			response.setMessage("Invalid Token");
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
