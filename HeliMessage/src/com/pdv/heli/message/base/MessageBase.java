package com.pdv.heli.message.base;

import java.util.Arrays;

import com.pdv.heli.message.common.MessageId;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.message.detail.TextMessage;

/**
 * Created by via on 2/3/15.
 */
public class MessageBase extends AbstractMessage {
	
    private String mTokenKey;
    private int mCRC;
    private byte[] mData;


    public MessageBase(String socketAddress, MessageMode messageMode) {
        super(socketAddress, messageMode);
    }

    public MessageBase(IMessage clone) {
        super(clone);
    }

    public MessageBase(MessageMode messageMode) {
        super(messageMode);
    }

    public MessageBase(String socketAddress, MessageMode messageMode, String mTokenKey, byte mMid, byte mMType, byte[] mData) {
        super(socketAddress, messageMode);
        this.setTokenKey(mTokenKey);
        this.mMid = mMid;       
        this.mData = mData;
    }

    public MessageBase(MessageMode messageMode, String mTokenKey, byte mMid, byte mMType, byte[] mData) {
        super(messageMode);
        this.setTokenKey(mTokenKey);
        this.mMid = mMid;
       
        this.mData = mData;
    }

    @Override
    public void fromBytes(byte[] data){
        this.mMid = data[0];      
        this.mData = Arrays.copyOfRange(data,1,data.length);
    }

    @Override
    public byte[] toSendBytes() {
        byte[] dataToSend = new byte[1 + mData.length];
        int index = 0;
        dataToSend[index++] = mMid;       
        System.arraycopy(mData,0,dataToSend,index,mData.length);
        //CRC
        return dataToSend;
    }


    @Override
    public void processMessage(){
        throw new UnsupportedOperationException("Not supported yet");
    }
    public IMessage getDetailMessage() throws MessageNotCorrectExeption{
        if(this.getMessageMode() == MessageMode.SEND){
            throw new UnsupportedOperationException("Not supported yet");
        }
        IMessage message = null;
        switch (mMid){
            case MessageId.TEXT_MESSAGE_MID:
                message = new TextMessage(this);
                break;
            case MessageId.SIGN_UP_MID:
            	message = new SignUpMessage(this);
            	break;
            default:
                return null;
        }
        message.fromBytes(mData);
        return message;
    }

    @Deprecated
    @Override
    public IMessage getBaseMessage()  {
        throw new UnsupportedOperationException("Not supported yet");
    }


    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] mData) {
        this.mData = mData;
    }
    /**
	 * @return the cRC
	 */
	public int getCRC() {
		return mCRC;
	}

	/**
	 * @param cRC the cRC to set
	 */
	public void setCRC(int cRC) {
		mCRC = cRC;
	}
	public String getTokenKey() {
		return mTokenKey;
	}

	public void setTokenKey(String pTokenKey) {
		mTokenKey = pTokenKey;
	}
	public interface IOnMessageReceive{
        public void onReceiveMessage(IMessage message);
    }
	public static class Util{
		public static byte[] toZeroEndBytes(String pString){			
			byte[] plaintBytes = pString.getBytes();
			byte[] result = new byte[plaintBytes.length + 1];			
			System.arraycopy(plaintBytes, 0,result, 0, plaintBytes.length);
			return result;
		}
	}
	

}
