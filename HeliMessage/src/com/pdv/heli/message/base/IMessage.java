package com.pdv.heli.message.base;


/**
 * Created by via on 2/4/15.
 */
public interface IMessage {

	public void fromBytes(byte[] data) throws MessageNotCorrectExeption;

	public byte[] toSendBytes() throws MessageNotCorrectExeption;	

	public void setMid(byte pMid);

	public byte getMid();

	public String getSocketAddress();

	public void setSocketAddress(String socketAddress);

	public IMessage getBaseMessage() throws MessageNotCorrectExeption;

	public String getAction();

	public void setAction(String actionName);

	public String getController();

	public void setController(String controllerName);
}
