package com.pdv.heli.message.base;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public abstract class JsonMessage extends AbstractMessage {

	protected String mJson;
	protected byte mType;

	public JsonMessage(IMessage pMessageBase) {
		super(pMessageBase);
	}

	public JsonMessage() {
	}

	@Override
	public void fromBytes(byte[] pData) throws MessageNotCorrectExeption {
		try {			
			mType = pData[0];
			mJson = new String(Arrays.copyOfRange(pData, 1, pData.length), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toSendBytes() throws MessageNotCorrectExeption {
		try {
			byte[] dataJson = mJson.getBytes("UTF-8");
			byte[] dataSend = new byte[dataJson.length + 1];
			dataSend[0] = mType;
			System.arraycopy(dataJson, 0, dataSend, 1, dataJson.length);
			return dataSend;
		} catch (UnsupportedEncodingException e) {
			throw new MessageNotCorrectExeption(e);
		}
	}
	

	public String getJson() {
		return mJson;
	}

	public void setJson(String pJson) {
		mJson = pJson;
	}
	public byte getType() {
		return mType;
	}

	public void setType(byte pType) {
		mType = pType;
	}

}
