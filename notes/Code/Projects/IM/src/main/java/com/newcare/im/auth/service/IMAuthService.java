package com.newcare.im.auth.service;

import com.newcare.auth.pojo.ReqAuthResult;
import com.newcare.auth.pojo.SvcAuthResult;
import com.newcare.im.exception.IMServiceException;
import com.newcare.im.protocal.ProtocalPackage;

public interface IMAuthService {

	public SvcAuthResult serviceLogin(ProtocalPackage pack) throws IMServiceException;
	
	public void serviceLogout(String sessionId) throws IMServiceException;
	
	public ReqAuthResult requestAuth(String sessionId, Long userId, String srcType, String authStr) throws IMServiceException;
	
}
