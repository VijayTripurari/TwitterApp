package com.cognizant.exception;

public class TokenException extends Exception {
	private static final long serialVersionUID = 1L;
	public TokenException(String message)
	{
		super("Token Exception "+message);
	}
}
