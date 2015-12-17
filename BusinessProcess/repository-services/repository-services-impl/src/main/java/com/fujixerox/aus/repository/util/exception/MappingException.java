package com.fujixerox.aus.repository.util.exception;

/** 
 * Henry Niu
 * 12/10/2015
 */ 
public class MappingException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public MappingException(String message) {
		super(message);
	}
	
	public MappingException(Exception ex) {
		super(ex);
	}

}
