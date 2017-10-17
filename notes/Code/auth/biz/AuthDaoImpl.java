package com.newcare.auth.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import com.newcare.auth.dao.IAuthDao;
import com.newcare.auth.pojo.ServiceTicket;
import com.newcare.auth.pojo.User;
import com.newcare.cache.service.ICacheService;
import com.newcare.util.StringUtils;

/**
 * 
 * @author guobxu
 *
 */
@Component("authDao")
public class AuthDaoImpl implements IAuthDao {

	// 服务票据key前缀
	public static final String SVC_TICKET_PREFIX = "SVC-TICKET-";
	
	@Autowired
	private ICacheService cacheService;
	
	@Override
	public void setServiceTicket(long userId, ServiceTicket ticket) {
		String key = SVC_TICKET_PREFIX + userId; 
		cacheService.set(key, ticket);
	}

	@Override
	public ServiceTicket getServiceTicket(long userId) {
		String key = SVC_TICKET_PREFIX + userId; 
		return (ServiceTicket)cacheService.get(key);
	}

}












