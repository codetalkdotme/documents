package com.newcare.auth.dao;

import java.util.List;

import com.newcare.auth.pojo.ServiceTicket;
import com.newcare.auth.pojo.User;

/**
 * 
 * 认证DAO接口
 * 
 * @author guobxu
 *
 */
public interface IAuthDao {

	/**
	 * 
	 * @param loginName 有可能是登录名(专干) 或者 身份证ID(专干 & 居民)
	 * @return
	 */
	public User getUserByName(String loginName);
	
	public User getUserByDeviceId(String deviceId);
	
	public User getUserById(Long uid);
	
	public User getUserByPid(String pid);
	
	public List<User> getUsersByIds(long[] uids);
	
	public List<User> getUsersByIds(List<Long> uids);
	
	public List<User> getUsersByIds(String[] uids);
	
	public List<User> getUsersByPids(String[] pids);
	
	// 返回UID
	public long addUser(User user);
	
	public void updateUser(User user);
	
	// newDeviceId==null 则表示不更新
	public void updateUserAndDevice(User user, String newDeviceId);
	
	// newPid==null 则表示不更新
	// newDeviceId==null 则表示不更新
	public void updateUserPidAndDevice(User user, String newPid, String newDeviceId);
	
	public boolean exists(User user);
	
	public void setSmsCode(String mobile, String type, String smsCode, long timeout);
	public String getSmsCode(String mobile, String type);
	
	public void deleteSmsCode(String mobile, String type);
	
	public void setServiceTicket(long userId, ServiceTicket ticket);
	public ServiceTicket getServiceTicket(long userId);
	
	public void unbindDevice(Long userId);
	
}
