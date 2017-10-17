package com.newcare.p3.jpush.service;

/**
 * 极光推送
 * @author guobxu
 *
 */
public interface IJpushService {

	/**
	 * 推送通知
	 * @param sourceType 
	 * @param platform 	平台
	 * @param alias		别名
	 * @param content	通知内容
	 * @return
	 */
	public boolean pushNotif(Integer sourceType, String platform, String alias, String content);
	
}
