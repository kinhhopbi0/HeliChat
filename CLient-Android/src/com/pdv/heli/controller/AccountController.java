package com.pdv.heli.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.app.service.SignInStateReceiver;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectException;
import com.pdv.heli.message.detail.LinearStringMessage;
import com.pdv.heli.model.ChatRow;
import com.pdv.heli.model.Contact;
import com.pdv.heli.model.Conversation;

public class AccountController {
	public void actionSignIn(IMessage iMessage) {
		try {
			final LinearStringMessage signInResponse = new LinearStringMessage(
					iMessage);

			signInResponse.fromBytes(((MessageBase) iMessage).getDetailData());
			if (!signInResponse.containns("error")) {
				TcpClientManager.getInstance().setLogined(true);
				String pn = signInResponse.getParam("pn");
				SharedPreferencesManager.saveLocalPhone(pn);
				String token = signInResponse.getParam("token");
				
				SharedPreferencesManager.saveTokenKey(token);				

				Intent intent = new Intent(
						SignInStateReceiver.ACTION_ON_SIGIN_SUCCESS);
				HeliApplication.getInstance().sendBroadcast(intent);
				// Đồng bộ danh bạ
				ContactController.callRequestSyncContact();
				// Sync conversation
				ConversationController.callRequestSyncConversation();
				// Đồng bộ tin nhắn
				TalkController.callRequestSyncChat();
				
			} else {
				// delete local user info if login fail
				SharedPreferencesManager.deleteLocalPhone();	
				SharedPreferencesManager.deleteTokenKey();
				String error = "server unable check login";
				if (signInResponse.getParam("error").equals(
						"passwordNotMatches")) {
					error = "Password or phone number not matches";
				}
				if (signInResponse.getParam("error").equals("notMatches")) {
					error = "Session error, please re login";
				}				
				Intent intent = new Intent(
						SignInStateReceiver.ACTION_ON_SIGIN_FAIL);
				Bundle extras = new Bundle();
				extras.putString("error", error);
				intent.putExtras(extras);
				HeliApplication.getInstance().sendBroadcast(intent);
			}


		} catch (MessageNotCorrectException e) {
			e.printStackTrace();
		}
	}

	public static void requestSignInByToken() {
		String phone = SharedPreferencesManager.getLocalPhone();
		String token = SharedPreferencesManager.getTokenKey();

		LinearStringMessage signInMessage = new LinearStringMessage();
		signInMessage.setController("Account");
		signInMessage.setAction("SignIn");
		signInMessage.putParam("pn", phone);
		signInMessage.putParam("token", token);
		MessageQueue.getInstance().offerOutMessage(signInMessage, null);
	}

	public static void requestSignInByPwd(String phone, String password,
			Activity context) {
		LinearStringMessage signInMessage = new LinearStringMessage();
		signInMessage.setController("Account");
		signInMessage.setAction("SignIn");
		signInMessage.putParam("pn", phone);
		signInMessage.putParam("pwd", password);
		MessageQueue.getInstance().offerOutMessage(signInMessage, context);
	}

	public static void doSignOut() {
		// clear user data
		SharedPreferencesManager.deleteLocalPhone();
		SharedPreferencesManager.deleteTokenKey();
		Contact.deleteAll();
		ChatRow.deleteAllLocal();
		Conversation.deleteAll();
		
		
		SharedPreferencesManager.saveTimeSyncContact(0);
		SharedPreferencesManager.saveTimeSyncChat(0);
		SharedPreferencesManager.saveTimeSyncConversation(0);
		
		// request to server
		LinearStringMessage signOut = new LinearStringMessage();
		signOut.setController("Account");
		signOut.setAction("SignOut");
		MessageQueue.getInstance().offerOutMessage(signOut, null);

	}
}
