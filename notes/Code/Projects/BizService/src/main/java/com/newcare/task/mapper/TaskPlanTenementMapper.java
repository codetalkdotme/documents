package com.newcare.task.mapper;

import java.util.List;
import java.util.Map;

import com.newcare.task.pojo.TaskPlanTenement;

public interface TaskPlanTenementMapper {

	public void insertTaskPlanTenements(List<TaskPlanTenement> tptList);
	
	public List<TaskPlanTenement> selectTenementIn(Map<String, Object> params);

	public List<Long> selectTenementListByUser(Long userId);
	
	public int updateTenementDone(Map<String, Long> params);
	
}
