package com.newcare.param.type;

public class StringParam extends AbstractParam {

	private boolean allowEmpty = false; // 是否允许空字符串
	
	public StringParam(String name) {
		this(name, true, false);
	}
	
	public StringParam(String name, boolean required) {
		this(name, required, false);
	}
	
	public StringParam(String name, boolean required, boolean allowEmpty) {
		super(name, required);
		
		this.allowEmpty = allowEmpty;
	}
	
	public boolean isValid(Object obj) {
		if(required && obj == null) return false;
		if(!required && obj == null) return true;
		
		if(!(obj instanceof String)) return false;
		
		String s = (String)obj;
		if(!allowEmpty && s.trim().length() == 0) return false;

		return true;
	}
	
}
