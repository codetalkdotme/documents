package com.newcare.auth.service;

import com.newcare.auth.pojo.ReqAuthResult;

/**
 * 
 * @author guobxu
 * 
 */
public interface IBizAuthService {

	public ReqAuthResult requestAuth(long uid, String srcType, String authStr) throws Exception;
	
	// ignore srcType
	public ReqAuthResult requestAuth(long uid, String authStr) throws Exception;
	
	// LIS 认证
	public ReqAuthResult requestAuthLis(String lisSn, String srcType, String authStr) throws Exception;
	
	// uri是否需要通讯认证
	public boolean isUriSecured(String uri);
	
	// 是否为LIS URI
	public boolean isLisUri(String uri);
	
}
