package com.newcare.task.mapper;

import java.util.Map;

import com.newcare.task.pojo.TaskPlan;

public interface TaskPlanMapper {

	public TaskPlan selectActiveTaskPlan(Long userId);
	
	public Long insertTaskPlan(TaskPlan plan);
	
	public void deactiveTaskPlan(String planId);
	
	public void decrHomeLeft(Long userId);
	
	public void decrOther(Map<String, Long> params);
	
	public Integer countDocBetween(Map<String, Object> params);
	
	public Integer countVisitBetween(Map<String, Object> params);
	
}
