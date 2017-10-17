package com.newcare.p3.jpush.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.newcare.auth.dao.IAuthDao;
import com.newcare.auth.pojo.ServiceTicket;
import com.newcare.cache.service.ICacheService;
import com.newcare.fnd.enums.PfType;
import com.newcare.fnd.enums.PushStatus;
import com.newcare.fnd.enums.SourceType;
import com.newcare.fnd.mapper.NoticeMapper;
import com.newcare.fnd.pojo.Notice;
import com.newcare.p3.jpush.service.IJpushService;
import com.newcare.util.DateUtils;
import com.newcare.util.StringUtils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.NativeHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

@Service("pushService")
public class JpushServiceImpl implements IJpushService {

	private static Logger LOGGER = LoggerFactory.getLogger(JpushServiceImpl.class);
	
	// 专干端
	public static final String JPUSH_HEC_APP_KEY ="194193953cf89c881cefbc10";
	public static final String JPUSH_HEC_MASTER_SECRET = "ce07e08b17ccc93c1bbd360c";
	
	// 居民端
	public static final String JPUSH_RES_APP_KEY ="d31dfa8351a8609be39bafdd";
	public static final String JPUSH_RES_MASTER_SECRET = "6fb1e4ba6d7ef02a833d05e4";
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private IAuthDao authDao;
	
	@Autowired
	private NoticeMapper noticeMapper;
	
	// 专干推送客户端
	private JPushClient hecPushClient;
	// 居民推送客户端
	private JPushClient resPushClient;
	
	private void initClients() {
		ClientConfig clientConfig = ClientConfig.getInstance();
		
		hecPushClient = new JPushClient(JPUSH_HEC_MASTER_SECRET, JPUSH_HEC_APP_KEY, null, clientConfig);
        String hecAuthCode = ServiceHelper.getBasicAuthorization(JPUSH_HEC_APP_KEY, JPUSH_HEC_MASTER_SECRET);
        NativeHttpClient hecHttpClient = new NativeHttpClient(hecAuthCode, null, clientConfig);
        hecPushClient.getPushClient().setHttpClient(hecHttpClient);
        
        resPushClient = new JPushClient(JPUSH_RES_MASTER_SECRET, JPUSH_RES_APP_KEY, null, clientConfig);
        String resAuthCode = ServiceHelper.getBasicAuthorization(JPUSH_RES_APP_KEY, JPUSH_RES_MASTER_SECRET);
        NativeHttpClient resHttpClient = new NativeHttpClient(resAuthCode, null, clientConfig);
        resPushClient.getPushClient().setHttpClient(resHttpClient);
	}
	
	private boolean pushNotif(JPushClient pushClient, PushPayload payload) {
		try {
        	PushResult result = pushClient.sendPush(payload);
        	
        	return result.isResultOK() ? true : false;
        } catch (APIConnectionException e) {
        	LOGGER.error("Connection error. Should retry later. ", e);
        	LOGGER.error("Sendno: " + payload.getSendno());
        } catch (APIRequestException e) {
        	LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
        	LOGGER.info("HTTP Status: " + e.getStatus());
        	LOGGER.info("Error Code: " + e.getErrorCode());
        	LOGGER.info("Error Message: " + e.getErrorMessage());
        	LOGGER.info("Msg ID: " + e.getMsgId());
        	LOGGER.error("Sendno: " + payload.getSendno());
        }

        return false;
	}
	
	@Override
	public boolean pushNotif(Integer sourceType, String platform, String alias, String content) {
		LOGGER.info("In pushNotif...sourceType=" + sourceType + ", platform=" + platform + ", alias=" + alias + ", content=" + content);
		
		if(sourceType != SourceType.HECADRE.getCode() && sourceType != SourceType.INHABITANT.getCode()) {
			LOGGER.info("错误的source type, 忽略此条推送通知!");
			return false;
		}
		
		if(!PfType.ANDROID.getName().equals(platform) && !PfType.IOS.getName().equals(platform)) {
			LOGGER.info("错误的platform, 忽略此条推送通知!");
			return false;
		}
		
		if(StringUtils.isNull(content)) {
			LOGGER.info("通知内容为空, 忽略此条推送通知!");
			return false;
		}
		
		// 推送通知
		ClientConfig clientConfig = ClientConfig.getInstance();
		
		String appKey = null, secret = null;
		if(sourceType == SourceType.HECADRE.getCode()) {
			appKey = JPUSH_HEC_APP_KEY;
			secret = JPUSH_HEC_MASTER_SECRET;
		} else if(sourceType == SourceType.INHABITANT.getCode()) {
			appKey = JPUSH_RES_APP_KEY;
			secret = JPUSH_RES_MASTER_SECRET;
		}
		
        JPushClient jpushClient = new JPushClient(secret, appKey, null, clientConfig);
        String authCode = ServiceHelper.getBasicAuthorization(appKey, secret);
        NativeHttpClient httpClient = new NativeHttpClient(authCode, null, clientConfig);
        jpushClient.getPushClient().setHttpClient(httpClient);
        
        PushPayload payload = PushPayload.newBuilder()
						        .setPlatform(PfType.ANDROID.getName().equals(platform) ? Platform.android() : Platform.ios())
						        .setAudience(Audience.alias(alias))
						        .setNotification(Notification.alert(content))
						        .build();
        
        return pushNotif(jpushClient, payload);
	}
	
	// 定时任务每次执行处理的消息数量
	public static final int COUNT_EACH_RUN = 30;
	public static final int MAX_DAYS_BEFORE = 5;
	
	// 重发次数超过5次, 标记为不予发送
	public static final String CACHE_JPUSH_ERRORS = "JPUSH_ERRORS";
	
	public static final int MAX_PUSH_ERRORS = 3;
	
	// 10-22小时每分钟执行一次
	@Scheduled(cron = "0 0/1 * * * ?")
	public void runJpushTask() {
		LOGGER.info("In runJpushTask...");
		
		String lockKey = "JpushTask-" + DateUtils.formatDate(new Date(), "yyyyMMddHHmm");
		if(!cacheService.setNX(lockKey, "X")) return;
		
		List<Notice> noticeList = noticeMapper.listUnreadPushNotice(MAX_DAYS_BEFORE, COUNT_EACH_RUN);
		if(noticeList == null || noticeList.size() == 0) {
			LOGGER.info("未发现需要推送的通知消息...");
			
			return;
		}
		
		// 初始化推送客户端
		initClients();
		
		// 更新为正在推送
		List<Long> allNoticeId = new ArrayList<Long>();
		for(Notice notice : noticeList) {
			allNoticeId.add(notice.getId());
		}
		noticeMapper.updatePushStatusByList(allNoticeId, PushStatus.PUSHING.getCode());
		
		// 列表: 不予推送 & 已推送 & 未推送
		List<Long> ignoreList = new ArrayList<Long>(), 
				pushedList = new ArrayList<Long>(),
				errorList = new ArrayList<Long>();
		
		for(Notice notice : noticeList) {
			Long noticeId = notice.getId();
			Integer sourceType = notice.getSrcType();
			Long userId = notice.getUserId();
			String content = notice.getContent();
			LOGGER.info("Notice props...sourceType=" + sourceType + ", userId=" + userId + ", content=" + content);
			
			if(sourceType != SourceType.HECADRE.getCode() && sourceType != SourceType.INHABITANT.getCode()) {
				LOGGER.info("错误的source type, 忽略此条推送通知!");
				ignoreList.add(noticeId);
				
				continue;
			}
			
			if(StringUtils.isNull(content)) {
				LOGGER.info("通知内容为空, 忽略此条推送通知!");
				
				ignoreList.add(noticeId);
				continue;
			}
			
			ServiceTicket ticket = authDao.getServiceTicket(userId);
			if(ticket == null) {
				LOGGER.info("未找到用户登录信息, 标记为不予推送...Notice Id=" + noticeId);
				ignoreList.add(noticeId);
			} else {
				String pfType = ticket.getPfType();
				if(!PfType.ANDROID.getName().equals(pfType) && !PfType.IOS.getName().equals(pfType)) {
					LOGGER.info("未知的平台类型, 标记为不予推送...Platform Type=" + pfType);
					ignoreList.add(noticeId);
				} else { // 推送
					LOGGER.info("开始推送通知, Notice Id=" + noticeId + ", Platform=" + pfType);
					
					PushPayload payload = PushPayload.newBuilder()
					        .setPlatform(PfType.ANDROID.getName().equals(pfType) ? Platform.android() : Platform.ios())
					        .setAudience(Audience.alias(String.valueOf(userId)))
					        .setNotification(Notification.alert(content))
					        .build();

					boolean rtFlag = pushNotif(sourceType == SourceType.HECADRE.getCode() ? hecPushClient : resPushClient, payload);
					
					if(rtFlag) {
						LOGGER.info("推送成功, Notice Id=" + noticeId);
						
						pushedList.add(noticeId);
					} else {
						LOGGER.info("推送失败, Notice Id=" + noticeId);
						
						int errors = getPushErrors(noticeId);
						if(errors < MAX_PUSH_ERRORS) { // 小于最大失败次数, 添加到失败队列
							errorList.add(noticeId);
							
							cacheService.hset(CACHE_JPUSH_ERRORS, String.valueOf(noticeId), String.valueOf(errors + 1));
						} else {
							ignoreList.add(noticeId);
						}
					}
				}
			}
		}
		
		// 更新通知消息状态
		if(ignoreList.size() > 0) noticeMapper.updatePushStatusByList(ignoreList, PushStatus.IGNORE.getCode());
		if(pushedList.size() > 0) noticeMapper.updatePushStatusByList(pushedList, PushStatus.PUSHED.getCode());
		if(errorList.size() > 0) noticeMapper.updatePushStatusByList(errorList, PushStatus.NOPUSH.getCode());
	}
	
	// 获取推送失败次数
	private int getPushErrors(Long noticeId) {
		Object obj = cacheService.hget(CACHE_JPUSH_ERRORS, String.valueOf(noticeId));
		if(obj == null) return 0;
		
		return Integer.parseInt(obj.toString());
	}
	
	// 每小时第三十分钟执行一次, 清除上一小时的锁
	@Scheduled(cron = "0 30 * * * ?")
	public void clearJpushLock() {
		LOGGER.info("In clearJpushLock...");
		
		Date oneHourBack = new Date(System.currentTimeMillis() - 1 * 60 * 60 * 1000);
		String prefix = "JpushTask-" + DateUtils.formatDate(oneHourBack, "yyyyMMddHH");
		
		String[] keys = new String[60];
		for(int i = 0; i < 60; i++) {
			String key = prefix;
			if(i < 10) {
				key += "0";
			}
			key += i;
			
			keys[i] = key;
		}
		
		LOGGER.info("In clearJpushLock...delete: " + Arrays.toString(keys));
		cacheService.delete(keys);
	}

}














