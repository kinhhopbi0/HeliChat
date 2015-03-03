package com.pdv.heli.message.detail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.pdv.heli.message.base.AbstractMessage;
import com.pdv.heli.message.base.IMessage;
import com.pdv.heli.message.base.MessageBase;
import com.pdv.heli.message.base.MessageNotCorrectExeption;
import com.pdv.heli.message.common.Constant;
import com.pdv.heli.message.common.MessageId;

/**
 * Created by via on 2/3/15.
 */
public class SignInMessage extends AbstractMessage {

	private String phone;
	private String password;
	private String token;
	public static final byte MID = MessageId.SIGN_IN_MID;
	private byte status = Status.REQUEST_NEW;
	private int user_id;

	public static final class Status {
		public static final byte REQUEST_NEW = 0x01;

		public static final byte SUCCESS = 0x02;
		public static final byte UNAME_NOT_MATCHES = 0x03;
		
		public static final byte OTHER = 0x04;
		public static final byte OVER_TIMES = 0x06;
		public static final byte RE_REQUEST = 0x05;
		public static final byte TOKEN_NOT_MATCHES = 0x07;

	}

	public SignInMessage(String pPhone, String password) {
		this.phone = pPhone;
		this.password = password;
	}

	public SignInMessage(IMessage pMessageBase) {
		super(pMessageBase);
	}

	public SignInMessage() {
		
	}

	@Override
	public void fromBytes(byte[] pData) throws MessageNotCorrectExeption {
		try {
			int index = 0;
			byte[] bytesOfPhone = null;
			byte[] bytesOfPassword = null;
			byte[] bytesOfToken = null;
			byte[] byteOfUserId = null;
			status = pData[index++];
			switch (status) {
			case Status.REQUEST_NEW:
				for (int i = index; i < pData.length ; i++) {
					if(pData[i] == 0x00){
						bytesOfPhone = Arrays.copyOfRange(pData, index, i);
						index = i+1;
						break;						
					}
				}
				for (int i = index; i < pData.length; i++) {
					if(pData[i] == 0x00){
						bytesOfPassword = Arrays.copyOfRange(pData, index, i);
						index = i+1;
						break;						
					}					
				}
				this.phone = new String(bytesOfPhone);
				this.password = new String(bytesOfPassword);			
				break;
				
			case Status.RE_REQUEST:		
			case Status.SUCCESS:
				byteOfUserId = Arrays.copyOfRange(pData, index, index + 4 );								
				index += 4;
				for (int i = index ; i < pData.length; i++) {
					if(pData[i] == 0x00 ){
						bytesOfToken = Arrays.copyOfRange(pData, index,i);						
						break;
					}
				}				
				user_id = ByteBuffer.wrap(byteOfUserId).order(ByteOrder.LITTLE_ENDIAN).getInt();
				token = new String(bytesOfToken);
				break;
			case Status.TOKEN_NOT_MATCHES:
			case Status.OVER_TIMES:
			case Status.UNAME_NOT_MATCHES:
			case Status.OTHER:

				break;
			default:
				throw new MessageNotCorrectExeption("Status code: " + status
						+ " not defined");

			}
		} catch (Exception ex) {
			throw new MessageNotCorrectExeption("Parse byte to signin exception "+ex.toString(),ex);
		}

	}

	@Override
	public byte[] toSendBytes() throws MessageNotCorrectExeption {
		try {
			byte[] result = null;
			byte[] bytesOfToken = null;
			byte[] bytesOfUserId = null;
			switch (status) {
			case Status.REQUEST_NEW:
				byte[] bytesOfPhone = MessageBase.Util.toZeroEndBytes(phone);
				byte[] bytesOfPassowrd = MessageBase.Util
						.toZeroEndBytes(password);
				result = new byte[1 + bytesOfPassowrd.length
						+ bytesOfPhone.length];
				int index = 0;
				result[index++] = status;
				System.arraycopy(bytesOfPhone, 0, result, index,
						bytesOfPhone.length);
				index += bytesOfPhone.length;
				System.arraycopy(bytesOfPassowrd, 0, result, index,
						bytesOfPassowrd.length);
				break;
				
			case Status.RE_REQUEST:				
				bytesOfUserId = ByteBuffer.allocate(4)
				.order(ByteOrder.LITTLE_ENDIAN).putInt(user_id).array();
				
				bytesOfToken = MessageBase.Util.toZeroEndBytes(token);
				result = new byte[1 + bytesOfUserId.length
						+ bytesOfToken.length];
				result[0] = status;
				System.arraycopy(bytesOfUserId, 0, result, 1,
						bytesOfUserId.length);
				System.arraycopy(bytesOfToken, 0, result,
						1 + bytesOfUserId.length, bytesOfToken.length);
				break;
				
			case Status.SUCCESS:				
				bytesOfUserId = ByteBuffer.allocate(4)
						.order(ByteOrder.LITTLE_ENDIAN).putInt(user_id).array();
				bytesOfToken = MessageBase.Util.toZeroEndBytes(token);
				result = new byte[1 + bytesOfToken.length + 4];
				result[0] = status;
				System.arraycopy(bytesOfUserId, 0, result, 1, 4);
				System.arraycopy(bytesOfToken, 0, result, 5,
						bytesOfToken.length);
				break;
			case Status.TOKEN_NOT_MATCHES:
			case Status.UNAME_NOT_MATCHES:
			case Status.OTHER:
			case Status.OVER_TIMES:
				result = new byte[] { status };
				break;
			default:
				throw new MessageNotCorrectExeption("Status code: " + status
						+ " not defined");
			}
			return result;
		} catch (Exception ex) {
			throw new MessageNotCorrectExeption(ex);
		}
	}

	/**
	 * 
	 */
	@Override
	public void processMessage() {
	}

	@Override
	public byte getMid() {
		return MID;
	}

	@Override
	public void setMid(byte pMid) {
		throw new UnsupportedOperationException("Detail message can't set MID");
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String pPhone) throws Exception {
		if (pPhone.getBytes().length > Constant.PHONE_MAX_SIZE) {
			throw new Exception("phone field must max of "
					+ Constant.PHONE_MAX_SIZE);
		}
		phone = pPhone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String pPassword) throws Exception {
		if (pPassword.getBytes().length > Constant.PASSWORD_MAX_SIZE) {
			throw new Exception("phone field must max of "
					+ Constant.PASSWORD_MAX_SIZE);
		}
		password = pPassword;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String pToken) {
		token = pToken;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte pStatus) {
		status = pStatus;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int pUser_id) {
		user_id = pUser_id;
	}
	

}
