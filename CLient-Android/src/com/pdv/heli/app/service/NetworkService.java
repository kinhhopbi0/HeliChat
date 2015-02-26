package com.pdv.heli.app.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.constant.NotificationId;
import com.pdv.heli.manager.TcpIOManager;

/**
 * Created by via on 2/6/15.
 */
public class NetworkService extends Service {
	private static final String TAG = NetworkService.class.getName();

	public static final String START_FOREGROUND = "com.phamvinh.alo.NetworkService.START_FOREGROUND";
	public static final String CLOSE_NETWORK_CONNECTION = "com.phamvinh.alo.NetworkService.CLOSE_NETWORK_CONNECTION";
	public static final String STOP_FOREGROUND = "com.phamvinh.alo.NetworkService.STOP_FOREGROUND";

	private static NetworkService instance;

	private NetworkServiceReceiver networkServiceReceiver;
	private TcpIOManager networkIO;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "Service onCreate ");
		IntentFilter maiIntentFilter = new IntentFilter(
				"com.phamvinh.alo.service.NetworkService");
		networkServiceReceiver = new NetworkServiceReceiver();
		registerReceiver(networkServiceReceiver, maiIntentFilter);
		instance = this;
		doWorkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				doForegroundJob();
			}
		});
		networkIO = TcpIOManager.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.v("Test", "service onStartCommand  " + (intent != null) + " "
				+ startId);
		if (intent == null) {
			return START_NOT_STICKY;
		}
		switch (intent.getAction()) {
		case START_FOREGROUND:
			Log.v(TAG, "Service START_FOREGROUND ");
			// Start foreground
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
					this);
			Intent notificationIntent = new Intent(this, HeliApplication.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
			notificationBuilder.setContentIntent(pendingIntent);
			
			this.startForeground(NotificationId.NETWORK_FOREGROUND_ID,
					notificationBuilder.build());
			doWorkThread.start();
			break;
		case STOP_FOREGROUND:
			stopForeground(true);
			stopSelf();
			break;
		default:
			return START_NOT_STICKY;

		}
		// If we get killed, after returning from here, restart
		return START_NOT_STICKY;
	}

	private Thread doWorkThread;

	public void doForegroundJob() {
		Log.v(TAG, "Service do work...");
		networkIO.startConnectAndReceive();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		Log.v(TAG, "service onBinding ");
		return null;
	}

	@Override
	public void onDestroy() {
		instance = null;
		super.onDestroy();
		networkIO.close();
		unregisterReceiver(networkServiceReceiver);
		if (doWorkThread.isAlive()) {
			doWorkThread.interrupt();
		}
		doWorkThread = null;
		Log.v(TAG, "Service done");
	}

	public static NetworkService getInstance() {
		return instance;
	}
}