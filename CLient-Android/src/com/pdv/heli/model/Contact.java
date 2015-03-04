package com.pdv.heli.model;

import java.util.Collections;
import java.util.List;

import android.support.v4.graphics.BitmapCompat;

public class Contact {
	private String phoneNumber;
	private int user_id;
	private String displayName;
	private String contactName;
	private boolean isStar;
	private BitmapCompat avatar;

	public static List<Contact> findAll() {
		
		return Collections.emptyList();
	}

	public static List<Contact> findByContactName(String contactName) {

		return Collections.emptyList();
	}
	public static List<Contact> getAllInPhoneContact(String contactName) {

		return Collections.emptyList();
	}
	

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public boolean isStar() {
		return isStar;
	}

	public void setStar(boolean isStar) {
		this.isStar = isStar;
	}

	public BitmapCompat getAvatar() {
		return avatar;
	}

	public void setAvatar(BitmapCompat avatar) {
		this.avatar = avatar;
	}

}
