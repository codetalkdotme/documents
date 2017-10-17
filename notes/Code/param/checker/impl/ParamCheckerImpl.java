package com.newcare.param.checker.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.newcare.constant.Constants;
import com.newcare.mesg.MessageService;
import com.newcare.param.checker.IParamChecker;
import com.newcare.param.checker.ParamCheckResult;
import com.newcare.param.type.IntEnumParam;
import com.newcare.param.type.IntListEnumParam;
import com.newcare.param.type.IntParam;
import com.newcare.param.type.IntParam;
import com.newcare.param.type.LongListParam;
import com.newcare.param.type.LongListParam;
import com.newcare.param.type.LongParam;
import com.newcare.param.type.Param;
import com.newcare.param.type.ParamListParam;
import com.newcare.param.type.RegexParam;
import com.newcare.param.type.StringEnumParam;
import com.newcare.param.type.StringListParam;
import com.newcare.param.type.StringParam;

@Component("paramChecker")
public class ParamCheckerImpl implements IParamChecker {

	@Autowired
	private MessageService mesgService;
	
	// src type参数
	static Param PARAM_SRCTYPE = new StringEnumParam("src_type", true, new String[] {Constants.SOURCE_TYPE_HECADRE, Constants.SOURCE_TYPE_INHABITANT});
	static Param PARAM_HECADRE_SRCTYPE = new StringEnumParam("src_type" , true ,new String[] {Constants.SOURCE_TYPE_HECADRE});
	static Param PARAM_INHABITANT_SRCTYPE = new StringEnumParam("src_type", true , new String[] {Constants.SOURCE_TYPE_INHABITANT}  );
	
	// pf type参数
	static Param PARAM_PFTYPE = new StringEnumParam("pf_type", true, new String[] {Constants.PF_TYPE_ANDROID, Constants.PF_TYPE_IOS, Constants.PF_TYPE_WEB});
	// user id
	static Param PARAM_UID = new LongParam("user_id", true, 1L);
	// auth str
	static Param PARAM_AUTHSTR = new StringParam("auth_str", true);
	// dev id
	static Param PARAM_DEVID_REQUIRED = new StringParam("dev_id", true);
	static Param PARAM_DEVID_OPTIONAL = new StringParam("dev_id", false);
	// SMS type
	static Param PARAM_SMSTYPE = new IntEnumParam("sms_code_type", true, 
			new Integer[]{Constants.AUTH_SMS_TYPE_REG, Constants.AUTH_SMS_TYPE_PWD_RESET, Constants.AUTH_SMS_TYPE_PHONE_RESET, Constants.AUTH_SMS_TYPE_SPECIAL_AUTH});

	// mobile 
	static Param PARAM_MOBILE_REQUIRED = new RegexParam("mobile", true, "1\\d{10}");
	static Param PARAM_MOBILE_OPTIONAL = new RegexParam("mobile", false, "1\\d{10}");
	
	// realname
	static Param PARAM_REALNAME_REQUIRED = new IntEnumParam("is_real_name", true, new Integer[] {Constants.AUTH_REALNAME_Y, Constants.AUTH_REALNAME_N});
	static Param PARAM_REALNAME_OPTIONAL = new IntEnumParam("is_real_name", false, new Integer[] {Constants.AUTH_REALNAME_Y, Constants.AUTH_REALNAME_N});
	
	// sex
	static Param PARAM_SEX_REQUIRED = new IntEnumParam("sex", true, new Integer[] {Constants.SEX_MALE, Constants.SEX_FEMALE});
	static Param PARAM_SEX_OPTIONAL = new IntEnumParam("sex", false, new Integer[] {Constants.SEX_MALE, Constants.SEX_FEMALE});

	// begin count
	static Param PARAM_BEGIN = new IntParam("begin", true, 0);	// 分页开始
	static Param PARAM_COUNT = new IntParam("count", true, 1);	// count分页条数
	
	static Param[] PARAM_ARR_COMMON = new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR};
	static Param[] PARAM_ARR_COMMON_PAGE = new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR, PARAM_BEGIN, PARAM_COUNT};
	
	// fdc service item 
	static Param PARAM_SERVICE_ITEM = new IntListEnumParam("service_item", true, new Integer[] {
			Constants.FD_SERVICE_LNR,
			Constants.FD_SERVICE_GXY,
			Constants.FD_SERVICE_TNB,
			Constants.FD_SERVICE_YCF,
			Constants.FD_SERVICE_CHILD06,
			Constants.FD_SERVICE_JSB
	});
	
	// 圈子ID
	static Param CIRCLE_ID = new LongParam("circle_id", true);
	
	// 修改或新增圈子
	static Param UPDATE_CIRCLE_ID = new LongParam("circle_id", false);
	
	// 帖子标题
	static Param POST_TITLE = new StringParam("post_title", true);
	
	// 帖子内容
	static Param POST_CONTENT = new StringParam("post_content", true);
	
	// 帖子ID
	static Param POST_ID = new LongParam("post_id", true);
	
	// 预约挂号ID
	static Param APPO_REG_ID = new LongParam("appo_reg_id", false);
	
	// 医生ID
	static Param ADOCTOR_ID = new LongParam("adoctor_id", false);
	
	// 科室ID
	static Param DEPT_ID = new LongParam("dept_id", false);
	
	// 就诊用户ID
	static Param USER_ID_CLINIC = new LongParam("user_id_clinic", false);
	
	// 预约用户ID
	static Param USER_ID_APPO = new LongParam("user_id_appo", false);
	
	// 病情说明
	static Param NOTE = new StringParam("note", false);
	
	// 专干备注
	static Param REMARK_HECADRE = new StringParam("remark_hecadre", false);
	
	// 状态
	static Param APPO_STATE = new IntParam("appo_state", true);
	
	// 预约就诊时间
	static Param APPO_CLINIC_TIME = new LongParam("appo_clinic_time", false);
	
	// 确认预约就诊时间
	static Param SURE_APPO_TIME = new LongParam("sure_appo_time", false);
	
	// 实际就诊时间
	static Param CLINIC_TIME = new LongParam("clinic_time", false);
	
	// 计划免疫ID
	static Param PLAN_IMMUNE_ID = new LongParam("plan_immune_id", false);
	
	// 免疫用户ID
	static Param USER_ID_IMMUNE = new LongParam("user_id_immune", false);
	
	// 免疫提醒消息内容
	static Param NOTICE_CONTENT = new StringParam("notice_content", false);
	
	// 预约免疫时间
	static Param APPO_IMMUNE_TIME = new LongParam("appo_immune_time", false);
	
	// 实际免疫时间
	static Param IMMUNE_TIME = new LongParam("immune_time", false);
	
	// 活动类型
	static Param ACTIVITY_TYPE = new IntParam("activity_type", true);
	
	// 活动ID
	static Param ACTIVITY_ID = new LongParam("activity_id", true);
	
	// 活动时间
	static Param ACTIVITY_TIME = new LongParam("activity_time", false);
	
	// 活动地点
	static Param ACTIVITY_PLACE = new StringParam("activity_place", false);
	
	// 活动主题
	static Param SUBJECT = new StringParam("subject", false);
	
	// 参加活动的居民用户ID列表
	static Param USER_ID_TO_NOTICE_LIST = new LongListParam("user_id_to_notice_list", false);
	
	// 签到时间
	static Param SIGN_TIME = new LongParam("sign_time", false);
	
	// 医生ID
	static Param DOCTOR_ID = new LongParam("doctor_id", false);
	
	// 签到图片
	static Param SIGN_PIC_URL = new StringParam("sign_pic_url", false);
	
	// 圈子名称
	static Param CIRCLE_NAME = new StringParam("circle_name", false);
	
	// 圈子简介
	static Param CIRCLE_INTRO = new StringParam("circle_intro", false);
	
	//圈子成员列表
	static LongListParam MEMBER_LIST = new LongListParam("member_list", false);
	
	//医院ID
	static LongParam HOSPITAL_ID = new LongParam("hospital_id", false);
	
	//病种ID
	static LongParam ILLNESS_ID = new LongParam("illness_id", false);
	
	// 头像URL
	static Param HEAD_PIC_URL = new StringParam("head_pic_url", false);
	
	static StringListParam POST_PIC_URL_LIST = new StringListParam("post_pic_url_list",false);
	
	//签到或登记的居民用户列表
	static ParamListParam USER_SIGN_LIST = new ParamListParam("user_sign_list",false ,new Param[] {PARAM_UID, SIGN_TIME, SIGN_PIC_URL , DOCTOR_ID});

	//文章id
	static LongParam ARTICLE_ID = new LongParam("article_id", true);
	//文章标题
	static Param ARTICLE_TITLE = new StringParam("article_title", true);
	//文章分类code
	static Param ARTICLE_CATEGORY = new StringParam("article_category", true);
	//文章作者
	static Param ARTICLE_AUTHOR = new StringParam("article_author", true);
	//文章来源
	static Param ARTICLE_SOURCE = new StringParam("article_source", true);
	//文章关键字
	static Param ARTICLE_KEYWORDS = new StringParam("article_keywords", true);
	//文章图片url
	static Param ARTICLE_IMG_URL = new StringParam("article_img_url", true);
	//文章摘要
	static Param ARTICLE_SUMMARY = new StringParam("article_summary", true);
	//文章正文
	static Param ARTICLE_CONTENT = new StringParam("article_content", true);
	
	
	// 映射
	static Map<String, Param[]> URI_PARAM_MAPPING = new HashMap<String, Param[]>();

	static {
		// 检测前端版本
		URI_PARAM_MAPPING.put("/api/update/checkver", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_DEVID_REQUIRED,
				new StringParam("ver", true), //  "ver":"xx" //前端版本号
				new LongParam("ver_code", false, 1L)});
		
		// 获取用户基本信息
		URI_PARAM_MAPPING.put("/api/auth/getuserbasicinfo", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_DEVID_REQUIRED});
		
		// 获取短信验证码
		URI_PARAM_MAPPING.put("/api/auth/getsmscode", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_SMSTYPE, PARAM_MOBILE_REQUIRED});
		
		// 添加用户
		URI_PARAM_MAPPING.put("/api/auth/adduser", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR, 
				new StringParam("login_name", false, false), // "login_name":"xx" //登录名，可选  
				new StringParam("person_id", false, false), // "person_id":"xx" //身份证号码，可选，如携带，则每用户唯一
				PARAM_REALNAME_REQUIRED, PARAM_MOBILE_REQUIRED, 
				new StringParam("passwd_str", false), 
				new StringParam("name", true), 
				new StringParam("email", false),
				PARAM_SEX_REQUIRED, 
				new StringParam("head_pic_url", false)});
		
		// 用户注册
		URI_PARAM_MAPPING.put("/api/auth/registeruser", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, 
				new StringParam("person_id", true), // "person_id":"xx" //身份证号码，每用户唯一
				PARAM_MOBILE_REQUIRED, 
				new StringParam("sms_code", true), 
				new StringParam("passwd_str", true), 
				PARAM_DEVID_REQUIRED});
		
		// 认证登录
		URI_PARAM_MAPPING.put("/api/auth/userlogin", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, 
				new StringParam("login_name"), // "login_name":"xx" //登录名，或身份证号码
				new StringParam("login_auth_str"), // "login_auth_str":"xx" //登录认证密文串
				PARAM_DEVID_REQUIRED});
		
		// 获取用户信息 
		URI_PARAM_MAPPING.put("/api/auth/getuserinfo", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR, 
				new LongListParam("user_id_get", false),
				new StringListParam("person_id_get", false)});
		
		// 修改用户信息
		URI_PARAM_MAPPING.put("/api/auth/setuserinfo", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongParam("user_id_mod", false), // 要修改的用户ID，u64，可选，修改自己的信息时不携带
				new StringParam("person_id", false), 
				PARAM_REALNAME_OPTIONAL, 
				PARAM_MOBILE_OPTIONAL, 
				new StringParam("passwd_str", false), 
				new StringParam("name", false), 
				new StringParam("email", false),
				PARAM_SEX_OPTIONAL, 
				new StringParam("head_pic_url", false),
				PARAM_DEVID_OPTIONAL});
		
		// 重置用户密码
//		{
//			  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
//			  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
//			  "login_name":"xx" //需重置密码的用户登录名，或身份证号码
//			  "mobile":"xx" //需重置密码的用户手机号码
//			  "sms_code":"xx" //短信验证码
//			  "passwd_str":"xx" //密码密文串
//			}
		URI_PARAM_MAPPING.put("/api/auth/resetuserpasswd", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, 
				new StringParam("login_name"), 
				PARAM_MOBILE_REQUIRED, 
				new StringParam("sms_code"), 
				new StringParam("passwd_str")});
		
		// 修改用户手机号码
		URI_PARAM_MAPPING.put("/api/auth/setusermobile", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new RegexParam("new_mobile", true, "1\\d{10}"),
				new StringParam("sms_code")});
		
		// 服务登录
		URI_PARAM_MAPPING.put("/api/business/login", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new StringParam("svc_ticket_str")});
		
		// 获取下级行政区列表
		URI_PARAM_MAPPING.put("/api/business/getarealist", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongParam("area_id_higher", false, 1L)});
		
		// 意见反馈
		URI_PARAM_MAPPING.put("/api/business/comment", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new StringParam("comment")});
		
		// 获取内部推送消息
		URI_PARAM_MAPPING.put("/api/business/getpushmsg", PARAM_ARR_COMMON);
		
		// 获取任务计划
		URI_PARAM_MAPPING.put("/api/business/hecadre/gettasksum", PARAM_ARR_COMMON);

		// 添加任务计划
		URI_PARAM_MAPPING.put("/api/business/hecadre/addtask", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongListParam("add_visit_tenement_id", false)});
		
		// 获取通知消息
		URI_PARAM_MAPPING.put("/api/business/hecadre/getnotice", PARAM_ARR_COMMON);
		
		// 获取备忘列表
		URI_PARAM_MAPPING.put("/api/business/hecadre/getmemolist", PARAM_ARR_COMMON_PAGE);

		// 修改备忘
		URI_PARAM_MAPPING.put("/api/business/hecadre/modmemo", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongParam("memo_id", false, 1L),
				new StringParam("title", false), new StringParam("content", false)});
		
		// 获取家庭医生签约列表
		URI_PARAM_MAPPING.put("/api/business/hecadre/getfdcontractlist", PARAM_ARR_COMMON_PAGE);
		
		// 获取家庭医生签约信息
		URI_PARAM_MAPPING.put("/api/business/hecadre/getfdcontract", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongParam("fdcontract_id", false, 1L), new LongParam("tenement_id", false, 1L)});
		
		// 修改家庭医生签约信息
		URI_PARAM_MAPPING.put("/api/business/hecadre/modfdcontract", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongParam("fdcontract_id", true, 1L), 
				PARAM_SERVICE_ITEM,
				new IntEnumParam("fdcontract_state", true, new Integer[]{Constants.FDC_STATE_NOT_APPLIED, Constants.FDC_STATE_APPLIED, Constants.FDC_STATE_CONFIRMED}),
				new LongParam("begin_time", true, 0L),
				new LongParam("end_time", true, 0L)});
		
		// 获取所辖居民
		URI_PARAM_MAPPING.put("/api/business/hecadre/getinhabitant", PARAM_ARR_COMMON_PAGE);
		
		// 获取通知消息
		URI_PARAM_MAPPING.put("/api/business/inhabitant/getnotice", PARAM_ARR_COMMON_PAGE);

		// 回复通知消息
		URI_PARAM_MAPPING.put("/api/business/inhabitant/replynotice", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR,
				new LongParam("notice_id", true, 1L), 
				new IntEnumParam("reply_code", true, new Integer[] {Constants.NOTICE_REPLY_NOTED, Constants.NOTICE_REPLY_NOTIME, Constants.NOTICE_REPLY_APPOINTED}),
				new LongParam("appo_time", true, 1L)});
		
		// 获取对应健教专干
		URI_PARAM_MAPPING.put("/api/business/inhabitant/gethecadre", PARAM_ARR_COMMON);
		
		// 获取家庭医生签约申请
		URI_PARAM_MAPPING.put("/api/business/inhabitant/getfdcontractstate", PARAM_ARR_COMMON);

		// 提交家庭医生签约申请
		URI_PARAM_MAPPING.put("/api/business/inhabitant/signfdcontract", 
				new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID, PARAM_AUTHSTR, PARAM_SERVICE_ITEM});
		

		//获取交流圈列表
		URI_PARAM_MAPPING.put("/api/business/getcirclelist", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID});
		//获取交流圈留言列表
		URI_PARAM_MAPPING.put("/api/business/getcirclepostlist", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , CIRCLE_ID , PARAM_BEGIN , PARAM_COUNT});
		//发表交流圈留言
		URI_PARAM_MAPPING.put("/api/business/submitcirclepost", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , CIRCLE_ID , POST_TITLE , POST_CONTENT , POST_PIC_URL_LIST});
		//删除交流圈留言
		URI_PARAM_MAPPING.put("/api/business/delcirclepost", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , POST_ID});
		//获取预约挂号列表
		URI_PARAM_MAPPING.put("/api/business/getapporeglist", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , PARAM_BEGIN , PARAM_COUNT});
		//修改预约挂号
		URI_PARAM_MAPPING.put("/api/business/modapporeg", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , APPO_REG_ID , ADOCTOR_ID , DEPT_ID , USER_ID_CLINIC , USER_ID_APPO , 
				NOTE , REMARK_HECADRE , APPO_STATE , APPO_CLINIC_TIME , SURE_APPO_TIME , CLINIC_TIME});
		//获取计划免疫列表
		URI_PARAM_MAPPING.put("/api/business/getappoimmunelist", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , PARAM_BEGIN , PARAM_COUNT});
		//修改计划免疫
		URI_PARAM_MAPPING.put("/api/business/modappoimmune", new Param[] {PARAM_SRCTYPE, PARAM_PFTYPE, PARAM_UID , PLAN_IMMUNE_ID , USER_ID_IMMUNE , USER_ID_APPO , NOTICE_CONTENT , REMARK_HECADRE ,
				APPO_STATE , APPO_IMMUNE_TIME , SURE_APPO_TIME , IMMUNE_TIME});
		//获取活动类型状态
		URI_PARAM_MAPPING.put("/api/business/hecadre/getactivitystate", new Param[] {PARAM_HECADRE_SRCTYPE, PARAM_PFTYPE, PARAM_UID});
		//获取活动信息
		URI_PARAM_MAPPING.put("/api/business/hecadre/getactivity", new Param[] {PARAM_HECADRE_SRCTYPE, PARAM_PFTYPE, PARAM_UID , ACTIVITY_TYPE});
		//发起活动
		URI_PARAM_MAPPING.put("/api/business/hecadre/startactivity", new Param[] {PARAM_HECADRE_SRCTYPE, PARAM_PFTYPE, PARAM_UID , ACTIVITY_TYPE , ACTIVITY_TIME , ACTIVITY_PLACE , SUBJECT , USER_ID_TO_NOTICE_LIST});
		//活动签到或登记
		URI_PARAM_MAPPING.put("/api/business/hecadre/signinactivity", new Param[] {PARAM_HECADRE_SRCTYPE, PARAM_PFTYPE, PARAM_UID , ACTIVITY_ID , USER_SIGN_LIST});
		//进入活动总结
		URI_PARAM_MAPPING.put("/api/business/hecadre/enteractivitysum", new Param[] {PARAM_HECADRE_SRCTYPE, PARAM_PFTYPE, PARAM_UID , ACTIVITY_ID });
		//修改交流圈
		URI_PARAM_MAPPING.put("/api/business/hecadre/modcircle", new Param[] {PARAM_HECADRE_SRCTYPE, PARAM_PFTYPE, PARAM_UID , UPDATE_CIRCLE_ID , CIRCLE_NAME , CIRCLE_INTRO , MEMBER_LIST , HEAD_PIC_URL });
		//获取预约挂号医院及常见病种列表
		URI_PARAM_MAPPING.put("/api/business/inhabitant/getappohospitalillnesslist", new Param[] {PARAM_INHABITANT_SRCTYPE, PARAM_PFTYPE, PARAM_UID });
		//获取预约挂号医师列表
		URI_PARAM_MAPPING.put("/api/business/inhabitant/getappodoctorlist", new Param[] {PARAM_INHABITANT_SRCTYPE, PARAM_PFTYPE, PARAM_UID , HOSPITAL_ID , ILLNESS_ID});
		
		//专家讲堂居民端获取文章列表
		URI_PARAM_MAPPING.put("/web/inhabitant/getarticlelist", new Param[] {PARAM_BEGIN, PARAM_COUNT });
		//专家讲堂居民端获取文章详情
		URI_PARAM_MAPPING.put("/web/inhabitant/getarticledetail", new Param[] {ARTICLE_ID});
		//居民端文章收藏列表
		URI_PARAM_MAPPING.put("/web/inhabitant/getarticlefavoritlist", new Param[] {PARAM_BEGIN, PARAM_COUNT });
		//居民端文章收藏提交
		URI_PARAM_MAPPING.put("/web/inhabitant/submitarticlefavorite", new Param[] {ARTICLE_ID, PARAM_UID });
		//居民端文章取消收藏
		URI_PARAM_MAPPING.put("/web/inhabitant/canclearticlefavorite", new Param[] {ARTICLE_ID, PARAM_UID });
        //专家讲堂健教端获取文章列表
		URI_PARAM_MAPPING.put("/web/hecadre/getarticlelist", new Param[] {PARAM_BEGIN, PARAM_COUNT });
		//专家讲堂健教端获取分类下拉
		URI_PARAM_MAPPING.put("/web/hecadre/getarticlecategory", new Param[] {});
		//专家讲堂健教端发表文章
		URI_PARAM_MAPPING.put("/web/hecadre/submitarticle", new Param[] {ARTICLE_TITLE, ARTICLE_CATEGORY, ARTICLE_AUTHOR, ARTICLE_SOURCE, ARTICLE_KEYWORDS, ARTICLE_IMG_URL, ARTICLE_SUMMARY, ARTICLE_CONTENT, PARAM_UID });
		//专家讲堂健教端获取修改文章信息
		URI_PARAM_MAPPING.put("/web/hecadre/getarticlemod", new Param[] {ARTICLE_ID});
		//专家讲堂健教端修改文章
		URI_PARAM_MAPPING.put("/web/hecadre/modarticle", new Param[] {ARTICLE_TITLE, ARTICLE_CATEGORY, ARTICLE_AUTHOR, ARTICLE_SOURCE, ARTICLE_KEYWORDS, ARTICLE_IMG_URL, ARTICLE_SUMMARY, ARTICLE_CONTENT, PARAM_UID });
		
	}
	
	@Override
	public ParamCheckResult checkPost(String uri, Map<String, Object> params) {
		Param[] paramArr = URI_PARAM_MAPPING.get(uri);
		if(paramArr == null || paramArr.length == 0) return ParamCheckResult.VALID;
		
		for(Param param : paramArr) {
			Object val = params.get(param.getName());
			if(!param.isValid(val)) {
				String errMsgFmt = mesgService.get("common_err_param");
				return ParamCheckResult.invalidWithMsg(String.format(errMsgFmt, param.getName()));
			}
		}
		
		return ParamCheckResult.VALID;
	}

}

















