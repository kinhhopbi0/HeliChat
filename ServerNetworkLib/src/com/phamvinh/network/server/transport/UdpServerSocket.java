package com.phamvinh.network.server.transport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServerSocket {
	DatagramSocket serverSocket;
	Thread receiveThread ;
	private CallBack mCallBack;
	public synchronized void start(int port){
		try {
			serverSocket = new DatagramSocket(8086);			
			serverSocket.setReceiveBufferSize(50000);
			serverSocket.setSendBufferSize(50000);
			receiveThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					receive();					
				}
			});
			receiveThread.start();
			
		} catch (SocketException e) {			
			e.printStackTrace();
		}
		
	}
	public void send(DatagramPacket datagramPacket){
		if(mCallBack!=null){
			mCallBack.onBeforeSend(datagramPacket);
		}
		try {
			serverSocket.send(datagramPacket);
			if(mCallBack!=null){
				mCallBack.onBeforeSend(datagramPacket);
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		
	}
	protected void receive() {		
		while (!receiveThread.isInterrupted()) {
			byte[] buffer = new byte[50000];
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			try {
				serverSocket.receive(datagramPacket);
				if(mCallBack!=null){
					mCallBack.onReceivePacket(datagramPacket);
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
	}
	public void setCallBack(CallBack back){
		this.mCallBack = back;
	}
	public interface CallBack{
		public void onReceivePacket(DatagramPacket datagramPacket);
		public void onBeforeSend(DatagramPacket datagramPacket);
		public void onSent(DatagramPacket datagramPacket);
	}
	
	
}
