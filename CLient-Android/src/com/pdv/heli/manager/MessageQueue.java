package com.pdv.heli.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pdv.heli.common.BytesUtil;
import com.pdv.heli.message.base.FrameConverter;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.FrameTemptObject;

public class MessageQueue {
	private static final String TAG = MessageQueue.class.getName();
	private static MessageQueue instance;
	private ArrayBlockingQueue<byte[]> inCommingBytesQueue = new ArrayBlockingQueue<>(
			100);
	private ArrayBlockingQueue<IMessage> goingOutMessageQueue = new ArrayBlockingQueue<>(
			100);
	static {
		instance = new MessageQueue();
	}

	public static MessageQueue getInstance() {
		return instance;
	}

	public void offerToIncommingBytes(byte[] buffer) {
		inCommingBytesQueue.offer(buffer);

	}

	public void offerOutMessage(IMessage message, final Activity activity) {
		if (!TcpClientManager.getInstance().getConnectState()
				.equals(TcpClientManager.State.READY)) {
			if (activity != null) {
				Handler handler = new Handler(activity.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, "not connect server",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			return;
		}
		goingOutMessageQueue.offer(message);
	}

	private Thread deInCommingQueueThread;
	private Thread deGoingOutQueueThread;
	private volatile boolean isInCommingRun = true;
	private volatile boolean isGoingOutgRun = true;

	public void startDeQueueTask() {
		if (deInCommingQueueThread == null || !deInCommingQueueThread.isAlive()) {
			deInCommingQueueThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (isInCommingRun) {
						doTakeIncommingBytesQueue();
					}
				}
			}, "Dequeue incoming thread");
			Log.v(TAG, "incomming thread starting...");
			deInCommingQueueThread.start();
		}
		if (deGoingOutQueueThread == null || !deGoingOutQueueThread.isAlive()) {
			deGoingOutQueueThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (isGoingOutgRun) {
						doTakeGoingOutMessageQueue();
					}
				}
			}, "dequeue goingout thread");
			Log.v(TAG, "goingout thread starting...");
			deGoingOutQueueThread.start();
		}
	}

	public void stopDeQueueTask() {
		isGoingOutgRun = false;
		if (deGoingOutQueueThread != null && deGoingOutQueueThread.isAlive()) {
			deGoingOutQueueThread.interrupt();
		}
		isInCommingRun = false;
		if (deInCommingQueueThread != null && deInCommingQueueThread.isAlive()) {
			deInCommingQueueThread.interrupt();
		}
		deGoingOutQueueThread = null;
		deInCommingQueueThread = null;
	}

	protected void doTakeGoingOutMessageQueue() {
		try {
			IMessage message = goingOutMessageQueue.take();
			if (message != null) {
				MessageBase base = (MessageBase) message.getBaseMessage();				
				byte[] dataSend = base.toSendBytes();
				Log.i(TAG, "send data: "
						+ BytesUtil.toDisplayString(dataSend));
				byte[] frame = FrameConverter.createFrame(dataSend);
				TcpClientManager.getInstance().sendBytes(frame);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MessageNotCorrectExeption e) {
			Log.v(TAG, "encode error: " + e);
		}
	}

	FrameTemptObject temptObject;

	protected void doTakeIncommingBytesQueue() {
		try {
			byte[] buffer = inCommingBytesQueue.take();
			if (buffer != null) {
				if (temptObject == null) {
					temptObject = new FrameTemptObject();
				}
				List<byte[]> messageData = FrameConverter.parseFrame(buffer,
						temptObject);

				for (int i = 0; i < messageData.size(); i++) {
					processInCommingByte(messageData.get(i));
				}

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MessageNotCorrectExeption e) {
			Log.v(TAG, "decode error: " + e);
		}
	}

	@SuppressWarnings("null")
	private void processInCommingByte(byte[] buffer)
			throws MessageNotCorrectExeption {
		Log.i(TAG, "receive data: "
				+ BytesUtil.toDisplayString(buffer));
		MessageBase messageBase = new MessageBase();
		messageBase.fromBytes(buffer);
		String controller = messageBase.getController();
		String action = messageBase.getAction();
		try {
			Class<?> controllerClass = Class.forName("com.pdv.heli.controller."
					+ controller + "Controller");
			Method method = controllerClass.getMethod("action" + action,
					IMessage.class);
			method.invoke(controllerClass.newInstance(), messageBase);
		} catch (ClassNotFoundException e) {

		} catch (NoSuchMethodException e) {

		} catch (IllegalAccessException e) {

		} catch (IllegalArgumentException e) {

		} catch (InvocationTargetException e) {

		} catch (InstantiationException e) {
		}

		Log.i(TAG,
				"receive undefined message with MID: " + messageBase.getMid());
	}

}
