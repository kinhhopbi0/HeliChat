package com.pdv.heli.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.activity.contact.AddFriendDialog;
import com.pdv.heli.activity.contact.FriendsFragment;
import com.pdv.heli.activity.conversation.ConversationFragment;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.activity.startup.ConfirmVerifyActivity;
import com.pdv.heli.activity.startup.FinishSignUpActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.app.ActivitiesManager;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;
import com.pdv.heli.message.detail.SignInMessage;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.SyncDeviceContactMessage;
import com.pdv.heli.message.detail.TextMessage;
import com.pdv.heli.message.detail.LinearStringMessage;

public class MessageNavigation {
	public static final String TAG = MessageNavigation.class.getSimpleName();

	public static void navigationTextMessage(final TextMessage message) {
		// if (message.getMessageMode() == MessageMode.RECEIVE) {
		// Log.i(TAG, "Proccessing text message from server");
		// final Activity currentAc = ActivitiesManager.getInstance()
		// .getCurrentActivity();
		// if (currentAc instanceof ConversationActivity) {
		// Handler handler = new Handler(currentAc.getMainLooper());
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		// ((ConversationActivity) currentAc)
		// .showTextMesage(message);
		// }
		// });
		// } else {
		// CustomNotification notification = new CustomNotification(
		// HeliApplication.getInstance());
		// notification.pushTextMessage(message);
		// }
		// }
	}

	public static void navigationResponseSignUp(final SignUpMessage response) {
		Log.i(TAG, "Response sign up from server");

		final Activity current = ActivitiesManager.getInstance()
				.getCurrentActivity();
		if (current != null && current instanceof StartFirstActivity) {
			Handler handler = new Handler(current.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					((StartFirstActivity) current)
							.processSignupMessage(response);
				}
			});

		} else {
			ProcessMsgInBackground.processSignUpMessage(response);
		}

	}

	public static void navigationConfirmMsg(final ConfirmPasscodeMsg detail) {
		Log.i(TAG, "Response passcode confirm from server");

		final Activity current = ActivitiesManager.getInstance()
				.getCurrentActivity();
		if (current != null && current instanceof ConfirmVerifyActivity) {
			Handler handler = new Handler(current.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					((ConfirmVerifyActivity) current)
							.onConfirmFromServer(detail);
				}
			});

		}
	}

	public static void navigateSignIn(final SignInMessage detail) {
		Log.i(TAG, "SignInMessage msg from server");
		if (SignInMessage.Status.SUCCESS == detail.getStatus()) {
			TcpClientManager.getInstance().setLogined(true);
			int id = detail.getUser_id();
			SharedPreferencesManager.saveLogInUserId(id);
			String token = detail.getToken();
			SharedPreferencesManager.saveSessionTokenKey(token);
		}

		

	}

	public static void navigate(LinearStringMessage detail) {

		Intent intent = new Intent();
		Bundle extras = new Bundle();

		if (detail.containns("controller")) {
			if (detail.getParam("controller").equals("relationship")) {
				final Activity currentActivity = ActivitiesManager
						.getInstance().getCurrentActivity();
				String action = detail.getParam("action");
				if (action != null && action.equals("requestPhone")) {										
					final String notifiText;
					String requestPhone = detail.getParam("phone");
					String state = detail.getParam("state");
					if(AddFriendDialog.isShow()){						
						intent.setAction(AddFriendDialog.RESPONSE_ACTION);
						Bundle bundle = new Bundle();
						bundle.putString("state", state);
						bundle.putString("phone", requestPhone);
						intent.putExtras(bundle);
						HeliApplication.getInstance().sendBroadcast(intent);
						return;
					}
					intent.setAction(AddFriendDialog.DISSMIS_ACTION);
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
							getFriendInfoMsg.putParam("controller","userInfo");
							getFriendInfoMsg.putParam("action", "GetFriendInfo");
							getFriendInfoMsg.putParam("phone", requestPhone);
							MessageQueue.getInstance().offerOutMessage(getFriendInfoMsg,null);
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

				}
				
			}
		}

	}

	public static void navigate(SyncDeviceContactMessage detail) {
		switch (detail.getType()) {
		case SyncDeviceContactMessage.Type.SERVER_UPDATE_SUCCESS:
			SyncDeviceContactMessage message = new SyncDeviceContactMessage();
			message.setType(SyncDeviceContactMessage.Type.CLIENT_GET_CONTACT);
			MessageQueue.getInstance().offerOutMessage(message, null);
			break;
		case SyncDeviceContactMessage.Type.SERVER_UPDATE_FAIL:

			break;
		default:
			break;
		}
	}

}
