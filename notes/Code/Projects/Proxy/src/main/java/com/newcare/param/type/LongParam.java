package com.newcare.param.type;

public class LongParam extends AbstractParam {

	private Long minVal;
	
	public LongParam(String name, boolean required) {
		this(name, required, null);
	}
	
	public LongParam(String name, boolean required, Long minVal) {
		super(name, required);
		
		this.minVal = minVal;
	}
	
	public boolean isValid(Object obj) {
		if(required && obj == null) return false;
		
		if(!required && obj == null) return true;
		
		if(!(obj instanceof Integer) && !(obj instanceof Long)) return false;
		
		Long tmp = Long.parseLong(obj.toString());
		if(minVal != null && tmp < minVal) return false;
		
		return true;
	}

}
