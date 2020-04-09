package com.cognizant.response;

import java.io.Serializable;

public class Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String status;
	private Object message;
	
	public Response() {
		super();
	}
	public Response(String status, Object message) {
		super();
		this.status = status;
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	

}
