package com.newcare.param.type;

public class IntEnumParam extends AbstractParam {

	private Integer[] values;
	
	public IntEnumParam(String name, boolean required, Integer[] values) {
		super(name, required);
		this.values = values;
	}
	
	public boolean isValid(Object obj) {
		if(required && obj == null) return false;
		if(!required && obj == null) return true;
		
		if(!(obj instanceof Integer)) return false;
		
		Integer tmp = (Integer)obj;
		for(Integer val : values) {
			if(tmp.equals(val)) return true;
		}
		
		return false;
	}

}














