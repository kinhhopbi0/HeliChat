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
	private String passcode;

	public static class Status {
		public static final byte CONFIRM = 0x01;
		public static final byte SUCCESS = 0x02;
		public static final byte NOT_MATCHES = 0x03;
		public static final byte OTHER_ERROR = 0x04;
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
				this.passcode = new String(Arrays.copyOfRange(pData, index, PASSCODE_SIZE+index+1));
				break;
			case Status.SUCCESS:
				
				break;
			case Status.NOT_MATCHES:
				
				break;
			default:
				throw new MessageNotCorrectExeption("status " + status
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
						.toZeroEndBytes(passcode);
				data2Send = new byte[dataOfPasscode.length + 1];
				int index = 0;
				data2Send[index++] = status;
				System.arraycopy(dataOfPasscode, 0, data2Send, index,
						dataOfPasscode.length);
			} else {
				data2Send = new byte[status];
			}
			
			return data2Send;
		} catch (Exception ex) {
			throw new MessageNotCorrectExeption(ex);
		}
	}

	@Override
	public void processMessage() {
		// TODO Auto-generated method stub

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
		return passcode;
	}

	public void setPasscode(String pPasscode) {
		passcode = pPasscode;
	}

	@Override
	public String toString() {
		return "ConfirmPasscodeMsg [status=" + status + ", passcode="
				+ passcode + "]";
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte pStatus) {
		status = pStatus;
	}
	
	
}
