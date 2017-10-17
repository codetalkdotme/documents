package com.newcare.auth.service;

import java.util.Map;

import com.newcare.auth.exception.AuthServiceException;

/**
 * 
 * @author guobxu
 * 
 */
public interface IAuthService {

	public String doPost(String uri, Map<String, Object> data) throws AuthServiceException;
	
}
