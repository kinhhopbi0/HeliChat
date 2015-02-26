/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phamvinh.alo.server.proccess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.TextMessage;
import com.pdv.heli.message.detail.SignUpMessage.FailType;
import com.phamvinh.alo.server.ServerManager;
import com.phamvinh.alo.server.common.PasswordUtil;
import com.phamvinh.alo.server.common.RandomString;
import com.phamvinh.alo.server.db.DbAction;
import com.phamvinh.network.server.transport.Client;

/**
 *
 * @author via
 */
public class MessageProcess {
    private static MessageProcess instance;
    private static Logger LOG = LogManager.getLogger();
    private RandomString saltGeneratetor;
    static{
       instance = new MessageProcess();
    }
    private MessageProcess() {
    	saltGeneratetor = new RandomString(6);
    }
    public void processMessageBytes(Client client, byte[] buffer){
        MessageBase messageBase = new MessageBase(MessageMode.RECEIVE);
        messageBase.fromBytes(buffer);
        LOG.info("new message with MID:{}",messageBase.getMid());
        messageBase.setSocketAddress(client.toString());
        IMessage detail;
		try {
			detail = messageBase.getDetailMessage();
		} catch (MessageNotCorrectExeption e) {
			LOG.warn("Message from {} error: {}",client.toString(),e);
			return ;
		}        
        if (detail instanceof TextMessage){
            processTextMessage(client,(TextMessage) detail);
            return;
        }
        if(detail instanceof SignUpMessage){
        	processCreateAcountMessage(client, (SignUpMessage)detail);
        	return;
        }
        if(detail instanceof ConfirmPasscodeMsg){
        	processConfirmPasscode(client, (ConfirmPasscodeMsg)detail);
        	return;
        }
       LOG.warn("Client {} sent a undefied message",client);
    }

    

    
    private void processConfirmPasscode(Client client, ConfirmPasscodeMsg detail) {
		if(detail.getStatus() == ConfirmPasscodeMsg.Status.CONFIRM){
			String phone_number;
			if ( (phone_number = (String) client.getSessionExtras().get("phone_number")) != null ) {
				LOG.info("phone number {} request confirm passcode",phone_number);
				ConfirmPasscodeMsg response = new ConfirmPasscodeMsg();
				response.setSocketAddress(detail.getSocketAddress());
				
				switch (DbAction.getInstance().checkConfirmPasscode(phone_number,detail.getPasscode())) {
				case -1:
					response.setStatus(ConfirmPasscodeMsg.Status.OTHER_ERROR);
					break;
				case 0:
					response.setStatus(ConfirmPasscodeMsg.Status.NOT_MATCHES);
					break;
					
				case 1:
					response.setStatus(ConfirmPasscodeMsg.Status.SUCCESS);
					client.getSessionExtras().remove("phone_number");
					break;
				}
				ServerManager.getInstance().sendMessage(response); 
	    		
			}else{
				LOG.warn("client not request sign up before confirm passcode");
			}
		}else{
			LOG.info("confirm passcode status {} not defined",detail.getStatus());
		}
	}
	private void processTextMessage(Client client ,TextMessage textMessage){       

    }
    private void processCreateAcountMessage(Client client ,SignUpMessage msg){    	
    	if(msg.getStatus() == SignUpMessage.Status.CREATE_NEW){
    		LOG.info("Client {} request create new account: phone:{}, ",client,msg.getPhone());
    		SignUpMessage response = new SignUpMessage(msg.getSocketAddress(),MessageMode.SEND);    		
    		switch (DbAction.getInstance().checkAccountExist(msg.getPhone()) ) {
			case 0:
				String salt = saltGeneratetor.nextString();
				String passcode = saltGeneratetor.nextPasscode();
	    		String encryptedPassword = PasswordUtil.encryptPassword(msg.getPassword(), salt);    		
	    		if(DbAction.getInstance().createAccount(encryptedPassword, msg.getPhone(),salt,passcode)){
	    			response.setStatus(SignUpMessage.Status.CREATE_SUCCESS);
	    			client.getSessionExtras().put("phone_number", msg.getPhone());
	    		}else{
	    			response.setStatus(SignUpMessage.Status.CREATE_FAIL);
	    			response.setFailType(FailType.OTHER);
	    		}
				break;
			case 1:
				LOG.info("Client {}: username:{} is realdy exist",client,msg.getUsername());
				response.setStatus(SignUpMessage.Status.CREATE_FAIL);
    			response.setFailType(FailType.USERNAME_EXIST);
				break;				
			case 2:
				LOG.info("phone number:{} is realdy exist",msg.getPhone());
				response.setStatus(SignUpMessage.Status.CREATE_FAIL);
    			response.setFailType(FailType.PHONE_EXIST);
				break;						
			}   	
    		ServerManager.getInstance().sendMessage(response); 
    		return;
    	}
    	LOG.warn("Client {} send a undefined Account message status:{}",client,msg.getStatus());
    }
    
    
    public static MessageProcess getInstance() {    	
        return instance;
    }
}
