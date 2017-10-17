package com.newcare.auth.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger LOGGER = LoggerFactory.getLogger(AuthDaoImpl.class);
	
	public static final String CACHE_USERS = "USERS";
	public static final String CACHE_USER_NAME_ID = "USER_NAME_ID";	// 登录名 <=> UID
	public static final String CACHE_USER_PID_ID = "USER_PID_ID";	// 身份证 <=> UID 
	public static final String CACHE_USER_DID_ID = "USER_DID_ID";	// 设备ID <=> UID 
	
	public static final String CACHE_USER_KEY = "KEY_USER_ID";
	
	// SMS缓存前缀
	public static final String SMS_KEY_PREFIX = "SMS-";
	
	// 服务票据key前缀
	public static final String SVC_TICKET_PREFIX = "SVC-TICKET-";
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public User getUserByName(String loginName) {
		String userId = StringUtils.toString(cacheService.hget(CACHE_USER_NAME_ID, loginName), false);
		if(StringUtils.isNull(userId)) {
			userId = StringUtils.toString(cacheService.hget(CACHE_USER_PID_ID, loginName), false);
		}
		
		return StringUtils.isNull(userId) ? null : (User)cacheService.hget(CACHE_USERS, userId);
	}
	
	@Override
	public User getUserById(Long uid) {
		return (User)cacheService.hget(CACHE_USERS, String.valueOf(uid));
	}

	@Override
	public User getUserByPid(String pid) {
		String userId = StringUtils.toString(cacheService.hget(CACHE_USER_PID_ID, pid), false);
		
		return StringUtils.isNull(userId) ? null : (User)cacheService.hget(CACHE_USERS, userId);
	}
	
	@Override
	public List<User> getUsersByIds(long[] uids) {
		LOGGER.info("getUsersByIds - long[] uids: " + Arrays.toString(uids));
		
		if(uids == null || uids.length == 0) return null;
		
		String[] keys = new String[uids.length];
		for(int i = 0; i < keys.length; i++) {
			keys[i] = String.valueOf(uids[i]);
		}
		
		return getUsersByIds(keys);
	}
	
	@Override
	public List<User> getUsersByIds(List<Long> uids) {
		LOGGER.info("getUsersByIds - List<Long> uids: " + uids);
		
		if(uids == null || uids.size() == 0) return null;
		
		String[] keys = new String[uids.size()];
		for(int i = 0; i < keys.length; i++) {
			keys[i] = String.valueOf(uids.get(i));
		}
		
		return getUsersByIds(keys);
	}
	
	@Override
	public List<User> getUsersByIds(String[] uids) {
		LOGGER.info("getUsersByIds - String[] uids: " + Arrays.toString(uids));
		
		if(uids == null || uids.length == 0) return null;
		
		List<Object> objList = cacheService.hMGet(CACHE_USERS, uids);
		List<User> userList = new ArrayList<User>();
		for(Object obj : objList) {
			if(obj != null) {
				userList.add((User)obj);
			}
		}
		
		return userList;
	}

	@Override
	public List<User> getUsersByPids(String[] pids) {
		LOGGER.info("getUsersByPids - String[] pids: " + Arrays.toString(pids));
		
		if(pids == null || pids.length == 0) return null;
		
		List<Object> objList = cacheService.hMGet(CACHE_USER_PID_ID, pids);
		List<String> uids = new ArrayList<String>();
		for(int i = 0; i < objList.size(); i++) {
			Object obj = objList.get(i);
			
			if(obj != null) {
				uids.add(obj.toString());
			}
		}
		
		String[] keys = new String[uids.size()];
		uids.toArray(keys);
		
		return getUsersByIds(keys);
	}

	@Override
	public long addUser(User user) {
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
		RedisCallback<Long> callback = new RedisCallback<Long>() {
		    @Override
		    public Long doInRedis(RedisConnection connection) throws DataAccessException {
		    	String loginName = user.getLoginName(), pid = user.getPersonId();
		    	Long id = connection.incr(CACHE_USER_KEY.getBytes());
		    	
		    	// MUTLI is currently not supported in cluster mode.
		    	connection.multi();
		    	
		    	String idStr = String.valueOf(id);
		    	user.setId(id);
		    	connection.hSet(CACHE_USERS.getBytes(), idStr.getBytes(), serializer.serialize(user));
		      
		    	// 映射
		    	if(!StringUtils.isNull(loginName)) {
		    		connection.hSet(CACHE_USER_NAME_ID.getBytes(), loginName.getBytes(), idStr.getBytes());
		    	}
		    	if(!StringUtils.isNull(pid)) {
		    		connection.hSet(CACHE_USER_PID_ID.getBytes(), pid.getBytes(), idStr.getBytes());
		    	}
	
		    	connection.exec();
		    	return id;
		    }
		};
		
		return redisTemplate.execute(callback);
	}

	@Override
	public void updateUser(User user) {
		cacheService.hset(CACHE_USERS, String.valueOf(user.getId()), user);
	}

	@Override
	public User getUserByDeviceId(String deviceId) {
		String userId = StringUtils.toString(cacheService.hget(CACHE_USER_DID_ID, deviceId), false);
		
		return StringUtils.isNull(userId) ? null : (User)cacheService.hget(CACHE_USERS, userId);
	}
	
	@Override
	public boolean exists(User user) {
		String loginName = user.getLoginName(), pid = user.getPersonId();
		
		return redisTemplate.execute(new RedisCallback<Boolean>() {

		    @Override
		    public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
		    	if(!StringUtils.isNull(loginName) && 
		    			connection.hGet(CACHE_USER_NAME_ID.getBytes(), loginName.getBytes()) != null) {
		    		return true;
			    }
		    	if(!StringUtils.isNull(pid) && 
		    			connection.hGet(CACHE_USER_PID_ID.getBytes(), pid.getBytes()) != null) {
		    		return true;
			    }
		    	
		    	return false;
		    }

		});
	}

	@Override
	public void updateUserAndDevice(User user, String newDeviceId) {
		updateUserPidAndDevice(user, null, newDeviceId);
	}
	
	@Override
	public void updateUserPidAndDevice(User user, String newPid, String newDeviceId) {
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
		RedisCallback callback = new RedisCallback() {
		    @Override
		    public Object doInRedis(RedisConnection connection) throws DataAccessException {
		    	// MUTLI is currently not supported in cluster mode.
		    	connection.multi();
		      
		    	// 删除旧PiD映射
		    	String oldPid = user.getPersonId();
		    	if(newPid != null && !StringUtils.isNull(oldPid)) {
		    		connection.hDel(CACHE_USER_PID_ID.getBytes(), oldPid.getBytes());
		    	}
		      
		    	// 删除旧device映射
		    	String oldDeviceId = user.getDeviceId();
		    	if(newDeviceId != null && !StringUtils.isNull(oldDeviceId)) {
		    		connection.hDel(CACHE_USER_DID_ID.getBytes(), oldDeviceId.getBytes());
		    	}
		      
		    	String idStr = String.valueOf(user.getId());
		    	if(newPid != null) user.setPersonId(newPid);
		    	if(newDeviceId != null) user.setDeviceId(newDeviceId);
		    	connection.hSet(CACHE_USERS.getBytes(), idStr.getBytes(), serializer.serialize(user));
		      
		    	// 设置新的PID映射
		    	if(!StringUtils.isNull(newPid)) {
		    		connection.hSet(CACHE_USER_PID_ID.getBytes(), newPid.getBytes(), idStr.getBytes());
		    	}
		      
		    	// 设置新的device映射
		    	if(!StringUtils.isNull(newDeviceId)) {
		    		connection.hSet(CACHE_USER_DID_ID.getBytes(), newDeviceId.getBytes(), idStr.getBytes());
		    	}
		      
		    	connection.exec();
		      
		    	return null;
		    }
		};
		
		redisTemplate.execute(callback);
	}

	@Override
	public String getSmsCode(String mobile, String type) {
		String key = SMS_KEY_PREFIX + mobile + "-" + type;
		
		return StringUtils.toString(cacheService.get(key), false);
	}

	@Override
	public void deleteSmsCode(String mobile, String type) {
		String key = SMS_KEY_PREFIX + mobile + "-" + type;
		cacheService.delete(key);
	}
	
	@Override
	public void setSmsCode(String mobile, String type, String smsCode, long timeout) {
		String key = SMS_KEY_PREFIX + mobile + "-" + type;
		cacheService.set(key, smsCode, timeout);
	}

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

	@Override
	public void unbindDevice(Long userId) {
		User user = getUserById(userId);
		if(user == null) return;	// defensive
		
		String deviceId = user.getDeviceId();
		
		if(!StringUtils.isNull(deviceId)) {
			updateUserAndDevice(user, "");
		}
	}

}












