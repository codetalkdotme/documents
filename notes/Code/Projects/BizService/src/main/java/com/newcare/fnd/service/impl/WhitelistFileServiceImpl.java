package com.newcare.fnd.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.fnd.mapper.WhitelistFileMapper;
import com.newcare.fnd.pojo.WhitelistFile;
import com.newcare.fnd.service.IWhitelistFileService;
import com.newcare.util.StringUtils;

/**
 * 白名单文件服务
 * @author guobxu
 *
 */
@Service("wlFileService")
public class WhitelistFileServiceImpl implements IWhitelistFileService {

	private static Logger LOGGER = LoggerFactory.getLogger(WhitelistFileServiceImpl.class);
	
	@Autowired
	private WhitelistFileMapper wlFileMapper;
	
	@Override
	public boolean addFile(String fdfsUrl) {
		return addFile(fdfsUrl, null, null, null);
	}

	@Override
	public boolean addFile(String fdfsUrl, String name, String type, Long length) {
		LOGGER.info("In addFile...fdfsUrl=" + fdfsUrl + ", name=" + name + ", type=" + type + ", length=" + length);
		
		String encodeUrl = StringUtils.encodeFileUri(fdfsUrl);
		LOGGER.info("encodeUrl: " + encodeUrl);
		
		if(encodeUrl == null) return false;
		
		WhitelistFile file = new WhitelistFile();
		file.setFdfsUrl(fdfsUrl);
		file.setEncodeUrl(encodeUrl);
		file.setName(name);
		file.setType(type);
		file.setLength(length);
		
		wlFileMapper.insertFile(file);
		
		return true;
	}

	@Override
	public boolean isWhitelistFile(String encodeUrl) {
		List<WhitelistFile> fileList = wlFileMapper.selectFileByEncodeUrl(encodeUrl);
		
		return fileList.size() > 0;
	}

}










