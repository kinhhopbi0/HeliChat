package com.pdv.heli.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.sqlite.ChatDbHelper;
import com.pdv.heli.sqlite.ContactDBHelper;
import com.pdv.heli.sqlite.ConversationContract;
import com.pdv.heli.sqlite.ContactReaderContract.ContactEntry;
import com.pdv.heli.sqlite.SqliteDataHelper;

public class Contact {
	private String phoneNumber;
	private String displayName;
	private String contactName;
	private boolean isStar;
	private Bitmap avatar;
	private boolean isBlock;
	private boolean isMute;
	private String conversationId;

	public static Contact fromCursor(Cursor cursor) {
		int colPn = cursor.getColumnIndex(ContactEntry.COLUMN_NAME_PHONE);
		int colDisplay = cursor
				.getColumnIndex(ContactEntry.COLUMN_NAME_DISPLAY);
		int colContactName = cursor
				.getColumnIndex(ContactEntry.COLUMN_NAME_CONTACT_NAME);
		int colSuper = cursor.getColumnIndex(ContactEntry.COLUMN_NAME_IS_SUPER);
		int colConversationId = cursor.getColumnIndex("conversationId");

		String pn = cursor.getString(colPn);
		String displayName = cursor.getString(colDisplay);
		String contactName = cursor.getString(colContactName);
		String conversaionId = cursor.getString(colConversationId);
		int isSuper = cursor.getInt(colSuper);
		Contact ct = new Contact();
		ct.setPhoneNumber(pn);
		ct.setDisplayName(displayName);
		ct.setContactName(contactName);
		ct.setStar((isSuper == 1) ? true : false);

		if (conversaionId != null) {
			ct.setConversationId(conversaionId);
		}
		return ct;
	}

	public static List<Contact> findAll() {

		Cursor cursor = findAllToCursor();

		List<Contact> contacts = new ArrayList<Contact>();
		if (cursor.getCount() == 0) {
			return contacts;
		}
		do {
			Contact ct = fromCursor(cursor);
			contacts.add(ct);
		} while (cursor.moveToNext());

		return contacts;
	}

	public static Cursor findAllToCursor() {
		SqliteDataHelper dbHelper = new SqliteDataHelper(
				HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql;
		int sortType = SharedPreferencesManager.getContactShortType();
		switch (sortType) {
		case ChatDbHelper.SORT_BY_CHAT_HIS:
			sql = ChatDbHelper.SQL_SELECT_NON_SORT
					+ ChatDbHelper.SQL_SORT_BY_LAST_CHAT;
			break;

		case ChatDbHelper.SORT_BY_CONTACT_NAME:
			sql = ChatDbHelper.SQL_SELECT_NON_SORT
					+ ChatDbHelper.SQL_SORT_BY_NAME;
			break;
		default:
			sql = ChatDbHelper.SQL_SELECT_NON_SORT;
			break;
		}
		Cursor cursor = db.rawQuery(sql, new String[] {});
		// Cursor cursor =
		// db.rawQuery("select ct.*, cv.id , max(c.sentTime) as `newestChat` , count(c.id) totalChat from contact as ct left join conversation as cv on cv.member = '[\"' || ct.pn || '\"]' left join chat as c on c.conversationId = cv.id group by ct.pn order by newestChat desc, ct.contact_name desc",
		// new String[]{});
		cursor.moveToFirst();

		return cursor;
	}

	public static List<Contact> findByContactName(String contactName) {

		return Collections.emptyList();
	}

	public static List<Contact> getAllInPhoneContact(String contactName) {

		return Collections.emptyList();
	}

	public Contact(String phoneNumber, String contactName) {
		super();
		this.phoneNumber = phoneNumber;
		this.contactName = contactName;

	}

	public Contact() {
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public static List<Contact> getDemos() {
		List<Contact> lst = new ArrayList<Contact>();

		return lst;
	}

	public void save(Context context) {
		if (this.phoneNumber == SharedPreferencesManager.getLocalPhone()) {

		} else {
			ContactDBHelper dbHelper = new ContactDBHelper();
			dbHelper.insertOrUpdate(this);
		}

	}

	public boolean isBlock() {
		return isBlock;
	}

	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}

	public static Contact findOneByPhone(String pPhone) {
		SQLiteDatabase db = null;
		try {
			SqliteDataHelper dbHelper = new SqliteDataHelper(
					HeliApplication.getInstance());
			db = dbHelper.getReadableDatabase();
			Cursor cs = db.rawQuery("select * from " + ContactEntry.TABLE_NAME
					+ " where " + ContactEntry.COLUMN_NAME_PHONE + "=? ",
					new String[] { pPhone });
			cs.moveToFirst();
			if (cs.getCount() == 0) {
				return null;
			}
			String contactName = cs.getString(cs
					.getColumnIndex(ContactEntry.COLUMN_NAME_CONTACT_NAME));
			String displayName = cs.getString(cs
					.getColumnIndex(ContactEntry.COLUMN_NAME_DISPLAY));
			boolean isStar = cs.getInt(cs
					.getColumnIndex(ContactEntry.COLUMN_NAME_IS_SUPER)) == 1;
			boolean isBlock = cs.getInt(cs
					.getColumnIndex(ContactEntry.COLUMN_NAME_IS_BLOCK)) == 1;
			Contact contact = new Contact();
			contact.setPhoneNumber(pPhone);
			contact.setDisplayName(displayName);
			contact.setContactName(contactName);
			contact.setStar(isStar);
			contact.setBlock(isBlock);
			return contact;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Contact) {
			Contact contact = (Contact) o;
			if (contact.getPhoneNumber().equals(this.phoneNumber)) {
				return true;
			}
		}
		return super.equals(o);
	}

	

	public static void deleteByPhone(String phoneNumber2) {
		SqliteDataHelper dbHelper = new SqliteDataHelper(
				HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete(ContactEntry.TABLE_NAME, "pn=?",
				new String[] { phoneNumber2 });
		db.close();
	}

	public static void deleteAll(){
		SqliteDataHelper dataHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dataHelper.getReadableDatabase();
		try{
			db.delete(ContactEntry.TABLE_NAME, null, null);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			db.close();			
		}
	}

	public boolean isMute() {
		return isMute;
	}

	public void setMute(boolean isMute) {
		this.isMute = isMute;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public static List<Contact> search(String keyWord) {
		SqliteDataHelper dbHelper = new SqliteDataHelper(
				HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Contact> contacts = new ArrayList<Contact>();
		try {
			String sql;
			int sortType = SharedPreferencesManager.getContactShortType();
			switch (sortType) {
			case ChatDbHelper.SORT_BY_CHAT_HIS:
				sql = ChatDbHelper.SQL_SEARCH
						+ ChatDbHelper.SQL_SORT_BY_LAST_CHAT;
				break;

			case ChatDbHelper.SORT_BY_CONTACT_NAME:
				sql = ChatDbHelper.SQL_SEARCH + ChatDbHelper.SQL_SORT_BY_NAME;
				break;
			default:
				sql = ChatDbHelper.SQL_SEARCH;
				break;
			}
			Cursor cursor = db.rawQuery(sql, new String[] { keyWord, keyWord,
					keyWord });
			// Cursor cursor =
			// db.rawQuery("select ct.*, cv.id , max(c.sentTime) as `newestChat` , count(c.id) totalChat from contact as ct left join conversation as cv on cv.member = '[\"' || ct.pn || '\"]' left join chat as c on c.conversationId = cv.id group by ct.pn order by newestChat desc, ct.contact_name desc",
			// new String[]{});
			cursor.moveToFirst();
			do {
				Contact ct = fromCursor(cursor);
				contacts.add(ct);
			} while (cursor.moveToNext());
			return contacts;
		} catch (Exception ex) {
			ex.printStackTrace();
			return contacts;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

}
