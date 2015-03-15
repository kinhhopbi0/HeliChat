package com.pdv.heli.message.detail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.pdv.heli.message.base.AbstractMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;

public class ChatMessage extends AbstractMessage {

	public static final byte MID = 0x07;
	private HashMap<String, String> extras;

	public ChatMessage() {
		extras = new HashMap<>(0);
	}

	public ChatMessage(MessageBase pMessageBase) {
		super(pMessageBase);
		extras = new HashMap<>(0);
	}

	@Override
	public void fromBytes(byte[] pData) throws MessageNotCorrectExeption {
		ByteArrayInputStream bais = new ByteArrayInputStream(pData);
		try {			
			int b;
			String key = null;
			String value = null;
			boolean isKey = true;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(0);
			while ((b = bais.read()) >= 0) {
				byte aByte = (byte) b;
				if (aByte == 0) {
					if (isKey) {
						baos.flush();
						key = new String(baos.toByteArray(),"UTF-8");
						baos.close();
						baos = new ByteArrayOutputStream(0);
						isKey = false;
					} else {
						baos.flush();
						value = new String(baos.toByteArray(),"UTF-8");
						baos.close();
						baos = new ByteArrayOutputStream(0);
						isKey = true;
						extras.put(key, value);
					}
				} else {
					baos.write(new byte[] { aByte });
				}
			}
		} catch (Exception ex) {
			throw new MessageNotCorrectExeption(ex);
		}
	}

	@Override
	public byte[] toSendBytes() throws MessageNotCorrectExeption {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream outputStream = new BufferedOutputStream(baos);
			Iterator<Entry<String, String>> it = extras.entrySet().iterator();
			byte[] keyData;
			byte[] valueData;
			
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				keyData = MessageBase.Util.toZeroEndBytes(entry.getKey());
				outputStream.write(keyData);
				outputStream.flush();
				valueData = MessageBase.Util.toZeroEndBytes(entry.getValue());
				outputStream.write(valueData);
				outputStream.flush();
			}
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new MessageNotCorrectExeption(ioe);
		}
	}

	
	@Override
	public byte getMid() {
		return MID;
	}

	@Override
	public void setMid(byte pMid) {
		super.setMid(pMid);
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key.
	 * 
	 * More formally, if this map contains a mapping from a key k to a value v
	 * such that (key==null ? k==null : key.equals(k)), then this method returns
	 * v; otherwise it returns null. (There can be at most one such mapping.)
	 * 
	 * A return value of null does not necessarily indicate that the map
	 * contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to null. The containsKey operation may be used to
	 * distinguish these two cases.
	 * 
	 * Specified by: get(...) in Map, Overrides: get(...) in AbstractMap
	 * Parameters: key the key whose associated value is to be returned Returns:
	 * the value to which the specified key is mapped, or null if this map
	 * contains no mapping for the key See Also: put(Object, Object)
	 * 
	 * @param name
	 * @return
	 */
	public String getParam(String name) {
		return extras.get(name);
	}

	public void setParam(String name, String value) {
		extras.put(name, value);
	}

}
