package com.phamvinh.alo.server.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.base.IMessage;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.alo.server.model.Account;
import com.phamvinh.network.server.transport.Client;

public abstract class BaseController{
	protected Client clientSender;
	protected ConcurrentHashMap<String, Object> SESSION = null;
	protected abstract boolean isLoginRequired();
	private static final Logger LOGGER = LogManager.getLogger();

	final public void dispatchMessage(Client client,IMessage message){		
		clientSender = client;
		SESSION = client.getSessionExtras();
		if(isLoginRequired()){
			if(client.getSessionExtras().get(SessionKey.SIGNED_USER_ID)!=null){
				processMessage(message);
			}			
		}else{		
			processMessage(message);			
		}
	}	
		
	
	public void processMessage(IMessage message) {
		String actionName = message.getAction();
		try {
			Method mt = this.getClass().getMethod("action"+actionName, IMessage.class);
			mt.invoke(this, message);			
		} catch (NoSuchMethodException e) {
			LOGGER.warn(e);
		} catch (SecurityException e) {
			LOGGER.error(e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e);			
		} catch (IllegalArgumentException e) {
			LOGGER.error(e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e);
		}
	}

	public Account getSignedAccount(){
		Account account = Account.findOneById( (int)SESSION.get(SessionKey.SIGNED_USER_ID));
		return account;
	}
}
