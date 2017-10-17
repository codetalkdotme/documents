package com.newcare.param.checker;

public class ParamCheckResult {

	public static final ParamCheckResult VALID = new ParamCheckResult(true, null);
	
	private boolean valid;
	private String errMsg;
	
	public static ParamCheckResult invalidWithMsg(String msg) {
		return new ParamCheckResult(false, msg);
	}
	
	private ParamCheckResult(boolean valid, String errMsg) {
		this.valid = valid;
		this.errMsg = errMsg;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public String getErrMsg() {
		return errMsg;
	}
	
	
}
