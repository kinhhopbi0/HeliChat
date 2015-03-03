package com.pdv.heli.manager;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.constant.ServerInfo;
import com.pdv.heli.message.detail.SignInMessage;
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
	private  boolean isLogined = false;	
	static {
		instance = new TcpClientManager();
	}
	static int countR = 0;
	private TcpClientManager() {
		connectState = State.NEW;
	}
	private final String[] server= new String[]{
			ServerInfo.HOST_LOCAL_RPC,
			ServerInfo.DDNS_HOST,
			ServerInfo.DDNS_RPC_HOST,
			ServerInfo.HOST_DEV_GENYMOTION,
			
	};
	private void init() {
		if(tcpClient != null){			
			tcpClient.stop();
			tcpClient = null;
		}
		isLogined = false;
		tcpClient = new TcpClient();
		
		int i = countR% server.length;		
		tcpClient.setServerHost(server[i]);
		countR++;
		tcpClient.setServerPort(ServerInfo.TCP_PORT);
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
			if(reconectThread != null){
				Thread moribund = reconectThread;
				reconectThread = null;
				moribund.interrupt();
			}			
		}
	}

	@Override
	public void onConnecting(Object sender) {
		Log.i(TAG, "Connecting");
		connectState = State.CONNECTING;
	}
	Thread reconectThread;
	@Override
	public void onConnectFail(Object sender, IOException ex) {
		connectState = State.NOTCONNECTED;
		Log.i(TAG, "Connect fail");
		HeliApplication.getInstance().showToastFromOtherThread("Fail connect");
		
		Intent intent = new Intent();
		intent.setAction(SplashActivity.ACTION_CONNECT_FAIL);	
		HeliApplication.getInstance().sendBroadcast(intent);
		reConnect();
	}
	public void reConnect(){
		reconectThread = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					Thread.sleep(5000);					
					startClient();
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}				
			}
		});
		reconectThread.start();
	}
	@Override
	public void onConnectSuccess(Object sender) {		
		Log.i(TAG, "Connect success");
		connectState = State.READY;
		if(reconectThread != null){
			reconectThread.interrupt();
			reconectThread = null;
		}
		MessageQueueProcessor.getInstance().startDeQueueTask();		
		Intent intent = new Intent();	
		intent.setAction(SplashActivity.ACTION_CONNECT_SUCCESS);
		HeliApplication.getInstance().sendBroadcast(intent);	
		if(SharedPreferencesManager.getLoginUserId()>0){
			reLoginFromCookie();
		}

	}
	public void reLoginFromCookie(){
		Intent intent = new Intent();	
		intent.setAction(SplashActivity.ACTION_UPDATE_STATUS);
		Bundle bundle = new Bundle();
		bundle.putString(SplashActivity.KEY_STATUS_TEXT, "Signing...");
		intent.putExtras(bundle);
		HeliApplication.getInstance().sendBroadcast(intent);				
		
		int userId = SharedPreferencesManager.getLoginUserId();
		SignInMessage message = new SignInMessage();
		message.setStatus(SignInMessage.Status.RE_REQUEST);
		message.setUser_id(userId);
		message.setToken(SharedPreferencesManager.getSessionTokenKey());
		MessageQueueProcessor.getInstance().offerOutMessage(message);
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
		MessageQueueProcessor.getInstance().stopDeQueueTask();		
	}

	@Override
	public void onDisconnect(Object sender) {
		HeliApplication.getInstance().showToastFromOtherThread("Disconnect");
		connectState = State.NOTCONNECTED;
		tcpClient.stop();
		MessageQueueProcessor.getInstance().stopDeQueueTask();
		reConnect();
		
	}
	
	@Override
	public void onReceiveBytes(Object sender, byte[] buffer) {
		MessageQueueProcessor.getInstance().offerToIncommingBytes(buffer);
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
	

}
