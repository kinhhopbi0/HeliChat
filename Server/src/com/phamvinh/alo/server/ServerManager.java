package com.phamvinh.alo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.phamvinh.alo.server.main.HeliServerStart;
import com.phamvinh.alo.server.proccess.MessageProcess;
import com.phamvinh.network.server.transport.Client;
import com.phamvinh.network.server.transport.ServerNetworkInterface;

/**
 * Created by via on 2/4/15.
 */
public class ServerManager implements ServerNetworkInterface {
	private static final Logger LOG = LogManager.getLogger();
	private static final Logger ACCESS_LOG = LogManager.getLogger("Access");
	private static ServerManager ourInstance = new ServerManager();

	/**
	 * @return ServerManager
	 */
	public static ServerManager getInstance() {
		return ourInstance;
	}

	private ServerManager() {

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
			MessageProcess.getInstance().processMessageBytes(client, buffer);
		} catch (Exception ex) {
			LOG.error("Recevice error: {}",ex);
			ex.printStackTrace();
		}
	}

	@Override
	public void onSent(Object sender) {
		LOG.info("Sent data to {} success ",sender);
	}

	public void sendMessage(IMessage response) {
		try {
			HeliServerStart.getInstance().getServerTcp()
					.sendBytes(response.getBaseMessage().toSendBytes(),
							response.getSocketAddress());
		} catch (MessageNotCorrectExeption e) {
			LOG.error("create byte from message to send to {} error:{}",
					response.getSocketAddress(), e);
		}
	}
}
