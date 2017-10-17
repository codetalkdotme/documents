package com.newcare.param.type;

public abstract class AbstractParam implements Param {

	protected String name;
	
	protected boolean required;
	
	public AbstractParam(String name, boolean required) {
		this.name = name;
		this.required = required;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isRequired() {
		return required;
	}
	
}
