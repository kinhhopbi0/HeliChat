package com.pdv.heli.message.common;

public class Constant {
	public static final int USERNAME_MAX_SIZE = 50;
	public static final int PASSWORD_MAX_SIZE = 32;
	public static final int PHONE_MAX_SIZE = 20;
	
	public static final byte FRAME_START = (byte) 0x0FA;
	public static final byte FRAME_END = (byte) 0x0FD;
	public static final byte FRAME_PADDING = (byte) 0x0FC;
}
