package com.newcare.im.mesg.service;

import java.util.List;
import java.util.Map;

import com.newcare.im.pojo.Message;

/**
 * 
 * IM消息服务
 * 
 * @author guobxu
 *
 */
public interface IMMessageService {

	public void addMessage(Message mesg);
	
	public void updateMessage(Message mesg);
	
	public void updateMessageBySn(Message mesg);
	
	public List<Map<String, Object>> findMessageCount(Long userId);
	
	public List<Map<String, Object>> findToUserMessageList(Map<String, Object> params);
	
	public List<Map<String, Object>> findMessageList(Map<String, Object> params);
	
	public void batchRecvById(List<Long> mesgIdList);
	
	public int countUnreadMessage(Long fromUser, Long toUser);
	
	public List<Message> listLastMessages(Long userId);
	
	public Map<Long, Integer> countSlowRepliesWithin(List<Long> userIdList, Long startTime, Long endTime, Long timeInterval);
	
	public List<Long> lazyHedcareListWithin(Long timeInterval);
	
}
















