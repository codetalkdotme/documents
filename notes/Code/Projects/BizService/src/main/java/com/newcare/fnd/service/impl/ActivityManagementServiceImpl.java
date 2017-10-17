package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.newcare.act.pojo.ActActivities;
import com.newcare.auth.pojo.User;
import com.newcare.auth.service.IAuthService;
import com.newcare.cache.service.ICacheService;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.fnd.enums.NoticeMode;
import com.newcare.fnd.enums.NoticeType;
import com.newcare.fnd.enums.SourceType;
import com.newcare.fnd.mapper.ActivityManagementMapper;
import com.newcare.fnd.mapper.LookupMapper;
import com.newcare.fnd.pojo.Lookup;
import com.newcare.fnd.pojo.Notice;
import com.newcare.fnd.service.ILookupService;
import com.newcare.fnd.service.INoticeService;
import com.newcare.fnd.service.activity.IActivityManagementService;
import com.newcare.select.pojo.Select;
import com.newcare.util.CommonUtil;
import com.newcare.util.MapUtils;

@Service("activityManagementService")
public class ActivityManagementServiceImpl implements IActivityManagementService {

	@Autowired
	private ActivityManagementMapper activityManagementMapper;

	@Autowired
	private LookupMapper lookupMapper;

	/**
	 * 下拉框 :组织者
	 */
	// private static final String ORGANIZER_MAPPERS = "actOrganizerMappers";

	/**
	 * 下拉框 :参与人群
	 */
	// private static final String CROWD_MAPPERS = "crowdMappers";

	/**
	 * 下拉框 :印刷资料材料类型
	 */
	private static final String MATERIAL_TYPE_MAPPERS = "materialTypeMappers";

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private IAuthService authService;

	@Autowired
	private ILookupService lookupService;

	@Autowired
	private INoticeService noticeService;

	/**
	 * 列表
	 */
	@Override
	public Page<ActActivities> getPageDataList(Page<ActActivities> page) {
		page.getSearchMap().put("offset", page.getPageOffset());
		page.getSearchMap().put("limit", page.getPageSize());
		List<ActActivities> activities = activityManagementMapper.getDataList(page.getSearchMap());
		if (activities.size() > 0) {
			for (ActActivities activity : activities) {
				activity.setActivity_timeStr(CommonUtil.getDateStr(activity.getActivity_time(), "yyyy-MM-dd HH:mm"));// 活动时间
				activity.setOrganizer_name(getNameByUserId(activity.getUser_id_organizer()));// 组织者名字
				activity.setRecorder_name(getNameByUserId(activity.getUser_id_recorder()));// 记录人名字
				activity.setReceiver_name(getNameByUserId(activity.getUser_id_receiver()));// 印刷资料接收人名字
				activity.setHecadres(getNamesByUserIds(activity.getHecadreIds()));// 参与专干
			}
		}
		page.setContent(activities);
		page.setTotal(activityManagementMapper.countDataList(page.getSearchMap()));
		return page;
	}

	/**
	 * 根据userId获取姓名
	 * 
	 * @param userId
	 * @return
	 */
	public String getNameByUserId(Long userId) {
		if (userId != null) {
			User user = authService.getUserById(userId);
			if (user != null) {
				return user.getRealName();
			}
		}
		return null;
	}

	/**
	 * 根据ids获取姓名，并拼接成字符串
	 * 
	 * @param userIds
	 * @return
	 */
	public String getNamesByUserIds(String userIds) {
		if (userIds != null && !"".equals(userIds)) {
			String[] userIdArr = userIds.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < userIdArr.length; i++) {
				User user = authService.getUserById(Long.parseLong(userIdArr[i]));
				if (user != null) {
					sb.append(user.getRealName());
				} else {
					continue;
				}
				if (i != userIdArr.length - 1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 详情
	 */
	@Override
	public ActActivities view(Map<String, Object> map) {
		ActActivities activity = activityManagementMapper.searchView(map);
		if (activity != null) {
			if (StringUtils.isNotBlank(activity.getPicture_urls())) {
				activity.setPicture_url_arr(activity.getPicture_urls().split(","));
			}
			if (activity.getActivity_time() != null) {
				activity.setActivity_timeStr(CommonUtil.getDateStr(activity.getActivity_time(), "yyyy-MM-dd HH:mm"));// 活动时间
			}
			if (activity.getUser_id_organizer() != null) {
				activity.setOrganizer_name(getNameByUserId(activity.getUser_id_organizer()));// 组织者名字
			}
			if (activity.getUser_id_recorder() != null) {
				activity.setRecorder_name(getNameByUserId(activity.getUser_id_recorder()));// 记录人名字
			}
			if (activity.getUser_id_receiver() != null) {
				activity.setReceiver_name(getNameByUserId(activity.getUser_id_receiver()));// 印刷资料接收人名字
			}
			if (activity.getHecadreIds() != null) {
				activity.setHecadres(getNamesByUserIds(activity.getHecadreIds()));// 参与专干
			}
			if (activity.getPersonIds() != null) {
				activity.setPersons(getNamesByUserIds(activity.getPersonIds()));// 参与人员
			}
		}
		return activity;
	}

	@Override
	public List<Select> getOrganizerList(Long loginOrganizationId) {
		// if (cacheService.exists(ORGANIZER_MAPPERS)) {
		// return (List<Select>) cacheService.get(ORGANIZER_MAPPERS);
		// }
		List<Select> mappers = new ArrayList<Select>();
		/*
		 * //临时代码begin，提交到正式环境时注掉，解开下面正式代码 Map<String, Object> dbMap = new
		 * HashMap<String, Object>(); dbMap.put("activity_id", id);
		 * dbMap.put("role_code", roleCode); dbMap.put("activity_type",
		 * activityType);
		 * 
		 * List<Map<String, Object>> organizerList =
		 * activityManagementMapper.getNoSelectedByActivityId(dbMap); for
		 * (Map<String, Object> organizer : organizerList) { mappers.add(new
		 * Select(String.valueOf(organizer.get("staff_id")),
		 * String.valueOf(organizer.get("staff_name")), null)); } //临时代码end
		 */
		/* 正式代码 */
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("roleCode", roleCode);
		map.put("loginOrganizationId", loginOrganizationId);
		map.put("hecadreCode", Constants.ROLE_CODE_HECADRE);
		List<Long> userIdList = activityManagementMapper.getHecadreList(map);
		if (userIdList.size() > 0) {
			List<User> userList = authService.getUsers(userIdList);
			if (userList.size() > 0) {
				for (User user : userList) {
					mappers.add(new Select(String.valueOf(user.getId()), user.getRealName(), null));
				}
				// cacheService.set(ORGANIZER_MAPPERS, mappers);
			}
		}
		return mappers;
	}

	/**
	 * 参与专干左右选择区域
	 */
	@Override
	public Map<String, Object> getHecadreList(Long loginOrganizationId, Long activity_id, int activityType) {
		Map<String, Object> map = new HashMap<String, Object>();
		/*
		 * //临时代码begin Map<String, Object> dbMap = new HashMap<String,
		 * Object>(); dbMap.put("activity_id", id); dbMap.put("role_code",
		 * roleCode); dbMap.put("activity_type", activityType);
		 * map.put("noSelectedList",
		 * activityManagementMapper.getNoSelectedByActivityId(dbMap));//未选
		 * map.put("selectedList",
		 * activityManagementMapper.getSelectedByActivityId(dbMap));//已选
		 * //临时代码end
		 */

		/* 正式代码begin 正式代码end */
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//dataMap.put("roleCode", roleCode);
		dataMap.put("loginOrganizationId", loginOrganizationId);
		//dataMap.put("activity_id", activity_id);
		//左边下拉框改为展示全部，不再随选择后减去左边
		dataMap.put("activity_id", null);
		dataMap.put("activityType", activityType);
		dataMap.put("hecadreCode", Constants.ROLE_CODE_HECADRE);
		List<Long> userIdList = activityManagementMapper.getHecadreList(dataMap);
		List<Map<String, Object>> userMapList = new ArrayList<Map<String, Object>>();
		JSONArray staffJson = new JSONArray();
		if (userIdList.size() > 0) {
			List<User> userList = authService.getUsers(userIdList);
			if (userList.size() > 0) {
				for (User user : userList) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("staff_id", user.getId());
					tempMap.put("staff_name", user.getRealName());
					userMapList.add(tempMap);
					
					JSONObject jo = new JSONObject();
					jo.put("staff_id", user.getId());
					jo.put("staff_name", user.getRealName());
					staffJson.add(jo);
				}
			}
		}
		map.put("noSelectedList", userMapList);
		map.put("noSelectedStaff", staffJson.toString());
		
		//JSONObject.fromObject(userMapList).toString();
		dataMap.put("activity_id", activity_id);
		List<Long> selectedUserIdList = activityManagementMapper.getSelectedHecadreList(dataMap);
		List<Map<String, Object>> selectedUserMapList = new ArrayList<Map<String, Object>>();
		if (selectedUserIdList.size() > 0) {
			List<User> selectedUserList = authService.getUsers(selectedUserIdList);
			if (selectedUserList.size() > 0) {
				for (User selectedUser : selectedUserList) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("staff_id", selectedUser.getId());
					tempMap.put("staff_name", selectedUser.getRealName());
					selectedUserMapList.add(tempMap);
				}
			}
		}
		map.put("selectedList", selectedUserMapList);
		return map;
	}

	/**
	 * 义诊、体检修改页面已选医师
	 * 
	 * @param roleCode
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getDoctorList(Long activity_id) {
		return activityManagementMapper.getDoctorList(activity_id);
	}

	/**
	 * 参与人群
	 */
	@Override
	public List<Lookup> getParticipatingPopulation() {
		// if (cacheService.exists(CROWD_MAPPERS)) {
		// return (List<Lookup>) cacheService.get(CROWD_MAPPERS);
		// }
		List<Lookup> mappers = lookupService.getLookupsByCategory(Constants.LOOKUPS_RESIDENT_GROUP);
		// cacheService.set(CROWD_MAPPERS, mappers);
		return mappers;
	}

	@Transactional
	@Override
	public void save(ActActivities actActivity) {
		activityManagementMapper.save(actActivity);

		if (actActivity.getActivity_type() == Constants.ACT_TYPE_CLINIC
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_MEDICALLY_EXAMINED) {
			// 2:义诊，3：体检； 插入专干和医师
			List<Long> staffList = checkHecadre(actActivity.getHecadres(), actActivity.getUser_id_organizer());
			actActivity.setStaffList(staffList);
			activityManagementMapper.saveHecadres(actActivity);
			String hecadreNames = getNamesByUserIds(actActivity.getHecadres());
			List<Notice> noticeList = new ArrayList<Notice>();
			for (Long hecadreId : staffList) {
				Notice notice = new Notice();
				notice.setUserId(hecadreId);
				notice.setFromUser(actActivity.getLongin_user_id());
				notice.setSrcType(SourceType.HECADRE.getCode());
				notice.setType(NoticeType.HACTNEW.getCode());
				String actType = null;
				if (actActivity.getActivity_type() == Constants.ACT_TYPE_CLINIC) {
					actType = Constants.ACT_CLINIC;
				} else {
					actType = Constants.ACT_MEDICALLY_EXAMINED;
				}
				notice.setData(MapUtils.mapOf("actType", actType, "actTopic", actActivity.getSubject(), "actDate",
						actActivity.getActivity_timeStr(), "actPlace", actActivity.getActivity_place(), "organizerName",
						authService.getUserById(actActivity.getUser_id_organizer()).getRealName(), "hecadreNames",
						hecadreNames));
				noticeList.add(notice);
			}
			noticeService.addNoticeList(noticeList, NoticeMode.APP | NoticeMode.PUSH);
			// map.put("doctors", actActivity.getDoctors().split(","));
			List<ActActivities> doctors_list = new ArrayList<ActActivities>();
			for (int i = 0; i < actActivity.getDoctor_arr().length; i++) {
				ActActivities activity = new ActActivities();
				activity.setActivity_id(actActivity.getActivity_id());
				activity.setDoctor_name(actActivity.getDoctor_arr()[i]);
				if (actActivity.getActivity_type() == Constants.ACT_TYPE_CLINIC) {
					activity.setDoctor_department_name(actActivity.getDoctor_department_arr()[i]);
				} else {
					activity.setDoctor_department_name("");
				}
				doctors_list.add(activity);
			}
			activityManagementMapper.saveDoctors(doctors_list);
			
			//List<ActActivities> crownList = new ArrayList<ActActivities>();
			//actActivity.getUser_crowdArr();
			//ActActivities activity = new ActActivities();
			
			activityManagementMapper.saveUserCrowd(actActivity);
			
			
		} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_LECTURES
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_EDUCATION_ACTIVITY
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_HEALTH_KNOWLEDGE
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_PROPAGANDA
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_REGISTER) {
			// 4：健康知识讲座，8：健康教育活动，9：健康知识问卷；5：健康教育宣传栏登记，6：宣传资料播放登记 插入专干
			List<Long> staffList = checkHecadre(actActivity.getHecadres(), actActivity.getUser_id_organizer());
			actActivity.setStaffList(staffList);
			activityManagementMapper.saveHecadres(actActivity);

			if(actActivity.getActivity_type() == Constants.ACT_TYPE_LECTURES
					|| actActivity.getActivity_type() == Constants.ACT_TYPE_EDUCATION_ACTIVITY){
				activityManagementMapper.saveUserCrowd(actActivity);
			}
			String hecadreNames = getNamesByUserIds(actActivity.getHecadres());
			if (hecadreNames == null) {
				hecadreNames = "";
			}
			List<Notice> noticeList = new ArrayList<Notice>();
			for (Long hecadreId : staffList) {
				Notice notice = new Notice();
				notice.setUserId(hecadreId);
				notice.setFromUser(actActivity.getLongin_user_id());
				notice.setSrcType(SourceType.HECADRE.getCode());
				notice.setType(NoticeType.HACTNEW.getCode());
				String actType = null;
				if (actActivity.getActivity_type() == Constants.ACT_TYPE_LECTURES) {
					actType = Constants.ACT_LECTURES;
				} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_EDUCATION_ACTIVITY) {
					actType = Constants.ACT_EDUCATION_ACTIVITY;
				} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_HEALTH_KNOWLEDGE) {
					actType = Constants.ACT_HEALTH_KNOWLEDGE;
				} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_PROPAGANDA) {
					actType = Constants.ACT_PROPAGANDA;
				} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_REGISTER) {
					actType = Constants.ACT_REGISTER;
				}
				notice.setData(MapUtils.mapOf("actType", actType, "actTopic", actActivity.getSubject(), "actDate",
						actActivity.getActivity_timeStr(), "actPlace", actActivity.getActivity_place(), "organizerName",
						authService.getUserById(actActivity.getUser_id_organizer()).getRealName(), "hecadreNames",
						hecadreNames));
				noticeList.add(notice);
			}
			noticeService.addNoticeList(noticeList, NoticeMode.APP | NoticeMode.PUSH);
		} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_WORKSHOP) {
			// 7：健康教育专题研讨会； 插入专干，此活动特殊处理，活动参与人员即是专干
			// List<Long> staffList = new ArrayList<Long>();
			// staffList.add(actActivity.getUser_id_organizer());
			// actActivity.setStaffList(staffList);
			activityManagementMapper.saveOrganizer(actActivity);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("activityStaffId", actActivity.getActivities_staff_id());
			List<Long> hecadreList = new ArrayList<Long>();
			for (String hecadre : actActivity.getHecadres().split(",")) {
				hecadreList.add(Long.parseLong(hecadre));
			}
			map.put("user_id_to_notice_list", hecadreList);
			activityManagementMapper.addActPerson(map);
			String hecadreNames = getNamesByUserIds(actActivity.getHecadres());
			List<Notice> noticeList = new ArrayList<Notice>();
			for (Long hecadreId : hecadreList) {
				Notice notice = new Notice();
				notice.setUserId(hecadreId);
				notice.setFromUser(actActivity.getLongin_user_id());
				notice.setSrcType(SourceType.HECADRE.getCode());
				notice.setType(NoticeType.HACTNEW.getCode());
				String actType = Constants.ACT_WORKSHOP;
				notice.setData(MapUtils.mapOf("actType", actType, "actTopic", actActivity.getSubject(), "actDate",
						actActivity.getActivity_timeStr(), "actPlace", actActivity.getActivity_place(), "organizerName",
						authService.getUserById(actActivity.getUser_id_organizer()).getRealName(), "hecadreNames",
						hecadreNames));
				noticeList.add(notice);
			}
			noticeService.addNoticeList(noticeList, NoticeMode.APP | NoticeMode.PUSH);
		}
	}

	public List<Long> checkHecadre(String hecadres, Long organizer_id) {
		List<Long> hecadreLongList = new ArrayList<Long>();
		hecadreLongList.add(organizer_id);
		if (hecadres != null && hecadres != "") {
			for (String hecadre : hecadres.split(",")) {
				if (Long.parseLong(hecadre) == organizer_id) {// hecadre.equals(String.valueOf(organizer_id))
					continue;
				}
				hecadreLongList.add(Long.parseLong(hecadre));
			}
		}
		return hecadreLongList;
	}

	@Transactional
	@Override
	public void update(ActActivities actActivity) {
		// 修改基本信息
		activityManagementMapper.update(actActivity);
		if (actActivity.getActivity_type() == Constants.ACT_TYPE_CLINIC
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_MEDICALLY_EXAMINED) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("activity_id", actActivity.getActivity_id());
			// 删除参与专干
			activityManagementMapper.deleteHecadres(map);
			List<Long> staffList = checkHecadre(actActivity.getHecadres(), actActivity.getUser_id_organizer());
			actActivity.setStaffList(staffList);
			// 插入参与专干
			activityManagementMapper.saveHecadres(actActivity);
			//删除参与人群
			activityManagementMapper.deleteUserCrowd(map);
			//插入参与人群
			activityManagementMapper.saveUserCrowd(actActivity);
			// 删除医师
			activityManagementMapper.deleteDoctors(map);
			// map.put("doctors", actActivity.getDoctors().split(","));
			// 插入医师
			List<ActActivities> doctors_list = new ArrayList<ActActivities>();
			for (int i = 0; i < actActivity.getDoctor_arr().length; i++) {
				ActActivities activity = new ActActivities();
				activity.setActivity_id(actActivity.getActivity_id());
				activity.setDoctor_name(actActivity.getDoctor_arr()[i]);
				if (actActivity.getActivity_type() == Constants.ACT_TYPE_CLINIC) {
					activity.setDoctor_department_name(actActivity.getDoctor_department_arr()[i]);
				} else {
					activity.setDoctor_department_name("");
				}
				doctors_list.add(activity);
			}
			activityManagementMapper.saveDoctors(doctors_list);

		} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_LECTURES
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_EDUCATION_ACTIVITY
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_HEALTH_KNOWLEDGE
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_PROPAGANDA
				|| actActivity.getActivity_type() == Constants.ACT_TYPE_REGISTER) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("activity_id", actActivity.getActivity_id());
			// 删除参与专干
			activityManagementMapper.deleteHecadres(map);
			List<Long> staffList = checkHecadre(actActivity.getHecadres(), actActivity.getUser_id_organizer());
			actActivity.setStaffList(staffList);
			// 插入参与专干
			activityManagementMapper.saveHecadres(actActivity);
			if(actActivity.getActivity_type() == Constants.ACT_TYPE_LECTURES
					|| actActivity.getActivity_type() == Constants.ACT_TYPE_EDUCATION_ACTIVITY){
				//删除参与人群
				activityManagementMapper.deleteUserCrowd(map);
				//插入参与人群
				activityManagementMapper.saveUserCrowd(actActivity);
			}
			
		} else if (actActivity.getActivity_type() == Constants.ACT_TYPE_WORKSHOP) {// 7：健康教育专题研讨会；
																					// 插入专干，此活动特殊处理，活动参与人员即是专干
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("activity_id", actActivity.getActivity_id());
			// 删除活动参与人员
			activityManagementMapper.deleteActPersons(map);
			// 删除参与专干
			activityManagementMapper.deleteHecadres(map);

			// 插入专干
			// List<Long> staffList = new ArrayList<Long>();
			// staffList.add(actActivity.getUser_id_organizer());
			// actActivity.setStaffList(staffList);
			// activityManagementMapper.saveHecadres(actActivity);
			activityManagementMapper.saveOrganizer(actActivity);

			map.put("activityStaffId", actActivity.getActivities_staff_id());
			List<String> hecadreList = new ArrayList<String>();
			for (String hecadre : actActivity.getHecadres().split(",")) {
				hecadreList.add(hecadre);
			}
			map.put("user_id_to_notice_list", hecadreList);
			activityManagementMapper.addActPerson(map);
		}
	}

	@Override
	public Integer deleteList(String id) {
		if (activityManagementMapper.deleteList(id.split(",")) > 0) {
			return 2;
		}
		return 0;
	}

	@Override
	public List<Select> getMaterialTypeList() {
		if (cacheService.exists(MATERIAL_TYPE_MAPPERS)) {
			return (List<Select>) cacheService.get(MATERIAL_TYPE_MAPPERS);
		}
		List<Select> mappers = new ArrayList<Select>();
		List<Lookup> materialTypeList = lookupMapper.getLookupsByCategory(Constants.LOOKUPS_MATERIAL_TYPE);
		for (Lookup lookup : materialTypeList) {
			mappers.add(new Select(lookup.getLookup_code(), lookup.getLookup_value(), null));
		}
		cacheService.set(MATERIAL_TYPE_MAPPERS, mappers);
		return mappers;
	}

	@Override
	public List<Select> getReceiverList(Map<String,Object> remap) {
		List<Select> mappers = new ArrayList<Select>();
		List<Map<String, Object>> receiverList = activityManagementMapper.getReceiverList(remap);
		if (receiverList.size() > 0) {
			for (Map<String, Object> map : receiverList) {
				mappers.add(new Select(map.get("user_id").toString(),
						getNameByUserId(Long.parseLong(map.get("user_id").toString())), null));
			}
			return mappers;
		}
		return null;
	}

	@Override
	public List<Select> getActivityYearList(Map<String, Object> paramMap) {

		List<Select> mappers = new ArrayList<Select>();
		List<Map<String, Object>> activityYears = activityManagementMapper.getActivityYearList(paramMap);
		if (activityYears.size() > 0) {
			for (Map<String, Object> map : activityYears) {
				mappers.add(new Select(map.get("activity_year").toString(), map.get("activity_year").toString(), null));
			}
		}
		return mappers;
	}

	@Override
	public ActActivities getStationListByStaffId(Long staff_id) {
		List<Map<String, Object>> stationList = activityManagementMapper.getStationListByStaffId(staff_id);
		ActActivities activity = new ActActivities();
		activity.setReceiving_agency("");
		if (stationList.size() > 0) {
			activity.setReceiving_agency((String) stationList.get(0).get("station_name"));
		}
		return activity;
	}

	@Override
	public Map<String, Object> getStationInfoByStaffId(Long organizationId) {
		return activityManagementMapper.getStationInfoByStaffId(organizationId);
	}

}
