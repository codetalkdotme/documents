package com.newcare.param.type;

public interface Param {

	// 参数名称
	public String getName();
	
	// 是否必须
	public boolean isRequired();
	
	// 参数是否合法
	public boolean isValid(Object obj);
	
	
}
