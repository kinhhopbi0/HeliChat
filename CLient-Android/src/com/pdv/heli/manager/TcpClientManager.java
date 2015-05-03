package com.pdv.heli.manager;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.activity.setting.AppSettingActivity;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.constant.ServerInfo;
import com.pdv.heli.controller.AccountController;
import com.pdv.transport.client.ClientNetworkingInterface;
import com.pdv.transport.client.TcpClient;

/**
 * Created by via on 2/3/15.
 */
public class TcpClientManager implements ClientNetworkingInterface {
	private static final String TAG = TcpClientManager.class.getSimpleName();
	private TcpClient tcpClient;
	private static TcpClientManager instance;
	private State connectState;
	private boolean isLogined = false;
	static {
		instance = new TcpClientManager();
	}
	static int countR = 0;

	private TcpClientManager() {
		connectState = State.NEW;
	}


	private void init() {
		if (tcpClient != null) {
			tcpClient.stop();
			tcpClient = null;
		}
		isLogined = false;
		tcpClient = new TcpClient();
		String server_host = AppSettingActivity.getServerHost();
		tcpClient.setServerHost(server_host);
		Log.v("connect", "connecting to " + server_host);
		countR++;
		tcpClient.setServerPort(AppSettingActivity.getServerPort());
		tcpClient.setConnectTimeout(ServerInfo.TIME_OUT);
		tcpClient.setNetworkingEventListener(this);
		connectState = State.NEW;
	}

	/**
	 * Phuong thuc khoi dong toan bo cac ket noi can thiet neu ket noi do chua
	 * ket noi
	 */
	public synchronized void startClient() {
		init();
		tcpClient.start();
	}

	public synchronized void stop() {
		if (connectState != State.NEW) {
			connectState = State.NOTCONNECTED;
			tcpClient.stop();
			tcpClient = null;
			isLogined = false;
			if (reconectThread != null) {
				Thread moribund = reconectThread;
				reconectThread = null;
				moribund.interrupt();
			}
		}
	}

	@Override
	public void onConnecting(Object sender) {		
		connectState = State.CONNECTING;
	}

	Thread reconectThread;

	@Override
	public void onConnectFail(Object sender, IOException ex) {
		connectState = State.NOTCONNECTED;
		
		// Log.i(TAG, "Connect fail");
		Intent intent = new Intent();
		intent.setAction(SplashActivity.ACTION_CONNECT_FAIL);
		HeliApplication.getInstance().sendBroadcast(intent);
		
		final Activity current = ActivitiesManager.getInstance().getCurrentActivity();
		if(current!=null){
			Handler handler = new Handler(current.getMainLooper());
			handler.postDelayed(new Runnable() {				
				@Override
				public void run() {					
					SnackbarManager.show(Snackbar.with(current).color(Color.RED).text("Fail connect to server"),current);
				}
			},550);
		}
		// reConnect();
	}

	public void reConnect() {
		if (reconectThread != null) {
			try {
				reconectThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		reconectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				startClient();
			}
		});
		reconectThread.start();
	}

	@Override
	public void onConnectSuccess(Object sender) {
		Log.i(TAG, "Connect success");
		connectState = State.READY;
		if (reconectThread != null) {
			reconectThread.interrupt();
			reconectThread = null;
		}
		
		Intent intent = new Intent();
		intent.setAction(SplashActivity.ACTION_CONNECT_SUCCESS);
		HeliApplication.getInstance().sendBroadcast(intent);
		final Activity current = ActivitiesManager.getInstance().getCurrentActivity();
		if(current!=null){
			Handler handler = new Handler(current.getMainLooper());
			handler.postDelayed(new Runnable() {				
				@Override
				public void run() {					
					SnackbarManager.show(Snackbar.with(current).text("Connect to server success"),current);
				}
			},550);
		}
		if (SharedPreferencesManager.getLocalPhone() != null) {
			loginByCookie();
		}

	}

	public void loginByCookie() {
		// update status login in UI
		Intent intent = new Intent();
		intent.setAction(SplashActivity.ACTION_UPDATE_STATUS);
		Bundle bundle = new Bundle();
		bundle.putString(SplashActivity.KEY_STATUS_TEXT, "Signing...");
		intent.putExtras(bundle);
		HeliApplication.getInstance().sendBroadcast(intent);
		// sent request to server
		AccountController.requestSignInByToken();
	}

	@Override
	public void onSending(Object sender) {
		Log.i(TAG, "sending");
	}

	@Override
	public void onSentSuccess(Object sender) {
		Log.i(TAG, "sent success");
	}

	@Override
	public void onSentFail(Object sender) {
		Log.i(TAG, "sent fail");
		
	}

	@Override
	public void onServerCloseConnection(Object sender) {
		Log.i(TAG, "Connect close");
		HeliApplication.getInstance().showToastFromOtherThread("Server close");
		connectState = State.NOTCONNECTED;
		tcpClient.stop();
		//MessageQueue.getInstance().stopDeQueueTask();
	}

	@Override
	public void onDisconnect(Object sender) {
		Log.v(TAG, "disconnect from server "+ ((TcpClient)sender).getServerHost());
		HeliApplication.getInstance().showToastFromOtherThread("Disconnect");
		connectState = State.NOTCONNECTED;
		tcpClient.stop();
		//MessageQueue.getInstance().stopDeQueueTask();
		
	}

	@Override
	public void onReceiveBytes(Object sender, byte[] buffer) {
		MessageQueue.getInstance().offerToIncommingBytes(buffer);
	}

	public void sendBytes(byte[] buffer) {
		tcpClient.sendAsync(buffer);
	}

	public enum State {
		NEW, CONNECTING, READY, NOTCONNECTED,
	}

	public static TcpClientManager getInstance() {
		return instance;
	}

	/**
	 * @return the connectState
	 */
	public State getConnectState() {
		return connectState;
	}

	public boolean isLogined() {
		return isLogined;
	}

	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
	}

	public TcpClient getTcpClient() {
		return tcpClient;
	}

	@Override
	public void onConnectError(Object sender, Exception ex) {
		connectState = State.NOTCONNECTED;
		Intent intent = new Intent();
		intent.setAction(SplashActivity.ACTION_CONNECT_FAIL);
		HeliApplication.getInstance().sendBroadcast(intent);
		
		final Activity current = ActivitiesManager.getInstance().getCurrentActivity();
		if(current!=null){
			Handler handler = new Handler(current.getMainLooper());
			handler.postDelayed(new Runnable() {				
				@Override
				public void run() {					
					SnackbarManager.show(Snackbar.with(current).color(Color.RED).text("Server address invalid"),current);
				}
			},550);
		}		
	}

}
