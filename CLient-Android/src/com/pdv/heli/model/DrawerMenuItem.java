package com.pdv.heli.model;

import java.util.ArrayList;
import java.util.List;

import com.pdv.heli.R;

public class DrawerMenuItem {
	
	public static final int ACTION_LOG_OUT = 1;
	public static final int ITEM_ALL_CONTACT = 2;
	public static final int ITEM_APP_SETTING = 3;
	public static final int ITEM_ACCOUNT_SETTING = 4;	
	
	private String name;
	private int iconResId;
	private int count;
	private boolean isDiviler;
	private boolean isSelected;
	private int id;
	
	
	public DrawerMenuItem(int id ,String name, int iconResId, int count) {
		this.id = id;
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
		lst.add(new DrawerMenuItem(ITEM_ALL_CONTACT,"All Contact", R.drawable.ic_action_account_box, 12){
			@Override
			public boolean isDiviler() {
				return true;			
			}
		});
			
		lst.add(new DrawerMenuItem(ITEM_APP_SETTING, "App settings",R.drawable.ic_action_settings,0));
		lst.add(new DrawerMenuItem(ITEM_ACCOUNT_SETTING,"Account settings", R.drawable.ic_image_wb_cloudy,0){
			@Override
			public boolean isDiviler() {
				return true;			
			}
		}
		);
		lst.add(new DrawerMenuItem(ACTION_LOG_OUT,"Logout", R.drawable.ic_action_input,0));		
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
	
	
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
