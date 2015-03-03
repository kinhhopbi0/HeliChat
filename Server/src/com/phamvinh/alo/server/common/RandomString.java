package com.phamvinh.alo.server.common;

import java.util.Random;
import java.util.UUID;

public class RandomString {
	private static final char[] symbols;
	private static final char[] numbers;

	  static {
	    StringBuilder tmp = new StringBuilder();
	    for (char ch = '0'; ch <= '9'; ++ch){
	    	tmp.append(ch);
	    }
	    numbers = tmp.toString().toCharArray();
	    for (char ch = 'a'; ch <= 'z'; ++ch)
	      tmp.append(ch);
	   
	    symbols = tmp.toString().toCharArray();
	  }   

	  private final Random random = new Random();

	  private final char[] buf;

	  public RandomString(int length) {
	    if (length < 1)
	      throw new IllegalArgumentException("length < 1: " + length);
	    buf = new char[length];
	  }

	  public String nextString() {
	    for (int idx = 0; idx < buf.length; ++idx) 
	      buf[idx] = symbols[random.nextInt(symbols.length)];
	    return new String(buf);
	  }
	  public String nextPasscode(){
		  for (int idx = 0; idx < buf.length; ++idx) 
		      buf[idx] = numbers[random.nextInt(numbers.length)];
		    return new String(buf);
	  }
	  public static String getUUIDToken(){
		  return UUID.randomUUID().toString();
	  }
}
