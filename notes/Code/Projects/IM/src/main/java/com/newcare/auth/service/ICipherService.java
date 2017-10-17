package com.newcare.auth.service;

/**
 * 加密解密服务
 * 
 * @author guobxu
 *
 */
public interface ICipherService {

	/**
	 * 
	 * 解密密文
	 * 
	 * @param ciphered 密文 - 使用AES加密, 然后使用Base64编码
	 * @param key	
	 * @return
	 */
	public String decipher(String ciphered, String key) throws Exception;
	
	/**
	 * 加密 - 使用AES加密, 然后使用Base64编码
	 * @param text 明文字符串
	 * @param key
	 * @return
	 */
	public String cipher(String text, String key) throws Exception;
	
	/**
	 * 生成长度为32的随机字符串
	 * 
	 * @return
	 */
	public String randomKey32();
	
}














