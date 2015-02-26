package com.phamvinh.alo.server.common;

import java.io.UnsupportedEncodingException;
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
			return bytesToHexString(hash);			
		} catch (NoSuchAlgorithmException e) {
			
		}
		
		return "";
	}
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHexString(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	public static void main(String[] args) {
		byte[] data;
		try {
			data = "xin chao".getBytes("UTF-8");
			
			System.out.println(bytesToHexString(data));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
