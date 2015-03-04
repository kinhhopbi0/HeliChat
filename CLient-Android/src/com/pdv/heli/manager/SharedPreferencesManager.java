package com.pdv.heli.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.pdv.heli.app.HeliApplication;

public class SharedPreferencesManager {

	public static final String USER_ID_KEY = "USER_ID";
	public static final String SESSION_TOKEN_KEY = "SESSION_TOKEN_KEY";
	public static final String COOKIE_FILE_PREF = "appCookie";
	
	
	public static int getLoginUserId() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(COOKIE_FILE_PREF, Context.MODE_PRIVATE);
		int user_id = preferences.getInt(USER_ID_KEY, 0);
		return user_id;
	}

	public static String getSessionTokenKey() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(COOKIE_FILE_PREF, Context.MODE_PRIVATE);
		String token = preferences.getString(SESSION_TOKEN_KEY, "");
		return token;
	}

	public static void saveLogInUserId(int id) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(COOKIE_FILE_PREF, Context.MODE_PRIVATE);
		preferences.edit().putInt(USER_ID_KEY, id).apply();		
	}

	public static void saveSessionTokenKey(String pToken) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(COOKIE_FILE_PREF, Context.MODE_PRIVATE);
		preferences.edit().putString(SESSION_TOKEN_KEY, pToken).apply();
	}
}
