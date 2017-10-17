package com.newcare.proxy;

import org.springframework.beans.factory.annotation.Autowired;

import com.newcare.constant.Constants;
import com.newcare.mesg.MessageService;

public abstract class AbstractServiceProxy {

	@Autowired
	protected MessageService messageService;
	
	protected String errorWithKey(String messageKey) {
		return String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, messageService.get(messageKey));
	}
	
	protected String errorWithMsg(String errMsg) {
		return String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, errMsg);
	}
	
}
