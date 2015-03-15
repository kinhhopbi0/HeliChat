package com.phamvinh.alo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phamvinh.alo.server.proccess.MessageIO;
import com.phamvinh.network.server.transport.Client;
import com.phamvinh.network.server.transport.ServerNetworkInterface;

/**
 * Created by via on 2/4/15.
 */
public class ServerIOManager implements ServerNetworkInterface {
	private static final Logger LOG = LogManager.getLogger();
	private static final Logger ACCESS_LOG = LogManager.getLogger("Access");
	private static ServerIOManager ourInstance = new ServerIOManager();

	/**
	 * @return ServerManager
	 */
	public static ServerIOManager getInstance() {
		return ourInstance;
	}

	private ServerIOManager() {

	}

	@Override
	public void onClientConnect(Client client) {
		ACCESS_LOG.info("Client {} connected", client);
	}

	@Override
	public void onClientClose(Object sender) {
		ACCESS_LOG.info("Closed client {}", sender);
	}

	@Override
	public void onClientDisconnect(Object sender) {
		ACCESS_LOG.info("Client {} disconnected", sender);
	}

	@Override
	public void onSendError(Object sender) {

	}

	@Override
	public void onReceiveFromClient(Object sender, byte[] buffer) {
		LOG.info("receive {}bytes from {}", buffer.length, sender);
		try {
			Client client = (Client) sender;
			MessageIO.getInstance().readedDataBytes(client, buffer);
		} catch (Exception ex) {
			LOG.error("Recevice error: {}",ex);
			ex.printStackTrace();
		}
	}

	@Override
	public void onSent(Object sender) {
		LOG.info("Sent data to {} success ",sender);
	}
	
	
}
