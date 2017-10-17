package com.newcare.im.mesg.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.im.mesg.mapper.MessageMapper;
import com.newcare.im.mesg.service.IMMessageService;
import com.newcare.im.pojo.Message;

@Service("imesgService")
public class IMMessageServiceImpl implements IMMessageService {

	@Autowired
	private MessageMapper mesgMapper;
	
	public void addMessage(Message mesg) {
		mesgMapper.insertMessage(mesg);
	}

	@Override
	public void updateMessage(Message mesg) {
		mesgMapper.updateMessage(mesg);
	}

	@Override
	public void updateMessageBySn(Message mesg) { // TODO 根据session拿到user_id 作为to_user_id 联合更新
		mesgMapper.updateMessageBySn(mesg);
	}

	@Override
	public List<Map<String, Object>> findMessageCount(Long userId) {
		List<Map<String, Object>> mcList = mesgMapper.selectMessageCount(userId);
		
		return mcList;
	}

	@Override
	public List<Map<String, Object>> findToUserMessageList(Map<String, Object> params) {
		List<Map<String, Object>> mesgList = mesgMapper.selectToUserMessageList(params);
		
		return convertMesgList(mesgList, params.get("userId").toString());
	}

	@Override
	public List<Map<String, Object>> findMessageList(Map<String, Object> params) {
		List<Map<String, Object>> mesgList = mesgMapper.selectMessageList(params);
		
		return convertMesgList(mesgList, params.get("userId").toString());
	}
	
	public void batchRecvById(List<Long> mesgIdList) {
		mesgMapper.batchRecvById(mesgIdList);
	}
	
	private List<Map<String, Object>> convertMesgList(List<Map<String, Object>> mesgList, String currentUser) {
		// from_user_id, to_user_id, msg_sn, msg_content, receive_date, ack_date
		// "updown":0 "sendtime":0L "arrivetime":0L "msg_sn":0L "msg":"xx" 
		if(mesgList == null || mesgList.size() == 0) return mesgList;
		
		List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> rec : mesgList) {
			Map<String, Object> tmp = new HashMap<String, Object>();
			if(currentUser.equals(rec.get("from_user_id").toString())) {
				tmp.put("updown", 1);
			} else {
				tmp.put("updown", 2);
			}
			
			Timestamp recvDate = (Timestamp)rec.get("receive_date");
			tmp.put("sendtime", recvDate.getTime());
			
			Timestamp arrvDate = (Timestamp)rec.get("send_date");
			// TODO: 根据updown作判断
			tmp.put("arrivetime", arrvDate == null ? 0L : String.valueOf(arrvDate.getTime()));
			
			tmp.put("msg_sn", rec.get("mesg_sn"));
			tmp.put("msg", rec.get("mesg_content"));
			
			tmp.put("msg_id", Long.parseLong(rec.get("mesg_id").toString()));
			
			Long fromUser = Long.parseLong(rec.get("from_user_id").toString()),
					toUser = Long.parseLong(rec.get("to_user_id").toString());
			tmp.put("to_user_id", currentUser.equals(fromUser) ? toUser : fromUser);
			
			rtList.add(tmp);
		}
		
		return rtList;
	}

	@Override
	public int countUnreadMessage(Long fromUser, Long toUser) {
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("fromUser", fromUser);
		params.put("toUser", toUser);
		
		return mesgMapper.countUnreadMessage(params);
	}

	@Override
	public List<Message> listLastMessages(Long userId) {
		List<Long> mesgIdList = mesgMapper.selectLastMesgIdList(userId);
		if(mesgIdList == null || mesgIdList.size() == 0) return null;
		
		
		return mesgMapper.selectMessageIn(mesgIdList);
	}

	@Override
	public Map<Long, Integer> countSlowRepliesWithin(List<Long> userIdList, Long startTime, Long endTime,
			Long timeInterval) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userIdList", userIdList);
		params.put("startTime", new Timestamp(startTime));
		params.put("endTime", new Timestamp(endTime));
		params.put("timeSeconds", timeInterval / 1000);
		
		List<Map<String, Object>> recList = mesgMapper.countSlowRepliesWithin(params);
		
		Map<Long, Integer> rt = new HashMap<Long, Integer>();
		for(Long userId : userIdList) {
			rt.put(userId, 0);
		}
		
		if(recList != null && recList.size() > 0) {
			for(Map<String, Object> rec : recList) {
				Long userId = Long.parseLong(rec.get("user_id").toString());
				rt.put(userId, Integer.parseInt(rec.get("msg_count").toString()));
			}
		}
		
		return rt;
	}

	@Override
	public List<Long> lazyHedcareListWithin(Long timeInterval) {
		Long timeSeconds = timeInterval / 1000;
		
		return mesgMapper.lazyHedcareListWithin(timeSeconds);
	}
	
}























