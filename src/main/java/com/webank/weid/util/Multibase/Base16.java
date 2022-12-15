package com.webank.weid.util.Multibase;

public class Base16 {
    public static byte[] decode(String hex) {
        if (hex.length() % 2 == 1)
            throw new IllegalStateException("Must have an even number of hex digits to convert to bytes!");
        byte[] res = new byte[hex.length()/2];
        for (int i=0; i < res.length; i++)
            res[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        return res;
    }

    public static String encode(byte[] data) {
        return bytesToHex(data);
    }

    private static String[] HEX_DIGITS = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private static String[] HEX = new String[256];
    static {
        for (int i=0; i < 256; i++)
            HEX[i] = HEX_DIGITS[(i >> 4) & 0xF] + HEX_DIGITS[i & 0xF];
    }

    public static String byteToHex(byte b) {
        return HEX[b & 0xFF];
    }

    public static String bytesToHex(byte[] data) {
        StringBuilder s = new StringBuilder();
        for (byte b : data)
            s.append(byteToHex(b));
        return s.toString();
    }
}
