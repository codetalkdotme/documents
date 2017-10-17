package com.newcare.auth.exception;

/**
 * 
 * @author guobxu
 *
 */
public class AuthServiceException extends Exception {

	public AuthServiceException(String msg, Exception ex) {
		super(msg, ex);
	}
	
	public AuthServiceException(Exception ex) {
		super(ex);
	}
	
	public AuthServiceException(String msg) {
		super(msg);
	}
	
}
