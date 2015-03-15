package com.pdv.heli.controller;

import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.detail.LinearStringMessage;

public class RelationshipController {
	public void actionRequestFriend(IMessage message){
		LinearStringMessage response;
		try {
			response = new LinearStringMessage(message);
			response.fromBytes(((MessageBase)message).getDetailData());
			response.getParam("pn");			
			
		} catch (MessageNotCorrectExeption e) {
			
		}
		
	}
}
