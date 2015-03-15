package com.phamvinh.alo.server.constant;

public class SessionKey {
	public static final String SIGNED_USER_ID = SessionKey.class.getName()+"SIGNED_USER_ID";
	public static final String SIGN_UP_PHONE = SessionKey.class.getName()+"SIGN_UP_PHONE";
	public static final String TOKEN = SessionKey.class.getName()+"TOKEN";
	public static final String ENCRYPTED_PASSWORD = SessionKey.class.getName()+"ECR_PASSWORD";
	public static final String PASSWORD_SALT = SessionKey.class.getName()+"PASSWORD_SALT";
	public static final String SIGN_UP_VERIFY_KEY = SessionKey.class.getName()+"SIGN_UP_VERIFY_KEY";
	public static final String FRAME_TEMPT_DATA = SessionKey.class.getName()+"MESSAGE_TEMPT_DATA";
	public static final String FRAME_IS_PADDING_TEMPT = SessionKey.class.getName()+"FRAME_IS_PADDING_TEMPT";
}
