package com.newcare.util;

import java.util.List;

public class ListUtils {

	public static final String concat(List<String> list, String sep) {
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0, size = list.size(); i < size; i++) {
			buf.append(list.get(i));
			
			if(i != size - 1) {
				buf.append(sep);
			}
		}
		
		return buf.toString();
	}
	
}
