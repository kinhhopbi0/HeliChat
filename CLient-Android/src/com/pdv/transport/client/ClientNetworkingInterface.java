package com.pdv.transport.client;

/**
 * Created by vinhanh on 11/01/2015.
 */
public interface ClientNetworkingInterface {
    void onReceiveBytes(Object sender, byte[] buffer);
    void onServerCloseConnection(Object sender);
    void onDisconnect(Object sender);
    void onConnectSuccess(Object sender);
    void onConnectFail(Object sender);
    void onConnecting(Object sender);
    void onSending(Object sender);
    void onSentSuccess(Object sender);
    void onSentFail(Object sender);
}
