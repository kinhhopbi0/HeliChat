/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phamvinh.network.server.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author via
 */
public class SSLTcpServer implements ServerNetworkInterface {

    private ServerSocket serverSocket;

    private int listenPort;
    private String bindAddress;
    private Thread listenThread;
    private ConcurrentHashMap<String, Client> clients;
    private ServerNetworkInterface networkInterfaceCallBack;
    
    public SSLTcpServer(int port,int numberClientMax) {
        this.listenPort = port;
        clients = new ConcurrentHashMap<>(numberClientMax);
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SSLTcpServer.this.listen();
            }
        }, "Tcp-server-listen-thread");

    }
    public void startServer() throws IOException {        	
    	//SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    	//serverSocket = ssf.createServerSocket(listenPort);    
    	serverSocket = new ServerSocket(listenPort);
        listenThread.start();
    }

    private void listen() {       
        while (true) {
            try {
                Socket client = serverSocket.accept();               
                Client receiveClient = new Client(client);
                onClientConnect(receiveClient);
                receiveClient.addNetworkingListener(this);
                clients.put(client.getRemoteSocketAddress().toString(), receiveClient);
                receiveClient.startReceive();
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
        }
    }


    public void closeClientFromAddress(String addressStrinng) {
        System.out.println("Closing client " + addressStrinng);
        Client client;
        if ((client = clients.remove(addressStrinng)) != null) {
            client.close();
        }
    }

    public void closeAllClientConnection() {
        for (Map.Entry<String, Client> entry : clients.entrySet()) {
           
            Client receiveClient = entry.getValue();
            receiveClient.close();
        }
        clients.clear();
    }

    public void stopListen() throws IOException {
        closeAllClientConnection();
        if (listenThread != null) {
            listenThread.interrupt();
            listenThread = null;
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    @Override
    public void onClientClose(Object sender) {
        if (sender instanceof Client) {
            Client client = (Client) sender;
            if(networkInterfaceCallBack != null){
                networkInterfaceCallBack.onClientClose(sender);
            }
            this.closeClientFromAddress(client.getSocket().getRemoteSocketAddress().toString());
        }

    }

    @Override
    public void onClientDisconnect(Object sender) {
        if (sender instanceof Client) {
            Client client = (Client) sender;
            if(networkInterfaceCallBack != null){
                networkInterfaceCallBack.onClientDisconnect(sender);
            }
            this.closeClientFromAddress(client.getSocket().getRemoteSocketAddress().toString());
        }
    }

    @Override
    public void onSendError(Object sender) {
        if(networkInterfaceCallBack != null){
            networkInterfaceCallBack.onSendError(sender);
        }
    }

    @Override
    public void onClientConnect(Client client) {
        if(networkInterfaceCallBack != null){
            networkInterfaceCallBack.onClientConnect(client);
        }
    }

    public void sendBytes(byte[] toSendBytes, String remoteAddr) {
        Client client = clients.get(remoteAddr);
        if(client != null){
            client.sendByte(toSendBytes);
        }
    }
    @Override
    public void onReceiveFromClient(Object sender, byte[] buffer) {
        if(networkInterfaceCallBack != null){
            networkInterfaceCallBack.onReceiveFromClient(sender, buffer);
        }
    }

    @Override
    public void onSent(Object sender) {
        if(networkInterfaceCallBack != null){
            networkInterfaceCallBack.onSent(sender);
        }
    }

    public void setNetworkInterfaceCallBack(ServerNetworkInterface networkInterfaceCallBack) {
        this.networkInterfaceCallBack = networkInterfaceCallBack;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public int getListenPort() {
        return listenPort;
    }

    public Thread getListenThread() {
        return listenThread;
    }

    public ConcurrentHashMap<String, Client> getClients() {
        return clients;
    }

    public String getBindAddress() {
        return bindAddress;
    }
}
