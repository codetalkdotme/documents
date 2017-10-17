package com.newcare.im.exception;

/**
 * IM服务异常
 * 
 * @author guobxu
 *
 */
public class IMServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public IMServiceException(String msg, Exception ex) {
		super(msg, ex);
	}
	
	public IMServiceException(Exception ex) {
		super(ex);
	}
	
	public IMServiceException(String msg) {
		super(msg);
	}
	
}
