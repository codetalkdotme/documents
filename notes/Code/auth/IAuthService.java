package com.newcare.auth.service;

import java.util.List;
import java.util.Map;

import com.newcare.auth.exception.AuthServiceException;
import com.newcare.auth.pojo.User;

/**
 * 
 * @author guobxu
 * 
 */
public interface IAuthService {

	public String doPost(String uri, Map<String, Object> data) throws AuthServiceException;
	
	public User getUserById(Long userId);
	
	// WEB 获取用户列表 如果传入空列表直接返回NULL
	public List<User> getUsers(List<Long> userIdList);
	
	// WEB 更新用户
	public void updateUser(User user) throws AuthServiceException;
	
	// WEB 解绑设备
	public void unbindDevice(Long userId);
	
	// WEB添加用户
	public Long addUser(User user) throws AuthServiceException;
	
	// 根据身份证或者登录名查询
	public User getUser(String loginName) throws AuthServiceException;
	
	// Web端认证登录
	public User userWebLogin(String loginName, String loginAuthStr) throws Exception;
	
}
