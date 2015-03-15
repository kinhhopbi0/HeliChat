/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phamvinh.alo.server.proccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.base.FrameConverter;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.FrameTemptObject;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.network.server.transport.Client;

/**
 *
 * @author via
 */
public class MessageIO {
	private static MessageIO instance;
	private static Logger LOG = LogManager.getLogger();

	static {
		instance = new MessageIO();
	}

	private MessageIO() {

	}

	public void readedDataBytes(Client client, byte[] buffer) {
		FrameTemptObject temptObject = (FrameTemptObject) client
				.getSessionExtras().get(SessionKey.FRAME_TEMPT_DATA);
		if (temptObject == null) {
			temptObject = new FrameTemptObject();
		}
		List<byte[]> messageData = FrameConverter.parseFrame(buffer,
				temptObject);
		if (temptObject.buffer != null) {
			client.getSessionExtras().put(SessionKey.FRAME_TEMPT_DATA,
					temptObject);
		}
		for (int i = 0; i < messageData.size(); i++) {
			readeMessageBytes(client, messageData.get(i));
		}
	}

	private void readeMessageBytes(Client client, byte[] data) {
		LOG.debug("receive data : {} ",data);
		MessageBase messageBase = new MessageBase();
		messageBase.fromBytes(data);
		String controllerName = messageBase.getController();
		LOG.debug("Call controller {}, action ",controllerName,messageBase.getAction());
		try {
			Class<?> controllerClass = Class
					.forName("com.phamvinh.alo.server.controller."
							+ controllerName + "Controller");
			Method dispatchMethod = controllerClass.getMethod(
					"dispatchMessage", Client.class, IMessage.class);
			dispatchMethod.invoke(controllerClass.newInstance(), client,
					messageBase);

		} catch (ClassNotFoundException e) {
			LOG.error(e);
		} catch (NoSuchMethodException e) {
			LOG.error(e);
		} catch (SecurityException e) {
			LOG.error(e);
		} catch (IllegalAccessException e) {
			LOG.error(e);
		} catch (IllegalArgumentException e) {
			LOG.error(e);
		} catch (InvocationTargetException e) {
			LOG.error(e);
		} catch (Exception e) {

		}

		// MessageBase messageBase = new MessageBase();
		// messageBase.fromBytes(data);
		// LOG.debug("Receive MID={} detail data :{}", messageBase.getMid(),
		// messageBase.getDetailData());
		// messageBase.setSocketAddress(client.toString());
		// BaseController controller = null;
		// AbstractMessage detail;
		// try {
		// detail = messageBase.getDetailMessage();
		// } catch (MessageNotCorrectExeption e) {
		// LOG.warn("Message from {} error: {}", client.toString(), e);
		// return;
		// }
		//
		// if (detail instanceof SignUpMessage) {
		// ProcessSignUp.ProcessSignUpStepOne(client, (SignUpMessage) detail);
		// return;
		// }
		// if (detail instanceof ConfirmPasscodeMsg) {
		// ProcessSignUp.processConfirmPasscode(client,
		// (ConfirmPasscodeMsg) detail);
		// return;
		// }
		// if (detail instanceof SignInMessage) {
		// //controller = new SignInController();
		// }
		//
		//
		// if (detail instanceof ChatMessage) {
		// //controller = new ChatController();
		// }
		// if (detail instanceof SyncDeviceContactMessage) {
		// //controller = new SyncDeviceContactController();
		// }
		// if (detail instanceof LinearStringMessage) {
		// LinearStringMessage linearStringMessage = (LinearStringMessage)
		// detail;
		// String controllerName = null;
		// if((controllerName = (String)
		// linearStringMessage.getParam("controller"))!=null &&
		// controllerName.equals("relationship")){
		// // controller = new RelationshipController();
		// }
		// }
		//

		// controller.dispatchMessage(client, detail);
		// TODO SERVER update code after create new message server
	}

	public static MessageIO getInstance() {
		return instance;
	}

	public void sendMessage(IMessage response, Client client) {
		try {
			IMessage base = response.getBaseMessage();
			LOG.debug("Sending detail data :{}", 
					((MessageBase) base).toSendBytes());
			byte[] frame = FrameConverter.createFrame(base.toSendBytes());
			client.sendByte(frame);
		} catch (MessageNotCorrectExeption e) {
			LOG.error("create byte from message to send to {} error:{}",
					response.getSocketAddress(), e);
		}
	}
	
}
