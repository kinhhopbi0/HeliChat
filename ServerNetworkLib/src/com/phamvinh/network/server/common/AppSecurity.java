package com.phamvinh.network.server.common;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by via on 2/3/15.
 */
public class AppSecurity {

    @SuppressWarnings("unused")
	private static int ModRTU_CRC(byte[] buf, int len) {
        int crc = 0xFFFF;

        for (int pos = 0; pos < len; pos++) {
            crc ^= (int) buf[pos];          // XOR byte into least sig. byte of crc

            for (int i = 8; i != 0; i--) {    // Loop over each bit
                if ((crc & 0x0001) != 0) {      // If the LSB is set
                    crc >>= 1;                    // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else                            // Else LSB is not set
                    crc >>= 1;                    // Just shift right
            }
        }
        // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
        return crc;
    }
    private static SecureRandom random = new SecureRandom();
    public static String getRandom(int size) {
        return new BigInteger(130, random).toString(size);
    }
    public static String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
    
}
