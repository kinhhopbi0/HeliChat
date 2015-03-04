package com.pdv.heli.common;

public class BytesUtil {
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
	public static String toDisplayString(byte[] bytes){
		StringBuilder builder = new StringBuilder(bytes.length);
		builder.append('[');
		for (int i = 0; i < bytes.length; i++) {
			builder.append(bytes[i]+", ");
		}		
		builder.append(']');
		return builder.toString();
	}
}
