package com.newcare.im.service;

import com.newcare.im.protocal.ProtocalPackage;

/**
 * 
 * IM服务接口
 * 
 * @author guobxu
 *
 */
public interface IMService {
	
    // 服务登录
	public void serviceLogin(ProtocalPackage pack) throws Exception;
	
	// 消息上行
	public void msgup(ProtocalPackage pack) throws Exception;
	
	// 消息回应
	public void msgack(ProtocalPackage pack) throws Exception;
	
	// 获取聊天数量
	public void getMesgCount(ProtocalPackage pack) throws Exception;
	
	// 获取聊天记录
	public void getMesgList(ProtocalPackage pack) throws Exception;
	
    // 断开连接, 异步处理
    public void disconnect(String uuid) throws Exception;

}
