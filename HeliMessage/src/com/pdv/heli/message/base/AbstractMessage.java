package com.pdv.heli.message.base;

import com.pdv.heli.message.common.MessageMode;

/**
 * Created by via on 2/4/15.
 */
public abstract class AbstractMessage implements IMessage{
    protected String socketAddress;
    protected MessageMode messageMode;
    protected byte mMid;
   
    public AbstractMessage(){
    	
    }
    public AbstractMessage(MessageMode messageMode) {
        this.messageMode = messageMode;
    }

    public AbstractMessage(IMessage message) {
        this.mMid = message.getMid();
        this.socketAddress = message.getSocketAddress();
        this.messageMode = message.getMessageMode();
       
    }
    public AbstractMessage(String socketAddress, MessageMode messageMode) {
        this.socketAddress = socketAddress;
        this.messageMode = messageMode;
    }
    public String getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(String socketAddress) {
        this.socketAddress = socketAddress;
    }

    public MessageMode getMessageMode() {
        return messageMode;
    }
    

    @Override
    public byte getMid() {
        return this.mMid;
    }

    

    @Override
    public void setMid(byte pMid) {
        this.mMid = pMid;
    }

   

    public void setMessageMode(MessageMode messageMode) {
        this.messageMode = messageMode;
    }
    @Override
    public IMessage getBaseMessage() throws MessageNotCorrectExeption {
        MessageBase msg = new MessageBase(this);
        msg.setData(this.toSendBytes());
        return msg;
    }
   
}
