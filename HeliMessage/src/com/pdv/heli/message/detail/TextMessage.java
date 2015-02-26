package com.pdv.heli.message.detail;

import com.pdv.heli.message.base.AbstractMessage;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.common.MessageId;
import com.pdv.heli.message.common.MessageMode;

import java.util.Arrays;

/**
 * Created by via on 2/3/15.
 */
public class TextMessage extends AbstractMessage {

	protected String mContent;
	protected String mUserId;

	public static final int CONTENT_SIZE = 256;
	public static final int USER_ID_SIZE = 60;

	public TextMessage(MessageMode messageMode) {
		super(messageMode);
		this.mMid = MessageId.TEXT_MESSAGE_MID;
	}

	public TextMessage(IMessage message) {
		super(message);
		this.mMid = MessageId.TEXT_MESSAGE_MID;
	}

	public TextMessage(String socketAddress, MessageMode messageMode,
			String mContent) {
		super(socketAddress, messageMode);
		this.mContent = mContent;
		this.mMid = MessageId.TEXT_MESSAGE_MID;
	}

	public TextMessage(MessageMode messageMode, String mContent) {
		super(messageMode);
		this.mContent = mContent;
		this.mMid = MessageId.TEXT_MESSAGE_MID;
	}

	public TextMessage(MessageMode messageMode, String mContent, String mUserId) {
		super(messageMode);
		this.mContent = mContent;
		this.mUserId = mUserId;
		this.mMid = MessageId.TEXT_MESSAGE_MID;
	}

	@Override
	public void fromBytes(byte[] data) {
		int index = 0;
		byte[] dataUsername = Arrays.copyOfRange(data, index,
				index += USER_ID_SIZE);
		byte[] dataContent = Arrays.copyOfRange(data, index,
				index += CONTENT_SIZE);
		mContent = new String(dataContent).trim();
		mUserId = new String(dataUsername).trim();
	}

	@Override
	public byte[] toSendBytes() {
		try {
			byte[] data2Send = new byte[CONTENT_SIZE + USER_ID_SIZE];
			byte[] dataContent = mContent.getBytes();
			byte[] dataUser = mUserId.getBytes();
			byte[] dataUserFull = new byte[USER_ID_SIZE];
			byte[] dataContentFull = new byte[CONTENT_SIZE];
			for (int i = 0; i < Math.min(dataUser.length, dataUserFull.length); i++) {
				dataUserFull[i] = dataUser[i];
			}
			for (int i = 0; i < Math.min(dataContent.length,
					dataContentFull.length); i++) {
				dataContentFull[i] = dataContent[i];
			}
			int index = 0;
			System.arraycopy(dataUserFull, 0, data2Send, index, USER_ID_SIZE);
			index += USER_ID_SIZE;
			System.arraycopy(dataContentFull, 0, data2Send, index, CONTENT_SIZE);
			return data2Send;
		} catch (ArrayIndexOutOfBoundsException ex) {

		}
		return null;
	}

	@Override
	public void processMessage() {

	}


	@Override
	public byte getMid() {
		return MessageId.TEXT_MESSAGE_MID;
	}

	@Override
	public void setMid(byte pMid) {
		// not support
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String mUserId) {
		this.mUserId = mUserId;
	}

}
