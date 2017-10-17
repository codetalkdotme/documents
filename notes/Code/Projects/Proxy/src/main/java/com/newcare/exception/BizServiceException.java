package com.newcare.exception;

/**
 * 
 * 业务服务异常
 * 
 * @author guobxu
 *
 */
public class BizServiceException extends Exception {

	public BizServiceException(String msg, Exception ex) {
		super(msg, ex);
	}
	
	public BizServiceException(Exception ex) {
		super(ex);
	}
	
	public BizServiceException(String msg) {
		super(msg);
	}
	
}
