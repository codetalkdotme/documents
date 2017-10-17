package com.newcare.im.enums;

public enum MesgStatus {

	NOTSENT(1),		
	SENT(2),
	RECEIVED(3);
	
	private int code;
	
	private MesgStatus(int code) {
        this.code = code;
    }
	
	public int getCode() {
		return code;
	}
	
}
