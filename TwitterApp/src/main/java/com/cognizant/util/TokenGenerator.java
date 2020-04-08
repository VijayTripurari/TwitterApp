package com.cognizant.util;

import java.util.Base64;
public class TokenGenerator {

	public static String encode(String input)
	{
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encoded = encoder.encode(input.getBytes());
		return new String(encoded);
	}
	
	public static String decode(String input)
	{
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decoded = decoder.decode(input);
		return new String(decoded);
	}
}
