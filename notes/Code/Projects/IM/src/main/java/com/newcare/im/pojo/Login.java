package com.newcare.im.pojo;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 
 * IM服务登录
 * 
 * @author guobxu
 *
 */
public class Login {

	private Long id;
	private Long userId;
	private String sessionId; 			// 会话UUID
	private int status;					// 1 有效 2 失效
	private String loginName;			// 登录名
	private String srcType;				// 登录源
	private String transportKey; 		// 传输密钥
	private String ticketCreateDate;	// 服务密钥创建时间
	private String clientIp;			// 客户端IP
	private String proxyIp;				// 代理IP
	private Timestamp logoutDate;
	private Timestamp createDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getSrcType() {
		return srcType;
	}
	public void setSrcType(String srcType) {
		this.srcType = srcType;
	}
	public String getTransportKey() {
		return transportKey;
	}
	public void setTransportKey(String transportKey) {
		this.transportKey = transportKey;
	}
	public String getTicketCreateDate() {
		return ticketCreateDate;
	}
	public void setTicketCreateDate(String ticketCreateDate) {
		this.ticketCreateDate = ticketCreateDate;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getProxyIp() {
		return proxyIp;
	}
	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}
	public Timestamp getLogoutDate() {
		return logoutDate;
	}
	public void setLogoutDate(Timestamp logoutDate) {
		this.logoutDate = logoutDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	
}
