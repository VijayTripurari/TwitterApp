package com.cognizant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.congnizant.model.Tweet;
@Repository
public interface TweetRepository extends JpaRepository<Tweet, Integer> {
 public List<Tweet> findByTweetId(int tweetId);
}
