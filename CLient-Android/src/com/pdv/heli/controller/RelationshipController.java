package com.pdv.heli.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pdv.heli.activity.contact.AddPhoneDialog;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.manager.ActivitiesManager;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectException;
import com.pdv.heli.message.detail.LinearStringMessage;

public class RelationshipController {
	public void actionAddPhoneNumber(IMessage message){
		LinearStringMessage response;
		try {
			response = new LinearStringMessage(message);
			response.fromBytes(((MessageBase)message).getDetailData());
		
			final Activity currentActivity = ActivitiesManager
					.getInstance().getCurrentActivity();
			
			final String notifiText;
			String requestPhone = response.getParam("pn");
			String state = response.getParam("state");
			Intent intent = new Intent();
			if(AddPhoneDialog.isShow()){						
				intent.setAction(AddPhoneDialog.RESPONSE_FIDN_ACTION);
				Bundle bundle = new Bundle();
				bundle.putString("state", state);
				bundle.putString("phone", requestPhone);
				intent.putExtras(bundle);
				HeliApplication.getInstance().sendBroadcast(intent);
				return;
			}
			intent.setAction(AddPhoneDialog.RESPONSE_ADD_ACTION);
			HeliApplication.getInstance().sendBroadcast(intent);
			
			if (currentActivity != null) {
				
				if (state == null) {
					return;
				}
				switch (state) {
				case "notFound":
					notifiText = "Phone number " + requestPhone
							+ " not use Helli";
					break;
				case "requested":
					notifiText = "Request friend with " + requestPhone
							+ " success";
					break;
				case "requestFail":
					notifiText = "Request friend with " + requestPhone
							+ " fail";
					break;
				case "requestBefore":
					notifiText = "You readly request " + requestPhone
							;
					break;
				case "readyFriend":
					notifiText = "You and " + requestPhone
							+ " is readly friend";
					break;
				case "accepted":
					notifiText = "You and " + requestPhone
					+ " is friend now";
					LinearStringMessage getFriendInfoMsg = new LinearStringMessage();
					getFriendInfoMsg.setController("Account");
					getFriendInfoMsg.setAction("GetFriendInfo");
					getFriendInfoMsg.putParam("pn", requestPhone);
					MessageQueue.getInstance().offerOutMessage(getFriendInfoMsg,currentActivity);
					break;
				default:
					notifiText = "Request friend with " + requestPhone
							+ " undefined error";
					break;
				}

				Handler handler = new Handler(
						currentActivity.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						SnackbarManager.show(
								Snackbar.with(
										HeliApplication.getInstance())
										.text(notifiText),
								currentActivity);
					}
				});

			}

		} catch (MessageNotCorrectException e) {
			
		}
		
	}
}
