package com.newcare.im.service;

import com.newcare.im.exception.IMServiceException;
import com.newcare.im.protocal.ProtocalPackage;

/**
 * 
 * 回调消息服务
 * 
 * @author guobxu
 *
 */
public interface IMCallbackService {

	/**
	 * 回调代理服务器, 用于处理消息下行
	 * 
	 * @param pack
	 * @throws IMServiceException
	 * 
	 * @return true表示已发送 false表示未发送
	 */
	public boolean doCallback(ProtocalPackage pack);
	
}
