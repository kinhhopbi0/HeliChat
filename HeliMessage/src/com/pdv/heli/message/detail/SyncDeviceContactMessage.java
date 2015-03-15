package com.pdv.heli.message.detail;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.JsonMessage;
import com.pdv.heli.message.base.MessageNotCorrectExeption;

public class SyncDeviceContactMessage extends JsonMessage {
	private ArrayList<String> phones;
	private ArrayList<String> names;

	public static final byte MID = 0x08;

	public static class Type {
		public static final byte CLIENT_GET_CONTACT = 0x01;
		public static final byte RESPONSE_CONTACT_FROM_SERVER = 0x02;
		public static final byte CLIENT_POST_CONTACT = 0x03;
		public static final byte SERVER_UPDATE_SUCCESS = 0x04;
		public static final byte SERVER_UPDATE_FAIL = 0x05;

	}

	public SyncDeviceContactMessage() {
		phones = new ArrayList<>();
		names = new ArrayList<>();
		this.mMid = MID;
		this.mType = Type.CLIENT_GET_CONTACT;
	}

	public SyncDeviceContactMessage(IMessage pMessageBase) {
		super(pMessageBase);
		this.mMid = MID;
		this.mType = Type.CLIENT_GET_CONTACT;
	}

	public void makeContactsToJSONString(){
		JSONArray jsonArray = new JSONArray();
		JSONArray row;
		for (int i = 0; i < phones.size(); i++) {
			row = new JSONArray();
			row.add(0, names.get(i));
			row.add(1, phones.get(i));
			jsonArray.add(row);
		}
		mJson = jsonArray.toJSONString();		
	}
	
	@Override
	public byte[] toSendBytes() throws MessageNotCorrectExeption {
		switch (mType) {
		case Type.CLIENT_POST_CONTACT:
		case Type.RESPONSE_CONTACT_FROM_SERVER:
			makeContactsToJSONString();
			break;
		case Type.CLIENT_GET_CONTACT:
		case Type.SERVER_UPDATE_FAIL:
		case Type.SERVER_UPDATE_SUCCESS:
			byte[] toSend = new byte[]{mType};
			return toSend;
		default:
			throw new MessageNotCorrectExeption("Message type "+mType+" not defined");			
		}

		return super.toSendBytes();
	}
	public void makeContactsFromJSON(String JSON){
		JSONParser jsonParser = new JSONParser();
		try {
			Object obj = jsonParser.parse(JSON);
			JSONArray array = (JSONArray) obj;
			String name = null;
			String phone = null;
			phones = new ArrayList<>();
			names = new ArrayList<>();
			for (int i = 0; i < array.size(); i++) {
				JSONArray contact = (JSONArray) array.get(i);
				name = (String) contact.get(0);
				phone = (String) contact.get(1);
				names.add(name);
				phones.add(phone);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void fromBytes(byte[] pData) throws MessageNotCorrectExeption {
		super.fromBytes(pData);
		switch (mType) {
		case Type.CLIENT_POST_CONTACT:
		case Type.RESPONSE_CONTACT_FROM_SERVER:
			makeContactsFromJSON(mJson);
			break;

		default:
			break;
		}
	}

	public ArrayList<String> getPhones() {
		return phones;
	}

	public void setPhones(ArrayList<String> pPhones) {
		phones = pPhones;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public void setNames(ArrayList<String> pNames) {
		names = pNames;
	}

}
