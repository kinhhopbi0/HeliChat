package com.pdv.heli.component;

import java.util.List;

import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.pdv.heli.R;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.model.ChatRow;
import com.pdv.heli.model.Contact;

/**
 * Created by via on 2/5/15.
 */
public class IncomingChatNotification {

	private static int count = 0;
	private Context context;
	private String ticker = "";
	public static final int ID_UNREAD_NOTIFICATION = 0x01;

	private NotificationManagerCompat notificationManager;
	private static IncomingChatNotification _instance;
	static {
		_instance = new IncomingChatNotification();
	}

	public static IncomingChatNotification getInstance() {
		if (_instance == null) {
			_instance = new IncomingChatNotification();
		}
		return _instance;
	}

	public IncomingChatNotification() {
		try {
			context = HeliApplication.getInstance();
			notificationManager = NotificationManagerCompat.from(context);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void pushReceivedChat(ChatRow chatRow) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		Intent resultIntent = new Intent(context, HomeActivity.class);
		Bundle extras = new Bundle();

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		String contentText = "";
		String titleText = "";
		if (count == 0) {
			Contact contact = Contact.findOneByPhone(chatRow.getSenderPhone());
			titleText = "New message from ";
			if (contact != null) {
				titleText += contact.getContactName();
			} else {
				titleText += chatRow.getSenderPhone();
			}
			if (chatRow.getContent().has("text")) {
				try {
					contentText = chatRow.getContent().getString("text");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			extras.putString("conversationId", chatRow.getConversationId());

		} else {
			contentText = "You have " + titleText + " new messages";
		}
		resultIntent.putExtras(extras);
		mBuilder.setWhen(chatRow.getTimeStamp());
		mBuilder.setContentText(contentText);
		mBuilder.setContentTitle(titleText);
		mBuilder.setSmallIcon(R.drawable.ic_notifi_textsms);
		mBuilder.setNumber(++count);
		ticker = titleText;
		mBuilder.setTicker(ticker);
		mBuilder.setAutoCancel(true);
		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

		notificationManager.notify("IncomingChatNotifi",
				ID_UNREAD_NOTIFICATION, mBuilder.build());
	}

	public void updateNotifi(boolean buzz) {
		List<ChatRow> lst = ChatRow.findUnreads();
		if (lst.size() == 0) {
			NotificationManager mNotificationManager = (NotificationManager) HeliApplication
					.getInstance().getSystemService(
							Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(ID_UNREAD_NOTIFICATION);
			return;
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		Intent resultIntent = new Intent(context, HomeActivity.class);

		String contentText = "";
		String titleText = "";
		if (lst.size() == 1) {
			Contact contact = Contact.findOneByPhone(lst.get(0)
					.getSenderPhone());
			titleText = "";
			if (contact != null) {
				titleText += contact.getContactName();
			} else {
				titleText += lst.get(0).getSenderPhone();
			}
			if (lst.get(0).getContent().has("text")) {
				try {
					contentText = lst.get(0).getContent().getString("text");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Bundle extras = new Bundle();
			extras.putString("conversationId", lst.get(0).getConversationId());
			resultIntent.putExtras(extras);

		} else {
			titleText = "Heli chat";
			contentText = "You have " + lst.size() + " new messages";
		}

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		long when = lst.get(0).getTimeStamp();

		mBuilder.setWhen(when);
		mBuilder.setContentText(contentText);
		mBuilder.setContentTitle(titleText);
		mBuilder.setSmallIcon(R.drawable.ic_stat_notification_sms);
		mBuilder.setNumber(lst.size());
		mBuilder.setTicker(titleText);
		mBuilder.setAutoCancel(true);
		if (buzz) {
			mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
			mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
		}
		mBuilder.setContentIntent(resultPendingIntent);

		Intent deleteNotifiItent = new Intent(
				NotificationReceiver.ACTION_DELETE_NOTIFICATION_CHAT);
		PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
				0, deleteNotifiItent, 0);
		mBuilder.setDeleteIntent(deletePendingIntent);

		Notification compat = mBuilder.build();

		notificationManager.notify("IncomingChatNotifi",
				ID_UNREAD_NOTIFICATION, compat);
	}

	public static class NotificationReceiver extends BroadcastReceiver {
		public static final String ACTION_DELETE_NOTIFICATION_CHAT = "com.pdv.heli.CANCEL_NOTIFI_CHAT";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_DELETE_NOTIFICATION_CHAT)) {
				ChatRow.clearAllUnread();
			}

		}

	}

}
