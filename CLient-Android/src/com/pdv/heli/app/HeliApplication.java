package com.pdv.heli.app;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.component.CustomNotification;
import com.pdv.heli.message.detail.TextMessage;

/**
 * Created by vinhanh on 12/01/2015.
 */
public class HeliApplication extends Application {
	private static HeliApplication _instance;
	private Handler handler;

	public static HeliApplication getInstance() {
		return _instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("AppCore", "Application core starting");
		handler = new Handler(Looper.getMainLooper());		
		_instance = this;	
		
	}
	
	@Override
	public void onTerminate() {		
		super.onTerminate();
		Log.i("AppCore", "Application terminate");
	}


	public void showToastFromOtherThread(final String message) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(HeliApplication.this, message, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	/**
	 * Show a text message to notification or to screen bubble chat
	 * 
	 * @param message
	 */
	public void receiveTextMessage(final TextMessage message) {
		if (ActivitiesManager.getInstance().getLiveConversation() != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					ActivitiesManager.getInstance().getLiveConversation()
							.insertBubbleChat(message);
				}
			});
		}
		new CustomNotification(this).pushTextMessage(message);
	}

	public void startService(Class<? extends Service> clazz) {
		Intent intent = new Intent(this, clazz);
		startService(intent);
	}

	public void stopService(Class<? extends Service> backgroundServiceClass) {
		Intent intent = new Intent(this, backgroundServiceClass);
		stopService(intent);
	}
}
