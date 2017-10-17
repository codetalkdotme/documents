package com.newcare.fnd.mapper;

import java.util.List;

import com.newcare.fnd.pojo.RoleType;

public interface RoleTypeMapper {

	public void deleteByRoleId(Long roleId);
	
	public void insertRoleTypes(List<RoleType> roleTypes);
	
}
