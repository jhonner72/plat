package com.fujixerox.aus.repository.util.exception;

/** 
 * Henry Niu
 * 18/05/2015
 */ 
public class RetriableException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public RetriableException(String message) {
		super(message);
	}
	
	public RetriableException(Exception ex) {
		super(ex);
	}
	
	public RetriableException(String message, Exception ex) {
		super(message, ex);
	}

}
