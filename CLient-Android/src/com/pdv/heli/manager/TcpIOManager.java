package com.pdv.heli.manager;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.common.android.InternetCheck;
import com.pdv.heli.constant.ServerInfo;
import com.pdv.transport.client.ClientNetworkingInterface;
import com.pdv.transport.client.TcpClient;

/**
 * Created by via on 2/3/15.
 */
public class TcpIOManager implements ClientNetworkingInterface {
    private static final  String TAG = TcpIOManager.class.getSimpleName();     
    private Context context = HeliApplication.getInstance();
    private TcpClient tcpClient;
    private static TcpIOManager instance;
    private State connectState;
    static{
    	instance = new TcpIOManager();
    }
    
    private TcpIOManager() {
    	
    }    
    
    
    public synchronized void init() {
        tcpClient = new TcpClient();
        tcpClient.setServerHost(ServerInfo.HOST_DEV_GENYMOTION);
        tcpClient.setServerPort(ServerInfo.TCP_PORT);
        connectState = State.NEW;
    }

    /**
     * Phuong thuc khoi dong toan bo cac ket noi can thiet neu ket noi do chua ket noi
     */
    public synchronized void startConnectAndReceive() {
        if (!InternetCheck.getInstance().hasActiveConnected()) {
        	Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();            
            return;
        } else {
           init();
           tcpClient.setNetworkingListener(this);
           tcpClient.start();
        }
    }
    public synchronized void close(){    	
    	if(connectState != State.NEW){
    		connectState = State.NOTCONNECT;
        	tcpClient.release();
        	tcpClient = null;
    	}
    }
    @Override
    public void onServerCloseConnection(Object sender) {
        Log.v(TAG, "Connect close");
        connectState = State.NOTCONNECT;       
    }

    @Override
    public void onDisconnect(Object sender) {
        HeliApplication.getInstance().showToastFromOtherThread("Disconnect");
        connectState = State.NOTCONNECT;
    }

    @Override
    public void onConnectSuccess(Object sender) {
        Log.v(TAG,"Connect success");
        connectState = State.READY;
        MessageQueueProcessor.getInstance().startDeQueueTask();
    }

    @Override
    public void onConnectFail(Object sender) {
        Log.v(TAG, "Connect fail");
        HeliApplication.getInstance().showToastFromOtherThread("Fail connect");
        connectState = State.NOTCONNECT;
    }

    @Override
    public void onConnecting(Object sender) {
        Log.v(TAG,"Connect ting");
        connectState = State.CONNECTING;
    }

    @Override
    public void onSending(Object sender) {
        Log.v(TAG,"sending");
    }

    @Override
    public void onSentSuccess(Object sender) {
        Log.v(TAG,"sent success");
    }

    @Override
    public void onSentFail(Object sender) {
    	Log.i(TAG,"sent fail");
    }

    @Override
    public void onReceiveBytes(Object sender, byte[] buffer) {
       MessageQueueProcessor.getInstance().offerToIncommingBytes(buffer);
    }
    public void sendBytes(byte[] buffer) {
    	tcpClient.sendAsync(buffer);
    }  
    
    public enum State{
    	NEW,
    	CONNECTING,
    	READY,
    	NOTCONNECT,    	
    }
    
    public static TcpIOManager getInstance(){
    	return instance;
    }


	/**
	 * @return the connectState
	 */
	public State getConnectState() {
		return connectState;
	}
    
    
}
