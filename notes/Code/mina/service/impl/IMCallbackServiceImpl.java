package com.newcare.im.service.impl;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.im.exception.IMServiceException;
import com.newcare.im.protocal.ProtocalPackage;
import com.newcare.im.service.IMCallbackService;
import com.newcare.im.session.SessionManager;

/**
 * 
 * @author guobxu
 *
 */
@Service("imCbService")
public class IMCallbackServiceImpl implements IMCallbackService {

	@Autowired
	private SessionManager sessionMgr;
	
	// 登录消息响应
	public static final String CMD_SC_LOGIN = "|^|^XXXX/api/im/sc/login^|^|";
	
	// 消息上行响应
	public static final String CMD_SC_MSGUP = "|^|^XXXX/api/im/sc/msgup^|^|";
	
	// 消息下行
	public static final String CMD_SC_MSGDOWN = "|^|^XXXX/api/im/sc/msgdown^|^|";
	
	// 消息数量响应
	public static final String CMD_SC_MSGCOUNT = "|^|^XXXX/api/im/sc/getmsgcount^|^|";
	
	// 消息列表响应
	public static final String CMD_SC_MSGLIST = "|^|^XXXX/api/im/sc/getmsg^|^|";
	
	@Override
	public boolean doCallback(ProtocalPackage pack) {
		
		String sessionId = pack.getSessionId();
		IoSession session = sessionMgr.getSession(sessionId);
		
		if(session != null) {
			session.write(pack);
			return true;
		}
		
		return false;
	}

}
