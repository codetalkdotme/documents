package com.newcare.im.protocal;

import java.io.Serializable;

import com.newcare.constant.Constants;

/**
 * Created by wangxuhaoon 2017/4/20.
 */

public class ProtocalPackage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String headUrl;// 报文请求头url
	private String content; // 报文内容

	private String sessionId;
	private String clientIp;
	private String proxyIp;
	
	public ProtocalPackage() {
	}

	public ProtocalPackage(String headUrl, String content) {
		this.headUrl = headUrl;
		this.content = content;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLength() throws Exception {
		return 4 + 4 + headUrl.getBytes(Constants.ENCODING_UTF8).length + 
				content.getBytes(Constants.ENCODING_UTF8).length;
	}
	
	// 重写toString方法
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("heanUrl:").append(headUrl);
		sb.append("content:").append(content);
		return sb.toString();
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
