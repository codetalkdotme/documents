package com.newcare.p3.jsms.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.newcare.cache.service.ICacheService;
import com.newcare.p3.jsms.enums.JsmsMsgStatus;
import com.newcare.p3.jsms.mapper.JsmsMapper;
import com.newcare.p3.jsms.pojo.JsmsMsg;
import com.newcare.p3.jsms.service.IJsmsService;
import com.newcare.util.DateUtils;
import com.newcare.util.StringUtils;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;
import cn.jsms.api.common.model.SMSPayload.Builder;

/**
 * 
 * @author guobxu
 *
 */
@Service("smsService")
public class JsmsServiceImpl implements IJsmsService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(JsmsServiceImpl.class);
	
	public static final String JSMS_APP_KEY ="61832993e8166b738de67f99";
	public static final String JSMS_MASTER_SECRET = "9e113fe4c162bddaf3dfe489";
	
	// JSMS 发送验证码
	public static final Integer TEMPL_SMS = 93311;
	
	// JSMS 发送专干上级短信
	public static final Integer TEMPL_HECADRE_ALERT = 93324;
	
	// JSMS 发送账号创建短信
	public static final Integer TEMPL_ACCOUNT_CREATION = 110030;
	
	public static final String ATTR_HECADRE_ALERT = "HecadreAlert";
	
	private SMSClient client = new SMSClient(JSMS_MASTER_SECRET, JSMS_APP_KEY);
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private JsmsMapper smsMapper;
	
	public boolean sendSms(String mobile, Integer temp, Map<String, String> params) {
		Builder builder = SMSPayload.newBuilder()
				.setMobildNumber(mobile)
				.setTempId(temp);
		
		if(params != null) {
			for(Map.Entry<String, String> entry : params.entrySet()) {
				builder.addTempPara(entry.getKey(), entry.getValue());
			}
		}
		
		SMSPayload payload = builder.build();
		try {
			SendSMSResult res = client.sendTemplateSMS(payload);
			return res.isResultOK() ? true : false;
		} catch (APIConnectionException e) {
			LOGGER.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
			LOGGER.info("HTTP Status: " + e.getStatus());
			LOGGER.info("Error Message: " + e.getMessage());
		}
		
		return false;
	}

	@Override
	public boolean sendSms(JsmsMsg msg) {
		boolean rt = sendSmsOnly(msg);
		
		if(rt) {
			smsMapper.updateMsgSent(msg.getId());
		}
		
		return rt;
	}

	// 仅发送sms, 不更新数据库
	private boolean sendSmsOnly(JsmsMsg msg) {
		Map<String, String> params = new HashMap<String, String>();
		if(msg.getParam1() != null) {
			params.put(msg.getParam1(), msg.getValue1());
		}
		if(msg.getParam2() != null) {
			params.put(msg.getParam2(), msg.getValue2());
		}
		if(msg.getParam3() != null) {
			params.put(msg.getParam3(), msg.getValue3());
		}
		if(msg.getParam4() != null) {
			params.put(msg.getParam4(), msg.getValue4());
		}
		if(msg.getParam5() != null) {
			params.put(msg.getParam5(), msg.getValue5());
		}
		
		return sendSms(msg.getMobile(), msg.getTemp(), params);
	}
	
	// 定时任务每次执行处理的消息数量
	public static final int COUNT_EACH_RUN = 20;
	
	// 10-22小时每分钟执行一次
	@Scheduled(cron = "0 0/1 10-22 * * ?")
	public void runJsmsTask() {
		LOGGER.info("In runJsmsTask...");
		
		String lockKey = "JsmsTask-" + DateUtils.formatDate(new Date(), "yyyyMMddHHmm");
		if(!cacheService.setNX(lockKey, "X")) return;
		
		List<JsmsMsg> msgList = findJMsgNotSent(COUNT_EACH_RUN);
		if(msgList == null || msgList.size() == 0) {
			return;
		}
		
		// 发送短消息
		for(JsmsMsg msg : msgList) {
			sendSms(msg);
		}
	}
	
	// 10-22小时每第三十分钟执行一次, 清除上一小时的锁
	@Scheduled(cron = "0 30 10-23 * * ?")
	public void clearJsmsLock() {
		LOGGER.info("In clearJsmsLock...");
		
		Date oneHourBack = new Date(System.currentTimeMillis() - 1 * 60 * 60 * 1000);
		String prefix = "JsmsTask-" + DateUtils.formatDate(oneHourBack, "yyyyMMddHH");
		
		String[] keys = new String[60];
		for(int i = 0; i < 60; i++) {
			String key = prefix;
			if(i < 10) {
				key += "0";
			}
			key += i;
			
			keys[i] = key;
		}
		
		LOGGER.info("In clearJsmsLock...delete: " + Arrays.toString(keys));
		cacheService.delete(keys);
	}
	
	private List<JsmsMsg> findJMsgNotSent(int count) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", JsmsMsgStatus.NOTSENT.getCode());
		params.put("begin", 0);
		params.put("count", count);
		
		return smsMapper.selectJsmsMsg(params);
	}

	@Override
	public void addHecadreAlert(Map<String, Object> data) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		dataList.add(data);
		
		addHecadreAlertList(dataList);
	}

	@Override
	public void addHecadreAlertList(List<Map<String, Object>> dataList) { // hecadreUid/supervisorUid/hecadreName/hecadreMobile/supervisorMobile/orderType
		if(dataList == null || dataList.size() == 0) return;
		
		// current
		Long current = System.currentTimeMillis();
		
		String inClause = getInClauseByDataList(dataList);
		LOGGER.info("In addHecadreAlertList...inClause: " + inClause);
		if(StringUtils.isNull(inClause)) return;
		// supervisorUid, hecadreUid, createDate
		List<Map<String, Object>> rtMapList = smsMapper.selectHAlertMaxDateIn(inClause);
		
		// 去重
		List<Map<String, Object>> filterMapList = new ArrayList<Map<String, Object>>();
		// 针对同一个专干, 最多添加一条消息
		Set<String> hiSet = new HashSet<String>();
		for(Map<String, Object> dataMap : dataList) {
			boolean ignore = false;
			Long interval = 2 * 60 * 60 * 1000L; // 2小时不重发
			
			String suidStr = StringUtils.toString(dataMap.get("supervisorUid"), false), 
					huidStr = StringUtils.toString(dataMap.get("hecadreUid"), false);
			for(Map<String, Object> rtDataMap : rtMapList) {
				String rtSuidStr = rtDataMap.get("supervisorUid").toString(), 
						rtHuidStr = rtDataMap.get("hecadreUid").toString();
				Timestamp latest = (Timestamp)rtDataMap.get("createDate");
				
				if(rtSuidStr.equals(suidStr) && rtHuidStr.equals(huidStr)
						&& current - latest.getTime() < interval) {
					ignore = true;
					break;
				}
			}
			
			String key = suidStr + "|" + huidStr;
			if(!ignore && !hiSet.contains(key)) {
				filterMapList.add(dataMap);
				hiSet.add(key);
				
				LOGGER.info("SMS message to be sent: " + dataMap);
			} else {
				LOGGER.info("SMS message ignored: " + dataMap);
			}
		}
		
		if(filterMapList.size() == 0) return;
		
		List<JsmsMsg> msgList = convertHAlertAsJMsg(filterMapList);
		smsMapper.insertJsmsMsgList(msgList);
	}
	
	// 转换专干上级通知为Jmsg
	private List<JsmsMsg> convertHAlertAsJMsg(List<Map<String, Object>> dataList) { // hecadreUid/supervisorUid/hecadreName/hecadreMobile/supervisorMobile/orderType
		List<JsmsMsg> msgList = new ArrayList<JsmsMsg>();
		
		for(Map<String, Object> dataMap : dataList) {
			JsmsMsg msg = new JsmsMsg();
			msg.setTemp(TEMPL_HECADRE_ALERT);
			msg.setAttribute1(ATTR_HECADRE_ALERT);
			msg.setMobile(dataMap.get("supervisorMobile").toString());
			msg.setParam1("supervisorUid");
			msg.setValue1(dataMap.get("supervisorUid").toString());
			msg.setParam2("hecadreUid");
			msg.setValue2(dataMap.get("hecadreUid").toString());
			msg.setParam3("hecadreName");
			msg.setValue3(dataMap.get("hecadreName").toString());
			msg.setParam4("hecadreMobile");
			msg.setValue4(dataMap.get("hecadreMobile").toString());
			msg.setParam5("orderType");
			msg.setValue5(dataMap.get("orderType").toString());
			
			msgList.add(msg);
		}
		
		return msgList;
	}
	
	private String getInClauseByDataList(List<Map<String, Object>> dataList) {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		
		Set<String> hiSet = new HashSet<String>(); // 用于去重
		for(int i = 0, j = dataList.size(); i < j; i++) {
			Map<String, Object> alert = dataList.get(i);
			
			Object sidObj = alert.get("supervisorUid"), hidObj = alert.get("hecadreUid");
			if(sidObj == null || hidObj == null) continue;
			
			// 检查重复
			String key = sidObj.toString() + "|" + hidObj.toString();
			if(hiSet.contains(key)) continue;
			hiSet.add(key);
			
			if(buf.length() > 1) {
				buf.append(", ");
			}
			
			buf.append("('").append(sidObj.toString()).append("', '")
				.append(hidObj.toString()).append("')");
		}
		
		buf.append(")");
		
		return buf.length() > 2 ? buf.toString() : "";
	}
	
	private String getHecadreAlertInClause(List<Map<String, Object>> dataList) {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		
		Set<String> hiSet = new HashSet<String>(); // 用于去重
		for(int i = 0, j = dataList.size(); i < j; i++) {
			Map<String, Object> alert = dataList.get(i);
			
			Object sidObj = alert.get("supervisorUid"), hidObj = alert.get("hecadreUid");
			if(sidObj == null || hidObj == null) continue;
			
			// 检查重复
			String key = sidObj.toString() + "|" + hidObj.toString();
			if(hiSet.contains(key)) continue;
			hiSet.add(key);
			
			if(buf.length() > 1) buf.append(",");
			buf.append("('").append(sidObj.toString()).append("', '")
				.append(hidObj.toString()).append("')");
		}
		buf.append(")");
		
		return buf.toString();
	}

	@Override
	public boolean sendAccountCreationSms(String mobile, String accountName, String passwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("accountName", accountName);
		params.put("passwd", passwd);
		
		return sendSms(mobile, TEMPL_ACCOUNT_CREATION, params);
	}
	
}

















