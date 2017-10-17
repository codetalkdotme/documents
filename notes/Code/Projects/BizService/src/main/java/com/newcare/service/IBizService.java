package com.newcare.service;

import java.util.Map;

import com.newcare.exception.BizServiceException;

/**
 * Created by guobxu on 2017/4/1.
 *
 * 业务服务接口
 *
 */
public interface IBizService {

    public String doPost(String uri, Map<String, Object> data) throws BizServiceException;

    public String doGet(String uri, Map<String, String[]> params) throws BizServiceException;
    
    public String doUploadFile(String uri, String uid, String authStr) throws BizServiceException;
    
    public String doGetFile(String uri, String uid, String authStr) throws BizServiceException;
    
    public String doServiceAuth(String uri, Map<String, Object> data) throws BizServiceException;

    public boolean addFileToWhitelist(String fdfsUrl, String name, String type, Long length);
    
    public boolean isWhitelistFile(String encodeUrl);
    
}
