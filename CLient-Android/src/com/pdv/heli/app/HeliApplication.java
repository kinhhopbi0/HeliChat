package com.pdv.heli.app;

import java.util.HashMap;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by vinhanh on 12/01/2015.
 */
public class HeliApplication extends Application {
	private static HeliApplication _instance;
	private Handler handler;
	private HashMap<String, Integer> onlineState = new HashMap<>(); 

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

	

	public void startService(Class<? extends Service> clazz) {
		Intent intent = new Intent(this, clazz);
		startService(intent);
	}

	public void stopService(Class<? extends Service> backgroundServiceClass) {
		Intent intent = new Intent(this, backgroundServiceClass);
		stopService(intent);
	}
	public Handler getHandler(){
		return new Handler(getMainLooper());
	}

	public HashMap<String, Integer> getOnlineState() {
		return onlineState;
	}

	public void setOnlineState(HashMap<String, Integer> onlineState) {
		this.onlineState = onlineState;
	}
}
