package com.newcare.im.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newcare.auth.pojo.ReqAuthResult;
import com.newcare.auth.pojo.SvcAuthResult;
import com.newcare.constant.Constants;
import com.newcare.fnd.enums.NoticeMode;
import com.newcare.fnd.service.INoticeService;
import com.newcare.im.auth.service.IMAuthService;
import com.newcare.im.enums.MesgStatus;
import com.newcare.im.exception.IMServiceException;
import com.newcare.im.login.service.LoginService;
import com.newcare.im.mesg.service.IMMessageService;
import com.newcare.im.pojo.Login;
import com.newcare.im.pojo.Message;
import com.newcare.im.protocal.ProtocalPackage;
import com.newcare.im.service.IMCallbackService;
import com.newcare.im.service.IMService;
import com.newcare.mesg.MessageService;

/**
 * 
 * @author guobxu
 *
 */
@Service("imService")
public class IMServiceImpl implements IMService {

	private static ObjectMapper MAPPER = new ObjectMapper();
	
	private ExecutorService asyncRunner = Executors.newFixedThreadPool(3);
	
	private Logger LOGGER = LoggerFactory.getLogger(IMServiceImpl.class);
	
	@Autowired
	private MessageService mesgService;
	
	@Autowired
	private IMAuthService imAuthService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private IMMessageService imesgService;
	
	@Autowired
	private IMCallbackService imCbService;
	
	@Autowired
	private INoticeService noticeService;
	
//	public static final String CONSUMER_CONFIG = "consumer.xml";
	
	protected static final String MSGUP_RET_FORMAT = "{\"ret_code\":%d, \"ret_msg\":\"%s\", \"ret_data\": {\"msg_sn\":%d}}";
	protected static final String MSGDOWN_FORMAT = "{\"from_user_id\": %d, \"msg_sn\": %d, \"msg\": \"%s\", \"new_count\": %d}";
	
	@Transactional
	public void serviceLogin(ProtocalPackage pack) {
		asyncRunner.submit(new Runnable() {

			@Override
			public void run() {
				SvcAuthResult rt = null;
				ProtocalPackage rtPack = new ProtocalPackage();
				rtPack.setHeadUrl(Constants.CMD_SC_LOGIN);
				rtPack.setSessionId(pack.getSessionId());
				rtPack.setProxyIp(pack.getProxyIp());
				try {
					rt = imAuthService.serviceLogin(pack);
					
					if(rt.getCode() != SvcAuthResult.SUCCESS.getCode()) {
						rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, mesgService.get(rt.getErrKey())));
	        		} else {
	        			rtPack.setContent(Constants.RESPONSE_SUCCESS);
	        		}
				} catch(Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					
					rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, ex.getMessage()));
				}
				
				imCbService.doCallback(rtPack);
			}
			
		});
	}
	
	public void msgup(ProtocalPackage pack) {
		asyncRunner.submit(new Runnable() {

			@Override
			public void run() {
				String content = pack.getContent(), 
						sessionId = pack.getSessionId(),
						clientIp = pack.getClientIp(),
						proxyIp = pack.getProxyIp();
				
				ProtocalPackage rtPack = new ProtocalPackage();
				rtPack.setHeadUrl(Constants.CMD_SC_MSGUP);
				rtPack.setSessionId(sessionId);
				rtPack.setProxyIp(proxyIp);
				
				ProtocalPackage msgdownPack = new ProtocalPackage();
				msgdownPack.setHeadUrl(Constants.CMD_SC_MSGDOWN);
				
				Map<String, String> params = null;
				String srcTypeParam = null, authStrParam = null, msgParam = null;
				Long userIdParam = null, toUserParam = null, snParam = null;
				
				try {
					params = MAPPER.readValue(content, new TypeReference<HashMap<String, String>>() {});
					
					srcTypeParam = params.get("src_type");
					authStrParam = params.get("auth_str");
					msgParam = params.get("msg");
					userIdParam = Long.parseLong(params.get("user_id"));
					toUserParam = Long.parseLong(params.get("to_user_id"));
					snParam = Long.parseLong(params.get("msg_sn"));
				} catch(Exception ex) {
					String errmsg = mesgService.get("im_error_parse_content");
					LOGGER.error(errmsg, ex);
					
					rtPack.setContent(String.format(MSGUP_RET_FORMAT, Constants.CODE_ERROR, errmsg, snParam));
					imCbService.doCallback(rtPack);
					return;
				}
				
				// 通信認證
				ReqAuthResult rt = null;
				try {
					rt = imAuthService.requestAuth(sessionId, userIdParam, srcTypeParam, authStrParam);
					
					if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {	// 通信认证失败
						String errmsg = mesgService.get(rt.getErrKey());
						rtPack.setContent(String.format(MSGUP_RET_FORMAT, Constants.CODE_ERROR, errmsg, snParam));
						imCbService.doCallback(rtPack);
						
						return;
					}
				} catch(IMServiceException ex) {
					rtPack.setContent(String.format(MSGUP_RET_FORMAT, Constants.CODE_ERROR, 
							mesgService.get("reqauth_err_unexpected"), snParam));
					imCbService.doCallback(rtPack);

					return;
				}
				
				// 保存消息
				Message mesg = new Message();
				mesg.setSn(snParam);
				mesg.setContent(msgParam);
				mesg.setFromUser(userIdParam);
				mesg.setToUser(toUserParam);
				mesg.setFromSession(sessionId);
				mesg.setStatus(MesgStatus.NOTSENT.getCode());
				mesg.setThread(getMesgThread(userIdParam, toUserParam));
				
				imesgService.addMessage(mesg);
				// 响应
				rtPack.setContent(String.format(MSGUP_RET_FORMAT, Constants.CODE_SUCCESS, "", snParam));
				imCbService.doCallback(rtPack);
				
				// 发送通知 app端 - 0703: 仅发送app短消息 
				noticeService.addIMNotice(userIdParam, toUserParam, srcTypeParam, mesg.getId(), msgParam, NoticeMode.APP);
				
				Login login = loginService.findActiveLogin(toUserParam);
				if(login == null) {
					// 0703: 如果不在线则发送push消息
					noticeService.addIMNotice(userIdParam, toUserParam, srcTypeParam, mesg.getId(), msgParam, NoticeMode.PUSH);
				} else {
					msgdownPack.setProxyIp(login.getProxyIp());
					msgdownPack.setSessionId(login.getSessionId());
					
					int newCount = imesgService.countUnreadMessage(userIdParam, toUserParam);
					msgdownPack.setContent(String.format(MSGDOWN_FORMAT, userIdParam, snParam, msgParam, newCount));
					
					imCbService.doCallback(msgdownPack);
				}
			}
			
		});
	}
	
	@Override
	public void msgack(ProtocalPackage pack) throws Exception {
		asyncRunner.submit(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("in msgack...");
				
				String content = pack.getContent();
				
				Map<String, Object> params = null;
				Long retCodeParam = null, snParam = null;
				
				try {
					params = MAPPER.readValue(content, new TypeReference<HashMap<String, Object>>() {});;
					
					retCodeParam = Long.parseLong(params.get("ret_code").toString());
					Map<String, Object> retData = (Map<String, Object>)params.get("ret_data");
					snParam = Long.parseLong(retData.get("msg_sn").toString());
				} catch(Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					
					return;
				}
				
				// 更新
				if(retCodeParam == 1L) {
					Message mesg = new Message();
					mesg.setSn(snParam);
					mesg.setStatus(MesgStatus.RECEIVED.getCode());
//					mesg.setAckDate(new Date(System.currentTimeMillis()));
					
					imesgService.updateMessageBySn(mesg);
				}
			}
			
		});
	}
	
	@Override
	public void getMesgCount(ProtocalPackage pack) throws Exception {
		asyncRunner.submit(new Runnable() {

			@Override
			public void run() {
				String content = pack.getContent(), 
						sessionId = pack.getSessionId(),
						proxyIp = pack.getProxyIp();
				
				ProtocalPackage rtPack = new ProtocalPackage();
				rtPack.setHeadUrl(Constants.CMS_SC_MSGCOUNT);
				rtPack.setSessionId(sessionId);
				rtPack.setProxyIp(proxyIp);
				
				Map<String, String> params = null;
				String srcTypeParam = null, authStrParam = null;
				Long userIdParam = null;
				
				try {
					params = MAPPER.readValue(content, new TypeReference<HashMap<String, String>>() {});
					
					srcTypeParam = params.get("src_type");
					authStrParam = params.get("auth_str");
					userIdParam = Long.parseLong(params.get("user_id"));
				} catch(Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					
					rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, ex.getMessage()));
					imCbService.doCallback(rtPack);
					return;
				}
				
				// 通信認證
				ReqAuthResult rt = null;
				try {
					rt = imAuthService.requestAuth(sessionId, userIdParam, srcTypeParam, authStrParam);
					
					if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {	// 通信认证失败
						String errmsg = mesgService.get(rt.getErrKey());
						rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, errmsg));
						imCbService.doCallback(rtPack);
						
						return;
					}
				} catch(IMServiceException ex) {
					rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, 
							mesgService.get("reqauth_err_unexpected")));
					imCbService.doCallback(rtPack);

					return;
				}
				
				// 读取消息
				List<Map<String, Object>> mcList = imesgService.findMessageCount(userIdParam);
				if(mcList != null && mcList.size() > 0) {
					List<Message> lastMessages = imesgService.listLastMessages(userIdParam);
					if(lastMessages != null && lastMessages.size() > 0) {
						for(Map<String, Object> rec : mcList) {
							Long toUser = Long.parseLong(rec.get("to_user_id").toString());
							
							for(Message mesg : lastMessages) {
								if(toUser.equals(mesg.getFromUser()) || toUser.equals(mesg.getToUser())) {
									rec.put("last_msg", mesg.getContent());
									
									// last_arrive_or_sendtime	2017/06/02
									Timestamp sendDate = mesg.getSendDate();
									if(sendDate != null) {
										rec.put("last_arrive_or_sendtime", sendDate.getTime());
									} else {
										rec.put("last_arrive_or_sendtime", mesg.getReceiveDate().getTime());
									}
									break;
								}
							}
						}
					}
				}
				
				Map<String, Object> rtMap = new HashMap<String, Object>();
				rtMap.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
				rtMap.put(Constants.KEY_DATA, mcList);
				
				try {
					rtPack.setContent(MAPPER.writeValueAsString(rtMap));
				} catch(Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					
					return;
				}
				
				imCbService.doCallback(rtPack);
			}
			
		});
	}

	@Override
	public void getMesgList(ProtocalPackage pack) throws Exception {
		asyncRunner.submit(new Runnable() {

			@Override
			public void run() {
				String content = pack.getContent(), 
						sessionId = pack.getSessionId(),
						proxyIp = pack.getProxyIp();
				
				ProtocalPackage rtPack = new ProtocalPackage();
				rtPack.setHeadUrl(Constants.CMD_SC_GETMSG);
				rtPack.setSessionId(sessionId);
				rtPack.setProxyIp(proxyIp);
				
				Map<String, String> params = null;
				String srcTypeParam = null, authStrParam = null;
				Long userIdParam = null, toUserParam = null, beginParam = null, countParam = null;
				
				try {
					params = MAPPER.readValue(content, new TypeReference<HashMap<String, String>>() {});
					
					srcTypeParam = params.get("src_type");
					authStrParam = params.get("auth_str");
					userIdParam = Long.parseLong(params.get("user_id"));
					toUserParam = params.get("to_user_id") == null ? null : Long.parseLong(params.get("to_user_id"));
					beginParam = Long.parseLong(params.get("begin"));
					countParam = Long.parseLong(params.get("count"));
				} catch(Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					
					rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, ex.getMessage()));
					imCbService.doCallback(rtPack);
					return;
				}
				
				// 通信認證
				ReqAuthResult rt = null;
				try {
					rt = imAuthService.requestAuth(sessionId, userIdParam, srcTypeParam, authStrParam);
					
					if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {	// 通信认证失败
						String errmsg = mesgService.get(rt.getErrKey());
						rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, errmsg));
						imCbService.doCallback(rtPack);
						
						return;
					}
				} catch(IMServiceException ex) {
					rtPack.setContent(String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, 
							mesgService.get("reqauth_err_unexpected")));
					imCbService.doCallback(rtPack);

					return;
				}
				
				// 读取消息
				List<Map<String, Object>> mesgList = null;
				Map<String, Object> queryParams = new HashMap<String, Object>();
				queryParams.put("userId", userIdParam);
				queryParams.put("begin", beginParam);
				queryParams.put("count", countParam);
				
				if(toUserParam == null) {
					mesgList = imesgService.findMessageList(queryParams);
				} else {
					queryParams.put("toUser", toUserParam);
					mesgList = imesgService.findToUserMessageList(queryParams);
				}
				Map<String, Object> rtMap = new HashMap<String, Object>();
				rtMap.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
				rtMap.put(Constants.KEY_DATA, mesgList);
				
				try {
					rtPack.setContent(MAPPER.writeValueAsString(rtMap));
				} catch(Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					
					return;
				}
				
				boolean sent = imCbService.doCallback(rtPack);
				
				// 更新消息状态
				if(sent && mesgList != null && mesgList.size() > 0) {
					List<Long> mesgIdList = new ArrayList<Long>();
					for(Map<String, Object> rec : mesgList) {
						String updown = rec.get("updown").toString();
						if("2".equals(updown)) {
							mesgIdList.add(Long.parseLong(rec.get("msg_id").toString()));
						}
					}
					
					if(mesgIdList.size() > 0) {
						imesgService.batchRecvById(mesgIdList);
						
						// 更新通知状态
						noticeService.updateIMReadByList(mesgIdList);
//						if(SourceType.HECADRE.getEnName().equals(srcTypeParam)) {
//							noticeService.updateAllRead(userIdParam, SourceType.HECADRE.getCode(), NoticeType.HQUERY.getCode());
//						} else if(SourceType.INHABITANT.getEnName().equals(srcTypeParam)) {
//							noticeService.updateAllRead(userIdParam, SourceType.INHABITANT.getCode(), NoticeType.IMESG.getCode());
//						}
					}
				}
			}
			
		});
	}
	
	public void disconnect(String uuid) throws Exception {
		asyncRunner.submit(new Runnable() {

			@Override
			public void run() {
				try {
					imAuthService.serviceLogout(uuid);
				} catch(IMServiceException ex) {
					ex.printStackTrace();
					
					LOGGER.error(ex.getMessage(), ex);
				}
			}
			
		});
	}

//	private IMCallbackService getImCbService() {
//		if(imCbService == null) {
//			synchronized(this) {
//				if(imCbService == null) {
//					ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONSUMER_CONFIG);
//					
//					imCbService = (IMCallbackService)applicationContext.getBean("imCbService");
//				}
//			}
//		}
//		
//		return imCbService;
//	}
	
	private String getMesgThread(Long fromUser, Long toUser) {
		if(fromUser < toUser) {
			return fromUser.toString() + "-" + toUser.toString();
		} else {
			return toUser.toString() + "-" + fromUser.toString();
		}
	}

	@Override
	public Map<Long, Integer> countSlowRepliesWithin(List<Long> userIdList, Long startTime, Long endTime, Long timeInterval) {
		if(userIdList == null || userIdList.size() == 0) return null; // @0620
		
		return imesgService.countSlowRepliesWithin(userIdList, startTime, endTime, timeInterval);
	}

	@Override
	public List<Long> lazyHedcareListWithin(Long timeInterval) {
		return imesgService.lazyHedcareListWithin(timeInterval);
	}
	
	
}



























