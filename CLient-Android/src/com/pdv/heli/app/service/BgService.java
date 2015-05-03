package com.pdv.heli.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.manager.TcpClientManager;

public class BgService extends Service implements Runnable {

	private volatile Thread runner;
	public static final String CONNECT_TO_SERVER = BgService.class.getName()+".CONNECT_TO_SERVER";
	public static final String RECONNECT_BY_USER = BgService.class.getName()+".RECONNECT_BY_USER";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public synchronized int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);		
		startThread();		
		return START_STICKY_COMPATIBILITY;
		
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "destroy");
		stopThread();

	}

	public synchronized void startThread() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public synchronized void stopThread() {
	//	MessageQueue.getInstance().stopDeQueueTask();
		if (runner != null) {
			Thread moribund = runner;
			runner = null;
			moribund.interrupt();			
		}
		TcpClientManager.getInstance().stop();		
	}

	// start or restart
	public void run() {
//		while (Thread.currentThread() == runner) {
//			
//		}
		MessageQueue.getInstance().startDeQueueTask();
		Intent intent = new Intent();
		intent.setAction(SplashActivity.ACTION_UPDATE_STATUS);
		Bundle bundle = new Bundle();
		bundle.putString(SplashActivity.KEY_STATUS_TEXT, "Connecting to server ...");
		intent.putExtras(bundle);
		sendBroadcast(intent);
		TcpClientManager.getInstance().startClient();
	}

}
