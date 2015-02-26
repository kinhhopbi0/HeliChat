package com.pdv.heli.processmessage;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.pdv.heli.activity.home.ConversationActivity;
import com.pdv.heli.activity.startup.ConfirmPasscodeActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.app.ActivitiesManager;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.common.BytesUtil;
import com.pdv.heli.component.CustomNotification;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.TextMessage;

public class MessageNavigation {
	public static final String TAG = MessageNavigation.class.getSimpleName();

	public static void navigationTextMessage(final TextMessage message) {
		if (message.getMessageMode() == MessageMode.RECEIVE) {
			Log.i(TAG, "Proccessing text message from server");
			final Activity currentAc = ActivitiesManager.getInstance()
					.getCurrentActivity();
			if (currentAc instanceof ConversationActivity) {
				Handler handler = new Handler(currentAc.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						((ConversationActivity) currentAc)
								.showTextMesage(message);
					}
				});
			} else {
				CustomNotification notification = new CustomNotification(
						HeliApplication.getInstance());
				notification.pushTextMessage(message);
			}
		}
	}

	public static void navigationResponseSignUp(final SignUpMessage  response) {
		Log.i(TAG, "Response sign up from server");

		final Activity current = ActivitiesManager.getInstance().getCurrentActivity();
		if(current != null && current instanceof StartFirstActivity){
			Handler handler = new Handler(current.getMainLooper());
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					((StartFirstActivity) current).processSignupMessage(response);					
				}
			});
			
		}else{
			ProcessMsgInBackground.processSignUpMessage(response);
		}
		
	}

	public static void navigationConfirmMsg(final ConfirmPasscodeMsg detail) {
		Log.i(TAG, "Response passcode confirm from server");
		
		final Activity current = ActivitiesManager.getInstance().getCurrentActivity();
		if(current != null && current instanceof ConfirmPasscodeActivity){
			Handler handler = new Handler(current.getMainLooper());
			handler.post(new Runnable() {				
				@Override
				public void run() {
					((ConfirmPasscodeActivity) current).onConfirmFromServer(detail);				
				}
			});
			
		}
	}

}
