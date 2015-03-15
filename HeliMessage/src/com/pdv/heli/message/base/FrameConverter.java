package com.pdv.heli.message.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pdv.heli.message.common.Constant;
import com.pdv.heli.message.common.FrameTemptObject;

public class FrameConverter {
	public static byte[] createFrame(byte[] buffer) {
		try {
			byte[] data = buffer;
			int dataLen = buffer.length;

			byte[] sendBuff = new byte[dataLen + 2 + dataLen];// max length
			int count = 0;
			sendBuff[count++] = Constant.FRAME_START;// first frame
			for (int i = 0; i < dataLen; i++) {
				switch (data[i]) {
				case Constant.FRAME_START:
				case Constant.FRAME_PADDING:
				case Constant.FRAME_END:
					sendBuff[count++] = Constant.FRAME_PADDING;
					sendBuff[count++] = (byte) (data[i] ^ 0x20);
					break;
				default:
					sendBuff[count++] = data[i];
					break;
				}
			}
			sendBuff[count++] = Constant.FRAME_END;// last byte of frame
			return Arrays.copyOf(sendBuff, count);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static List<byte[]> parseFrame(byte[] buffer,
			FrameTemptObject temptObj) {
		List<byte[]> messageDatas = new ArrayList<byte[]>();
		try {
			byte[] data = new byte[buffer.length - 2];
			int position = 0;
			boolean isPadding = false;
			boolean isNewPacket = false;
			// TempData temp = tempReceiveFrameMapData.get(packet.sessionId);

			for (int i = 0; i < buffer.length; i++) {
				if (i == 0 && temptObj.buffer != null) { // if have before
					isNewPacket = true;
					isPadding = temptObj.isPadding;
					position = temptObj.buffer.length;
					data = new byte[position + buffer.length];
					System.arraycopy(temptObj.buffer, 0, data, 0,
							temptObj.buffer.length);
				}
				switch (buffer[i]) {
				case Constant.FRAME_START:// khoi tao khung
					if (i == (buffer.length - 1)) { // if start byte is end of
													// byte array
						temptObj.buffer = new byte[0];
						temptObj.isPadding = false;
					}
					isNewPacket = true;
					isPadding = false;
					position = 0;
					data = new byte[buffer.length];
					break;
				case Constant.FRAME_PADDING:
					if (isNewPacket) {
						isPadding = true;
						if (i == (buffer.length - 1)) {
							// tempt
							// after
							// here
							temptObj.buffer = Arrays.copyOf(data, position);
							temptObj.isPadding = true;
						}
					}
					break;
				case Constant.FRAME_END:// ket thuc khung
					if (isNewPacket) {
						isNewPacket = false;
						byte[] realdata = Arrays.copyOf(data, position);
						if (temptObj.buffer != null) {
							temptObj.buffer = null;
						}
						messageDatas.add(realdata);
					}
					break;
				default:
					if (isNewPacket) {
						if (isPadding) {
							data[position++] = (byte) (buffer[i] ^ (0x20));
							isPadding = false;
						} else {
							data[position++] = buffer[i];
						}
						// if end of byte array but not end of frame, save to
						// tempt
						if (i == (buffer.length - 1)) {							
							temptObj.buffer = Arrays.copyOf(data, position); // save
																			// tempt
																			// after
																			// here
							temptObj.isPadding = false;
						}

					}
					break;
				}

			}

		} catch (Exception ex) {
			throw ex;
		}
		return messageDatas;
	}
}
