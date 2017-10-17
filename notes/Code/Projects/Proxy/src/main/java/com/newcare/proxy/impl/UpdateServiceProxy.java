package com.newcare.proxy.impl;

import java.net.URLDecoder;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.newcare.constant.Constants;
import com.newcare.param.checker.IParamChecker;
import com.newcare.param.checker.ParamCheckResult;
import com.newcare.proxy.AbstractServiceProxy;
import com.newcare.storage.exception.FileStorageException;
import com.newcare.storage.service.IStorageService;
import com.newcare.storage.service.StorageConstants;
import com.newcare.update.exception.UpdateServiceException;
import com.newcare.update.service.IUpdateService;

/**
 * 更新服务
 * @author guobxu
 *
 */
@RestController
public class UpdateServiceProxy extends AbstractServiceProxy {

	private static Logger LOGGER = LoggerFactory.getLogger(UpdateServiceProxy.class);
	
	@Autowired
	private IStorageService storageService;
	
	@Autowired
    private IUpdateService updateService;
	
	@Autowired
	private IParamChecker paramChecker;
	
	@RequestMapping(value = "/hca/api/update/checkver", method = RequestMethod.POST)
	public String doCheckVers(@RequestBody Map<String, Object> data) {
		LOGGER.info("doCheckVers...");
		
		ParamCheckResult rt = paramChecker.checkPost("/hca/api/update/checkver", data);
    	if(!rt.isValid()) {
    		return errorWithMsg(rt.getErrMsg());
    	}
		
        try {
            return updateService.checkVersion(data);
        } catch(UpdateServiceException ex) {
    		LOGGER.error(ex.getMessage(), ex);
        	
    		return errorWithKey("update_exception_msg");
    	}
    }
	
	@RequestMapping(value = "/hca/api/update/getfile/**", method = RequestMethod.GET)
    public ResponseEntity doGetFile(HttpServletRequest request) {
    	try {
    		String uri = request.getRequestURI();
    		String fileUri = uri.substring(uri.lastIndexOf("/") + 1);
    		
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
				headers.add(HttpHeaders.CONTENT_DISPOSITION, 
						"attachment;filename=\"" + metas.get(StorageConstants.META_KEY_FILENAME) + "\"");
			}
			
			return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    	} catch(FileStorageException ex) {
    		ex.printStackTrace();
    		
    		return ResponseEntity
    	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
    	            .body(errorWithKey("fdfs_fetch_error"));
    	} catch(Exception ex) {
    		LOGGER.error(ex.getMessage(), ex);
        	
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWithKey("update_exception_msg"));
    	}
    }
	
}











