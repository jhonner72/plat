package com.fujixerox.aus.repository.util.exception;

/** 
 * Henry Niu
 * 18/05/2015
 */ 
public class NonRetriableException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public NonRetriableException(String message) {
		super(message);
	}
	
	public NonRetriableException(Exception ex) {
		super(ex);
	}
	
	public NonRetriableException(String message, Exception ex) {
		super(message, ex);
	}

}
