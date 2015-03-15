package com.phamvinh.alo.server.proccess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.detail.ConfirmPasscodeMsg;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.SignUpMessage.FailType;
import com.phamvinh.alo.server.common.PasswordUtil;
import com.phamvinh.alo.server.common.RandomString;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.alo.server.db.DbAction;
import com.phamvinh.network.server.transport.Client;

public class ProcessSignUp {
	private static RandomString saltGeneratetor = new RandomString(6);
	private static Logger LOG = LogManager.getLogger();

	public static void ProcessSignUpStepOne(Client client, SignUpMessage msg) {
		if (msg.getStatus() == SignUpMessage.Status.CREATE_NEW) {
			LOG.info("Client {} request create new account: phone:{}, ",
					client, msg.getPhone());
			SignUpMessage response = new SignUpMessage(msg);
			switch (DbAction.getInstance().checkAccountExist(msg.getPhone())) {
			case 0:
				String salt = saltGeneratetor.nextString();
				String passcode = saltGeneratetor.nextPasscode();
				String encryptedPassword = PasswordUtil.encryptPassword(
						msg.getPassword(), salt);

				// Save sign up info in session
				client.getSessionExtras().put(SessionKey.SIGN_UP_PHONE,
						msg.getPhone());
				client.getSessionExtras().put(SessionKey.ENCRYPTED_PASSWORD,
						encryptedPassword);
				client.getSessionExtras().put(SessionKey.PASSWORD_SALT, salt);
				client.getSessionExtras().put(SessionKey.SIGN_UP_VERIFY_KEY,
						passcode);

				// make response
				response.setStatus(SignUpMessage.Status.CREATE_SUCCESS);
				client.getSessionExtras().put(SessionKey.SIGN_UP_PHONE,
						msg.getPhone());
				LOG.info("Phone {} sign up success step 1, save passcode:{}",
						msg.getPhone(), passcode);

				break;
			case -1:
				LOG.info("check account for phone {} fail", msg.getPhone());
				response.setStatus(SignUpMessage.Status.CREATE_FAIL);
				response.setFailType(FailType.OTHER);
				break;
			case 1:
				LOG.info("Phone number:{} is realdy exist", msg.getPhone());
				response.setStatus(SignUpMessage.Status.CREATE_FAIL);
				response.setFailType(FailType.PHONE_EXIST);
				break;
			}
			MessageIO.getInstance().sendMessage(response, client);
			return;
		}
		LOG.warn("Client {} send a undefined Account message status:{}",
				client, msg.getStatus());
	}

	public static void processConfirmPasscode(Client client,
			ConfirmPasscodeMsg detail) {
		if (detail.getStatus() == ConfirmPasscodeMsg.Status.CONFIRM) {
			String phone_number;
			if ((phone_number = (String) client.getSessionExtras().get(
					SessionKey.SIGN_UP_PHONE)) != null) {
				LOG.info("phone number {} request confirm passcode",
						phone_number);
				ConfirmPasscodeMsg response = new ConfirmPasscodeMsg();
				try {

					String savedCode = (String) client.getSessionExtras().get(
							SessionKey.SIGN_UP_VERIFY_KEY);

					if (savedCode.equals(detail.getPasscode())) {
						String salt = (String) client.getSessionExtras().get(
								SessionKey.PASSWORD_SALT);
						String password = (String) client.getSessionExtras()
								.get(SessionKey.ENCRYPTED_PASSWORD);

						switch (DbAction.getInstance().createAccount(password,
								phone_number, salt)) {
						case 0:
							client.getSessionExtras().clear();
							response.setStatus(ConfirmPasscodeMsg.Status.SUCCESS);
							LOG.info("Finish create new account for phone {}",
									phone_number);
							break;
						case -1:
							LOG.info("Fail create new account for phone {}",
									phone_number);
							response.setStatus(ConfirmPasscodeMsg.Status.OTHER_ERROR);
							break;

						case 1:
							LOG.info("Fail create new account for phone {}, account created before",
									phone_number);
							response.setStatus(ConfirmPasscodeMsg.Status.ACCOUNT_EXIST);
							break;
						}
					} else {
						LOG.info("Fail create new account for phone {}, verify code not matches",
								phone_number);
						response.setStatus(ConfirmPasscodeMsg.Status.NOT_MATCHES);
					}
				} catch (Exception ex) {
					LOG.error(ex);
					response.setStatus(ConfirmPasscodeMsg.Status.OTHER_ERROR);
				}
				MessageIO.getInstance().sendMessage(response, client);

			} else {
				LOG.warn("client not request sign up before confirm passcode");
			}
		} else {
			LOG.info("confirm passcode status {} not defined",
					detail.getStatus());
		}
	}
}
