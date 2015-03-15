package com.phamvinh.alo.server.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.detail.LinearStringMessage;
import com.phamvinh.alo.server.base.BaseController;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.alo.server.main.HeliServerStart;
import com.phamvinh.alo.server.model.Account;
import com.phamvinh.alo.server.proccess.MessageIO;
import com.phamvinh.network.server.transport.Client;

public class UserInfoController extends
		BaseController {
	private static final Logger LOG = LogManager.getLogger();

	@Override
	protected boolean isLoginRequired() {		
		return true;
	}

	
	protected void processMessage1(LinearStringMessage message) {
		String actionName = message.getParam("action");
		try {			
			Method actionMt = UserInfoController.class.getMethod("action"+actionName,LinearStringMessage.class);
			actionMt.invoke(this, message);
			return;
		} catch (NoSuchMethodException e) {			
			e.printStackTrace();
		} catch (SecurityException e) {			
			e.printStackTrace();
		} catch (IllegalAccessException e) {			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {			
			e.printStackTrace();
		} catch (InvocationTargetException e) {			
			e.printStackTrace();
		}
		
		 
		LOG.info("Process update info message");
		if (message.getParam("displayName") != null) {
			String newDisplayname = message.getParam("displayName");
			LOG.debug("user {} update name {}",
					SESSION.get(SessionKey.SIGNED_USER_ID), newDisplayname);
		}
		if (message.getParam("getFriend").equals("0")) {			
			ConcurrentHashMap<String, Client> clients = HeliServerStart
					.getInstance().getServerTcp().getClients();
			Iterator<Entry<String, Client>> itt = clients.entrySet().iterator();
			while (itt.hasNext()) {
				Entry<String, Client> entry = itt.next();
				Client client = entry.getValue();
				if(client.toString().equals(clientSender.toString())){
					continue;
				}
				String userid;
				if(client.getSessionExtras().containsKey(SessionKey.SIGNED_USER_ID)){
					userid  = client.getSessionExtras().get(SessionKey.SIGNED_USER_ID) + "";
					LinearStringMessage infoMessage = new LinearStringMessage();
					infoMessage.putParam("friendId", userid);
					MessageIO.getInstance().sendMessage(infoMessage,this.clientSender);
				}
			}
		}
		if(message.getParam("receiverId")!=null){		
			ConcurrentHashMap<String, Client> clients = HeliServerStart
					.getInstance().getServerTcp().getClients();
			Iterator<Entry<String, Client>> itt = clients.entrySet().iterator();
			while (itt.hasNext()) {
				Entry<String, Client> entry = itt.next();
				Client client = entry.getValue();
				if(client.toString().equals(clientSender.toString())){
					continue;
				}
				String userid;
				if(client.getSessionExtras().containsKey(SessionKey.SIGNED_USER_ID)){
					userid  = client.getSessionExtras().get(SessionKey.SIGNED_USER_ID) + "";
					if(userid.equals(message.getParam("recvierId"))){
						LinearStringMessage infoMessage = new LinearStringMessage();
						infoMessage.putParam("senderId", SESSION.get(SessionKey.SIGNED_USER_ID)+"");
						infoMessage.putParam("textContent", message.getParam("textContent"));
						MessageIO.getInstance().sendMessage(infoMessage,client);
						break;
					}
					
				}
			}
		}
	}
	public void actionGetFriendInfo(LinearStringMessage linearStringMessage){
		String phone = linearStringMessage.getParam("phone");
		String id = linearStringMessage.getParam("id");
		Account friend = null;
		if(id!=null){
			try{
				friend = Account.findOneById(Integer.parseInt(id));
			}catch(NumberFormatException ex){
				
			}
		}else{
			friend = Account.findOneByPhoneNumber(phone);
		}
		
	}

}
