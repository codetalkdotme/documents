package com.newcare.auth.dao;

import com.newcare.auth.pojo.ServiceTicket;

/**
 * 
 * 认证DAO接口
 * 
 * @author guobxu
 *
 */
public interface IAuthDao {

	public void setServiceTicket(long userId, ServiceTicket ticket);
	public ServiceTicket getServiceTicket(long userId);
	
}
