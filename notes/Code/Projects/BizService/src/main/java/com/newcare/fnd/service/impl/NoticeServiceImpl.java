package com.newcare.fnd.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newcare.auth.pojo.User;
import com.newcare.constant.Constants;
import com.newcare.doc.service.IResidentService;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.enums.NoticeMode;
import com.newcare.fnd.enums.NoticeReply;
import com.newcare.fnd.enums.NoticeStatus;
import com.newcare.fnd.enums.NoticeType;
import com.newcare.fnd.enums.PushStatus;
import com.newcare.fnd.enums.SourceType;
import com.newcare.fnd.mapper.NoticeMapper;
import com.newcare.fnd.pojo.Notice;
import com.newcare.fnd.service.INoticeService;
import com.newcare.mesg.MessageService;
import com.newcare.service.AbstractBizService;
import com.newcare.util.DateUtils;
import com.newcare.util.MapUtils;
import com.newcare.util.StringUtils;

/**
 * 通知服务实现
 * @author guobxu
 *
 */
@Service("noticeService")
public class NoticeServiceImpl extends AbstractBizService implements INoticeService {
	
	@Autowired
	private NoticeMapper noticeMapper;
	
//	@Autowired
//	private MessageService mesgService;
	
	@Autowired 
	private IResidentService residentService;
	
	// 专干获取通知消息
	public static final String URI_HECADRE_GETNOTICE = "/hca/api/business/hecadre/getnotice";
	
	// 居民获取通知
	public static final String URI_INHABITANT_GETNOTICE = "/hca/api/business/inhabitant/getnotice";
	
	// 居民回复通知
	public static final String URI_INHABITANT_REPLY = "/hca/api/business/inhabitant/replynotice";
	
	// 主动获取推送通知
	public static final String URI_COMMON_GETPUSH = "/hca/api/business/getpushmsg";
	
	@Override
	public int updateAllRead(Long userId, Integer srcType, Integer type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("srcType", srcType);
		params.put("type", type);
		
		return noticeMapper.updateAllRead(params);
	}
	
	@Override
	public int updateAllRead(Long userId, Integer srcType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("srcType", srcType);
		
		return noticeMapper.updateAllRead(params);
	}
	
	@Override
	public int updateAllReadByTypeList(Long userId, Integer srcType, List<Integer> typeList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("srcType", srcType);
		params.put("typeList", typeList);
		
		return noticeMapper.updateAllReadByTypeList(params);
	}
	
	// 初始化Notice属性: 内容, 通知类型等
	private void initNotice(Notice notice, int mode) {
		String content = getNoticeContent(notice);
		notice.setContent(content);
		
		if(notice.getType() == NoticeType.HVISITREPLY.getCode() || 
				notice.getType() == NoticeType.HACTNEW.getCode() ||
				notice.getType() == NoticeType.HNEWDOC.getCode()) {
			notice.setDtlContent(getNoticeDtlContent(notice));
		}
		
		notice.setIsApp( (mode & NoticeMode.APP) > 0 ? "Y" : "N" );
		notice.setIsPush( (mode & NoticeMode.PUSH) > 0 ? "Y" : "N" );
		notice.setIsSms( (mode & NoticeMode.SMS) > 0 ? "Y" : "N" );
	}
	
	public void addNotice(Notice notice, int mode) {
		initNotice(notice, mode);
		
		noticeMapper.insertNotice(notice);
	}
	
	@Transactional
	public void addNoticeList(List<Notice> noticeList, int mode) {
		for(Notice notice : noticeList) {
			initNotice(notice, mode);
		}
		
		noticeMapper.insertNoticeList(noticeList);
	}
	
	private String getNoticeContent(Notice notice) {
		String mesgKey = "notice_fmt_" 
							+ (SourceType.HECADRE.getCode() == notice.getSrcType() ? "H" : "I")
							+ notice.getType();
		Map<String, String> dataMap = notice.getData(); 
		
//		return getContent(mesgKey, dataMap);
		return messageService.getWithParams(mesgKey, dataMap);
	}
	
	private String getNoticeDtlContent(Notice notice) {
		String mesgKey = "notice_fmt_dtl_" 
				+ (SourceType.HECADRE.getCode() == notice.getSrcType() ? "H" : "I")
				+ notice.getType();
		Map<String, String> dataMap = notice.getData(); 
		
//		return getContent(mesgKey, dataMap);
		return messageService.getWithParams(mesgKey, dataMap);
	}
	
//	private String getContent(String mesgKey, Map<String, String> dataMap) {
//		String mesg = messageService.get(mesgKey);
//		
//		if(dataMap != null) {
//			for(String key : dataMap.keySet()) {
//				mesg = StringUtils.replaceNoRegex(mesg, "{" + key + "}", dataMap.get(key));
//			}
//		}
//		
//		return mesg;
//	}
	
	/**
	 * 
	 * Return:
	 * notice_type => 0
	 * new_count => 0
	 * 
	 */
	public List<Map<String, Integer>> countUnreadAppNotice(Long userId, int srcType) {
		Map<String, Object> countParams = new HashMap<String, Object>();
		countParams.put("userId", userId);
		countParams.put("srcType", srcType);
		
		return noticeMapper.countUnreadAppNotice(countParams);
	}

	@Override
	public List<Notice> getAppNoticeList(Notice param, int begin, int count) {
		Long userId = param.getUserId();
		Integer srcType = param.getSrcType(), type = param.getType();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("srcType", srcType);
		params.put("type", type);
		params.put("begin", begin);
		params.put("count", count);
		
		return noticeMapper.selectAppNoticeList(params);
	}
	
	@Override
	public int updateAppNoticeReplied(Notice notice) {
		return noticeMapper.updateAppNoticeReplied(notice);
	}
	
	@Transactional
	public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
		if(URI_HECADRE_GETNOTICE.equals(uri)) {
			return hecadreGetNotice(data);
		} else if(URI_INHABITANT_GETNOTICE.equals(uri)) {
			return inhabitGetNotice(data);
		} else if(URI_INHABITANT_REPLY.equals(uri)) {
			return inhabitReply(data);
		}
		
		return null;
	}
	
	private String getPush(Map<String, Object> params) {
		String srcTypeParam = params.get("src_type").toString();
		Long userId = Long.parseLong(params.get("user_id").toString());
		
		List<Notice> noticeList = noticeMapper.listUnreadPushNoticeByUser(userId);
		List<Map<String, String>> rtData = new ArrayList<Map<String, String>>();
		List<Long> idList = new ArrayList<Long>(); // for更新为已读
		for(Notice notice : noticeList) {
			rtData.add(MapUtils.mapOf("msg", notice.getContent()));
			
			idList.add(notice.getId());
		}
		
		if(idList.size() > 0) {
			noticeMapper.updatePushStatusByList(idList, PushStatus.PUSHED.getCode());
		}
		
		return successWithObject(rtData);
	}

	/**
	 * 
	 *  Params:
	 	{
		  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
		  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
		  "user_id":0L //用户ID，u64
		  "auth_str":"xx" //通信认证密文串
		  "notice_get_list": //要获取的消息类型及数量列表，可选
		  [
		    {
		      "notice_type":0 //通知类型，u8，1：预约挂号，2：咨询，3：预约免疫，4：家庭医生签约申请，5：预约体检，6：入户随访居民回复，7：活动邀请居民回复，8：建档提醒，9：活动通知，11：其他
		      "begin":0 //要获取记录的起始位置，u32
		      "count":0 //要获取的记录条数，u32
		    }
		    ...
		  ]
		}
	 * 
	 * Return:
	  {
		  "ret_code":0 //返回码，u16
		  "ret_msg":"xx" //可选，返回错误时的错误描述
		  "ret_data": //响应数据
		  {
		    new_count_list: //新通知数量列表
		    [
		      {
		        "notice_type":0 //通知类型，u8，1：预约挂号，2：咨询，3：预约免疫，4：家庭医生签约申请，5：预约体检，6：入户随访居民回复，7：活动邀请居民回复，8：建档提醒，11：其他
		        "new_count":0 //新通知个数，u32
		      }
		      ...
		    ]
		    notice_list: //通知列表
		    [
		      {
		        "notice_id":0L //通知ID，u64
		        "notice_type":0 //通知类型，u8，1：预约挂号，2：咨询，3：预约免疫，4：家庭医生签约申请，5：预约体检，6：入户随访居民回复，7：活动邀请居民回复，8：建档提醒，11：其他
		        "notice_content":"xx" //消息内容
		        "user_id_notice":0L //涉及的用户ID，u64
		        "appo_time":0L //预约挂号、免疫、体检时，约的时间，u64，可选
		        "create_time":0L //通知时间，u64
		      }
		      ...
		    ]
		  }
		}
	 * 
	 * @param params
	 * @return
	 */
	private String hecadreGetNotice(Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("user_id").toString());
		
		// 判断请求源类型
		String srcType = StringUtils.toString(params.get("src_type"), false);
		if(!Constants.SOURCE_TYPE_HECADRE.equals(srcType)) {
			return errorWithKey("notice_err_srctype");
		}
		
		Map<String, Object> rtData = new HashMap<String, Object>();
		// 获取数量
		List<Map<String, Integer>> countList = countUnreadAppNotice(userId, SourceType.HECADRE.getCode());
		rtData.put("new_count_list", countList);
		if(countList == null || countList.size() == 0) {
			rtData.put("notice_list", null);
		} else {
			List<Map<String, Integer>> types = new ArrayList<Map<String, Integer>>(); // 查询参数
			
			Object noticeGetObj = params.get("notice_get_list");
			if(noticeGetObj == null) {
				for(Map<String, Integer> element : countList) {
					Map<String, Integer> paramMap = MapUtils.mapOf("notice_type", element.get("notice_type"), "begin", 0, "count", 8);
					types.add(paramMap);
				}
				List<Notice> noticeList = getHecadreAppNoticeList(userId, types);
				rtData.put("notice_list", Notice.toMapList(noticeList, false, false));
			} else {
//				"notice_get_list": //要获取的消息类型及数量列表，可选
//				  {
//				    "notice_type_list:" //通知类型列表
//				    [
//				      0 //u8，1：预约挂号，2：咨询，3：预约免疫，4：家庭医生签约申请，5：预约体检，6：入户随访居民回复，7：活动邀请居民回复，8：建档提醒，9：活动通知，11：其他
//				      ...
//				    ]
//				    "begin":0 //要获取记录的起始位置，u32
//				    "count":0 //要获取的记录条数，u32
//				  }
				Map<String, Object> noticeGetMap = (Map<String, Object>)noticeGetObj;
				List<Integer> typeIdList = (List<Integer>)noticeGetMap.get("notice_type_list");
				if(typeIdList.size() == 0) {
					return errorWithKey("notice_err_idlist_empty");
				}
				
				Integer begin = Integer.parseInt(noticeGetMap.get("begin").toString()),
						count = Integer.parseInt(noticeGetMap.get("count").toString());
//				List<Notice> noticeList = getHecadreAppNoticeList(userId, types);
				List<Notice> noticeList = getHecadreAppNoticeByTypeList(userId, typeIdList, begin, count);
				rtData.put("notice_list", Notice.toMapList(noticeList, true, false));
				
				// 更新通知状态
				this.updateAllReadByTypeList(userId, SourceType.HECADRE.getCode(), typeIdList);
			}
		}
		
		return successWithObject(rtData);
	}
	
	private List<Notice> getHecadreAppNoticeList(Long userId, List<Map<String, Integer>> types) {
		// 获取列表
		List<Notice> noticeList = new ArrayList<Notice>();
		Notice param = new Notice();
		param.setUserId(userId);
		param.setSrcType(SourceType.HECADRE.getCode());
		for(Map<String, Integer> typeMap : types) {
			param.setType(typeMap.get("notice_type"));
			
			List<Notice> listByType = getAppNoticeList(param, typeMap.get("begin"), typeMap.get("count"));
			if(listByType != null && listByType.size() > 0) {
				noticeList.addAll(listByType);
			}
		}
		
		return noticeList;
	}
	
	private List<Notice> getAppNoticeByTypeList(Long userId, Integer srcType, List<Integer> typeIdList, Integer begin, Integer count) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("srcType", srcType);
		params.put("typeList", typeIdList);
		params.put("begin", begin);
		params.put("count", count);
		
		return noticeMapper.selectAppNoticeByTypeList(params);
	}
	
	private List<Notice> getHecadreAppNoticeByTypeList(Long userId, List<Integer> typeIdList, Integer begin, Integer count) {
		return getAppNoticeByTypeList(userId, SourceType.HECADRE.getCode(), typeIdList, begin, count);
	}
	
	private List<Notice> getInhabitantAppNoticeByTypeList(Long userId, List<Integer> typeIdList, Integer begin, Integer count) {
		return getAppNoticeByTypeList(userId, SourceType.INHABITANT.getCode(), typeIdList, begin, count);
	}
	
	private String inhabitGetNotice(Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("user_id").toString());
		
		// 判断请求源类型
		String srcType = StringUtils.toString(params.get("src_type"), false);
		if(!Constants.SOURCE_TYPE_INHABITANT.equals(srcType)) {
			return errorWithKey("notice_err_srctype");
		}
		
		int beginParam = Integer.parseInt(params.get("begin").toString()), 
				countParam = Integer.parseInt(params.get("count").toString());
		
		// 获取列表
//		Notice param = new Notice();
//		param.setUserId(userId);
//		param.setSrcType(SourceType.INHABITANT.getCode());
		
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(NoticeType.IIMMUNE.getCode());
		typeList.add(NoticeType.ICHECK.getCode());
		typeList.add(NoticeType.IVISIT.getCode());
		typeList.add(NoticeType.IACTNEW.getCode());
		typeList.add(NoticeType.IOTHER.getCode());
		
		List<Notice> noticeList = getInhabitantAppNoticeByTypeList(userId, typeList, beginParam, countParam);
		
		// 更新通知状态
		this.updateAllRead(userId, SourceType.INHABITANT.getCode());
		
		return successWithObject(Notice.toMapList(noticeList, false, true));
	}
	
	/**
	 * Params:
	 	{
		  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
		  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
		  "user_id":0L //用户ID，u64
		  "auth_str":"xx" //通信认证密文串
		  "notice_id":0L //通知ID，u64
		  "reply_code":0 //回复码，u16，1：我知道了，2：不太方便
		}
		
		{
		  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
		  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
		  "user_id":0L //用户ID，u64
		  "auth_str":"xx" //通信认证密文串
		  "notice_id":0L //通知ID，u64
		  "reply_code":0 //回复码，u16，1：我知道了，2：不太方便，3：已预约
		  "appo_time":0L //预约体检的时间，u64，回复预约体检消息时使用
		}

		
	 * @param params
	 * @return
	 * 
	 * TODO: 回复预约体检
	 */
	private String inhabitReply(Map<String, Object> params) {
		// 判断请求源类型
		String srcType = StringUtils.toString(params.get("src_type"), false);
		if(!Constants.SOURCE_TYPE_INHABITANT.equals(srcType)) {
			return errorWithKey("notice_err_srctype");
		}
		
		Long userId = Long.parseLong(params.get("user_id").toString()),
				replyTo = Long.parseLong(params.get("notice_id").toString());
		int replyCode = Integer.parseInt(params.get("reply_code").toString());
		
		// 通知是否存在
		Notice notice = noticeMapper.selectAppNoticeById(replyTo);
		if(notice == null) {
			return errorWithKey("notice_err_notexist");
		} else if(!userId.equals(notice.getUserId())) {
			return errorWithKey("notice_err_user");
		} else if(notice.getAppStatus() != null && notice.getAppStatus() == NoticeStatus.REPLIED.getCode()) {
			return errorWithKey("notice_err_replied");
		}
		
		Notice param = new Notice();
		param.setId(replyTo);
		param.setReplyCode(replyCode);
		
		String replyContent = null;
		if(replyCode == NoticeReply.NOTED.getCode()) {
			replyContent = NoticeReply.NOTED.getContent();
		} else if(replyCode == NoticeReply.NOTIME.getCode()) {
			replyContent = NoticeReply.NOTIME.getContent();
		} else if(replyCode == NoticeReply.APPOINTED.getCode()) {
			replyContent = NoticeReply.APPOINTED.getContent();
		} else {
			return errorWithKey("notice_err_replycode");
		}
		param.setReplyContent(replyContent);
		
		// 回复通知
		if(notice.getType() == NoticeType.IVISIT.getCode()) {	// 随访反馈
			Notice newNotice = new Notice();
			newNotice.setUserId(notice.getFromUser());
			newNotice.setFromUser(userId);
			newNotice.setSrcType(SourceType.HECADRE.getCode());
			newNotice.setType(NoticeType.HVISITREPLY.getCode());
			
			User user = authService.getUserById(userId);
			Timestamp appoDate = notice.getAppoDate();
			Map<String, String> data = MapUtils.mapOf("realName", user.getRealName(),
					"replyContent", replyContent, 
					"appoDate", appoDate == null ? "" : DateUtils.formatTimestamp(appoDate, "yyyy年MM月dd日 HH时"));
			newNotice.setData(data);
			
			addNotice(newNotice, NoticeMode.APP | NoticeMode.PUSH);
		} else if(notice.getType() == NoticeType.IIMMUNE.getCode()) { // 预约免疫
			Long fromUser = notice.getFromUser();
			if(fromUser == null) {
				fromUser = residentService.getHecadreByResident(userId);
			}
			if(fromUser == null) {
				return errorWithKey("notice_err_hecadre_notfound");
			}
			
			Timestamp appoTime = new Timestamp(Long.parseLong(params.get("appo_time").toString())); // 免疫时间
			
			Notice newNotice = new Notice();
			newNotice.setUserId(fromUser);
			newNotice.setFromUser(userId);
			newNotice.setSrcType(SourceType.HECADRE.getCode());
			newNotice.setType(NoticeType.HIMMUNE.getCode());
			newNotice.setAppoDate(appoTime);
			
			User user = authService.getUserById(userId);
			Map<String, String> data = MapUtils.mapOf("realName", user.getRealName());
			newNotice.setData(data);
			
			addNotice(newNotice, NoticeMode.APP | NoticeMode.PUSH);
		}
		
		updateAppNoticeReplied(param);
		
		return Constants.RESPONSE_SUCCESS;
	}

	@Override
	public void addIMNotice(Long fromUserId, Long toUserId, String srcType, Long mesgId, String content, int mode) {
		Notice notice = new Notice();
		notice.setFromUser(fromUserId);
		notice.setUserId(toUserId);
		notice.setMesgId(mesgId);

		User user = authService.getUserById(fromUserId);
		Map<String, String> dataMap = new HashMap<String, String>();
		if(SourceType.HECADRE.getEnName().equals(srcType)) {
			notice.setType(NoticeType.IMESG.getCode());
			notice.setSrcType(SourceType.INHABITANT.getCode());
			
			dataMap.put("hecadreName", user.getRealName());
			dataMap.put("msgContent", processIMMesg(content));
			notice.setData(dataMap);
			
			this.addNotice(notice, mode);
		} else if(SourceType.INHABITANT.getEnName().equals(srcType)) {
			notice.setType(NoticeType.HQUERY.getCode());
			notice.setSrcType(SourceType.HECADRE.getCode());
			
			dataMap.put("realName", user.getRealName());
			notice.setData(dataMap);
			
			this.addNotice(notice, mode);
		}
	}

	/**
	 * 处理消息内容
	 * 1. <text>很好</text>		
	 * 			=============> 很好
	 * 2. <image>group1/M00/00/02/wKhQBFlZrYqAJ1pvAABLK6vYjks9182453</image>
	 * 			=============> 发来一张图片
	 * @param content
	 * @return
	 */
	public String processIMMesg(String mesg) {
		String textNode = "<text>", imgNode = "<image>";
		
		if(mesg.startsWith(textNode)) {
			return mesg.substring(6, mesg.length() - 7);
		} else if(mesg.startsWith(imgNode)) {
			return messageService.get("notice_im_recvimg");
		}
		
		return mesg;
	}
	
	@Override
	public void updateIMReadByList(List<Long> mesgIdList) {
		noticeMapper.updateIMReadByList(mesgIdList);
	}

	
	
	

}
















