package com.pdv.heli.message.detail;

import java.util.Arrays;

import com.pdv.heli.message.base.AbstractMessage;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.MessageId;

public class ConfirmPasscodeMsg extends AbstractMessage {

	public static final byte MID = MessageId.CONFIRM_PASSCODE;
	private byte status = Status.CONFIRM;
	public static final int PASSCODE_SIZE = 6;
	private String verifyCode;

	public static class Status {
		public static final byte CONFIRM = 0x01;
		public static final byte SUCCESS = 0x02;
		public static final byte NOT_MATCHES = 0x03;
		public static final byte OTHER_ERROR = 0x04;
		public static final byte ACCOUNT_EXIST = 0x05;
	}

	public ConfirmPasscodeMsg(IMessage pMessage) {
		super(pMessage);
	}

	public ConfirmPasscodeMsg() {
		super();
	}

	@Override
	public void fromBytes(byte[] pData) throws MessageNotCorrectExeption {
		try{
			int index = 0;
			byte status = pData[index++];
			this.status = status;
			switch (status) {
			case Status.CONFIRM:
				byte[] dataOfPasscode = Arrays.copyOfRange(pData, index, PASSCODE_SIZE+index);
				this.verifyCode = new String(dataOfPasscode).trim();
				break;
			case Status.SUCCESS:
				
				break;
			case Status.NOT_MATCHES:
			case Status.ACCOUNT_EXIST:
			case Status.OTHER_ERROR:
				break;
			default:
				throw new MessageNotCorrectExeption(this.getClass()+" status " + status
						+ " no defined");

			}
		}catch(Exception ex){
			throw new MessageNotCorrectExeption(ex);
		}
	}

	
	@Override
	public byte[] toSendBytes() throws MessageNotCorrectExeption {
		try {
			byte[] data2Send = null;
			if (status == Status.CONFIRM) {
				byte[] dataOfPasscode = MessageBase.Util
						.toZeroEndBytes(verifyCode);
				data2Send = new byte[dataOfPasscode.length + 1];
				int index = 0;
				data2Send[index++] = status;
				System.arraycopy(dataOfPasscode, 0, data2Send, index,
						dataOfPasscode.length);
			} else {
				data2Send = new byte[]{status};
			}
			
			return data2Send;
		} catch (Exception ex) {
			throw new MessageNotCorrectExeption(ex);
		}
	}

	
	@Override
	public byte getMid() {
		return MID;
	}
	@Override
	public void setMid(byte pMid) {
		throw new UnsupportedOperationException();
	}

	public String getPasscode() {
		return verifyCode;
	}

	public void setPasscode(String pPasscode) throws Exception {
		if(pPasscode.length() != 6){
			throw new Exception("Passcode length must be 6");
		}
		verifyCode = pPasscode;
	}

	@Override
	public String toString() {
		return "ConfirmPasscodeMsg [status=" + status + ", passcode="
				+ verifyCode + "]";
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte pStatus) {
		status = pStatus;
	}
	
	
}
