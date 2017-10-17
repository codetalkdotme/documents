package com.newcare.im.login.mapper;

import com.newcare.im.pojo.Login;

public interface LoginMapper {

	public void insertLogin(Login login);
	
	public void logoutSession(String sessionId);
	
	public void logoutUser(Long userId);
	
	public Login selectLoginBySession(String sessionId);
	
	public Login selectActiveLogin(Long userId);
	
}
