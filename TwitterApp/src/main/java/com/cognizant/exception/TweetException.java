package com.cognizant.exception;

public class TweetException extends Exception {
	private static final long serialVersionUID = 1L;
	public TweetException(String message)
	{
		super("Tweet Exception "+message);
	}
}
