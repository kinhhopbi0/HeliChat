package com.pdv.heli.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.activity.home.ConversationActivity;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.activity.startup.ConfirmVerifyActivity;
import com.pdv.heli.activity.startup.FinishSignUpActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.app.ActivitiesManager;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.component.CustomNotification;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;
import com.pdv.heli.message.detail.SignInMessage;
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
		if(SignInMessage.Status.SUCCESS == detail.getStatus()){
			TcpClientManager.getInstance().setLogined(true);
			int id = detail.getUser_id();
			SharedPreferencesManager.saveLogInUserId(id);
			String token = detail.getToken();
			SharedPreferencesManager.saveSessionTokenKey(token);
		}
		
		final Activity current = ActivitiesManager.getInstance()
				.getCurrentActivity();
		if (current != null && ((current instanceof FinishSignUpActivity)
				|| (current instanceof StartFirstActivity)
				|| (current instanceof SplashActivity))) {
			Log.i("Signined", "posting");
			Handler handler = new Handler(HeliApplication.getInstance().getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					byte status = detail.getStatus();
					Log.i(TAG, "run post "+detail.getStatus());
					switch (status) {
					case SignInMessage.Status.SUCCESS:	
						Log.i("Signined", "successing");
						Intent intent = new Intent(current, HomeActivity.class);
						current.startActivity(intent);
						current.finish();
						Log.i("Signined", "succesed");
						break;
					case SignInMessage.Status.UNAME_NOT_MATCHES:
						if(current instanceof SplashActivity){
							((SplashActivity) current).gotoSigninAcivity();
							break;
						}
						Toast.makeText(current,
								"Password or phone number not matches",
								Toast.LENGTH_LONG).show();
						break;

					case SignInMessage.Status.OVER_TIMES:
						Toast.makeText(current,
								"You request over number login",
								Toast.LENGTH_LONG).show();											
						break;
					case SignInMessage.Status.TOKEN_NOT_MATCHES:
						Toast.makeText(current,
								"Session over. please re mogin",
								Toast.LENGTH_LONG).show();
						Intent intent2 = new Intent(current, StartFirstActivity.class);
						current.startActivity(intent2);
						current.finish();
						break;
					}
				}
			});

		}

	}

}
