package com.newcare.param.type;

import com.newcare.util.StringUtils;

public class StringEnumParam extends AbstractParam {

	private String[] values;
	
	public StringEnumParam(String name, boolean required, String[] values) {
		super(name, required);
		this.values = values;
	}
	
	public boolean isValid(Object obj) {
		if(required && obj == null) return false;
		if(!required && obj == null) return true;
		
		if(!(obj instanceof String)) return false;
		
		String s = (String)obj;
		if(required && StringUtils.isNull(s)) {
			return false;
		}
		
		for(String val : values) {
			if(s.equals(val)) return true;
		}
		
		return false;
	}

}














