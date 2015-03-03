package com.pdv.transport.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

/**
 * Created by via on 2/4/15.
 */

public class TcpClient {
	private static final String TAG = TcpClient.class.getSimpleName();
	private Socket socket;
	private int connectTimeout = 2000;
	private int bufferSize = 1024;
	private OutputStream outputStream = null;
	private InputStream inputStream;
	private String serverHost;
	private int serverPort = 9090;
	private ClientNetworkingInterface callBack;
	private Thread connectThread;
	private Thread receiveThread;
	private volatile boolean flagReceive = true;

	public TcpClient() {
		connectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				connectFn();
			}
		});
	}

	public void start() {
		connectThread.start();
	}

	public boolean isConnect() {
		if (socket != null) {
			if (socket.isConnected() && !socket.isClosed()) {
				// socket connecting
				return true;
			}
		}
		return false;
	}

	private boolean connectFn() {
		Log.v(TAG, "Connecting to server...");
		if (callBack != null)
			callBack.onConnecting(TcpClient.this);
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			socket = null;
		}
		// System.setProperty("javax.net.ssl.trustStore","clientTrustStore.key");
		// System.setProperty("javax.net.ssl.trustStorePassword","qwerty");
		// SSLSocketFactory ssf = (SSLSocketFactory)
		// SSLSocketFactory.getDefault();

		socket = new Socket();
		try {
			// socket = ssf.createSocket(serverHost, serverPort);
			socket.connect(new InetSocketAddress(this.serverHost,
					this.serverPort), connectTimeout);

			Log.v(TAG, "Connected success to server");
			if (callBack != null)
				callBack.onConnectSuccess(TcpClient.this);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			receiveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (flagReceive) {
						receiveFn();
					}
				}
			});
			receiveThread.start();
			return true;
		} catch (IOException e) {
			Log.v(TAG, "connect fail");
			if (callBack != null) {
				callBack.onConnectFail(TcpClient.this, e);
			}
			e.printStackTrace();

		}
		return false;
	}

	private void receiveFn() {		
		byte[] buffer;
		try {
			buffer = new byte[bufferSize];
			int readSize = inputStream.read(buffer);
			if (readSize > 0) {
				byte[] realRead = new byte[readSize];
				System.arraycopy(buffer, 0, realRead, 0, readSize);				
				if (callBack != null) {
					callBack.onReceiveBytes(TcpClient.this, realRead);
				}
			} else {
				Log.v(TAG,
						"0 byte data from server "
								+ socket.getRemoteSocketAddress());
				flagReceive = false;
				if (callBack != null) {
					callBack.onServerCloseConnection(TcpClient.this);
				}				
			}
		} catch (IOException ex) {
			Log.v(TAG, "disconnect from server:" + socket.getRemoteSocketAddress());
			flagReceive = false;
			if (callBack != null) {
				callBack.onDisconnect(TcpClient.this);
			}			
		}

	}

	public void stop() {
		Log.v(TAG, "release");
		try {
			flagReceive = false;
			if(inputStream !=null ){				
				inputStream.close();
				inputStream = null;
			}
			if(outputStream !=null){
				outputStream.close();
				outputStream = null;
			}			
			
			if(connectThread != null && connectThread.isAlive()){
				connectThread.interrupt();
			}
			if(receiveThread != null && receiveThread.isAlive()){
				receiveThread.interrupt();
			}
			if(socket != null){
				socket.shutdownOutput();
				socket = null;
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setNetworkingEventListener(ClientNetworkingInterface pCallBack) {
		callBack = pCallBack;
	}

	public void sendAsync(final byte[] buff) {
		if(buff==null){
			return;
		}
		Thread sendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (callBack != null) {
						callBack.onSending(TcpClient.this);
					}
					outputStream.write(buff);
					outputStream.flush();

					if (callBack != null) {
						callBack.onSentSuccess(TcpClient.this);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (callBack != null) {
						callBack.onSentFail(TcpClient.this);
					}
				}

			}
		}, "Send thread");
		sendThread.start();
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
}
