package com.newcare.param.type;

import java.util.Arrays;
import java.util.List;

public class LongListEnumParam extends AbstractParam {

	private List<Long> valList;
	
	private boolean allowEmpty = false;
	
	public LongListEnumParam(String name, boolean required, Long[] values) {
		this(name, required, values, false);
	}
	
	public LongListEnumParam(String name, boolean required, Long[] values, boolean allowEmpty) {
		super(name, required);
		this.valList = Arrays.asList(values);
		
		this.allowEmpty = allowEmpty;
	}
	
	public boolean isValid(Object objParam) {
		if(required && objParam == null) return false;
		if(!required && objParam == null) return true;
		
		try {
			List<Object> objList = (List<Object>)objParam;
			
			for(Object obj : objList) {
				if(obj == null || (!(obj instanceof Integer) && !(obj instanceof Long))) return false;
				
				Long tmp = Long.parseLong(obj.toString());
				if(!valList.contains(tmp)) return false;
			}
			
			if(!allowEmpty && objList.size() == 0) return false;
		} catch(Exception ex) {
			return false;
		}
		
		return true;
	}

}














