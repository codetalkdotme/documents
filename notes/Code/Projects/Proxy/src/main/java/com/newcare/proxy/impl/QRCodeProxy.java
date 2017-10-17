package com.newcare.proxy.impl;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.newcare.constant.Constants;

@RestController
public class QRCodeProxy {
	
	@RequestMapping(value = "/hca/app/uploadqr", method = RequestMethod.POST)
    public String doUploadQR(@RequestParam("file") MultipartFile file, 
    						   HttpServletRequest request) throws Exception {
    	try {
    		String tmpdir = System.getProperty("java.io.tmpdir");
    		File dest = new File(tmpdir, file.getOriginalFilename());
    		file.transferTo(dest);
    		
			return Constants.RESPONSE_SUCCESS;
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		
    		return Constants.RESPONSE_ERROR;
    	}
    }
	
	@RequestMapping(value = "/hca/app/getqr", method = RequestMethod.GET)
	@ResponseBody
    public FileSystemResource doGetQR(HttpServletRequest request, HttpServletResponse response) throws Exception {
		File tmpdir = new File(System.getProperty("java.io.tmpdir"));
		
		ServletContext cntx= request.getServletContext();
		File[] files = tmpdir.listFiles();
		for(File file : files) {
			if(!file.getName().startsWith("hcaqr")) continue;
			
			response.setContentType(cntx.getMimeType(file.getAbsolutePath()));;
			
			return new FileSystemResource(file);
		}
		
		return null;
    }
	
}
