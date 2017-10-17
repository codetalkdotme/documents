package com.newcare.im.mesg.mapper;

import java.util.List;
import java.util.Map;

import com.newcare.im.pojo.Message;

/**
 * 消息MAPPER
 * 
 * @author guobxu
 *
 */
public interface MessageMapper {

	public void insertMessage(Message mesg);
	
	public void updateMessage(Message mesg);
	
	public void updateMessageBySn(Message mesg);
	
	public List<Map<String, Object>> selectMessageCount(Long userId);
	
	public List<Map<String, Object>> selectToUserMessageList(Map<String, Object> params);
	
	public List<Map<String, Object>> selectMessageList(Map<String, Object> params);
	
	public void batchRecvById(List<Long> mesgIdList);
	
	// 来自fromUser的消息未读数量
	public int countUnreadMessage(Map<String, Long> params);
	
	public List<Long> selectLastMesgIdList(Long userId); 
	
	public List<Message> selectMessageIn(List<Long> mesgIdList);
	
	public List<Map<String, Object>> countSlowRepliesWithin(Map<String, Object> params);

	public List<Long> lazyHedcareListWithin(Long timeSeconds);
	
}
