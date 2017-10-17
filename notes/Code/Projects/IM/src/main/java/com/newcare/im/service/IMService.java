package com.newcare.im.service;

import java.util.List;
import java.util.Map;

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
    
    // 统计未及时回复数
    public Map<Long, Integer> countSlowRepliesWithin(List<Long> userIdList, Long startTime, Long endTime, Long timeInterval);
    
    // 获取指定时间内未及时处理消息所有专干ID
    public List<Long> lazyHedcareListWithin(Long timeInterval);

}
