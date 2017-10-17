package com.newcare.storage.exception;

/**
 * 
 * 文件存储异常
 * 
 * @author guobxu
 *
 */
public class FileStorageException extends Exception {

	public FileStorageException(String msg, Exception ex) {
		super(msg, ex);
	}
	
	public FileStorageException(Exception ex) {
		super(ex);
	}
	
}
