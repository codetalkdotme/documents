package com.newcare.proxy.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.AbstractMap.SimpleEntry;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newcare.constant.Constants;
import com.newcare.exception.BizServiceException;
import com.newcare.param.checker.IParamChecker;
import com.newcare.param.checker.ParamCheckResult;
import com.newcare.proxy.AbstractServiceProxy;
import com.newcare.service.IBizService;
import com.newcare.storage.exception.FileStorageException;
import com.newcare.storage.service.IStorageService;
import com.newcare.storage.service.StorageConstants;

/**
 * Created by guobxu on 2017/4/1.
 */
@RestController
public class BizServiceProxy extends AbstractServiceProxy {

	@Autowired
	private IStorageService storageService;
	
	@Autowired
    private IBizService bizService;
	
	@Autowired
	private IParamChecker paramChecker;
    
    private static ObjectMapper MAPPER = new ObjectMapper();
    
    @RequestMapping(value = "/hca/api/business/**", method = RequestMethod.GET)
    public String doGet(HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();
        Map<String, String[]> params = request.getParameterMap();

        try {
            return bizService.doGet(uri, params);
        } catch(BizServiceException ex) {
    		ex.printStackTrace();
    		
    		return errorWithKey("biz_exception_msg");
    	}
    }

    @RequestMapping(value = {"/hca/api/business/**", "/hca/web/hecadre/**", "/hca/web/inhabitant/**", "/hca/web/doctor/**"}, method = RequestMethod.POST)
    public String doPost(@RequestBody Map<String, Object> data, HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();
        
        ParamCheckResult rt = paramChecker.checkPost(uri, data);
    	if(!rt.isValid()) {
    		return errorWithMsg(rt.getErrMsg());
    	}
        
        try {
            return bizService.doPost(uri, data);
        } catch(BizServiceException ex) {
    		ex.printStackTrace();
    		
    		return errorWithKey("biz_exception_msg");
    	}
    }
    
    @RequestMapping(value = "/hca/api/business/login", method = RequestMethod.POST)
    public String doServiceAuth(@RequestBody Map<String, Object> data, HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();
        
        ParamCheckResult rt = paramChecker.checkPost(uri, data);
    	if(!rt.isValid()) {
    		return errorWithMsg(rt.getErrMsg());
    	}
        
        try {
            return bizService.doServiceAuth(uri, data);
        } catch(BizServiceException ex) {
    		ex.printStackTrace();
    		
    		return errorWithKey("biz_exception_msg");
    	}
    }
    
    @RequestMapping(value = "/hca/api/business/putfile/**", method = RequestMethod.PUT)
    public String doUploadFile(@RequestParam("file") MultipartFile file, 
    						   HttpServletRequest request) throws Exception {
    	String uri = request.getRequestURI();
    	
    	try {
    		// {uid}/{authStr}
    		String[] vars = extractReqVariables(uri, "/hca/api/business/putfile/");
    		String uid = vars[0], authStr = vars[1];
    		
    		// 白名单文件 - 0704
    		boolean isWhitelist = false;
    		if(vars.length > 2 && Constants.FILE_WL_YES.equals(vars[2])) {
    			isWhitelist = true;
    		}
    		
    		String authStrDecoded = URLDecoder.decode(authStr, Constants.ENCODING_UTF8);	// 先作URL解码
    		String rt = bizService.doUploadFile(uri, uid, authStrDecoded); // 1 or {rt_code: 2, rt_msg: 'xxx'}
    		if(Constants.CODE_SUCCESS_S.equals(rt)) {
    			byte[] data = file.getBytes();
    			
    			// meta info
    			Map<String, String> metas = new HashMap<String, String>();
    	    	
    			String name = file.getOriginalFilename(), type = file.getContentType();
    	    	metas.put(StorageConstants.META_KEY_FILENAME, name);
    	    	metas.put(StorageConstants.META_KEY_FILELENGTH, String.valueOf(data.length));
    	    	metas.put(StorageConstants.META_KEY_CONTENT_TYPE, type);
    			
    			String serverFile = storageService.store(data, metas);
    			if(serverFile == null) {
    				return errorWithKey("fdfs_upload_error");
    			}
    			
    			// 是否加入白名单
    			if(isWhitelist && !bizService.addFileToWhitelist(serverFile, name, type, 
						Long.valueOf(String.valueOf(data.length)))) {
    				return errorWithKey("fdfs_upload_error");
    			}
    			
    			SimpleEntry<String, String> retData = new SimpleEntry<String, String>("file_url", serverFile);
				return getSuccessResponse(retData);
    		}
    		
    		return rt;
    	} catch(FileStorageException ex) {
    		ex.printStackTrace();
    		
    		return errorWithKey("fdfs_upload_error");
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		
    		return errorWithKey("biz_exception_msg");
    	}
    }
    
    @RequestMapping(value = "/hca/api/business/getfile/**", method = RequestMethod.GET)
    public ResponseEntity doGetFile(HttpServletRequest request) throws Exception {
    	String uri = request.getRequestURI();
    	try {
    		// {fileUri}/{uid}/{authStr}
    		String[] vars = extractReqVariables(uri, "/hca/api/business/getfile/");
    		String fileUri = vars[0], uid = null, authStr = null;
    		
    		// 白名单文件 - 0704
    		if(vars.length == 1) {
    			if(!bizService.isWhitelistFile(fileUri)) 
    				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWithKey("biz_file_notwhitelist"));
    			
    			return getFile(fileUri);
    		} else if(vars.length == 3){
    			uid = vars[1];
    			authStr = vars[2];
    			
    			String authStrDecoded = URLDecoder.decode(authStr, Constants.ENCODING_UTF8);	// 先作URL解码
        		String rt = bizService.doGetFile(uri, uid, authStrDecoded);
        		if(Constants.CODE_SUCCESS_S.equals(rt)) {
        			return getFile(fileUri);
        		}
        		
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rt);
    		}
    		
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWithKey("biz_bad_request"));
    	} catch(FileStorageException ex) {
    		ex.printStackTrace();
    		
    		return ResponseEntity
    	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
    	            .body(errorWithKey("fdfs_fetch_error"));
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		
    		return ResponseEntity
    	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
    	            .body(errorWithKey("biz_exception_msg"));
    	}
    }
    
    private ResponseEntity getFile(String fileUri) throws FileStorageException, UnsupportedEncodingException {
    	// 文件存储及传输: <file_url_str>，URLCode(Base64("文件URL"))
		String realFileUri = new String(Base64.getDecoder().decode(
				URLDecoder.decode(fileUri, Constants.ENCODING_UTF8)));
		
		Map<String, Object> fileWithMeta = storageService.fetchWithMeta(realFileUri);
		if(fileWithMeta == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWithKey("fdfs_file_missing"));
		}
		
		byte[] data = (byte[])fileWithMeta.get(StorageConstants.MAP_FILE_DATA);
		Map<String, String> metas = (Map<String, String>)fileWithMeta.get(StorageConstants.MAP_FILE_META);
		HttpHeaders headers = null;
		if(metas != null) {
			headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, metas.get(StorageConstants.META_KEY_CONTENT_TYPE));
		}
		
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }
    
    private String[] extractReqVariables(String uri, String prefix) {
    	String suffix = uri.substring(prefix.length());
    	String[] arr = suffix.split("/");
    	
    	return arr;
    }
    
    private String getSuccessResponse(Object retData) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
        map.put(Constants.KEY_DATA, retData);

        return MAPPER.writeValueAsString(map);
    }

}



















