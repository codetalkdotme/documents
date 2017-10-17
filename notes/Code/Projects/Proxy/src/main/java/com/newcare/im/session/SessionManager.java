package com.newcare.im.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import com.newcare.constant.Constants;

/**
 * 會話管理組件
 * 
 * @author guobxu
 *
 */
@Component("sessionManager")
public class SessionManager {

	// UUID 到 IoSession的映射
	Map<String, IoSession> sessionMap = new ConcurrentHashMap<String, IoSession>();
	
	public IoSession getSession(String uuid) {
		return sessionMap.get(uuid);
	}

	public void addSession(IoSession session) {
		String uuid = UUID.randomUUID().toString();

		session.setAttribute(Constants.KEY_SESSION_UUID, uuid);
		sessionMap.put(uuid, session);
	}

	public void removeSession(IoSession session) {
		String uuid = (String) session.getAttribute(Constants.KEY_SESSION_UUID);

		sessionMap.remove(uuid);
	}
	
}
