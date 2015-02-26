/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phamvinh.network.server.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author via
 */
public class Client {

    private Socket socket;
    private Thread thread;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    private ConcurrentHashMap<String, Object> sessionExtras;

    private int bufferSize = 1024;

    private List<ServerNetworkInterface> callBacks;

    public Client(Socket client) {
        try {
            this.socket = client;
            callBacks = new ArrayList<>();
            sessionExtras = new ConcurrentHashMap<>();
            inputStream = client.getInputStream();
            outputStream = client.getOutputStream();
            thread = new Thread(new Runnable() {				
				@Override
				public void run() {
					receive();					
				}
			});
            thread.setName("Receive-" + client.getRemoteSocketAddress().toString());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startReceive() {
        thread.start();
    }

    private void receive() {
        while (true) {
            try {
                byte[] buffer;
                buffer = new byte[bufferSize];
                int read = inputStream.read(buffer);
                if (read > 0) {
                    byte[] realRead = new byte[read];
                    System.arraycopy(buffer, 0, realRead, 0, read);
                    receiveByte(realRead);
                } else {
                    for (ServerNetworkInterface listener : callBacks) {
                    	 listener.onClientClose(this);
					}
                    break;
                }
            } catch (IOException ex) {
            	for (ServerNetworkInterface listener : callBacks) {
               	 listener.onClientDisconnect(this);
				}
                break;
            }
        }

    }

    private void receiveByte(byte[] realRead) {
    	for (ServerNetworkInterface listener : callBacks) {
       	 listener.onReceiveFromClient(this,realRead);
		}

    }

    public void sendByte(byte[] buffer) {
        try {
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
        	for (ServerNetworkInterface listener : callBacks) {
           	 listener.onSendError(this);
			}
            ex.printStackTrace();
        }
    }


    public void addNetworkingListener(ServerNetworkInterface listener) {
        if (!callBacks.contains(listener)) {
            callBacks.add(listener);
        }
    }

    public void close() {
        try {
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
            inputStream.close();
            outputStream.close();
            socket.close();
            inputStream = null;
            outputStream = null;
            socket = null;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public String toString() {
        return this.socket.getRemoteSocketAddress().toString();
    }


	public ConcurrentHashMap<String, Object> getSessionExtras() {
		return sessionExtras;
	}
    
    
}
