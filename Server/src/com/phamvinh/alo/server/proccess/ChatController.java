package com.phamvinh.alo.server.proccess;

import com.pdv.heli.message.detail.ChatMessage;
import com.phamvinh.alo.server.base.BaseController;

public class ChatController extends BaseController{

	@Override
	protected boolean isLoginRequired() {		
		return true;
	}

	

}
