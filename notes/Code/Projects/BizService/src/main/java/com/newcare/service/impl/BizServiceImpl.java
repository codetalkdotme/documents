package com.newcare.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.newcare.auth.pojo.ReqAuthResult;
import com.newcare.auth.service.IBizAuthService;
import com.newcare.constant.Constants;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.service.IWhitelistFileService;
import com.newcare.service.AbstractBizService;
import com.newcare.service.IBizService;
import com.newcare.util.StringUtils;

/**
 * Created by guobxu on 2017/4/1.
 *
 * 业务服务实现
 *
 */
@Service("bizService")
public class BizServiceImpl extends AbstractBizService implements ApplicationContextAware {

	private static Logger LOGGER = LoggerFactory.getLogger(BizServiceImpl.class);
	
	@Autowired
	private IBizAuthService bizAuthService;
	
	private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	
    public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
    	LOGGER.info("In doPost...uri: " + uri + ", data=" + data);
    	
    	String userIdParam = StringUtils.toString(data.get("user_id"), false),
				authStrParam = StringUtils.toString(data.get("auth_str"), false),
				srcTypeParam = StringUtils.toString(data.get("src_type"), false);
    	if(bizAuthService.isUriSecured(uri)) {
    		try {
        		ReqAuthResult rt = bizAuthService.requestAuth(Long.parseLong(userIdParam), srcTypeParam, authStrParam);
        		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
        			if(rt.loginAgain()) {
        				return errorWithCodeKey(Constants.ERRCODE_AUTH_LOGIN_AGAIN, rt.getErrKey());
        			} else {
        				return errorWithKey(rt.getErrKey());
        			}
        		}
    		} catch(Exception ex) {
    			ex.printStackTrace();
    			
    			return errorWithKey("biz_reqauth_exception");
    		}
    	} else if(bizAuthService.isLisUri(uri)) {
    		try {
    			String lisSn = data.get("ioss_lis_sn").toString();
        		ReqAuthResult rt = bizAuthService.requestAuthLis(lisSn, srcTypeParam, authStrParam);
        		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
        			return errorWithKey(rt.getErrKey());
        		}
    		} catch(Exception ex) {
    			ex.printStackTrace();
    			
    			return errorWithKey("biz_reqauth_exception");
    		}
    	}
    	
        try {
        	String beanName = URI_MAP.get(uri);
        	if(beanName == null) {
        		return errorWithKey("biz_uri_notfound");
        	}
        	
        	IBizService bean = (IBizService)applicationContext.getBean(beanName);
        	String rtStr = bean.doPost(uri, data);
        	// LOGGER.info("In doPost...Result String: " + rtStr);
        	
        	return rtStr;
        } catch(Exception ex) {
        	ex.printStackTrace();
        	
        	throw new BizServiceException(ex);
        }
    }
    
    // 暂时不需要作通信认证
    public String doGet(String uri, Map<String, String[]> params) throws BizServiceException {
        try {
        	String beanName = URI_MAP.get(uri);
        	if(beanName == null) {
        		return errorWithKey("biz_uri_notfound");
        	}
        	
        	IBizService bean = (IBizService)applicationContext.getBean(beanName);
        	
        	return bean.doGet(uri, params);
        } catch(Exception ex) {
        	ex.printStackTrace();
        	
        	throw new BizServiceException(ex);
        }
    }
    
    @Override
	public String doServiceAuth(String uri, Map<String, Object> data) throws BizServiceException {
    	try {
        	IBizService bean = (IBizService)applicationContext.getBean(URI_MAP.get(uri));
        	
        	return bean.doServiceAuth(uri, data);
        } catch(Exception ex) {
        	ex.printStackTrace();
        	
        	throw new BizServiceException(ex);
        }
	}
    
    public String doUploadFile(String uri, String uid, String authStr) throws BizServiceException {
    	return doFileTransfer(uri, uid, authStr);
    }
    
    public String doGetFile(String uri, String uid, String authStr) throws BizServiceException {
    	return doFileTransfer(uri, uid, authStr);
    }
    
    private String doFileTransfer(String uri, String uid, String authStr) throws BizServiceException {
    	try {
    		ReqAuthResult rt = bizAuthService.requestAuth(Long.parseLong(uid), authStr);
    		if(rt.getCode() != ReqAuthResult.SUCCESS.getCode()) {
    			if(rt.loginAgain()) {
    				return errorWithCodeKey(Constants.ERRCODE_AUTH_LOGIN_AGAIN, rt.getErrKey());
    			} else {
    				return errorWithKey(rt.getErrKey());
    			}
    		}
		} catch(Exception ex) {
			ex.printStackTrace();
			
			return errorWithKey("biz_reqauth_exception");
		}
    	
    	return Constants.CODE_SUCCESS_S;
    }

}


















