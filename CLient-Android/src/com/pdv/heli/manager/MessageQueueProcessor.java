package com.pdv.heli.manager;

import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;
import android.widget.Toast;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.TextMessage;
import com.pdv.heli.processmessage.MessageNavigation;

public class MessageQueueProcessor {
	private static final String TAG = MessageQueueProcessor.class.getName();
	private static MessageQueueProcessor instance;
	private ArrayBlockingQueue<byte[]> inCommingBytesQueue = new ArrayBlockingQueue<>(
			100);
	private ArrayBlockingQueue<IMessage> goingOutMessageQueue = new ArrayBlockingQueue<>(
			100);
	static {
		instance = new MessageQueueProcessor();
	}

	public static MessageQueueProcessor getInstance() {
		return instance;
	}

	public void offerToIncommingBytes(byte[] buffer) {
		inCommingBytesQueue.offer(buffer);

	}

	public void offerOutMessage(IMessage message) {
		goingOutMessageQueue.offer(message);
	}

	private Thread deInCommingQueueThread;
	private Thread deGoingOutQueueThread;

	public void startDeQueueTask() {
		if(deInCommingQueueThread == null || !deInCommingQueueThread.isAlive()){
			deInCommingQueueThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						doTakeIncommingBytesQueue();
					}
				}
			},"Dequeue incoming thread");
			Log.v(TAG, "incomming thread starting...");
			deInCommingQueueThread.start();
		}
		if(deGoingOutQueueThread == null || !deGoingOutQueueThread.isAlive()){
			deGoingOutQueueThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						doTakeGoingOutMessageQueue();
					}
				}
			},"dequeue goingout thread");
			Log.v(TAG, "goingout thread starting...");
			deGoingOutQueueThread.start();
		}		
	}

	public void stopDeQueueTask() {
		if (deGoingOutQueueThread != null && deGoingOutQueueThread.isAlive()) {
			deGoingOutQueueThread.interrupt();
		}
		if (deInCommingQueueThread != null && deInCommingQueueThread.isAlive()) {
			deInCommingQueueThread.interrupt();
		}
		deGoingOutQueueThread = null;
		deInCommingQueueThread = null;
	}

	protected void doTakeGoingOutMessageQueue() {
		try {
			IMessage message = goingOutMessageQueue.take();
			if(message != null){							
				byte[] dataSend = message.getBaseMessage().toSendBytes();
				TcpIOManager.getInstance().sendBytes(dataSend);	
			}
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (MessageNotCorrectExeption e) {
			Log.v(TAG, "encode error: "+e);
			Toast.makeText(HeliApplication.getInstance(), "encocode message fail", Toast.LENGTH_SHORT).show();
		}
	}

	protected void doTakeIncommingBytesQueue() {
		try {
			byte[] buffer = inCommingBytesQueue.take();
			if(buffer != null){
				MessageBase messageBase = new MessageBase(MessageMode.RECEIVE);
				messageBase.fromBytes(buffer);
				IMessage detail = messageBase.getDetailMessage();
				if(detail instanceof TextMessage){
					MessageNavigation.navigationTextMessage((TextMessage)detail);
					return;
				}
				if(detail instanceof SignUpMessage){
					MessageNavigation.navigationResponseSignUp((SignUpMessage)detail);
					return;
				}
				if(detail instanceof ConfirmPasscodeMsg){
					MessageNavigation.navigationConfirmMsg((ConfirmPasscodeMsg)detail);
					return;
				}
				
			}
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (MessageNotCorrectExeption e) {			
			Log.v(TAG, "decode error: "+e);
			Toast.makeText(HeliApplication.getInstance(), "decode message fail", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	

}
