package com.phamvinh.alo.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.detail.LinearStringMessage;
import com.phamvinh.alo.server.base.BaseController;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.alo.server.model.Account;
import com.phamvinh.alo.server.model.Relationship;
import com.phamvinh.alo.server.proccess.MessageIO;

public class RelationshipController extends BaseController {

	private static final Logger sLogger = LogManager.getLogger();

	@Override
	protected boolean isLoginRequired() {
		return true;
	}

	public void actionRequestFriend(IMessage iMessage) {
		MessageBase messageBase = (MessageBase) iMessage;
		LinearStringMessage request;
		try {
			request = new LinearStringMessage(iMessage);
			request.fromBytes(messageBase.getDetailData());
			String phone = request.getParam("pn");

			if (phone == null) {
				return;
			}
			LinearStringMessage response = new LinearStringMessage(iMessage);
			Account findedAccount = Account.findOneByPhoneNumber(phone);
			response.putParam("pn", phone);
			Relationship friendToYou = null;
			if (findedAccount == null || findedAccount.isLock()) {
				response.putParam("state", "notFound");
			} else {
				friendToYou = findedAccount
						.findRelationShipWith(getSignedAccount());
				Relationship youToFriend = getSignedAccount()
						.findRelationShipWith(findedAccount);
				if (friendToYou != null) {
					if (friendToYou.getId() == 3) {
						response.putParam("state", "notFound");
					} else {
						if(youToFriend != null){
							if(youToFriend.getId() == 3){
								response.putParam("state", "notFound");
							}else{
								response.putParam("state", "readlyFriend");
							}
						}else{
							if (getSignedAccount().requestFriend(findedAccount)) {
								response.putParam("state", "accepted");
							} else {
								response.putParam("state", "acceptFail");
							}
						}
					}
					
				}else{
					if(youToFriend!=null){
						if(youToFriend.getId()==3){
							response.putParam("state", "notFound");
						}else{
							response.putParam("state", "readlyRequest");
						}
					}else{
						if (getSignedAccount().requestFriend(findedAccount)) {
							response.putParam("state", "requested");
						} else {
							response.putParam("state", "requestFail");
						}
					}
				}
			}
			MessageIO.getInstance().sendMessage(response,
					clientSender);
			
		} catch (MessageNotCorrectExeption e) {
			sLogger.error(e);
		}

	}

	protected void processMessage(LinearStringMessage message) {
		sLogger.info(" processing request relationship of user {}",
				SESSION.get(SessionKey.SIGNED_USER_ID));
		String phone = message.getParam("findPhone");
		LinearStringMessage response = null;
		if (phone != null) {
			sLogger.info("Finding user have phone number {}", phone);
			Account findedAccount = Account.findOneByPhoneNumber(phone);
			response = new LinearStringMessage();
			response.putParam("controller", "relationship");
			response.putParam("action", "requestPhone");
			response.putParam("phone", phone);
			if (findedAccount != null && !findedAccount.isLock()) {
				Relationship relation1 = getSignedAccount()
						.findRelationShipWith(findedAccount);
				Relationship relationship2 = findedAccount
						.findRelationShipWith(getSignedAccount());

				if (relation1 == null && relationship2 == null) {

					if (getSignedAccount().requestFriend(findedAccount)) {
						response.putParam("state", "requested");
					} else {
						response.putParam("state", "requestFail");
					}
				} else {
					if (relationship2 != null && relationship2.getId() == 3) { // this
																				// user
																				// is
																				// block
						response.putParam("state", "notFound");
						MessageIO.getInstance().sendMessage(response,
								clientSender);
						return;
					}
					if (relation1 != null && relation1.getId() == 3) { // this
																		// user
																		// is
																		// block
						response.putParam("state", "notFound");
						MessageIO.getInstance().sendMessage(response,
								clientSender);
						return;
					}

					if (relation1 != null && relation1.getId() == 1
							&& relationship2 == null) {
						response.putParam("state", "requestBefore");
						MessageIO.getInstance().sendMessage(response,
								clientSender);
						return;
					} else {
						if (relation1 == null && relationship2 != null
								&& relationship2.getId() != 3) {
							// kết bạn thành công
							if (getSignedAccount().requestFriend(findedAccount)) {
								response.putParam("state", "accepted");

							} else {
								response.putParam("state", "acceptFail");
							}
							MessageIO.getInstance().sendMessage(response,
									clientSender);
							return;
						}
						response.putParam("state", "readyFriend");
						MessageIO.getInstance().sendMessage(response,
								clientSender);
						return;
					}

				}

			} else {
				response.putParam("state", "notFound");
			}
			MessageIO.getInstance().sendMessage(response, clientSender);
			return;
		}

		if (message.containns("acceptPhone")) {
			phone = message.getParam("acceptPhone");
			sLogger.info("User {} accept request friend for phone {}",
					getSignedAccount().getUserID(), phone);
			Account findedAccount = Account.findOneByPhoneNumber(phone);
			response = new LinearStringMessage();
			response.putParam("controller", "relationship");
			if (findedAccount != null && !findedAccount.isLock()) {
				response.putParam("exist", "true");
				if (getSignedAccount().requestFriend(findedAccount)) {
					response.putParam("accepted", "true");
				} else {
					response.putParam("accepted", "false");
				}
			} else {
				response.putParam("exist", "false");
			}
			MessageIO.getInstance().sendMessage(response, clientSender);
			return;
		}
	}

}
