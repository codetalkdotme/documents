package com.newcare.task.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newcare.cache.service.ICacheService;
import com.newcare.constant.Constants;
import com.newcare.doc.mapper.ResidentBaseMapper;
import com.newcare.exception.BizServiceException;
import com.newcare.service.AbstractBizService;
import com.newcare.task.mapper.TaskPlanDetailMapper;
import com.newcare.task.mapper.TaskPlanMapper;
import com.newcare.task.mapper.TaskPlanTenementMapper;
import com.newcare.task.mapper.TaskTypeMapper;
import com.newcare.task.pojo.TaskPlan;
import com.newcare.task.pojo.TaskPlanDetail;
import com.newcare.task.pojo.TaskPlanTenement;
import com.newcare.task.pojo.TaskType;
import com.newcare.task.service.ITaskPlanService;
import com.newcare.util.SqlUtils;

@Service("taskPlanService")
public class TaskPlanServiceImpl extends AbstractBizService implements ITaskPlanService {

	public static final String URI_TASK_ADD = "/hca/api/business/hecadre/addtask";			// 添加任務
	public static final String URI_TASK_QUERY = "/hca/api/business/hecadre/gettasksum";		// 查詢任務
	
	// cache
//	public static final String CACHE_KEY_TASK = "TASKS";
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private TaskPlanMapper taskPlanMapper;
	
	@Autowired
	private TaskPlanTenementMapper tptMapper;
	
	@Autowired
	private TaskPlanDetailMapper taskPlanDtlMapper;
	
	@Autowired
	private TaskTypeMapper ttMapper;
	
	@Autowired
	private ResidentBaseMapper rbMapper;
	
	@Override
	public void deactiveTaskPlan(String planId) {
		taskPlanMapper.deactiveTaskPlan(planId);
	}
	
	@Override
	public TaskPlan getActiveTaskPlan(Long userId) {
//		TaskPlan cached = (TaskPlan)cacheService.hget(CACHE_KEY_TASK, String.valueOf(userId));
//		if(cached != null) return cached;
		
		TaskPlan plan = taskPlanMapper.selectActiveTaskPlan(userId);
//		if(plan != null) {
//			cacheService.hset(CACHE_KEY_TASK, String.valueOf(userId), plan);
//		}
		
		return plan;
	}

	@Transactional
	public String addTaskPlan(TaskPlan plan) {
		Long userId = plan.getUserId();
		
		// 0608: 改为全量增加
//		if(existsTenement(userId, plan.getTenementIdList())) {
//			return errorWithKey("task_error_tntexists");
//		}
		
		TaskPlan current = getActiveTaskPlan(userId);
		List<TaskPlanDetail> dtlList = plan.getDtlList();
		if(current != null) {
			deactiveTaskPlan(current.getId());
			
			List<TaskPlanDetail> currentDtlList = current.getDtlList();
			for(TaskPlanDetail dtl : dtlList) {
				Long typeId = dtl.getTaskTypeId();
//				if(typeId == 1L) continue;		// 0608: 改为全量增加	0621: 入户与其他任务的相同处理
				
				for(TaskPlanDetail currentDtl : currentDtlList) {
					if(typeId.equals(currentDtl.getTaskTypeId())) {
						dtl.setHistCount(currentDtl.getLeftCount());
						dtl.setLeftCount(dtl.getNewCount() + currentDtl.getLeftCount());
						
						break;
					}
				}
			}
		}
		
		String planId = UUID.randomUUID().toString();
		plan.setId(planId);
		taskPlanMapper.insertTaskPlan(plan);
		
		for(TaskPlanDetail dtl : dtlList) {
			dtl.setPlanId(planId);
		}
		taskPlanDtlMapper.insertTaskPlanDetails(dtlList);
		
		// 入户随访
//		List<Long> tidList = plan.getTenementIdList();
//		if(tidList != null && tidList.size() > 0) {
//			List<TaskPlanTenement> tptList = new ArrayList<TaskPlanTenement>();
//			for(Long tid : tidList) {
//				TaskPlanTenement tpt = new TaskPlanTenement();
//				tpt.setPlanId(planId);
//				tpt.setTenementId(tid);
//				tpt.setUserId(userId);
//				tpt.setStatus(TaskPlanTenementStatus.NOTDONE.getCode());
//				
//				tptList.add(tpt);
//			}
//		
//			tptMapper.insertTaskPlanTenements(tptList);
//		}
		
		// cache 
//		cacheService.hset(CACHE_KEY_TASK, String.valueOf(userId), plan);
//		cacheService.hdel(CACHE_KEY_TASK, String.valueOf(userId));
		return Constants.RESPONSE_SUCCESS;
	}

	@Transactional
	public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
		if(URI_TASK_QUERY.equals(uri)) {
			Long userId = Long.parseLong(data.get("user_id").toString());
			TaskPlan plan = getActiveTaskPlan(userId);
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			if(plan == null) {
//				dataMap.put("visit_tenement_id", new ArrayList<Long>());
				
				List<TaskType> types = ttMapper.selectAllTypes();
				List<Map<String, Object>> typeList = new ArrayList<Map<String, Object>>();
				for(TaskType tt : types) {
					Map<String, Object> ttmap = new HashMap<String, Object>();
					ttmap.put("task_type_id", tt.getId());
					ttmap.put("task_type_name", tt.getName());
					ttmap.put("task_type_name_hp", tt.getHpName());
					ttmap.put("task_count", 0);
					
					typeList.add(ttmap);
				}
				dataMap.put("task_count_list", typeList);
				dataMap.put("create_doc_count_this_month", 0);
				dataMap.put("visit_count_this_month", 0);
			} else {
//				dataMap.put("visit_tenement_id", tptMapper.selectTenementListByUser(userId));
				List<Map<String, Object>> typeList = new ArrayList<Map<String, Object>>();
				List<TaskPlanDetail> dtlList = plan.getDtlList();
				for(TaskPlanDetail dtl : dtlList) {
					Long typeId = dtl.getTaskTypeId();
//					if(typeId == 1) continue;
					
					Map<String, Object> ttmap = new HashMap<String, Object>();
					ttmap.put("task_type_id", typeId);
					ttmap.put("task_type_name", dtl.getTaskTypeName());
					ttmap.put("task_type_name_hp", dtl.getTaskTypeHpName());
					ttmap.put("task_count", dtl.getLeftCount());
					
					typeList.add(ttmap);
				}
				dataMap.put("task_count_list", typeList);
				dataMap.put("create_doc_count_this_month", countDocThisMonth(userId));
				dataMap.put("visit_count_this_month", countVisitThisMonth(userId));
			}
			
			return successWithObject(dataMap);
		} else if(URI_TASK_ADD.equals(uri)) {
			TaskPlan plan = TaskPlan.objectFromMap(data);
			return addTaskPlan(plan);
		}
		
		return null;
	}

	@Override
	public boolean existsTenement(Long userId, List<Long> tenementIdList) {
		if(tenementIdList == null || tenementIdList.size() == 0) return false;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("tenementList", tenementIdList);
		
		List<TaskPlanTenement> tenementList = tptMapper.selectTenementIn(params);
		if(tenementList != null && tenementList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Long> findTenementListByUser(Long userId) {
		return tptMapper.selectTenementListByUser(userId);
	}

	@Override
	public int countDocThisMonth(Long userId) {
		Long startDate = SqlUtils.monthStartOfNow(), endDate = SqlUtils.monthEndOfNow();
		Integer docThisMonth = rbMapper.countDocByHecadreBetween(userId, startDate, endDate);
		
		return docThisMonth;
	}

	@Override
	public int countVisitThisMonth(Long userId) {
		Long startDate = SqlUtils.monthStartOfNow(), endDate = SqlUtils.monthEndOfNow();
		Integer visitThisMonth = rbMapper.countVisitByHecadreBetween(userId, startDate, endDate);
		
		return visitThisMonth;
	}

	@Transactional
	public void decrHome(Long userId, Long tenementId) {
//		Map<String, Long> params = new HashMap<String, Long>();
//		params.put("userId", userId);
//		params.put("tenementId", tenementId);
//		
//		int updateCount = tptMapper.updateTenementDone(params);
//		
//		if(updateCount > 0) {
//			taskPlanMapper.decrHomeLeft(userId);
//		}
		
		taskPlanMapper.decrHomeLeft(userId);
		
//		if(updateCount > 0) {
//			cacheService.hdel(CACHE_KEY_TASK, String.valueOf(userId));
//		}
	}

	@Transactional
	public void decrOther(Long userId, Long taskTypeId, Long tenementId) {
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("userId", userId);
		params.put("taskTypeId", taskTypeId);
		
		taskPlanMapper.decrOther(params);
//		decrHome(userId, tenementId);
		
//		cacheService.hdel(CACHE_KEY_TASK, String.valueOf(userId));
	}

	
}


















