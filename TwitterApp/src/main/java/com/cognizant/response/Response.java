package com.cognizant.response;

public class Response {
	
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
