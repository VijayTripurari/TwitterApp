package com.cognizant.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.cognizant.exception.TweetException;
import com.cognizant.exception.UserException;
import com.cognizant.repository.TweetRepository;
import com.cognizant.repository.UserRepository;
import com.cognizant.response.Response;
import com.cognizant.service.IUserService;
import com.cognizant.util.TokenGenerator;
import com.congnizant.model.FollowUser;
import com.congnizant.model.Tweet;
import com.congnizant.model.TweetLike;
import com.congnizant.model.User;
import com.congnizant.model.UserTweet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserServiceImpl implements IUserService {
	private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);
	@Autowired
	UserRepository userRepository;
	@Autowired
	TweetRepository tweetRepository;

	/**
	 * This method is used for registering a new User so that the user can login
	 * then onwards
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
			if (user.getEmail() == null || user.getEmail().length() == 0)
				throw new UserException("Email can not be empty");
			if (user.getPassword() == null || user.getPassword().length() == 0)
				throw new UserException("Password can not be empty");
			String encodedPassword = TokenGenerator.encode(user.getPassword());
			user.setPassword(encodedPassword);
			User savedUser = userRepository.save(user);
			if (savedUser != null) {
				response = new Response();
				response.setStatus("success");
				response.setMessage(savedUser);
			}
		} catch (UserException e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
			if (user.getEmail() == null || user.getEmail().length() == 0)
				throw new UserException("Email can not be empty");
			if (user.getPassword() == null || user.getPassword().length() == 0)
				throw new UserException("Password can not be empty");
			List<User> userList = userRepository.findAll();
			for (User currentUser : userList) {
				if (currentUser.getEmail().equals(user.getEmail())
						&& currentUser.getPassword().equals(TokenGenerator.encode(user.getPassword()))) {
					isExists = true;
					loginUser = currentUser;
				}
			}
		} catch (UserException e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Database error");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		if (isExists == true) {
			response = new Response();
			response.setStatus("success");
			return new ResponseEntity<>(new Response("success",
					Jwts.builder().setSubject(loginUser.getEmail()).claim("roles", "user").setIssuedAt(new Date())
							.signWith(SignatureAlgorithm.HS256, "a3VtYXJAZ21haWwuY29t").compact()),
					HttpStatus.OK);
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
			String header = request.getHeader("token");
			Claims claims = getAllClaimsFromToken(header.substring(7));
			if (claims == null)
				throw new UserException("Email can not be empty");
			String email = claims.getSubject();
			User user = userRepository.findByEmail(email).get(0);
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
			response.setMessage(e.getMessage());
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

	public ResponseEntity<Response> getFollowers(HttpServletRequest request) {
		Response response = new Response();
		try {
			String header = request.getHeader("token");
			Claims claims = getAllClaimsFromToken(header.substring(7));
			if (claims == null)
				throw new UserException("Email can not be empty");
			String email = claims.getSubject();
			User currentUser = userRepository.findByEmail(email).get(0);
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
		} catch (UserException e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Database error!");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
		Response response = new Response();
		String header = request.getHeader("token");
		try {
			Claims claims = getAllClaimsFromToken(header.substring(7));
			if (claims == null)
				throw new UserException("Email can not be empty");
			String email = claims.getSubject();
			List<User> userList = userRepository.findByEmail(email);
			LOGGER.debug("tweet post limited to 100 characters");

			if (userTweet.getTweetText().length() > 100)
				throw new TweetException("Tweet post text can not be more than 100 characters");
			LOGGER.debug("updating the tweet details of a user");
			LOGGER.debug("date will be updated inthe TWEET , USER_DETAILS , USER_DETAILS_TWEET tables.");
			if (!userList.isEmpty()) {
				User currentUser = userList.get(0);
				Tweet tweet = new Tweet();
				tweet.setTweetText(userTweet.getTweetText());
				tweet.setCreatedDate(new Date());
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
		} catch (UserException e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
		Response response = new Response();
		String header = request.getHeader("token");
		try {
			Claims claims = getAllClaimsFromToken(header.substring(7));
			if (claims == null)
				throw new UserException("Email can not be empty");
			String email = claims.getSubject();
			if (email == null)
				throw new UserException("Email id is mandatory");
			if (likeTweet.getTweetId() < 0)
				throw new TweetException("Invalid Tweet id");
			List<User> userList = userRepository.findByEmail(email);
			List<Tweet> tweetList = tweetRepository.findByTweetId(likeTweet.getTweetId());
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
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
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
	public ResponseEntity<Response> home( HttpServletRequest request) {
		Response response = new Response();
		try {
			String header = request.getHeader("token");
			Claims claims = getAllClaimsFromToken(header.substring(7));
			if (claims == null)
				throw new UserException("Email can not be empty");
			String email = claims.getSubject();
			User user = userRepository.findByEmail(email).get(0);
			
			List<Tweet> tweetList =user.getTweet();
			LOGGER.debug("Sorting the Tweets in descending order of Date");
			tweetList.sort((Tweet o1, Tweet o2) -> o1.getCreatedDate().before(o2.getCreatedDate()) ? 1 : -1);
			List<Tweet> list = tweetList.stream().limit(10).collect(Collectors.toList());
			LOGGER.debug("list : " + list);
			response.setStatus("success");
			response.setMessage(list.toString());

		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey("a3VtYXJAZ21haWwuY29t").parseClaimsJws(token).getBody();
		} catch (Exception e) {
			LOGGER.error("Could not get all claims Token from passed token");
			claims = null;
		}
		return claims;
	}

}
