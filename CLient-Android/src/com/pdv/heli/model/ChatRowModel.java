package com.pdv.heli.model;

import java.util.ArrayList;
import java.util.List;


public class ChatRowModel {
	private Contact sender;
	private String contentText;
	private String strTime;
	
	
	public ChatRowModel(Contact sender, String contentText) {
		super();
		this.sender = sender;
		this.contentText = contentText;
	}
	
	public ChatRowModel(int sernderId, String contentText) {
		super();
		this.contentText = contentText;
		this.sender = Contact.findById(sernderId);
	}

	public Contact getSender() {
		return sender;
	}
	public void setSender(Contact sender) {
		this.sender = sender;
	}
	public String getContentText() {
		return contentText;
	}
	public void setContentText(String contentText) {
		this.contentText = contentText;
	}
	public String getStrTime() {
		return strTime;
	}
	public void setStrTime(String strTime) {
		this.strTime = strTime;
	}
	
	public static List<ChatRowModel> getDemos() {
		List<ChatRowModel> list = new ArrayList<ChatRowModel>();
//		list.add(new ChatRowModel(16,"1 hello you"));
//		list.add(new ChatRowModel(1,"2 hello you"));
//		list.add(new ChatRowModel(2," 3hello you"));
//		list.add(new ChatRowModel(16," 4hello you"));
//		list.add(new ChatRowModel(1," 5hello you"));
//		list.add(new ChatRowModel(16,"hello you"));
//		list.add(new ChatRowModel(16,"hello you"));
//		list.add(new ChatRowModel(1,"hello you"));
//		list.add(new ChatRowModel(2,"hello you"));
//		list.add(new ChatRowModel(16,"hello you"));
//		list.add(new ChatRowModel(1,"hello you"));
//		list.add(new ChatRowModel(16,"23 hello you"));
		return list;
	}
	
	
}
