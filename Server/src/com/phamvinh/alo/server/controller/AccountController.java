package com.phamvinh.alo.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.detail.LinearStringMessage;
import com.phamvinh.alo.server.base.BaseController;
import com.phamvinh.alo.server.common.RandomString;
import com.phamvinh.alo.server.model.Account;
import com.phamvinh.alo.server.proccess.MessageIO;

public final class AccountController extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	protected boolean isLoginRequired() {
		return false;
	}

	public void actionSignIn(IMessage iMessage) {
		try {
			LinearStringMessage detailMsg = new LinearStringMessage(iMessage);			
			detailMsg.fromBytes(((MessageBase) iMessage ).getDetailData());
			LinearStringMessage response = new LinearStringMessage(iMessage);
			if (detailMsg.containns("id")) {
				int id = Integer.parseInt(detailMsg.getParam("id"));
				String token = detailMsg.getParam("token");
				LOGGER.debug("Sign in with id:{} and token:{}", id, token);
				Account account = new Account();
				account.setUserID(id);
				int re = account.checkToken(token);
				
				switch (re) {
				case 0:
					LOGGER.info("token matches");
					String newToken = RandomString.getUUIDToken();					
					if (account.insertLoginInfo(newToken,
							clientSender.getRemoteIP(),
							clientSender.getRemotePort()) == 0) {
						LOGGER.info("Updated login info, new token {}",
								newToken);

						response.putParam("id", account.getUserID() + "");
						response.putParam("token", newToken);
					} else {
						response.putParam("error", "can't update");
					}
					break;
				case 1:
					response.putParam("error", "notMatches");
					LOGGER.debug("token not matches");
					break;
				case -1:
					response.putParam("error", "serverError");
					LOGGER.debug("server system error");
					break;
				}			
			} else {
				String phone = detailMsg.getParam("pn");
				String password = detailMsg.getParam("pwd");
				LOGGER.debug("Sign in with phone:{} and password:{}", phone,
						password);
				Account account = new Account();
				account.setPhoneNumber(phone);
				int rs = account.checkPassword(password);
				switch (rs) {
				case 0:
					LOGGER.info("token matches");
					String newToken = RandomString.getUUIDToken();					
					if (account.insertLoginInfo(newToken,
							clientSender.getRemoteIP(),
							clientSender.getRemotePort()) == 0) {
						LOGGER.info("Updated login info, new token {}",
								newToken);

						response.putParam("id", account.getUserID() + "");
						response.putParam("token", newToken);
					} else {
						response.putParam("error", "can't update");
					}
					break;					
				case 1:
					response.putParam("error", "passwordNotMatches");
					LOGGER.debug("token not matches");
					break;					
				default:
					response.putParam("error", "serverError");
					LOGGER.debug("server system error");
					break;
				}
			}
			MessageIO.getInstance().sendMessage(response, clientSender);
		} catch (MessageNotCorrectExeption e) {
			LOGGER.warn(e);
		}

	}

	public void actionSignUp(IMessage iMessage) {

	}

}
