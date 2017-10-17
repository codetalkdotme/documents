package com.newcare.update.exception;

/**
 * 更新服务异常
 * @author guobxu
 *
 */
public class UpdateServiceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpdateServiceException(String msg, Exception ex) {
		super(msg, ex);
	}
	
	public UpdateServiceException(Exception ex) {
		super(ex);
	}
	
	public UpdateServiceException(String msg) {
		super(msg);
	}
	
}

