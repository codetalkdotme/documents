package com.newcare.storage.service;

import java.util.Map;

import com.newcare.storage.exception.FileStorageException;

/**
 * 
 * 文件存储服务接口
 * 
 * @author guobxu
 *
 */

public interface IStorageService {

	/**
	 * 
	 * 存储文件
	 * 
	 * @param data
	 * @param metas	文件元数据信息，比如文件名、文件类型 等
	 * @return null 表示失败，否则返回文件路径
	 * @throws FileStorageException
	 */
	public String store(byte[] data, Map<String, String> metas) throws FileStorageException;
	
	/**
	 * 
	 * 获取文件
	 * 
	 * @param uri eg: 
	 * @return null表示文件不存在
	 * @throws FileStorageException
	 */
	public byte[] fetch(String uri) throws FileStorageException;
	
	/**
	 * 
	 * 获取文件，以及元数据
	 * 
	 * @param uri
	 * @return String["data"] => byte[], String["meta"] => Map<String, String>, null表示文件不存在
	 * @throws FileStorageException
	 */
	public Map<String, Object> fetchWithMeta(String uri) throws FileStorageException;
	
}

















