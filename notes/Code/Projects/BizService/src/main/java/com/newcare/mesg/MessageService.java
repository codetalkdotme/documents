package com.newcare.mesg;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.newcare.util.StringUtils;

@Component
public class MessageService {
	
	@Autowired
    private MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, Locale.CHINESE);
    }
    
    // 消息模板示例: 健教专干：{hecadreName}将在{appoDate}对您进行随访
    public String getWithParams(String code, Map<String, String> params) {
		String mesg = get(code);
		
		if(params != null) {
			for(String key : params.keySet()) {
				mesg = StringUtils.replaceNoRegex(mesg, "{" + key + "}", params.get(key));
			}
		}
		
		return mesg;
	}
    
}
