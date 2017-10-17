package com.newcare.im.login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.im.login.mapper.LoginMapper;
import com.newcare.im.login.service.LoginService;
import com.newcare.im.pojo.Login;

/**
 *  
 * 登录服务
 * @author guobxu
 *
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginMapper loginMapper;
	
	@Override
	public void addLogin(Login login) {
		loginMapper.insertLogin(login);
	}

	@Override
	public void logoutSession(String sessId) {
		loginMapper.logoutSession(sessId);
	}
	
	@Override
	public void logoutUser(Long userId) {
		loginMapper.logoutUser(userId);
	}

	@Override
	public Login findLoginBySession(String sessionId) {
		return loginMapper.selectLoginBySession(sessionId);
	}

	@Override
	public Login findActiveLogin(Long userId) {
		return loginMapper.selectActiveLogin(userId);
	}
	
	

}
