package com.newcare.p3.jsms.service;

import java.util.Map;

/**
 * 极光发送SMS短信
 * @author guobxu
 *
 */
public interface IJsmsService {

	/**
	 * 
	 * @param mobile
	 * @param temp
	 * @param params
	 */
	public boolean sendSms(String mobile, Integer temp, Map<String, String> params);
	
	// 发送验证码
	public boolean sendSmsCode(String mobile, String smsCode);
	
}
