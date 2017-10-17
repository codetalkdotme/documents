package com.newcare.im.login.service;

import org.springframework.stereotype.Service;

import com.newcare.im.pojo.Login;

@Service("loginService")
public interface LoginService {

	public void addLogin(Login login);
	
	public void logoutSession(String sessId);
	
	public void logoutUser(Long userId);
	
	public Login findLoginBySession(String sessionId);
	
	public Login findActiveLogin(Long userId);
	
}
