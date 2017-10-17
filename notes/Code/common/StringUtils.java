package com.newcare.util;

/**
 * 
 * @author guobxu
 *
 */
public final class StringUtils {

	// 如果null 或者 空, 则返回true
	public static boolean isNull(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean isNotNull(String str) {
		return str != null && str.length() > 0;
	}
	
	public static String toString(Object obj, boolean nullAsEmpty) {
		return obj == null ? 
				(nullAsEmpty ? "" : null) : obj.toString();
	}
	
	public static String replaceNoRegex(String str, String target, String replacement) {
		int i = str.indexOf(target);
		if(i == -1) return str;
		
		return str.substring(0, i) + replacement + str.substring(i + target.length());
	}
	
}
