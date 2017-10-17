package com.newcare.p3.jsms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.newcare.p3.jsms.pojo.JsmsMsg;

public interface JsmsMapper {

	public void insertJsmsMsg(JsmsMsg msg);
	
	public void insertJsmsMsgList(List<JsmsMsg> msgList);
	
	public void updateMsgSent(Long msgId);
	
	public void updateMsgSentByList(List<Long> msgIdList);

	public List<JsmsMsg> selectJsmsMsg(Map<String, Object> params);
	
	public List<Map<String, Object>> selectHAlertMaxDateIn(@Param("inClause") String inClause);
	
}
