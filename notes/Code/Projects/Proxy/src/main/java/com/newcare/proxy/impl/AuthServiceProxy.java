package com.newcare.proxy.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.newcare.auth.exception.AuthServiceException;
import com.newcare.auth.service.IAuthService;
import com.newcare.param.checker.IParamChecker;
import com.newcare.param.checker.ParamCheckResult;
import com.newcare.proxy.AbstractServiceProxy;

@RestController
public class AuthServiceProxy extends AbstractServiceProxy {

	public static final String MSG_AUTH_EXCEPTION_MSG = "auth_exception_msg";
    
	@Autowired
	private IParamChecker paramChecker;
	
	@Autowired
	private IAuthService authService;
	
//    @PostConstruct
//    public void postConstruct() {
//        this.authService = (IAuthService)applicationContext.getBean("authService");
//    }

    /********************************************   Auth   ********************************************/ 
    @RequestMapping(value = "/hca/api/auth/**", method = RequestMethod.POST)
    public String doAuthPost(@RequestBody Map<String, Object> data, HttpServletRequest request) throws Exception {
    	String uri = request.getRequestURI();
    	
    	ParamCheckResult rt = paramChecker.checkPost(uri, data);
    	if(!rt.isValid()) {
    		return errorWithMsg(rt.getErrMsg());
    	}
    	
        try {
            return authService.doPost(uri, data);
        } catch(AuthServiceException ex) {
    		ex.printStackTrace();
    		
    		return errorWithKey(MSG_AUTH_EXCEPTION_MSG);
    	}
    }
    
}














