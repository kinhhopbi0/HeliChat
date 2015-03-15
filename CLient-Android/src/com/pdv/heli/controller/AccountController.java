package com.pdv.heli.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.activity.startup.FinishSignUpActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.app.ActivitiesManager;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.detail.LinearStringMessage;

public class AccountController {
	public void actionSignIn(IMessage iMessage){
		try {
			final LinearStringMessage signInResponse = new LinearStringMessage(iMessage);
			signInResponse.fromBytes(((MessageBase) iMessage).getDetailData());
			
			final Activity current = ActivitiesManager.getInstance()
					.getCurrentActivity();
			if (current != null
					&& ((current instanceof FinishSignUpActivity)
							|| (current instanceof StartFirstActivity) || (current instanceof SplashActivity))) {
				Log.i("Signined", "posting");
				Handler handler = new Handler(HeliApplication.getInstance()
						.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(!signInResponse.containns("error")){
							TcpClientManager.getInstance().setLogined(true);
							int id = Integer.parseInt(signInResponse.getParam("id"));
							SharedPreferencesManager.saveLogInUserId(id);
							String token =signInResponse.getParam("token");
							SharedPreferencesManager.saveSessionTokenKey(token);
							
							Intent intent = new Intent(current, HomeActivity.class);
							current.startActivity(intent);
							current.finish();
						}else{
							if (current instanceof SplashActivity) {
								((SplashActivity) current).gotoSigninAcivity();						
							}
							if(signInResponse.getParam("error").equals("passwordNotMatches")){					
								Toast.makeText(current,
										"Password or phone number not matches",
										Toast.LENGTH_LONG).show();
								return ;
							}
							if(signInResponse.getParam("error").equals("notMatches")){
								Toast.makeText(current,
										"Session error, please re login",
										Toast.LENGTH_LONG).show();					
								return;
							}
							Toast.makeText(current,
									"Server can't check login",
									Toast.LENGTH_LONG).show();	
							
						}
					}
				});

			}
			
		} catch (MessageNotCorrectExeption e) {
			
			e.printStackTrace();
		}
	}
}
