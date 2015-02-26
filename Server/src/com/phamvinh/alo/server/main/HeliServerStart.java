/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phamvinh.alo.server.main;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phamvinh.alo.server.ServerIOManager;
import com.phamvinh.alo.server.constant.ServerInfo;
import com.phamvinh.network.server.transport.SSLTcpServer;

/**
 * 
 * @author via
 */
public class HeliServerStart {
	private static HeliServerStart instance = null;
	private Logger LOG = null;

	private SSLTcpServer serverTcp;
	static {
		instance = new HeliServerStart();
	}

	public static HeliServerStart getInstance() {
		return instance;
	}
	private Logger getLogger(String name){			
		System.setProperty("log4j.configurationFile", "res/log4j2.xml");
		Logger logger = LogManager.getLogger(name) ;			
		return logger;
	}

	public static void main(String[] args) {    	
       instance.startModule(args);       
       Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		
		@Override
		public void run() {			
			instance.stop();
			instance.LOG.info("Server stop, goodbye");
		}
	},"Hook"));
       
    }

	private void startModule(String[] args) {
		try {
			LOG = getLogger("AloServer");
			LOG.info("Server is starting ...");
			//System.setProperty("javax.net.ssl.keyStore","res/clientKeyStore.key");
	    	//System.setProperty("javax.net.ssl.keyStorePassword","123456");
			serverTcp = new SSLTcpServer(ServerInfo.SERVER_PORT_TCP, ServerInfo.MAX_CLIENT);
			serverTcp.setNetworkInterfaceCallBack(ServerIOManager.getInstance());
			serverTcp.startServer();			
			LOG.info("Server listening on: {} ",serverTcp.getServerSocket().getLocalSocketAddress());					
			
		} catch (IOException ex) {
			LOG.error(ex.toString());
			System.exit(1);
		} catch (Exception e) {
			LOG.error(e.toString());
			System.exit(1);
		}
	}

	private void stop() {
		try {
			serverTcp.stopListen();			
		} catch (IOException ex) {
			LOG.error(ex.toString());
		}

	}

	public SSLTcpServer getServerTcp() {
		return serverTcp;
	}

}
