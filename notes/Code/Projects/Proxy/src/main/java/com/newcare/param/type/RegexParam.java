package com.newcare.param.type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParam extends AbstractParam {

	private String regex;
	
	// 模式
	Pattern ptrn = null;
	
	public RegexParam(String name, boolean required, String regex) {
		super(name, required);
		
		this.regex = regex;
		ptrn = Pattern.compile(this.regex);
	}
	
	@Override
	public boolean isValid(Object obj) {
		if(required && obj == null) return false;
		
		if(!required && obj == null) return true;
		
		String s = obj.toString();
		Matcher m = ptrn.matcher(s);
		
		return m.matches();
	}

}










