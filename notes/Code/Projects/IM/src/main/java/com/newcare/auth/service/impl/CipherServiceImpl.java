package com.newcare.auth.service.impl;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.newcare.auth.service.ICipherService;
import com.newcare.constant.Constants;

/**
 * 
 * @author guobxu
 *
 */
@Service("cipherService")
public class CipherServiceImpl implements ICipherService {
	
	// Random byte generator - thread safe
	private SecureRandom rand = new SecureRandom();
	
	private ThreadLocal<Cipher> threadCipher = new ThreadLocal<Cipher>();

	protected static final char[] HEX_ARRAY= {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	public String cipher(String text, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), Constants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(Constants.IV_SPEC_KEY.getBytes());
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(text.getBytes(Constants.ENCODING_UTF8));
        String cipherBase64 = Base64.getEncoder().encodeToString(cipherBytes);
		
		return cipherBase64;
	}
	
	public String decipher(String ciphered, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), Constants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(Constants.IV_SPEC_KEY.getBytes());
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(ciphered));
        String text = new String(bytes, Constants.ENCODING_UTF8);
        
		return text;
	}

	private Cipher getCipher() throws Exception {
		Cipher cipher = threadCipher.get();
		if(cipher == null) {
			cipher = Cipher.getInstance(Constants.CIPHER_AES_CFB_NOPADDING);
			threadCipher.set(cipher);
		}
		
		return cipher;
	}
	
	public String randomKey32() {
		byte[] bytes = new byte[16];
		rand.nextBytes(bytes);
		
		return bytesToHex(bytes);
	}
	
	private String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		
		return new String(hexChars);
	}

}
