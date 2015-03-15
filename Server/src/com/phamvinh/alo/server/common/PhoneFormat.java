package com.phamvinh.alo.server.common;

public class PhoneFormat {
	public static String formatPhone(String input){
		input = input.replace("(", "");
		input = input.replace(")", "");
		input = input.replace("-", "");
		input = input.replace(" ", "");
		if(input.startsWith("0")){
			input = input.replaceFirst("0", "+84");
		}
		return input;
	}
}
