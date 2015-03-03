package com.pdv.heli.message.base;

import com.pdv.heli.message.common.MessageMode;

/**
 * Created by via on 2/4/15.
 */
public interface IMessage {

	public void fromBytes(byte[] data) throws MessageNotCorrectExeption;

	public byte[] toSendBytes() throws MessageNotCorrectExeption;

	public void processMessage();

	public void setMid(byte pMid);

	public byte getMid();

	public String getSocketAddress();

	public void setSocketAddress(String socketAddress);

	public MessageMode getMessageMode();

	public IMessage getBaseMessage() throws MessageNotCorrectExeption;

	public void setMessageMode(MessageMode mode);


}
