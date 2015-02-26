package com.phamvinh.alo.server.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
	
	public synchronized static  String encryptPassword(String password, String salt){
		try {
			MessageDigest digestSHA = MessageDigest.getInstance("SHA-256");
			MessageDigest digestMD5 = MessageDigest.getInstance("MD5");
			byte[] md5Pass = digestMD5.digest(password.getBytes());
			byte[] saltData = salt.getBytes();
			byte[] password1 = new byte[saltData.length + md5Pass.length];
			System.arraycopy(saltData, 0, password1, 0, saltData.length);			
			System.arraycopy(md5Pass, 0, password1, saltData.length, md5Pass.length);
			byte[] hash = digestSHA.digest(password1);
			return BytesUtil.bytesToHexString(hash);			
		} catch (NoSuchAlgorithmException e) {
			
		}
		
		return "";
	}
	
	
	
}
