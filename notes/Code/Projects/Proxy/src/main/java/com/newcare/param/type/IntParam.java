package com.newcare.param.type;

public class IntParam extends AbstractParam {

	private Integer minVal;
	
	public IntParam(String name, boolean required) {
		this(name, required, null);
	}
	
	public IntParam(String name, boolean required, Integer minVal) {
		super(name, required);
		
		this.minVal = minVal;
	}
	
	public boolean isValid(Object obj) {
		if(required && obj == null) return false;
		
		if(!required && obj == null) return true;
		
		if(!(obj instanceof Integer)) return false;
		
		Integer tmp = (Integer)obj;
		if(minVal != null && tmp < minVal) return false;
		
		return true;
	}

}
