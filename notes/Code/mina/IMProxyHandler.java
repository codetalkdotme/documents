package com.newcare.im;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.newcare.constant.Constants;
import com.newcare.im.protocal.ProtocalPackage;
import com.newcare.im.service.IMService;
import com.newcare.im.session.SessionManager;

/**
 * 
 * 处理来自客户端的TCP 连接 以及 消息
 * 
 * @author guobxu
 *
 */
@Component("proxyHandler")
public class IMProxyHandler extends IoHandlerAdapter {

	private Logger LOGGER = LoggerFactory.getLogger(IMProxyHandler.class);
	
	private IMService imService;
	
	@Autowired
	private SessionManager sessionMgr;

	/********************************** Session **********************************/

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		sessionMgr.addSession(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// ...
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		sessionMgr.removeSession(session);
		
		// 服务端记录连接已经关闭
		String uuid = (String)session.getAttribute(Constants.KEY_SESSION_UUID);
		imService.disconnect(uuid);
	}

	/********************************** Message **********************************/

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		LOGGER.info("enter messageReceived...");

		ProtocalPackage pack = (ProtocalPackage)message;
		String sessionId = getSessionId(session), localIp = getLocalIp(session), 
				clientIp = getClientIp(session);
		pack.setSessionId(sessionId);
		pack.setProxyIp(localIp);
		pack.setClientIp(clientIp);
		
	    String url = pack.getHeadUrl();
	    if(Constants.CMD_CS_LOGIN.equals(url)) { // 服务登录
	    	imService.serviceLogin(pack);
	    } else if(Constants.CMD_CS_MSGUP.equals(url)) {	// 消息上行
	    	imService.msgup(pack);
	    } else if(Constants.CMD_CS_MSGDOWN.equals(url)) {	// 消息下行响应
//	    	imService.msgack(pack);
	    } else if(Constants.CMD_CS_MSGCOUNT.equals(url)) { // 获取消息数量
	    	imService.getMesgCount(pack);
	    } else if(Constants.CMD_CS_GETMSG.equals(url)) { // 获取消息
	    	imService.getMesgList(pack);
	    }
		
		LOGGER.info("leave messageReceived...");
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// ...
	}

	/********************************** Exception **********************************/

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}
	
	/********************************** Helper Methods **********************************/
	
	private String getSessionId(IoSession session) {
		String sessionId = (String)session.getAttribute(Constants.KEY_SESSION_UUID);
		
		return sessionId;
	}
	
	private String getClientIp(IoSession session) {
		InetSocketAddress sockaddr = (InetSocketAddress)session.getRemoteAddress();
		
		InetAddress address = sockaddr.getAddress();
		
		return address.getHostAddress();
	}
	
	private String getLocalIp(IoSession session) {
		InetSocketAddress sockaddr = (InetSocketAddress)session.getLocalAddress();

		InetAddress address = sockaddr.getAddress();
		
		return address.getHostAddress();
	}

	public IMService getImService() {
		return imService;
	}
	
	public void setImService(IMService imService) {
		this.imService = imService;
	}
	
	
}








