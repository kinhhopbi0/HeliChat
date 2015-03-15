package com.pdv.heli.message.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by via on 2/3/15.
 */
public class MessageBase extends AbstractMessage {

	private int mCRC;
	private byte[] mDetailData;

	public MessageBase(String socketAddress) {
		this.socketAddress = socketAddress;
	}

	public MessageBase(IMessage clone) {
		super(clone);
	}

	public MessageBase(String socketAddress, byte mMid, byte mMType,
			byte[] mData) {
		this.mMid = mMid;
		this.mDetailData = mData;
	}	

	public MessageBase() {
		
	}

	@Override
	public void fromBytes(byte[] data) {
		this.mMid = data[0];
		if(this.mMid == 0x00){
			this.mDetailData = Arrays.copyOfRange(data, 1, data.length);
		}else{
			String controllerName = null;
			String actionName = null;
			byte[] after = Arrays.copyOfRange(data, 1, data.length);
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(after);		
			int pos = 1;
			try {
				int bytez;				
				while( (bytez = arrayInputStream.read()) >= 0){
					pos++;
					if(bytez == 0){
						arrayOutputStream.flush();
						if(controllerName == null){
							controllerName = new String(arrayOutputStream.toByteArray());
							arrayOutputStream.reset();
						}else{
							actionName = new String(arrayOutputStream.toByteArray());
							break;
						}		
					}else{
						arrayOutputStream.write(bytez);
					}					
				}
				this.controllerName = controllerName;
				this.actionName = actionName;
			} catch (IOException e) {			
				e.printStackTrace();
			}
			this.mDetailData = Arrays.copyOfRange(data, pos, data.length);
		}
		
	}

	@Override
	public byte[] toSendBytes() {
		byte[] dataToSend = new byte[1 + 2 + mDetailData.length + controllerName.length() + actionName.length()];
		int index = 0;
		dataToSend[index++] = mMid;
		byte[] dataOfController = controllerName.getBytes();
		byte[] dataOfAction = actionName.getBytes();
		System.arraycopy(dataOfController, 0, dataToSend, index, dataOfController.length);
		index += dataOfController.length + 1;
		System.arraycopy(dataOfAction, 0, dataToSend, index, dataOfAction.length);
		index += dataOfAction.length +1;
		System.arraycopy(mDetailData, 0, dataToSend, index, mDetailData.length);
		// TODO calculate CRC
		return dataToSend;
	}
	

	/*public AbstractMessage getDetailMessage() throws MessageNotCorrectExeption {
		AbstractMessage message = null;
		switch (mMid) {
		case MessageId.TEXT_MESSAGE_MID:
			message = new TextMessage(this);
			break;
		case MessageId.SIGN_UP_MID:
			message = new SignUpMessage(this);
			break;
		case MessageId.CONFIRM_PASSCODE:
			message = new ConfirmPasscodeMsg(this);
			break;
		case MessageId.SIGN_IN_MID:
			message = new SignInMessage(this);
			break;
		case LinearStringMessage.MID:
			message = new LinearStringMessage(this);
			break;
		case ChatMessage.MID:
			message = new ChatMessage(this);
			break;
		case SyncDeviceContactMessage.MID:
			message = new SyncDeviceContactMessage(this);
			break;

		default:
			// TODO Message Base update code after create new message
			throw new MessageNotCorrectExeption("MID " + mMid + " unknow");
		}
		message.fromBytes(mDetailData);
		return message;
	}*/

	@Deprecated
	@Override
	public IMessage getBaseMessage() {
		throw new UnsupportedOperationException("Not supported yet");
	}

	public byte[] getDetailData() {
		return mDetailData;
	}

	public void setDetailData(byte[] mData) {
		this.mDetailData = mData;
	}

	/**
	 * @return the cRC
	 */
	public int getCRC() {
		return mCRC;
	}

	/**
	 * @param cRC
	 *            the cRC to set
	 */
	public void setCRC(int cRC) {
		mCRC = cRC;
	}

	public interface IOnMessageReceive {
		public void onReceiveMessage(IMessage message);
	}

	public static class Util {
		public static byte[] toZeroEndBytes(String pString)
				throws UnsupportedEncodingException {
			byte[] plaintBytes = pString.getBytes("UTF-8");
			byte[] result = new byte[plaintBytes.length + 1];
			System.arraycopy(plaintBytes, 0, result, 0, plaintBytes.length);
			return result;
		}
	}

	

}
