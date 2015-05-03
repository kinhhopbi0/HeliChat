package com.pdv.heli.manager;

import java.util.concurrent.ArrayBlockingQueue;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.pdv.heli.component.HeliSnackbars;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.processor.GoingOutQueueProcessor;
import com.pdv.heli.processor.IncommingQueueProcessor;

public class MessageQueue {
	private static final String TAG = MessageQueue.class.getName();
	private static MessageQueue instance;
	private ArrayBlockingQueue<IMessage> goingOutMessageQueue = new ArrayBlockingQueue<>(
			100);
	private ArrayBlockingQueue<byte[]> inCommingBytesQueue = new ArrayBlockingQueue<>(
			100);	
	static {
		instance = new MessageQueue();
		
	}

	public static MessageQueue getInstance() {
		return instance;
	}

	

	

	private IncommingQueueProcessor deInCommingQueueThread;
	private GoingOutQueueProcessor deGoingOutQueueThread;
	
	

	public synchronized void startDeQueueTask() {
		if (deInCommingQueueThread == null || !deInCommingQueueThread.isAlive()) {
			deInCommingQueueThread = new  IncommingQueueProcessor();
			Log.v(TAG, "incomming thread starting...");
			deInCommingQueueThread.start();
		}
		if (deGoingOutQueueThread == null || !deGoingOutQueueThread.isAlive()) {
			deGoingOutQueueThread = new GoingOutQueueProcessor();
			Log.v(TAG, "goingout thread starting...");
			deGoingOutQueueThread.start();
		}
	}

	/**
	 * @deprecated
	 */	
	public synchronized void stopDeQueueTask() {		
		
		if (deGoingOutQueueThread != null && deGoingOutQueueThread.isAlive()) {			
			deGoingOutQueueThread.interrupt();
		}
		
		if (deInCommingQueueThread != null && deInCommingQueueThread.isAlive()) {
			deInCommingQueueThread.interrupt();
		}
		deGoingOutQueueThread = null;
		deInCommingQueueThread = null;
	}

	


	public boolean offerOutMessage(IMessage message, final Activity activity) {
		if (!TcpClientManager.getInstance().getConnectState()
				.equals(TcpClientManager.State.READY)) {
			if (activity != null) {
				Handler handler = new Handler(activity.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {						
						HeliSnackbars.showNetWorkNotConnect(activity);
					}
				});				
			}

			return false;
		}		
		goingOutMessageQueue.offer(message);
		return true;
	}
	public void offerToIncommingBytes(byte[] buffer) {
		inCommingBytesQueue.offer(buffer);
	}

	public ArrayBlockingQueue<byte[]> getInQueue(){
		return inCommingBytesQueue;
	}

	public ArrayBlockingQueue<IMessage> getOutQueue() {
		return goingOutMessageQueue;
	}

	

	
	

}
