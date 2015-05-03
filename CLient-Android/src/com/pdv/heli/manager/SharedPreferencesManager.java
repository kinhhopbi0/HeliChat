package com.pdv.heli.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pdv.heli.app.HeliApplication;

public class SharedPreferencesManager {

	public static final String KET_LOCAL_PHONE = "phone";
	public static final String KEY_TOKEN = "SESSION_TOKEN_KEY";
	public static final String KEY_COOKIE_FILE_PREF = "appCookie";

	public static final String FILE_SYNC_INFO = "sync";
	public static final String KEY_LAST_SYNC_TIME_CONTACT = "CONTACT";
	public static final String KEY_LAST_SYNC_TIME_CHAT = "CHAT";
	public static final String KEY_LAST_SYNC_TIME_CONVERSATION = "CONVERSATION";

	public static final String FILE_SETTING = "setting";
	public static final String KEY_SORT_TYPE = "SORT_TYPE";
	
	public static String getLocalPhone() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(KEY_COOKIE_FILE_PREF,
						Context.MODE_PRIVATE);
		String pn = preferences.getString(KET_LOCAL_PHONE, null);
		Log.v("delete", "get phone " + pn);
		return pn;
	}

	public static String getTokenKey() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(KEY_COOKIE_FILE_PREF,
						Context.MODE_PRIVATE);
		String token = preferences.getString(KEY_TOKEN, null);
		return token;
	}

	public static void saveLocalPhone(String phone) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(KEY_COOKIE_FILE_PREF,
						Context.MODE_PRIVATE);
		preferences.edit().putString(KET_LOCAL_PHONE, phone).commit();
		Log.v("delete", "save phone " + phone);
	}

	public static void saveTokenKey(String pToken) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(KEY_COOKIE_FILE_PREF,
						Context.MODE_PRIVATE);
		preferences.edit().putString(KEY_TOKEN, pToken).apply();
	}

	public static void deleteLocalPhone() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(KEY_COOKIE_FILE_PREF,
						Context.MODE_PRIVATE);
		preferences.edit().remove(KET_LOCAL_PHONE).commit();
		Log.v("delete", "remove phone");
	}

	public static void deleteTokenKey() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(KEY_COOKIE_FILE_PREF,
						Context.MODE_PRIVATE);
		preferences.edit().remove(KEY_TOKEN).commit();

	}

	// ////////////////////////////////////////////////////////////////
	// Get set for chat sync info
	// ///////////////////////////////////////////////////////////////
	
	public static long getTimeSyncChat() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SYNC_INFO, Context.MODE_PRIVATE);
		return preferences.getLong(KEY_LAST_SYNC_TIME_CHAT, 0);
	}

	public static void saveTimeSyncChat(long value) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SYNC_INFO, Context.MODE_PRIVATE);
		preferences.edit().putLong(KEY_LAST_SYNC_TIME_CHAT, value).commit();
	}

	// ////////////////////////////////////////////////////////////////
	// Get set for contact sync info
	// ///////////////////////////////////////////////////////////////

	public static long getTimeSyncContact() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SYNC_INFO, Context.MODE_PRIVATE);
		return preferences.getLong(KEY_LAST_SYNC_TIME_CONTACT, 0);
	}

	public static void saveTimeSyncContact(long value) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SYNC_INFO, Context.MODE_PRIVATE);
		preferences.edit().putLong(KEY_LAST_SYNC_TIME_CONTACT, value).commit();
	}

	// ////////////////////////////////////////////////////////////////
	// Get set for conversation sync info
	// ///////////////////////////////////////////////////////////////
	public static long getTimeSyncConversation() {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SYNC_INFO, Context.MODE_PRIVATE);
		return preferences.getLong(KEY_LAST_SYNC_TIME_CONVERSATION, 0);
	}

	public static void saveTimeSyncConversation(long value) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SYNC_INFO, Context.MODE_PRIVATE);
		preferences.edit().putLong(KEY_LAST_SYNC_TIME_CONVERSATION, value)
				.commit();
	}

	public static void saveContactShortType(int sortByContactName) {
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE);
		preferences.edit().putInt(KEY_SORT_TYPE, sortByContactName)
				.commit();		
	}
	public static int getContactShortType(){
		SharedPreferences preferences = HeliApplication.getInstance()
				.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE);
		return preferences.getInt(KEY_SORT_TYPE, 0);
	}
}
