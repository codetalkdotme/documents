package com.newcare.update.service;

import java.util.Map;

import com.newcare.update.exception.UpdateServiceException;

/**
 * 
 * @author guobxu
 *
 */
public interface IUpdateService {

	public String checkVersion(Map<String, Object> params) throws UpdateServiceException;
	
}
