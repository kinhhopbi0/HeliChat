/**
 * 
 */
package com.pdv.heli.message.detail;

import java.util.Arrays;

import com.pdv.heli.message.base.AbstractMessage;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.MessageId;
import com.pdv.heli.message.common.MessageMode;

/**
 * @author via
 *
 */
public final class SignUpMessage extends AbstractMessage {

	private byte status = Status.CREATE_NEW;
	private byte failType;
	private String username = "";
	private String password = "";
	private String phone = "";

	private final byte mMid = MessageId.SIGN_UP_MID;
	public static final int USERNAME_MAX_SIZE = 50;
	public static final int PASSWORD_MAX_SIZE = 32;
	public static final int PHONE_MAX_SIZE = 20;

	public static class Status {
		public static final byte CREATE_NEW = 0x01;
		public static final byte CREATE_SUCCESS = 0x02;
		public static final byte CREATE_FAIL = 0x03;
	}

	public static class FailType {
		public static final byte USERNAME_EXIST = 0x01;
		public static final byte PHONE_EXIST = 0x02;
		public static final byte OTHER = 0x03;
	}

	public SignUpMessage(MessageMode pMessageMode) {
		super(pMessageMode);
	}

	public SignUpMessage(String pSocketAddress, MessageMode pMessageMode) {
		super(pSocketAddress, pMessageMode);

	}

	public SignUpMessage(IMessage pMessage) {
		super(pMessage);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phamvinh.alo.message.base.IMessage#fromBytes(byte[])
	 */
	@Override
	public void fromBytes(byte[] pData) throws MessageNotCorrectExeption {
		try {
			int index = 0;
			this.status = pData[index];
			switch (status) {
			case Status.CREATE_NEW:

				break;
			case Status.CREATE_SUCCESS:
				return;
			case Status.CREATE_FAIL:
				this.failType = pData[++index];
				return;

			default:
				throw new MessageNotCorrectExeption(
						"message status not defined");
			}
			index++;
			byte[] dataUsername;
			byte[] dataPassword;
			byte[] dataPhone;

			for (int i = index; i < pData.length; i++) {
				if (i == (index + USERNAME_MAX_SIZE)) {
					throw new MessageNotCorrectExeption("username too long");
				}
				if (pData[i] == 0) {
					dataUsername = Arrays.copyOfRange(pData, index, i);
					this.username = new String(dataUsername).trim();
					index = i + 1;
					break;
				}

			}
			for (int i = index; i < pData.length; i++) {
				if (i == (index + PASSWORD_MAX_SIZE)) {
					throw new MessageNotCorrectExeption(
							"bytes of password too long than "
									+ PASSWORD_MAX_SIZE);
				}
				if (pData[i] == 0) {
					dataPassword = Arrays.copyOfRange(pData, index, i);
					this.password = new String(dataPassword).trim();
					index = i + 1;
					break;
				}
			}
			for (int i = index; i < pData.length; i++) {
				if (i == (index + PHONE_MAX_SIZE)) {
					throw new MessageNotCorrectExeption(
							"bytes of password too long than " + PHONE_MAX_SIZE);
				}
				if (pData[i] == 0) {
					dataPhone = Arrays.copyOfRange(pData, index, i);
					this.phone = new String(dataPhone).trim();
					break;
				}
			}

		} catch (Exception exception) {
			throw new MessageNotCorrectExeption(exception);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phamvinh.alo.message.base.IMessage#toSendBytes()
	 */
	@Override
	public byte[] toSendBytes() throws MessageNotCorrectExeption {
		try {
			byte[] result = null;
			switch (status) {

			case Status.CREATE_NEW:
				String username = this.username.substring(0,
						Math.min(this.username.length(), USERNAME_MAX_SIZE));
				byte[] dataOfUsername = MessageBase.Util
						.toZeroEndBytes(username);
				String password = this.password.substring(0,
						Math.min(this.password.length(), PASSWORD_MAX_SIZE));
				byte[] dataOfPassword = MessageBase.Util
						.toZeroEndBytes(password);
				String phone = this.phone.substring(0,
						Math.min(PHONE_MAX_SIZE, this.phone.length()));
				byte[] dataOfPhone = MessageBase.Util.toZeroEndBytes(phone);
				result = new byte[dataOfPhone.length + dataOfPassword.length
						+ dataOfUsername.length + 1];
				result[0] = Status.CREATE_NEW;
				int index = 1;
				System.arraycopy(dataOfUsername, 0, result, index,
						dataOfUsername.length);
				System.arraycopy(dataOfPassword, 0, result,
						index += dataOfUsername.length, dataOfPassword.length);
				System.arraycopy(dataOfPhone, 0, result,
						index += dataOfPassword.length, dataOfPhone.length);
				break;
			case Status.CREATE_SUCCESS:
				result = new byte[] { Status.CREATE_SUCCESS };
				break;
			case Status.CREATE_FAIL:
				result = new byte[] { Status.CREATE_FAIL, failType };
				break;
			default:
				throw new MessageNotCorrectExeption(
						"message status not defined");
			}
			return result;
		} catch (Exception ex) {
			throw new MessageNotCorrectExeption(ex);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phamvinh.alo.message.base.IMessage#processMessage()
	 */
	@Override
	public void processMessage() {
		// TODO Auto-generated method stub

	}

	public static void demo(int in) {
		System.out.println(in);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param pUsername
	 *            the username to set
	 */
	public void setUsername(String pUsername) {
		username = pUsername;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param pPassword
	 *            the password to set
	 */
	public void setPassword(String pPassword) {
		password = pPassword;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param pPhone
	 *            the phone to set
	 */
	public void setPhone(String pPhone) {
		phone = pPhone;
	}

	/**
	 * @return the mid
	 */
	public byte getMid() {
		return mMid;
	}

	/**
	 * @return the status
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * @param pStatus
	 *            the status to set
	 */
	public void setStatus(byte pStatus) {
		status = pStatus;
	}

	/**
	 * @return the failType
	 */
	public byte getFailType() {
		return failType;
	}

	/**
	 * @param pFailType
	 *            the failType to set
	 */
	public void setFailType(byte pFailType) {
		failType = pFailType;
	}

}
