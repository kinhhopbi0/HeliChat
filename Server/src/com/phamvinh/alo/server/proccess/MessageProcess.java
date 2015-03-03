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
import com.pdv.heli.message.detail.SignInMessage;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.TextMessage;
import com.phamvinh.network.server.transport.Client;

/**
 *
 * @author via
 */
public class MessageProcess {
	private static MessageProcess instance;
	private static Logger LOG = LogManager.getLogger();
	
	static {
		instance = new MessageProcess();
	}

	private MessageProcess() {
		
	}

	public void readMessageBytes(Client client, byte[] buffer) {
		MessageBase messageBase = new MessageBase(MessageMode.RECEIVE);
		messageBase.fromBytes(buffer);
		LOG.debug("Receive MID={} detail data :{}", messageBase.getMid(),
				messageBase.getData());
		messageBase.setSocketAddress(client.toString());
		IMessage detail;
		try {
			detail = messageBase.getDetailMessage();
		} catch (MessageNotCorrectExeption e) {
			LOG.warn("Message from {} error: {}", client.toString(), e);
			return;
		}
		
		if (detail instanceof SignUpMessage) {
			ProcessSignUp.ProcessSignUpStepOne(client, (SignUpMessage) detail);
			return;
		}
		if (detail instanceof ConfirmPasscodeMsg) {
			ProcessSignUp.processConfirmPasscode(client, (ConfirmPasscodeMsg) detail);
			return;
		}
		if (detail instanceof SignInMessage) {
			ProcessSignIn.processSignIn(client, (SignInMessage) detail);
			return;
		}
		if (detail instanceof TextMessage) {
			//processTextMessage(client, (TextMessage) detail);
			return;
		}
		// TODO  SERVER update code after create new message server
		LOG.warn("Client {} sent a undefied message", client);
	}

	

	

	public static MessageProcess getInstance() {
		return instance;
	}

	public void sendMessage(IMessage response, Client client) {
		try {
			IMessage base = response.getBaseMessage();
			LOG.debug("Sending MID={} detail data :{}", base.getMid(),
					((MessageBase) base).getData());
			client.sendByte(base.toSendBytes());			
		} catch (MessageNotCorrectExeption e) {
			LOG.error("create byte from message to send to {} error:{}",
					response.getSocketAddress(), e);
		}
	}
}
