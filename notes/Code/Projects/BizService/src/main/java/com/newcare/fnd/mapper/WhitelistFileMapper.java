package com.newcare.fnd.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.newcare.fnd.pojo.WhitelistFile;

public interface WhitelistFileMapper {

	public void insertFile(WhitelistFile file);
	
	public List<WhitelistFile> selectFileByEncodeUrl(@Param("encodeUrl") String encodeUrl);
	
}
