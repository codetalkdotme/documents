package com.newcare.p3.jpush.enums;

public enum JpushMsgStatus {

	NOPUSH(1, "未推送"),
	PUSHED(2, "已推送");
	
	private int code; 
	private String name;
	
	private JpushMsgStatus(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
}
