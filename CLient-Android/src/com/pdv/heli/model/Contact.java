package com.pdv.heli.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.net.Uri;

public class Contact {
	private String phoneNumber;
	private int user_id;
	private String displayName;
	private String contactName;
	private boolean isStar;
	private Bitmap avatar;

	public static List<Contact> findAll() {
		
		return Collections.emptyList();
	}

	public static List<Contact> findByContactName(String contactName) {

		return Collections.emptyList();
	}
	public static List<Contact> getAllInPhoneContact(String contactName) {

		return Collections.emptyList();
	}
	
	

	public Contact(String phoneNumber, String displayName) {
		super();
		this.phoneNumber = phoneNumber;
		this.displayName = displayName;
		
	}

	public Contact() {
		// TODO Auto-generated constructor stub
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

	public Bitmap getAvatar() {
		return avatar;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public Uri getAvatarUri() {
		// TODO Auto-generated method stub
		return null;
	}
	public static List<Contact> getDemos(){
		List<Contact> lst = new ArrayList<Contact>();
		
		return lst;
	}

	public static Contact findById(int sernderId) {
		Contact ct = new Contact("01678155662", "anh vinh");
		ct.setUser_id(sernderId);
		return ct;
		
	}

}
