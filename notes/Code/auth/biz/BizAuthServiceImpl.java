package com.newcare.auth.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.auth.dao.IAuthDao;
import com.newcare.auth.pojo.ReqAuthResult;
import com.newcare.auth.pojo.ServiceTicket;
import com.newcare.auth.pojo.SvcAuthResult;
import com.newcare.auth.service.IBizAuthService;
import com.newcare.auth.service.ICipherService;
import com.newcare.constant.Constants;
import com.newcare.doc.service.ResidentInfoService;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.service.community.IStaffService;
import com.newcare.service.AbstractBizService;
import com.newcare.util.StringUtils;

@Service("bizAuthService")
public class BizAuthServiceImpl extends AbstractBizService implements IBizAuthService {

	private ThreadLocal<SimpleDateFormat> threadFormatter = new ThreadLocal<SimpleDateFormat>();
	
	@Autowired
	private ICipherService cipherService;
	
	@Autowired
	private IAuthDao authDao;
	
	@Autowired
	private ResidentInfoService residentService;
	
	@Autowired
	private IStaffService staffService;
	
	// 提交iOSS LIS数据
	public static final String URI_LIS_SUBMIT = "/hca/api/business/hecadre/submitiosslisret";
	
	public static final String[] SECURE_URI_ARRAY = new String[] {
			"/hca/api/business/putfile",				// 文件上传
			"/hca/api/business/getfile",				// 文件下载
			
			/*************** 公用 ***************/
			
			"/hca/api/business/getfamilymemberlist",	// 获取家庭成员列表或个人基本信息
			"/hca/api/business/getcirclelist",			// 获取交流圈列表
			"/hca/api/business/getcirclepostlist",		// 获取交流圈留言列表
			"/hca/api/business/submitcirclepost",		// 发表交流圈留言
			"/hca/api/business/delcirclepost",			// 删除交流圈留言
			"/hca/api/business/getapporeglist",			// 获取预约挂号列表
			"/hca/api/business/modapporeg",				// 修改预约挂号
			"/hca/api/business/getappoimmunelist",		// 获取计划免疫列表
			"/hca/api/business/modappoimmune",			// 修改计划免疫
			"/hca/api/business/getarealist",			// 获取下级行政区列表
			"/hca/api/business/comment",				// 意见反馈
			"/hca/api/business/getpushmsg",				// 获取内部推送消息 
			
			/*************** 专干端 ***************/
			
			"/hca/api/business/hecadre/gettasksum",			// 获取任务计划
			"/hca/api/business/hecadre/addtask",			// 添加任务计划
			"/hca/api/business/hecadre/getactivitystate",	// 获取活动类型状态
			"/hca/api/business/hecadre/getnotice",			// 获取通知消息
			"/hca/api/business/hecadre/getmemolist",		// 获取备忘列表
			"/hca/api/business/hecadre/modmemo",			// 修改备忘
			"/hca/api/business/hecadre/getgrid",			// 获取网格地图
			"/hca/api/business/hecadre/addtenement",		// 添加新住址
			"/hca/api/business/hecadre/modfamilymember",	// 修改家庭成员或个人基本信息
			"/hca/api/business/hecadre/moveoutfamilymember",// 家庭成员迁出
			"/hca/api/business/hecadre/getusertomovein",	// 获取可迁入人员
			"/hca/api/business/hecadre/moveinfamilymember",	// 家庭成员迁入
			"/hca/api/business/hecadre/getdocdir",			// 获取健康档案目录
			"/hca/api/business/hecadre/getdocui",			// 获取健康档案界面
			"/hca/api/business/hecadre/submitdoc",			// 提交居民健康档案
			"/hca/api/business/hecadre/getactivity",		// 获取活动信息
			"/hca/api/business/hecadre/startactivity",		// 发起活动
			"/hca/api/business/hecadre/signinactivity",		// 活动签到或登记
			"/hca/api/business/hecadre/submitactivitysum",	// 提交活动总结
			"/hca/api/business/hecadre/activityaccredit",	// 将活动授权给其他专干
			"/hca/api/business/hecadre/modcircle",			// 修改交流圈
			"/hca/api/business/hecadre/getfdcontractlist",	// 获取家庭医生签约列表
			"/hca/api/business/hecadre/getfdcontract",		// 获取家庭医生签约信息
			"/hca/api/business/hecadre/modfdcontract",		// 修改家庭医生签约信息
			"/hca/api/business/hecadre/getinhabitant", 		// 获取所辖居民
			
			/*************** 居民端 ***************/
			
			"/hca/api/business/inhabitant/getnotice",		// 获取通知消息
			"/hca/api/business/inhabitant/replynotice",		// 回复通知消息
			"/hca/api/business/inhabitant/docaccredit",		// 授权查看个人健康档案
			"/hca/api/business/inhabitant/gethecadre",		// 获取对应健教专干
			"/hca/api/business/inhabitant/getfdcontractstate",	// 获取家庭医生签约申请
			"/hca/api/business/inhabitant/signfdcontract",		// 提交家庭医生签约申请
			"/hca/api/business/inhabitant/submittestret",		// 提交测一测结果

			// 医生端
			"/hca/web/doctor/evaluate/create",
			"/hca/web/doctor/evaluate/search",
			"/hca/web/doctor/evaluate/document"
	};
	
	// 需通信认证URI集合
	public static final Set<String> SECURE_URI_SET = new HashSet<String>(Arrays.asList(SECURE_URI_ARRAY));
	
	/**
	 * 
	 * Params:
	 * {
	 *    "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
	 *	  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
	 * 	  "user_id":0L //用户ID，u64
	 *	  "auth_str":"xx" //通信认证密文串
	 *	  "svc_ticket_str":"xx" //服务票据密文串
	 *	}
	 *
	 * Result:
	 * 
	 * {
	 *  "ret_code":0 //返回码，u16
	 *  "ret_msg":"xx" //可选，返回错误时的错误描述
	 * }
	 * 
	 */
	public String doServiceAuth(String uri, Map<String, Object> data) throws BizServiceException {
		try {
			return doServiceAuthInternal(uri, data);
		} catch(Exception ex) {
			ex.printStackTrace();
			
			return errorWithKey("svcauth_exception_msg");
		}
	}
	
	private String doServiceAuthInternal(String uri, Map<String, Object> data) throws Exception {
		String srcTypeParam = StringUtils.toString(data.get("src_type"), false),
				pfTypeParam = StringUtils.toString(data.get("pf_type"), false),
				userIdParam = StringUtils.toString(data.get("user_id"), false),
				authStrParam = StringUtils.toString(data.get("auth_str"), false), 
				svcTicketParam = StringUtils.toString(data.get("svc_ticket_str"), false);
		
		Long userId = Long.parseLong(userIdParam);
		
		// 检查source type - 0621
		if(!checkSourceType(srcTypeParam, userId)) {
			return errorWithKey("svcauth_err_srctype");
		}
		
		// 服务票据明文: 传输密钥 + 请求源类型/登录名 + 日期
		String svcTicketText = cipherService.decipher(svcTicketParam, Constants.UNI_SERVICE_KEY);
		List<String> ticketTokens = parseClearText(svcTicketText, true, true);
		
		if (ticketTokens == null) {
			return errorWithKey(SvcAuthResult.ERRTICKET.getErrKey());
		}
		
		String transportKey = ticketTokens.get(0), ticketSrcType = ticketTokens.get(1), ticketLoginName = ticketTokens.get(2), ticketDate = ticketTokens.get(3);
		// 请求源类型
		if (StringUtils.isNull(srcTypeParam) || !srcTypeParam.equals(ticketSrcType)) {
			return errorWithKey(SvcAuthResult.ERRSRCTYPE.getErrKey());
		}
		
		try {
			if (isSvcTicketExipred(ticketDate)) {
				return errorWithKey(SvcAuthResult.ERRTIMEOUT.getErrKey());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
			return errorWithKey(SvcAuthResult.ERRTICKET.getErrKey());
		}
		
		// 明文: "登录名+前端系统UTC时间yyyyMMddhhmmss"
		String authText = cipherService.decipher(authStrParam, transportKey);
		List<String> tokens = parseClearText(authText, false, false);
		if (tokens == null) {
			return errorWithKey(SvcAuthResult.ERRAUTHSTR.getErrKey());
		}
		
		String requestDate = tokens.get(1), authLoginName = tokens.get(0);
		try {
			if (isRequestExipred(requestDate)) {
				return errorWithKey(SvcAuthResult.ERRREQTIMEOUT.getErrKey());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
			return errorWithKey(SvcAuthResult.ERRAUTHSTR.getErrKey());
		}
		
		// 对比登录名
		if(ticketLoginName == null || authLoginName == null || !ticketLoginName.equals(authLoginName)) {
			return errorWithKey(SvcAuthResult.ERRNAME.getErrKey());
		}
		
		// 保存服务票据
		ServiceTicket ticket = new ServiceTicket(transportKey, ticketSrcType, ticketLoginName, ticketDate);
		ticket.setPfType(pfTypeParam);
		authDao.setServiceTicket(Long.parseLong(userIdParam), ticket);

		return Constants.RESPONSE_SUCCESS;
	}

	private boolean checkSourceType(String srcType, Long userId) {
		if(Constants.SOURCE_TYPE_INHABITANT.equals(srcType)) {
			return residentService.isResident(userId) ? true : false;
		} else if(Constants.SOURCE_TYPE_HECADRE.equals(srcType)) {
			List<String> codeList = staffService.getRoleCodeByUserId(userId);
			
			return codeList != null && codeList.contains(Constants.ROLE_CODE_HECADRE) ? true : false;
		} else if(Constants.SOURCE_TYPE_DOCTOR.equals(srcType)) {
			List<String> codeList  = staffService.getRoleCodeByUserId(userId);
			
			return codeList != null && codeList.contains(Constants.ROLE_CODE_DOCTOR) ? true : false;
		}
		
		return false;
	}

	@Override
	/**
	 * 通讯认证
	 * 
	 * @param uid
	 * @param srcType
	 * @param authStr
	 * @return
	 * 
	 */
	public ReqAuthResult requestAuth(long uid, String srcType, String authStr) throws Exception {
		return requestAuth(uid, srcType, authStr, false);
	}
	
	@Override
	public ReqAuthResult requestAuth(long uid, String authStr) throws Exception {
		return requestAuth(uid, null, authStr, true);
	}
	
	private ReqAuthResult requestAuth(long uid, String srcType, String authStr, boolean ignoreSrcType) throws Exception {
		ServiceTicket ticket = authDao.getServiceTicket(uid);
		if (ticket == null) {
			return ReqAuthResult.ERRNOTKT;
		}
		
		if(isSvcTicketExipred(ticket.getCreateDate())) {
			return ReqAuthResult.ERRTKTIMEOUT;
		}
		
		// 请求源类型
		if (!ignoreSrcType && !ticket.getSrcType().equals(srcType)) {
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
	
	@Override
	public ReqAuthResult requestAuthLis(String lisSn, String srcType, String authStr) throws Exception {
		// 明文: "iOSS LIS SN+前端系统UTC时间yyyyMMddhhmmss"
		String clearText = cipherService.decipher(authStr, Constants.IOSS_LIS_KEY);
		List<String> tokens = parseClearText(clearText, false, false);
		if (tokens == null) {
			return ReqAuthResult.ERRAUTHSTR;
		}

		// 登录名 和 日期
		String lisSnToken = tokens.get(0), dateToken = tokens.get(1);
		if (!lisSnToken.equals(lisSn)) {
			return ReqAuthResult.ERRLISSN;
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
	
	@Override
	public boolean isUriSecured(String uri) {
		return SECURE_URI_SET.contains(uri);
	}
	
	@Override
	public boolean isLisUri(String uri) {
		return URI_LIS_SUBMIT.equals(uri);
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

	@Override
	public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public String doGet(String uri, Map<String, String[]> params) throws BizServiceException {
		throw new UnsupportedOperationException("Not implemented!");
	}


}
