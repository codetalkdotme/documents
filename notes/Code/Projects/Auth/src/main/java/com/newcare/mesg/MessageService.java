package com.newcare.mesg;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageService {
	
	@Autowired
    private MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, Locale.CHINESE);
    }
    
}
