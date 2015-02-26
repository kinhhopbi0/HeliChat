/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phamvinh.network.server.transport;

/**
 *
 * @author via
 */
public interface ServerNetworkInterface {
    public void onClientConnect(Client client);
    public void onClientClose(Object sender);
    public void onClientDisconnect(Object sender);
    public void onSendError(Object sender);
    void onReceiveFromClient(Object sender, byte[] buffer);
    public void onSent(Object sender);
}
