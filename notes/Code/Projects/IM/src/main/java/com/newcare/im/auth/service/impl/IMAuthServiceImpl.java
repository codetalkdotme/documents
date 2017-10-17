package com.newcare.im.auth.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newcare.auth.pojo.ReqAuthResult;
import com.newcare.auth.pojo.SvcAuthResult;
import com.newcare.auth.service.ICipherService;
import com.newcare.constant.Constants;
import com.newcare.im.auth.service.IMAuthService;
import com.newcare.im.exception.IMServiceException;
import com.newcare.im.login.service.LoginService;
import com.newcare.im.pojo.Login;
import com.newcare.im.protocal.ProtocalPackage;
import com.newcare.mesg.MessageService;
import com.newcare.util.StringUtils;

/**
 * 
 * IM认证服务
 * 
 * @author guobxu
 *
 */
@Service("imAuthService")
public class IMAuthServiceImpl implements IMAuthService {

	private static ObjectMapper MAPPER = new ObjectMapper();

	private Logger LOGGER = LoggerFactory.getLogger(IMAuthServiceImpl.class);

	@Autowired
	private ICipherService cipherService;
	
	@Autowired
	private MessageService mesgService;
	
	@Autowired
	private LoginService loginService;

	private ThreadLocal<SimpleDateFormat> threadFormatter = new ThreadLocal<SimpleDateFormat>();
	
	/**
	 * 
	 * { 
	 * "src_type":"xx" 			//请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" 			//请求源的终端平台类型，如"Android"、"iOS"、"Web"等 "
	 * "user_id":0L 			//用户ID，u64 
	 * "auth_str":"xx" 			//通信认证密文串 
	 * "svc_ticket_str":"xx" 	//服务票据密文串 
	 * }
	 * 
	 */
	@Transactional
	public SvcAuthResult serviceLogin(ProtocalPackage pack) throws IMServiceException {
		String url = pack.getHeadUrl(), content = pack.getContent(), 
				sessionId = pack.getSessionId(), clientIp = pack.getClientIp(),
				proxyIp = pack.getProxyIp();

		Map<String, String> params = readContentAsMap(content);

		String srcTypeParam = params.get("src_type"), pfTypeParam = params.get("pf_type"),
				userIdParam = params.get("user_id"), authStrParam = params.get("auth_str"),
				svcTicketParam = params.get("svc_ticket_str");

		// 服务票据明文: 传输密钥 + 请求源类型/登录名 + 日期
		List<String> ticketTokens = decipherAndParse(svcTicketParam, Constants.UNI_SERVICE_KEY, true, true);
		if (ticketTokens == null) {
			return SvcAuthResult.ERRTICKET;
		}

		String transportKey = ticketTokens.get(0), ticketSrcType = ticketTokens.get(1),
				ticketLoginName = ticketTokens.get(2), ticketDate = ticketTokens.get(3);
		// 请求源类型
		if (StringUtils.isNull(srcTypeParam) || !srcTypeParam.equals(ticketSrcType)
				|| (!Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam)
						&& !Constants.SOURCE_TYPE_INHABITANT.equals(srcTypeParam))) {
			return SvcAuthResult.ERRSRCTYPE;
		}

		try {
			if (isSvcTicketExipred(ticketDate)) {
				return SvcAuthResult.ERRTIMEOUT;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
			return SvcAuthResult.ERRTICKET;
		}

		// 明文: "登录名+前端系统UTC时间yyyyMMddhhmmss"
		List<String> tokens = decipherAndParse(authStrParam, transportKey, false, false);
		if (tokens == null) {
			return SvcAuthResult.ERRAUTHSTR;
		}
		
		String requestDate = tokens.get(1), authLoginName = tokens.get(0);
		try {
			if (isRequestExipred(requestDate)) {
				throw new IMServiceException(mesgService.get("im_error_req_timeout"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
			return SvcAuthResult.ERRAUTHSTR;
		}

		// 对比登录名
		if (ticketLoginName == null || authLoginName == null || !ticketLoginName.equals(authLoginName)) {
			return SvcAuthResult.ERRNAME;
		}

		// 登出已有会话
		Long userId = Long.parseLong(userIdParam);
		loginService.logoutUser(userId);
		
		// 保存登录
		Login login = new Login();
		login.setUserId(userId);
		login.setSessionId(sessionId);
		login.setLoginName(ticketLoginName);
		login.setSrcType(ticketSrcType);
		login.setTransportKey(transportKey);
		login.setTicketCreateDate(ticketDate);
		login.setClientIp(clientIp);
		login.setProxyIp(proxyIp);
		
		loginService.addLogin(login);
		
		return SvcAuthResult.SUCCESS;
	}

	@Transactional
	public void serviceLogout(String sessionId) throws IMServiceException {
		loginService.logoutSession(sessionId);
	}
	
	@Override
	public ReqAuthResult requestAuth(String sessionId, Long userId, String srcType, String authStr) throws IMServiceException {
		Login login = loginService.findLoginBySession(sessionId);
		if(login == null) {
			return ReqAuthResult.ERRNOTKT;
		}
		
		try {
			if(isSvcTicketExipred(login.getTicketCreateDate())) {
				return ReqAuthResult.ERRTKTIMEOUT;
			}
		} catch (Exception ex) {
			return ReqAuthResult.ERRUNEXPECTED;
		}
		
		// 请求源类型
		if (!login.getSrcType().equals(srcType)) {
			return ReqAuthResult.ERRSRCTYPE;
		}

		// 明文: "登录名+前端系统UTC时间yyyyMMddhhmmss"
		List<String> tokens = decipherAndParse(authStr, login.getTransportKey(), false, false);
		if (tokens == null) {
			return ReqAuthResult.ERRAUTHSTR;
		}

		// 登录名 和 日期
		String nameToken = tokens.get(0), dateToken = tokens.get(1);
		if (!login.getLoginName().equals(nameToken)) {
			return ReqAuthResult.ERRNAME;
		}

		try {
			if (isRequestExipred(dateToken)) {
				return ReqAuthResult.ERRTIMEOUT;
			}
		} catch (Exception ex) {
			return ReqAuthResult.ERRAUTHSTR;
		}
		
		return ReqAuthResult.SUCCESS;
	}
	
	private Map<String, String> readContentAsMap(String content) throws IMServiceException {
		Map<String, String> map = null;
		
		try {
			map = MAPPER.readValue(content, new TypeReference<HashMap<String, String>>() {});
		} catch(Exception ex) {
			ex.printStackTrace();
			
			String errmsg = mesgService.get("im_error_parse_content");
			LOGGER.error(errmsg, ex);
			
			throw new IMServiceException(errmsg, ex);
		}

		return map;
	}

	private void logAndThrow(String key, Exception ex) throws IMServiceException {
		String errmsg = mesgService.get(key);
		LOGGER.error(errmsg, ex);
		
		throw new IMServiceException(errmsg, ex);
	}
	
	private void logAndThrow(String key) throws IMServiceException {
		String errmsg = mesgService.get(key);
		LOGGER.error(errmsg);
		
		throw new IMServiceException(errmsg);
	}
	
	private List<String> decipherAndParse(String ciphered, String key, boolean withKey, boolean withSrcType) throws IMServiceException {
		String clearText = null;
		try {
			clearText = cipherService.decipher(ciphered, key);
		} catch(Exception ex) {
			ex.printStackTrace();
			
			logAndThrow("im_decipher_error", ex);
		}
		
		List<String> tokens = parseClearText(clearText, withKey, withSrcType);
		
		return tokens;
	}
	
	// 返回null表示解析失败
	private List<String> parseClearText(String clearText, boolean withKey, boolean withSrcType) {
		int minLen = Constants.AUTH_DATE_LEN;
		if (withKey) {
			minLen += Constants.AUTH_KEY_LEN;
		}

		if (withSrcType) {
			minLen += 3; // srcType和登录名使用 / 分隔
		} else {
			minLen += 1;
		}

		int len = clearText.length();
		if (len < minLen)
			return null;

		List<String> listStr = new ArrayList<String>();
		if (withKey) {
			listStr.add(clearText.substring(0, 32)); // key
			if (withSrcType) {
				String tmp = clearText.substring(32, len - 14);
				String[] tmparr = tmp.split(Constants.AUTH_SRCNAME_SEP);

				if (tmparr.length != 2)
					return null;
				listStr.add(tmparr[0]); // src type
				listStr.add(tmparr[1]); // login name
			} else {
				listStr.add(clearText.substring(32, len - 14)); // login name
			}
		} else {
			if (withSrcType) {
				String tmp = clearText.substring(0, len - 14);
				String[] tmparr = tmp.split(Constants.AUTH_SRCNAME_SEP);

				if (tmparr.length != 2)
					return null;
				listStr.add(tmparr[0]); // src type
				listStr.add(tmparr[1]); // login name
			} else {
				listStr.add(clearText.substring(0, len - 14)); // login name
			}
		}
		listStr.add(clearText.substring(len - 14)); // date

		return listStr;
	}

	private SimpleDateFormat getDateFormatter() {
		SimpleDateFormat formatter = threadFormatter.get();
		if (formatter == null) {
			formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			threadFormatter.set(formatter);
		}

		return formatter;
	}

	private boolean isRequestExipred(String dateToken) throws Exception {
		return isExpired(dateToken, Constants.REQUEST_TIMEOUT);
	}

	private boolean isSvcTicketExipred(String dateToken) throws Exception {
		return isExpired(dateToken, Constants.SERVICE_TICKET_TIMEOUT);
	}

	private boolean isExpired(String dateToken, long timeout) throws Exception {
		SimpleDateFormat formatter = getDateFormatter();

		Date date = formatter.parse(dateToken), now = new Date();
		if (Math.abs(now.getTime() - date.getTime()) > timeout) {
			return true;
		}

		return false;
	}

}












