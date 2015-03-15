package com.pdv.heli.model;

import java.util.ArrayList;
import java.util.List;

import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendsFragment;
import com.pdv.heli.activity.setting.AccountSettingFragment;

public class DrawerMenuItem {
	
	private String key;
	private String name;
	private int iconResId;
	private int count;
	private boolean isDiviler;
	private boolean isSelected;
	
	public DrawerMenuItem(String pKye ,String name, int iconResId, int count) {
		key = pKye;
		this.name = name;
		this.iconResId = iconResId;
		this.count = count;
	}
	public DrawerMenuItem(String name, int iconResId, int count) {
		
		this.name = name;
		this.iconResId = iconResId;
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIconResId() {
		return iconResId;
	}
	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public static List<DrawerMenuItem> getDefaultItems() {
		List<DrawerMenuItem> lst = new ArrayList<DrawerMenuItem>();
		lst.add(new DrawerMenuItem(FriendsFragment.class.getName(),"All Contact", 0, 12));
		lst.add(new DrawerMenuItem("Start friend", 0, 12));
		DrawerMenuItem itemBl = new DrawerMenuItem("Block list", 0, 12);
		itemBl.setDiviler(true);
		lst.add(itemBl);		
		lst.add(new DrawerMenuItem("Settings", R.drawable.ic_settings,0));
		lst.add(new DrawerMenuItem(AccountSettingFragment.class.getName(),"Account settings", R.drawable.ic_settings,0));
		lst.add(new DrawerMenuItem("logout","Logout", R.drawable.ic_settings,0));		
		return lst;
	}
	public DrawerMenuItem(boolean isDiviler) {		
		this.isDiviler = isDiviler;
	}
	public boolean isDiviler() {
		return isDiviler;
	}
	public void setDiviler(boolean isDiviler) {
		this.isDiviler = isDiviler;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	
}
