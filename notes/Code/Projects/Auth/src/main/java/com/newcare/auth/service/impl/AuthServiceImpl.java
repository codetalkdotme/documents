package com.newcare.auth.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newcare.auth.dao.IAuthDao;
import com.newcare.auth.exception.AuthServiceException;
import com.newcare.auth.pojo.ReqAuthResult;
import com.newcare.auth.pojo.ServiceTicket;
import com.newcare.auth.pojo.User;
import com.newcare.auth.service.IAuthService;
import com.newcare.auth.service.ICipherService;
import com.newcare.constant.Constants;
import com.newcare.mesg.MessageService;
import com.newcare.p3.jsms.service.IJsmsService;
import com.newcare.util.MapSupportUtil;
import com.newcare.util.StringUtils;

/**
 * 
 * @author guobxu
 *
 * Created: 04/14
 *
 */
@Service("authService")
public class AuthServiceImpl implements IAuthService {

	private static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	private static ObjectMapper MAPPER = new ObjectMapper();
	
	private MapSupportUtil<User> mapUtil = new MapSupportUtil<User>();
	
	private ThreadLocal<SimpleDateFormat> threadFormatter = new ThreadLocal<SimpleDateFormat>();
	
	public static final int FIRST_LOGIN_YES = 1;
	public static final int FIRST_LOGIN_NO = 2;
	
	public static final String URL_GET_USERBASICINFO = "/hca/api/auth/getuserbasicinfo";
	public static final String URL_GET_SMSCODE = "/hca/api/auth/getsmscode";
	public static final String URL_ADD_USER = "/hca/api/auth/adduser";
	public static final String URL_REGISTER_USER = "/hca/api/auth/registeruser";
	public static final String URL_USER_LOGIN = "/hca/api/auth/userlogin";
	public static final String URL_GET_USERINFO = "/hca/api/auth/getuserinfo";
	public static final String URL_SET_USERINFO = "/hca/api/auth/setuserinfo";
	public static final String URL_RESET_USERPASSWD = "/hca/api/auth/resetuserpasswd";
	public static final String URL_SET_USERMOBILE = "/hca/api/auth/setusermobile";

	/***************** 验证码类别 *****************/
	public static final String SMS_TYPE_REG = "1"; // 注册
	public static final String SMS_TYPE_SETPWD = "2"; // 重置密码
	public static final String SMS_TYPE_SETMOBILE = "3"; // 更换手机号码
	public static final String SMS_TYPE_GRANT = "4"; // 特殊授权
	
	public static final int SMS_CODE_TIMEOUT = 30 * 60 * 1000; // 短信验证码过期时间: 30分钟

	// 认证成功
	public static final String AUTH_SUCCESS_TMPL = "{\"ret_code\":1, "
													+ "\"ret_data\": {"
															+ "\"user_id\": %d, "
															+ "\"auth_ret_str\": \"%s\", "
															+ "\"svc_ticket_str\":\"%s\", "
															+ "\"is_first_login\": %d"
													+  "}"
												+ "}";
	
	// 根据设备ID查找用户
	public static final String AUTH_USERBASCIINFO_TMPL = "{\"ret_code\":1, "
														+ "\"ret_data\": {"
																+ "\"login_name\": \"%s\", "
																+ "\"name\": \"%s\""
														+  "}"
													+ "}";
	
	public static final String AUTH_ADDUSER_TMPL = "{\"ret_code\":1, "
													+ "\"ret_data\": {"
													+ "\"user_id\": %d"
													+  "}"
												 + "}";
	
	@Autowired
	private IJsmsService smsService;
	
	@Autowired
	private IAuthDao authDao;
	
	@Autowired
	private ICipherService cipherService;
	
	@Autowired
	private MessageService messageService;
	
	@Override
	public String doPost(String uri, Map<String, Object> data) throws AuthServiceException {
		LOGGER.info("Auth doPost start...uri: " + uri);
		
		try {
			String rtStr = doPostInternal(uri, data);
			// LOGGER.info("In doPost...Result: " + rtStr);
			
			return rtStr;
		} catch(Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage(), ex);
			
			throw new AuthServiceException(ex);
		}
	}
	
	private String doPostInternal(String uri, Map<String, Object> data) throws Exception {
		if(URL_GET_USERBASICINFO.equals(uri)) {
			return getUserBasicInfo(data);
		} else if(URL_GET_SMSCODE.equals(uri)) {
			return getSmsCode(data);
		} else if(URL_ADD_USER.equals(uri)) {
			return addUser(data);
		} else if(URL_REGISTER_USER.equals(uri)) {
			return userReg(data);
		} else if(URL_USER_LOGIN.equals(uri)) {
			return userLogin(data);
		} else if(URL_GET_USERINFO.equals(uri)) {
			return getUserInfo(data);
		} else if(URL_SET_USERINFO.equals(uri)) {
			return setUserInfo(data);
		} else if(URL_RESET_USERPASSWD.equals(uri)) {
			return resetUserPasswd(data);
		} else if(URL_SET_USERMOBILE.equals(uri)) {
			return setUserMobile(data);
		}
		
		return errorWithKey("autherror_wrong_uri");
	}
	 
	/**
	 * 
	 * @param params
	 * 
	 * {
	 *	 "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 *   "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * 	 "login_name":"xx" //登录名，或身份证号码
	 *	 "login_auth_str":"xx" //登录认证密文串
	 *	 "dev_id":"xx" //设备ID，Web端用户不带此键值，
	 * }
	 *
	 * @return
	 * 
	 * {
	 * "ret_code":0 //返回码，u16
	 * "ret_msg":"xx" //可选，返回错误时的错误描述
	 * "ret_data": //响应数据
	 * {
	 *   "user_id":0L //用户ID，u64
	 *   "auth_ret_str":"xx" //认证结果密文串
	 *   "svc_ticket_str":"xx" //服务票据密文串
	 * }
	 * }
	 * 
	 */
	private String userLogin(Map<String, Object> params) throws Exception {
		String loginNameParam = params.get("login_name").toString(),
				authStrParam = params.get("login_auth_str").toString(),
//				devIdParam = params.get("dev_id").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				srcTypeParam = params.get("src_type").toString(),
				deviceIdParam = params.get("dev_id").toString();
		
		LOGGER.info("In userLogin...login_name = " + loginNameParam);
		
		// 解密
		User user = authDao.getUserByName(loginNameParam);
		if(user == null) {
			return errorWithKey("autherror_user_notexist");
		}
		
		// 认证系统不作判断
//		if(!srcTypeParam.equals(user.getSourceType())) {
//			return errorWithKey("autherror_err_srctype");
//		}
		
		String passwd = user.getPasswd();
		if(StringUtils.isNull(passwd)) {
			return errorWithKey("autherror_user_notreg");
		}
		
		// 32字节随机密钥 + 登录名  + yyyyMMddhhmmss
		String clearText = cipherService.decipher(authStrParam, passwd);
		List<String> tokens = parseClearText(clearText, true, false); // key loginName date
		if(tokens == null) {
			return errorWithKey("autherror_wrong_authstr");
		}
		
		String clientKeyToken = tokens.get(0), nameToken = tokens.get(1), dateToken = tokens.get(2);
		if(!loginNameParam.equals(nameToken)) {
			return errorWithKey("autherror_wrong_authstr");
		}
		
		// 是否过期
		try {
			if(isRequestExipred(dateToken)) {
				return errorWithKey("autherror_request_expired");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return errorWithKey("autherror_wrong_authstr");
		}
		
		// 检查专干device ID 是否与当前设备ID不一致
		String deviceId = user.getDeviceId();
		if(Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam)) {
			if(!StringUtils.isNull(deviceId) && !deviceId.equals(deviceIdParam)) {
				return errorWithKey("autherror_wrong_deviceid");
			}
		}
		
		// 检查当前设备ID是否已绑定其他专干
		User bindUser = authDao.getUserByDeviceId(deviceIdParam);
		if(Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam)) {
			if(bindUser != null && bindUser.getId().longValue() != user.getId().longValue()) {
				return errorWithKey("autherror_device_bound");
			}
		}
		
		// 最后登录时间
		boolean isFirstLogin = false;
		if(user.getLastLogin() == null) {
			isFirstLogin = true;
		}
		user.setLastLogin(new Date());
		
		// 保存设备ID
//		if(Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam) && !deviceIdParam.equals(deviceId)) {
//			authDao.updateUserAndDevice(user, deviceIdParam);
//		} else {
//			user.setDeviceId(deviceIdParam);
//			authDao.updateUser(user);
//		}
		
		// 0612: 居民端不记录设备ID
		if(Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam) && !deviceIdParam.equals(deviceId)) {
			authDao.updateUserAndDevice(user, deviceIdParam);
		}
		
		// 生成服务票据 + 认证结果
		String transportKey = cipherService.randomKey32(); // 传输密钥
		String currentDate = currentDateStr();	// 当前日期
		
		// 认证服务器保存服务票据
		ServiceTicket ticket = new ServiceTicket(transportKey, srcTypeParam, loginNameParam, currentDate);
		authDao.setServiceTicket(user.getId(), ticket);
		
		String rtClearText = transportKey + loginNameParam + currentDate; // 认证结果明文: 传输密钥 + 登录名 + 日期
		String svrTicketText = transportKey + srcTypeParam + "/" + loginNameParam + currentDate; // 服务票据明文: 传输密钥 + 请求源类型/登录名 + 日期
		
		String rtCiphered = cipherService.cipher(rtClearText, clientKeyToken); // 认证结果串加密使用: 客户端提交的随机密钥
		String svrTicketCiphered = cipherService.cipher(svrTicketText, Constants.UNI_SERVICE_KEY); // 服务票据: 服务密钥加密
		
		return String.format(AUTH_SUCCESS_TMPL, user.getId(), rtCiphered, svrTicketCiphered, isFirstLogin ? FIRST_LOGIN_YES : FIRST_LOGIN_NO);
	}
	
	/**
	 * 
	 * 获取用户基本信息, 用于专干平板根据device ID得到基本信息
	 * 
	 * Params:
	 * {
	 * "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * "dev_id":"xx" //设备ID，可选
	 * }
	 * 
	 * Return:
	 * {
	 * 	  "ret_code":0 //返回码，u16
	 *	  "ret_msg":"xx" //可选，返回错误时的错误描述
	 *	  "ret_data": //响应数据
	 *	  {
	 *	    "login_name":"xx" //用户登录名
	 *	    "name":"xx" //真实姓名
	 *	  }
	 *	}
	 * 
	 * @return
	 */
	private String getUserBasicInfo(Map<String, Object> params) {
		LOGGER.info("In getUserBasicInfo...");
		
		String deviceIdParam = params.get("dev_id").toString();
		User user = authDao.getUserByDeviceId(deviceIdParam);
		
		if(user == null) {
			return errorWithKey("auth_user_notfoundby_device");
		} else {
			return String.format(AUTH_USERBASCIINFO_TMPL, user.getLoginName(), user.getRealName());
		}
	}
	
	/**
	 * Params: 
	 * 
	 * {
	 * "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * "sms_code_type":0 //验证码类型，u8，1：注册，2：重置密码，3：更换手机号码，4：特殊授权
	 * "mobile":"xx" //目标手机号码（更换手机号码时指新手机号码）
	 * }
	 *
	 * Result: 
	 * {
	 * "ret_code":0 //返回码，u16
	 * "ret_msg":"xx" //可选，返回错误时的错误描述
	 * }
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private String getSmsCode(Map<String, Object> params) {
		LOGGER.info("In getSmsCode...");
		
		String type = params.get("sms_code_type").toString(),
				mobile = params.get("mobile").toString();
		
		String randCode = randomSmsCode();
		if(smsService.sendSmsCode(mobile, randCode)) {
			authDao.setSmsCode(mobile, type, randCode, SMS_CODE_TIMEOUT);
			
			return Constants.RESPONSE_SUCCESS;
		} else {
			return errorWithKey("auth_sms_send_error");
		}
	}
	
	/**
	 * 
	 * Params: 
	 * {
	 * "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * "user_id":0L //用户ID，u64
	 * "auth_str":"xx" //通信认证密文串
	 * //以下为要添加的用户信息
	 * "login_name": "xx" // 登录名, 可选
	 * "person_id":"xx" //身份证号码，可选，如携带，则每用户唯一
	 * "is_real_name":0 //实名认证标志，u8，1：已确认实名，2：未确认实名
	 * "mobile":"xx" //手机号码，可选，如果不携带，则不能注册登录，多个帐号手机号码可能相同
	 * "passwd_str":"xx" //密码密文串，可选，如果不携带，需要注册后才能登录
	 * "name":"xx" //真实姓名
	 * "email":"xx" //邮件地址，可选
	 * "sex":0 //性别，u8，1：男，2：女
	 * "head_pic_url":"xx" //头像URL，可选
	 * } 
	 * 
	 * Return: 
	 * 
	 * {
	 *   "ret_code":0 //返回码，u16
	 *   "ret_msg":"xx" //可选，返回错误时的错误描述
	 *   "ret_data": //响应数据
	 *   {
	 *     "user_id":0L //新用户ID，u64
	 *   }
	 * }
	 * 
	 * @return
	 * @throws Exception
	 */
	private String addUser(Map<String, Object> params) throws Exception {
		LOGGER.info("In addUser...");
		
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				userIdParam = params.get("user_id").toString(),
				authStrParam = params.get("auth_str").toString(),
				loginNameParam = StringUtils.toString(params.get("login_name"), true),	// 可选
				pidParam = StringUtils.toString(params.get("person_id"), true),			// 可选
				isVerifiedParam = params.get("is_real_name").toString(),					// 1：已确认实名，2：未确认实名
				mobileParam = params.get("mobile").toString(),							// 必须
				passwdParam = StringUtils.toString(params.get("passwd_str"), true),		// 可选, 如果不携带，需要注册后才能登录
				realNameParam = params.get("name").toString(),
				emailParam = StringUtils.toString(params.get("email"), true),				// 可选
				sexParam = params.get("sex").toString(),									// 1：男，2：女
				headPicParam = StringUtils.toString(params.get("head_pic_url"), true);		// 可选
		
		if(!Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam)) {
			return errorWithKey("adduser_error_nothecadre");
		}
		
		ReqAuthResult rt = requestAuth(Long.parseLong(userIdParam), srcTypeParam, authStrParam);
		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
			return errorWithKey(rt.getErrKey());
		}
		
		// 如果重复添加, 返回错误 和 user_id
		User userInDb = this.getUser(pidParam);
		if(userInDb != null) {
			Map<String, Long> rtData = new HashMap<String, Long>();
			rtData.put("user_id", userInDb.getId());
			return errorWith(Constants.ERRCODE_AUTH_USER_EXISTS, "adduser_error_exists", rtData);
		}
		
		// User entity 
		User user = new User();
		user.setSourceType(Constants.SOURCE_TYPE_INHABITANT);
		user.setLoginName(loginNameParam);
		user.setPersonId(pidParam);
		user.setVerified(Integer.parseInt(isVerifiedParam));
		user.setMobile(mobileParam);
		if(StringUtils.isNotNull(passwdParam)) {
			String realPwd = cipherService.decipher(passwdParam, Constants.PWD_AES_KEY);
			user.setPasswd(realPwd);
		}
		user.setRealName(realNameParam);
		user.setEmail(emailParam);
		user.setSex(Integer.parseInt(sexParam));
		user.setHeadPic(headPicParam);
		user.setDeviceId("");
		user.setCreateDate(new Date());
		
		long userId = authDao.addUser(user);
		return String.format(AUTH_ADDUSER_TMPL, userId);
	}

	/**
	 * 通讯认证
	 * 
	 * @param uid
	 * @param srcType
	 * @param authStr
	 * @return
	 * 
	 */
	private ReqAuthResult requestAuth(long uid, String srcType, String authStr) throws Exception {
		ServiceTicket ticket = authDao.getServiceTicket(uid);
		if (ticket == null) {
			return ReqAuthResult.ERRNOTKT;
		}
		
		if(isSvcTicketExipred(ticket.getCreateDate())) {
			return ReqAuthResult.ERRTKTIMEOUT;
		}
		
		// 请求源类型
		if (!ticket.getSrcType().equals(srcType)) {
			return ReqAuthResult.ERRSRCTYPE;
		}

		// 明文: "登录名+前端系统UTC时间yyyyMMddhhmmss"
		String clearText = cipherService.decipher(authStr, ticket.getTransportKey());
		List<String> tokens = parseClearText(clearText, false, false);
		if (tokens == null) {
			return ReqAuthResult.ERRAUTHSTR;
		}

		// 登录名 和 日期
		String nameToken = tokens.get(0), dateToken = tokens.get(1);
		if (!ticket.getLoginName().equals(nameToken)) {
			return ReqAuthResult.ERRNAME;
		}

		try {
			if (isRequestExipred(dateToken)) {
				return ReqAuthResult.ERRTIMEOUT;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ReqAuthResult.ERRAUTHSTR;
		}

		return ReqAuthResult.SUCCESS;
	}
	
	/**
	 * 
	 * Params:
	 * {
	 * "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * "person_id":"xx" //身份证号码，每用户唯一
	 * "mobile":"xx" //手机号码，多个帐号手机号码可能相同
	 * "sms_code":"xx" //短信验证码
	 * "passwd_str":"xx" //密码密文串
	 * "dev_id":"xx" //设备ID
	 * }
	 *
	 * Result:
	 * {
	 * "ret_code":0 //返回码，u16
	 * "ret_msg":"xx" //可选，返回错误时的错误描述
	 * }
	 *
	 * @return
	 */
	private String userReg(Map<String, Object> params) throws Exception {
		LOGGER.info("In userReg...");
		
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				pidParam = params.get("person_id").toString(),			
				mobileParam = params.get("mobile").toString(),							
				smsCodeParam = params.get("sms_code").toString(),				
				passwdParam = params.get("passwd_str").toString();
		
		Object deviceIdObj = params.get("dev_id");
		
		String smsCode = authDao.getSmsCode(mobileParam, SMS_TYPE_REG);
		if(smsCode == null || !smsCode.equals(smsCodeParam)) {
			return errorWithKey("userreg_error_smscode");
		}
		
		// 验证身份证 和 手机号
		User user = authDao.getUserByName(pidParam);
		if(user == null || !user.getMobile().equals(mobileParam)) {
			return errorWithKey("userreg_error_reginfo");
		}
		
		// bugfix-0623: 不允许重复注册
		if(StringUtils.isNotNull(user.getPasswd())) {
			return errorWithKey("userreg_error_registered");
		}
		
		// 更新用户
		String realPwd = cipherService.decipher(passwdParam, Constants.PWD_AES_KEY);
		user.setPasswd(realPwd);
		user.setDeviceId(deviceIdObj == null ? "" : deviceIdObj.toString());
		authDao.updateUser(user);
//		authDao.updateUserAndDevice(user, deviceIdParam);
		
		// 删除验证码
		authDao.deleteSmsCode(mobileParam, SMS_TYPE_REG);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
	/**
	 * 
	 * 获取用户信息
	 * 
	 * Params:
	 * {
	 * "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * "user_id":0L //用户ID，u64
	 * "auth_str":"xx" //通信认证密文串
	 * "user_id_get": //要获取信息的用户ID列表，可选，获取自己的信息时不携带
	 * [
	 *   0L //u64
	 *   ...
	 * ]
	 * "person_id_get": //要获取信息的用户身份证号列表，可选，获取自己的信息时不携带
	 * [
	 *   "xx"
	 *   ...
	 * ]
	 * }
	 *
	 * Result:
	 * 
	 * {
	 *  "ret_code":0 //返回码，u16
	 *  "ret_msg":"xx" //可选，返回错误时的错误描述
	 *  "ret_data": //响应数据
	 *  [
	 *   {
	 *     "user_id":0L //用户ID，u64
	 *     "login_name":"xx" //用户登录名，如果没有则为空串
	 *     "person_id":"xx" //身份证号码，可选
	 *     "is_real_name":0 //实名认证标志，u8，1：已确认实名，2：未确认实名
	 *     "mobile":"xx" //手机号码，可选
	 *     "name":"xx" //真实姓名
	 *     "email":"xx" //邮件地址，可选
	 *     "sex":0 //性别，u8，1：男，2：女
	 *     "head_pic_url":"xx" //头像URL，如果没有则为空串
	 *   }
	 *   ...
	 * ]
	 * }
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private String getUserInfo(Map<String, Object> params) throws Exception {
		LOGGER.info("In getUserInfo...");
		
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				userIdParam = params.get("user_id").toString(),
				authStrParam = params.get("auth_str").toString();
		
		List<String> uidListParam = (List<String>)params.get("user_id_get"),
				pidListParam = (List<String>)(List<String>)params.get("person_id_get");
		
		// 通信认证
		long uid = Long.parseLong(userIdParam);
		ReqAuthResult rt = requestAuth(uid, srcTypeParam, authStrParam);
		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
			if(rt.loginAgain()) {
				return errorWithCodeKey(Constants.ERRCODE_AUTH_LOGIN_AGAIN, rt.getErrKey());
			} else {
				return errorWithKey(rt.getErrKey());
			}
		}
		
		List<User> userList = null;
		if(uidListParam == null && pidListParam == null) { 	// 获取自己的信息
			String[] uids = new String[]{ String.valueOf(uid) };
			
			userList = authDao.getUsersByIds(uids);
		} else {											// 获取其他人信息
			userList = new ArrayList<User>();
			if(uidListParam != null && uidListParam.size() > 0) {
				String[] uids = new String[uidListParam.size()];
				for(int i = 0; i < uids.length; i++) {
					uids[i] = StringUtils.toString(uidListParam.get(i), false);
				}
				
				List<User> usersByIds = authDao.getUsersByIds(uids);
				if(usersByIds != null && usersByIds.size() > 0) {
					userList.addAll(usersByIds);
				}
			}
			
			if(pidListParam != null && pidListParam.size() > 0) {
				String[] pids = new String[pidListParam.size()];
				for(int i = 0; i < pids.length; i++) {
					pids[i] = StringUtils.toString(pidListParam.get(i), false);
				}
				
				List<User> usersByPids = authDao.getUsersByPids(pids);
				if(usersByPids != null && usersByPids.size() > 0) {
					userList.addAll(usersByPids);
				}
			}
			
		}

		return successWithObject(mapUtil.mapListWithoutKeys(userList, null));
	}
	
	/**
	 * 修改用户信息
	 * 
	 * Params:
	 * 
	 * {
	 * "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 * "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * "user_id":0L //用户ID，u64
	 * "auth_str":"xx" //通信认证密文串
	 * "user_id_mod":0L //要修改的用户ID，u64，可选，修改自己的信息时不携带
	 * //以下为要修改的用户信息
	 * "person_id":"xx" //身份证号码
	 * "is_real_name":0 //实名认证标志，u8，1：已确认实名，2：未确认实名
	 * "mobile":"xx" //手机号码
	 * "passwd_str":"xx" //密码密文串，可用于修改密码
	 * "name":"xx" //真实姓名
	 * "email":"xx" //邮件地址
	 * "sex":0 //性别，u8，1：男，2：女
	 * "head_pic_url":"xx" //头像URL
	 * "dev_id":"xx" //设备ID，可用于修改或清空设备绑定
	 * }
	 * 
	 * Result:
	 * 
	 * {
	 *  "ret_code":0 //返回码，u16
	 *  "ret_msg":"xx" //可选，返回错误时的错误描述
	 * }
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private String setUserInfo(Map<String, Object> params) throws Exception {
		LOGGER.info("In setUserInfo - params: " + params);
		
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				userIdParam = params.get("user_id").toString(),
				authStrParam = params.get("auth_str").toString(),
				userIdModParam = StringUtils.toString(params.get("user_id_mod"), false),
				personIdParam = StringUtils.toString(params.get("person_id"), false),
				verifiedParam = StringUtils.toString(params.get("is_real_name"), false),
				mobileParam = StringUtils.toString(params.get("mobile"), false),
				passwdParam = StringUtils.toString(params.get("passwd_str"), false),
				nameParam = StringUtils.toString(params.get("name"), false),
				emailParam = StringUtils.toString(params.get("email"), false),
				sexParam = StringUtils.toString(params.get("sex"), false),
				headPicParam = StringUtils.toString(params.get("head_pic_url"), false),
				deviceIdParam = StringUtils.toString(params.get("dev_id"), false);
		
		// 仅有专干才能修改其他用户的信息
		if(!Constants.SOURCE_TYPE_HECADRE.equals(srcTypeParam) && userIdModParam != null) {
			return errorWithKey("setuserinfo_error_nopriv");
		}
		
		// 修改自己信息不能修改: 身份证, 实名认证标志, 手机号, 真实姓名
		if(userIdModParam == null && (personIdParam != null || verifiedParam != null 
				|| mobileParam != null || nameParam != null)) {
			return errorWithKey("setuserinfo_attr_nopriv");
		}
		
		// 通信认证
		long uid = Long.parseLong(userIdParam);
		ReqAuthResult rt = requestAuth(uid, srcTypeParam, authStrParam);
		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
			return errorWithKey(rt.getErrKey());
		}
		
		long userIdMod = Long.parseLong(userIdModParam == null ? userIdParam : userIdModParam);
		User userMod = authDao.getUserById(userIdMod);
		if(userMod == null) {
			return errorWithKey("setuserinfo_error_notexist");
		}
		
		if(verifiedParam != null) userMod.setVerified(Integer.parseInt(verifiedParam));
		if(mobileParam != null) userMod.setMobile(mobileParam);
		if(passwdParam != null) {
			String realPwd = cipherService.decipher(passwdParam, Constants.PWD_AES_KEY);
			userMod.setPasswd(realPwd);
		}
		if(nameParam != null) userMod.setRealName(nameParam);
		if(emailParam != null) userMod.setEmail(emailParam);
		if(sexParam != null) userMod.setSex(Integer.parseInt(sexParam));
		if(headPicParam != null) userMod.setHeadPic(headPicParam);
		
//		if(personIdParam != null) userMod.setPersonId(personIdParam);
//		if(deviceIdParam != null) userMod.setDeviceId(deviceIdParam);
		
		authDao.updateUserPidAndDevice(userMod, personIdParam, deviceIdParam);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
	/**
	 * 重置用户密码
	 * 
	 * Params:
	 * {
	 *	  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 *	  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 *	  "login_name":"xx" //需重置密码的用户登录名，或身份证号码
	 *	  "mobile":"xx" //需重置密码的用户手机号码
	 *	  "sms_code":"xx" //短信验证码
	 *	  "passwd_str":"xx" //密码密文串
	 *	}
	 * 
	 * Result:
	 * {
	 *  "ret_code":0 //返回码，u16
	 *  "ret_msg":"xx" //可选，返回错误时的错误描述
	 * }
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private String resetUserPasswd(Map<String, Object> params) throws Exception {
		LOGGER.info("In resetUserPasswd - params: " + params);
		
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				loginNameParam = params.get("login_name").toString(),
				mobileParam = params.get("mobile").toString(),
				smsCodeParam = params.get("sms_code").toString(),
				passwdParam = params.get("passwd_str").toString();
		
		String smsCode = authDao.getSmsCode(mobileParam, SMS_TYPE_SETPWD);
		if(smsCode == null || !smsCode.equals(smsCodeParam)) {
			return errorWithKey("userreg_error_smscode");
		}
		
		// 验证身份证 和 手机号
		User user = authDao.getUserByName(loginNameParam);
		if(user == null || !user.getMobile().equals(mobileParam)) {
			return errorWithKey("userreg_error_userinfo");
		}
		
		// bugfix-1152: 未注册账号直接进入忘记密码提示修改成功 0619
		if(StringUtils.isNull(user.getPasswd())) {
			return errorWithKey("userreg_error_noreg");
		}
		
		// 更新用户
		String realPwd = cipherService.decipher(passwdParam, Constants.PWD_AES_KEY);
		user.setPasswd(realPwd);
		authDao.updateUser(user);
		
		// 删除验证码
		authDao.deleteSmsCode(mobileParam, SMS_TYPE_SETPWD);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
	/**
	 * 设置用户手机号码
	 * 
	 * Params:
	 * 
	 * {
	 *  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 *  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 *  "user_id":0L //用户ID，u64
	 *  "auth_str":"xx" //通信认证密文串
	 *  "new_mobile":"xx" //更换的新手机号码
	 *  "sms_code":"xx" //新手机号码收到的短信验证码
	 * }
	 *
	 * Result:
	 * 
	 * {
	 *  "ret_code":0 //返回码，u16
	 *  "ret_msg":"xx" //可选，返回错误时的错误描述
	 * }
	 *
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private String setUserMobile(Map<String, Object> params) throws Exception {
		LOGGER.info("In setUserMobile...");
		
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				userIdParam = params.get("user_id").toString(),
				authStrParam = params.get("auth_str").toString(),
				newMobileParam = params.get("new_mobile").toString(),
				smsCodeParam = params.get("sms_code").toString();
		
		String smsCode = authDao.getSmsCode(newMobileParam, SMS_TYPE_SETMOBILE);
		if(smsCode == null || !smsCode.equals(smsCodeParam)) {
			return errorWithKey("userreg_error_smscode");
		}
		
		// 通信认证
		long uid = Long.parseLong(userIdParam);
		ReqAuthResult rt = requestAuth(uid, srcTypeParam, authStrParam);
		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
			return errorWithKey(rt.getErrKey());
		}
		
		// 更新用户
		User user = authDao.getUserById(uid);
		user.setMobile(newMobileParam);
		authDao.updateUser(user);
		
		// 删除验证码
		authDao.deleteSmsCode(newMobileParam, SMS_TYPE_SETMOBILE);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
	private String errorWithCodeKey(int errcode, String key) {
		return String.format(Constants.RESPONSE_ERROR_TMPL, errcode, messageService.get(key));
	}
	
	private String errorWith(int errcode, String key, Object data) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, errcode);
        map.put(Constants.KEY_MSG, messageService.get(key));
        map.put(Constants.KEY_DATA, data);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	private String errorWithKey(String messageKey) {
		return String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, messageService.get(messageKey));
	}
	
	private String successWithObject(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
        map.put(Constants.KEY_DATA, obj);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	// 返回null表示解析失败
	private List<String> parseClearText(String clearText, boolean withKey, boolean withSrcType) {
		int minLen = Constants.AUTH_DATE_LEN;
		if(withKey) {
			minLen += Constants.AUTH_KEY_LEN;
		}
		
		if(withSrcType) {
			minLen += 3; // srcType和登录名使用 / 分隔
		} else {
			minLen += 1;
		}
		
		int len = clearText.length(); 
		if(len < minLen) return null;
		
		List<String> listStr = new ArrayList<String>();
		if(withKey) {
			listStr.add(clearText.substring(0, 32));	// key
			if(withSrcType) {
				String tmp = clearText.substring(32, len - 14);
				String[] tmparr = tmp.split(Constants.AUTH_SRCNAME_SEP);
				
				if(tmparr.length != 2) return null;
				listStr.add(tmparr[0]);	// src type
				listStr.add(tmparr[1]);	// login name
			} else {
				listStr.add(clearText.substring(32, len - 14));	// login name
			}
		} else {
			if(withSrcType) {
				String tmp = clearText.substring(0, len - 14);
				String[] tmparr = tmp.split(Constants.AUTH_SRCNAME_SEP);
				
				if(tmparr.length != 2) return null;
				listStr.add(tmparr[0]);	// src type
				listStr.add(tmparr[1]);	// login name
			} else {
				listStr.add(clearText.substring(0, len - 14));	// login name
			}
		}
		listStr.add(clearText.substring(len - 14));	// date
		
		return listStr;
	}
	
	private SimpleDateFormat getDateFormatter() {
		SimpleDateFormat formatter = threadFormatter.get();
		if(formatter == null) {
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
	
	// 返回: 示例 20170419111534
	private String currentDateStr() {
		SimpleDateFormat formatter = getDateFormatter();
		
		return formatter.format(new Date());
	}
	
	// 
	private String randomSmsCode() {
		int min = 10000, max = 99999;
		
		Random r = new Random();
		int rand = r.nextInt((max - min) + 1) + min;
		
		return String.valueOf(rand);
	}

	@Override
	public List<User> getUsers(List<Long> userIdList) {
		if(userIdList == null || userIdList.size() == 0) return null;
		
		List<User> userList = authDao.getUsersByIds(userIdList);
		
		return userList;
	}

	@Override
	public void updateUser(User user) throws AuthServiceException {
		LOGGER.info("In updateUser - pwd: " + user.getPasswd());
		
		User userInDb = authDao.getUserById(user.getId());
		if(userInDb == null) {
			throw new AuthServiceException(messageService.get("autherror_user_notexist"));
		}
		
		String loginName = user.getLoginName();
		if(StringUtils.isNotNull(loginName)) {
			userInDb.setLoginName(loginName);
		}
		
		String realName = user.getRealName();
		if(StringUtils.isNotNull(realName)) {
			userInDb.setRealName(realName);
		}
		
		String mobile = user.getMobile();
		if(StringUtils.isNotNull(mobile)) {
			userInDb.setMobile(mobile);
		}
		
//		String passwd = user.getPasswd();
//		if(StringUtils.isNotNull(passwd)) {
//			userInDb.setPasswd(passwd);
//		}
		try {
			String passwdParam = user.getPasswd();
			if(StringUtils.isNotNull(passwdParam)) {
				String realPwd = cipherService.decipher(passwdParam, Constants.PWD_AES_KEY);
				userInDb.setPasswd(realPwd);
			}
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AuthServiceException(messageService.get("updateuser_error_cipher"), ex);
		}
		
		int verified = user.getVerified();
		if(verified == 1 || verified == 2) {
			userInDb.setVerified(verified);
		}
		
		String email = user.getEmail();
		if(StringUtils.isNotNull(email)) {
			userInDb.setEmail(email);
		}
		
		int sex = user.getSex();
		if(sex == 1 || sex == 2) {
			userInDb.setSex(sex);
		}
		
		String headPic = user.getHeadPic();
		if(StringUtils.isNotNull(headPic)) {
			userInDb.setHeadPic(headPic);
		}
		
		String personId = user.getPersonId();
		if(StringUtils.isNotNull(personId)) {
			authDao.updateUserPidAndDevice(userInDb, personId, null);
		} else {
			userInDb.setPersonId(personId);
			authDao.updateUser(userInDb);
		}
	}

	@Override
	public void unbindDevice(Long userId) {
		authDao.unbindDevice(userId);
	}

	@Override
	public Long addUser(User user) throws AuthServiceException {
		try {
			return _addUser(user);
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			
			throw new AuthServiceException(ex);
		}
	}
	
	public Long _addUser(User user) throws Exception {
		if(authDao.exists(user)) {
			throw new AuthServiceException(messageService.get("adduser_error_exists"));
		}
		
		String passwdParam = user.getPasswd();
		if(StringUtils.isNotNull(passwdParam)) {
			String realPwd = cipherService.decipher(passwdParam, Constants.PWD_AES_KEY);
			user.setPasswd(realPwd);
		}
		
		return authDao.addUser(user);
	}

	@Override
	public User getUser(String loginName) throws AuthServiceException {
		return authDao.getUserByName(loginName);
	}

	@Override
	public User getUserById(Long userId) {
		return authDao.getUserById(userId);
	}
	
	@Override
	public User userWebLogin(String loginName, String loginAuthStr) throws Exception {
		User user = authDao.getUserByName(loginName);
		if(user == null) {
			return null;
		}
		
		String passwd = user.getPasswd();
		if(StringUtils.isNull(passwd)) {
			return null;
		}
		
		// 32字节随机密钥 + 登录名  + yyyyMMddhhmmss
		String clearText = cipherService.decipher(loginAuthStr, passwd);
		List<String> tokens = parseClearText(clearText, true, false); // key loginName date
		if(tokens == null) {
			return null;
		}
		
		String clientKeyToken = tokens.get(0), nameToken = tokens.get(1), dateToken = tokens.get(2);
		if(!loginName.equals(nameToken)) {
			return null;
		}
		
		// 是否过期 bugfix-1660
//		try {
//			if(isRequestExipred(dateToken)) {
//				return null;
//			}
//		} catch(Exception ex) {
//			ex.printStackTrace();
//			return null;
//		}
		
		// 最后登录时间
		user.setLastLogin(new Date());
		authDao.updateUser(user);
		
		return user;
	}

}

























