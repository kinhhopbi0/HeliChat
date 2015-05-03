package com.pdv.heli.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.sqlite.ChatDbHelper;
import com.pdv.heli.sqlite.SqliteDataHelper;

public class ChatRow {
	private static int sq = 0;
	
	private JSONObject content;
	private String id;
	private long timeStamp;
	private String from;
	private String conversationId;		
	private boolean isSending;
	private String status;
	private boolean isUnread;
	
	private int sequence;	 

	private ChatRow(boolean isSend){
		if(isSend){
			sequence = sq++;
			this.timeStamp = new Date().getTime();
		}
	}
	public static ChatRow makeChatToSend(){
		return new ChatRow(true);
	}
	public static ChatRow makeReceiveChat(){
		return new ChatRow(false);
	}
	
	public JSONObject getContent() {
		return content;
	}


	public void setContent(JSONObject content) {
		this.content = content;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSenderPhone() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	
	public String getConversationId() {
		return conversationId;
	}


	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}


	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public static List<ChatRow> findChatByConversation(String conversationId, int offset)  {
		List<ChatRow> lst = new ArrayList<ChatRow>();
		SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getReadableDatabase();		
		Cursor cursor = db.rawQuery(
				"select * from chat where conversationId=? order by sentTime desc",
				new String[] {conversationId });
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			ChatRow chatRowModel;			  
			do {
				chatRowModel = fromCursor(cursor);
				if(chatRowModel!=null){
					lst.add(chatRowModel);
				}
				
			} while (cursor.moveToNext());
		}
		db.close();
		return lst;
	}
	
	
	public String getContentText(){
		try {
			return this.content.getString("text");
		} catch (JSONException e) {			
			e.printStackTrace();
			return null;
		}
	}	
	public String getVoiceUri(){
		try {
			return this.content.getString("voice");
		} catch (JSONException e) {			
			e.printStackTrace();
			return null;
		}
	}
	public void save(boolean insert) {
		SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();		
		if(this.content==null){
			return;
		}else{
			values.put("content", this.content.toString());
		}
		values.put("`id`", id);
		values.put("`from`", this.from);
		values.put("conversationId", this.conversationId);		
		values.put("sentTime", this.timeStamp);	
		values.put("unread", this.isUnread);
		
		if (insert) {
			db.insertWithOnConflict("chat", "id", values,
					SQLiteDatabase.CONFLICT_ABORT);
		} else {			
			db.insertWithOnConflict("chat", null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
		}
		db.close();
	}
	
	public boolean delete() {
		SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int kq = db.delete("chat", "id=?",
				new String[] { String.valueOf(this.id) });
		db.close();
		if (kq <= 0) {
			return false;
		}
		
		return true;
	}

	

	public static void clearLocalAllByPhone(String phone) {
		SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String localPhone = SharedPreferencesManager.getLocalPhone();
		db.delete("chat", "(from=? and toPn=?) or (from=? and toPn=?)", new String[]{phone,localPhone,localPhone,phone});		
		db.close();
	}
		
	public int  getSequence(){
		return this.sequence;
	}
//	public void postToUI() {		
//		Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//		utcCalendar.setTimeInMillis(timeStamp);		
//		Log.v("time stamp ", timeStamp+"");
//		Log.v("send time local ", utcCalendar.getTime().toLocaleString());
//		Log.v("send time utc ", utcCalendar.getTime().toGMTString());
//		
//		final ConversationFragment fragment = ConversationFragmentManager
//				.getInstance().getSingleFragments().get(pn);
//
//		if (fragment != null) {
//			if (fragment.isVisible()) {
//				HeliApplication.getInstance().getHandler().post(new Runnable() {
//					@Override
//					public void run() {
//						fragment.insertBottomChatRow(ChatRow.this);
//					}
//				});
//				return;
//			}
//		}
//
//		IncomingChatNotifi customNotification = new IncomingChatNotifi(
//				HeliApplication.getInstance());
//		Contact contact = Contact.findOneByPhone(pn);
//		if (contact == null) {
//			contact = new Contact();
//			contact.setPhoneNumber(pn);
//			contact.save(HeliApplication.getInstance());
//			Intent callUpdateNewContact = new Intent(UpdateContactUIReceiver.INSERT_NEW_CONTACT_ACTION);
//			Bundle extras = new Bundle();			
//			extras.putString("pn", pn);
//			callUpdateNewContact.putExtras(extras);
//			HeliApplication.getInstance().sendBroadcast(callUpdateNewContact);
//			customNotification.pushTextMessage(this.contentText, pn);
//		} else {
//			customNotification.pushTextMessage(this.contentText,
//					contact.getContactName());
//		}
//
//	}


	public static void deleteAllLocal() {
		SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("chat", null, null);
		db.close();
		
	}


	public boolean isSending() {
		return isSending;
	}


	public void setSending(boolean isSending) {
		this.isSending = isSending;
	}
	
	public static ConcurrentHashMap<Integer, ChatRow> sendingList = new ConcurrentHashMap<>();

	public static ChatRow findById(String chatId) {
		SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "+ChatDbHelper.TABLE_NAME+" where `id`=?", new String[]{chatId});
		if(cursor.moveToFirst()){
			return fromCursor(cursor);
		}else{
			return null;
		}
		
	}


	public boolean isUnread() {
		return isUnread;
	}


	public void setUnread(boolean isUnread) {
		this.isUnread = isUnread;
	}

	public static  ChatRow fromCursor(Cursor  cursor){
		int colId = cursor.getColumnIndex("id");
		int colFrom = cursor.getColumnIndex("from");
		int colConversationId = cursor.getColumnIndex("conversationId");
		int colContent = cursor.getColumnIndex("content");  
		int colSentTime = cursor.getColumnIndex("sentTime"); 
		int colUnread = cursor.getColumnIndex("unread");
		
		ChatRow chatRow = new ChatRow(false);
		chatRow.setId(cursor.getString(colId));
		chatRow.setFrom(cursor.getString(colFrom));		
		chatRow.setConversationId(cursor.getString(colConversationId));
		chatRow.setTimeStamp(cursor.getLong(colSentTime));
		chatRow.setUnread(cursor.getInt(colUnread)==0?false:true);
		try {
			chatRow.setContent(new JSONObject(cursor.getString(colContent)));
		} catch (JSONException e) {
			return null;
		}				
		return chatRow;
	}
	public static List<ChatRow> findUnreads() {
		List<ChatRow> lst = new ArrayList<ChatRow>();
		SQLiteDatabase db  =null;
		try{
			SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
			 db = dbHelper.getReadableDatabase();		
			Cursor cursor = db.rawQuery(
					"select * from chat where unread=1 order by sentTime desc",null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				ChatRow chatRowModel;			  
				do {
					chatRowModel = fromCursor(cursor);
					if(chatRowModel!=null){
						lst.add(chatRowModel);
					}
					
				} while (cursor.moveToNext());
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				db.close();
			}catch(Exception ex){
				
			}
		}
		return lst;
	}
	public static void clearAllUnread() {
		SQLiteDatabase db  =null;
		try{
			SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
			 db = dbHelper.getReadableDatabase();	
			 ContentValues values = new ContentValues(1);
			 values.put("unread", false);
			 db.update(ChatDbHelper.TABLE_NAME, values, null, null);			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				db.close();
			}catch(Exception ex){
				
			}
		}		
	}
	public static void clearUnreadForConversation(String conversationId) {
		SQLiteDatabase db  =null;
		try{
			SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
			 db = dbHelper.getReadableDatabase();	
			 ContentValues values = new ContentValues(1);
			 values.put("unread", false);
			 db.update(ChatDbHelper.TABLE_NAME, values, "conversationId=?", new String[]{conversationId});			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				db.close();
			}catch(Exception ex){
				
			}
		}		
	}
	public static void deleteAllLocalbyConversationId(String id2) {
		SQLiteDatabase db = null	;
		try{
			SqliteDataHelper dbHelper = new SqliteDataHelper(HeliApplication.getInstance());
			 db = dbHelper.getReadableDatabase();	
			 db.delete(ChatDbHelper.TABLE_NAME, "conversationId=?", new String[]{id2});	
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				db.close();
			}catch(Exception ex){
				
			}
		}
	}
}
