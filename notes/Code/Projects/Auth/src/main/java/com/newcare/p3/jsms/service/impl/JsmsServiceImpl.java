package com.newcare.p3.jsms.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.newcare.p3.jsms.service.IJsmsService;

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
	
	private SMSClient client = new SMSClient(JSMS_MASTER_SECRET, JSMS_APP_KEY);
	
	// JSMS 发送验证码
	public static final Integer TEMPL_SMS = 93311;
	// JSMS 发送专干上级短信
	public static final Integer TEMPL_HECADRE_ALERT = 93324;
	
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
	
	public boolean sendSmsCode(String mobile, String smsCode) {
		Map<String, String> smsParams = new HashMap<String, String>();
		smsParams.put("smsCode", smsCode);
		
		return sendSms(mobile, TEMPL_SMS, smsParams);
	}
	
}
