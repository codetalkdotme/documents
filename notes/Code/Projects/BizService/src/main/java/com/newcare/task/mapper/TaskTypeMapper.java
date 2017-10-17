package com.newcare.task.mapper;

import java.util.List;

import com.newcare.task.pojo.TaskType;

public interface TaskTypeMapper {

	public List<TaskType> selectTypesExceptHome();
	
	public List<TaskType> selectAllTypes();
	
}
