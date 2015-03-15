package com.phamvinh.alo.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.detail.SyncDeviceContactMessage;
import com.phamvinh.alo.server.base.BaseController;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.alo.server.db.DbAction;
import com.phamvinh.alo.server.proccess.MessageIO;

public class SyncDeviceContactController extends
		BaseController {
	private static Logger LOG = LogManager.getLogger();

	@Override
	protected boolean isLoginRequired() {
		return true;
	}

	
	protected void processMessage(SyncDeviceContactMessage message) {
		SyncDeviceContactMessage response = null;
		int uid = (int) SESSION.get(SessionKey.SIGNED_USER_ID);
		switch (message.getType()) {
		case SyncDeviceContactMessage.Type.CLIENT_GET_CONTACT:
			LOG.info("client {} get contacts", uid);
			
			
			break;
		case SyncDeviceContactMessage.Type.CLIENT_POST_CONTACT:			
			LOG.info("client {} post update contact list", uid);
			int rs = DbAction.getInstance().insertContact(uid,
					message.getPhones(), message.getNames());
			response = new SyncDeviceContactMessage();
			if (rs == 0) {
				
				response.setType(SyncDeviceContactMessage.Type.SERVER_UPDATE_SUCCESS);
				LOG.info("update contact success");
			}
			if (rs == -1) {
				response.setType(SyncDeviceContactMessage.Type.SERVER_UPDATE_FAIL);
				LOG.info("update contact fail");
			}
			MessageIO.getInstance().sendMessage(response, clientSender);
			break;
		default:
			break;
		}
	}

}
