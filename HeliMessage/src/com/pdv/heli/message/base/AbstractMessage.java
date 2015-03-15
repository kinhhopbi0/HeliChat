package com.pdv.heli.message.base;

/**
 * Created by via on 2/4/15.
 */
public abstract class AbstractMessage implements IMessage {

	protected String socketAddress;
	protected byte mMid;
	protected String controllerName = "";
	protected String actionName = "";

	public AbstractMessage() {

	}

	public AbstractMessage(IMessage message) {
		this.mMid = message.getMid();
		this.socketAddress = message.getSocketAddress();
		this.controllerName = message.getController();
		this.actionName = message.getAction();		
	}

	public String getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(String socketAddress) {
		this.socketAddress = socketAddress;
	}

	@Override
	public byte getMid() {
		return this.mMid;
	}

	@Override
	public void setMid(byte pMid) {
		this.mMid = pMid;
	}

	@Override
	public IMessage getBaseMessage() throws MessageNotCorrectExeption {
		MessageBase msg = new MessageBase(this);
		msg.setDetailData(this.toSendBytes());
		return msg;
	}

	@Override
	public void setAction(String pActionName) {
		this.actionName = pActionName;
	}

	@Override
	public void setController(String pControllerName) {
		this.controllerName = pControllerName;
	}

	@Override
	public String getAction() {
		return this.actionName;
	}

	@Override
	public String getController() {
		return this.controllerName;
	}
	
		
	
}
