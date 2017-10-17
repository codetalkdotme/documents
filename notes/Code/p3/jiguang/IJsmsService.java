package com.newcare.p3.jsms.service;

import java.util.List;
import java.util.Map;

import com.newcare.p3.jsms.pojo.JsmsMsg;

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
	
	// 发送短消息, 并更新数据
	public boolean sendSms(JsmsMsg msg);
	
	// 添加专干上级通知, 包含数据: // hecadreUid/supervisorUid/hecadreName/hecadreMobile/supervisorMobile/orderType
	public void addHecadreAlert(Map<String, Object> data);
	
	// 添加专干上级通知(批量)
	public void addHecadreAlertList(List<Map<String, Object>> dataList);
	
	// 发送账号创建短信
	// 您好，您的新康医疗后台管理账号已创建，账号：{{accountName}}；密码：{{passwd}}，后台网址：admin.newcaresz.com:8081，请妥善保管账号！
	public boolean sendAccountCreationSms(String mobile, String accountName, String passwd);
	
}







