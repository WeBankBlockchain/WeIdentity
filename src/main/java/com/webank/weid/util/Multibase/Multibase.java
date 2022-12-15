package com.webank.weid.util.Multibase;

import java.util.*;


public class Multibase {

    public enum Base {
        Base1('1'),
        Base2('0'),
        Base8('7'),
        Base10('9'),
        Base16('f'),
        Base16Upper('F'),
        Base32('b'),
        Base32Upper('B'),
        Base32Pad('c'),
        Base32PadUpper('C'),
        Base32Hex('v'),
        Base32HexUpper('V'),
        Base32HexPad('t'),
        Base32HexPadUpper('T'),
        Base36('k'),
        Base36Upper('K'),
        Base58BTC('z'),
        Base58Flickr('Z'),
        Base64('m'),
        Base64Url('u'),
        Base64Pad('M'),
        Base64UrlPad('U');

        public char prefix;

        Base(char prefix) {
            this.prefix = prefix;
        }

        private static Map<Character, Base> lookup = new TreeMap<>();
        static {
            for (Base b: Base.values())
                lookup.put(b.prefix, b);
        }

        public static Base lookup(char p) {
            if (!lookup.containsKey(p))
                throw new IllegalStateException("Unknown Multibase type: " + p);
            return lookup.get(p);
        }
    }

    public static String encode(Base b, byte[] data) {
        switch (b) {
            case Base58BTC:
                return b.prefix + Base58.encode(data);
            case Base16:
                return b.prefix + Base16.encode(data);
            case Base16Upper:
                return b.prefix + Base16.encode(data).toUpperCase();
            case Base32:
                return b.prefix + new String(new Base32().encode(data)).toLowerCase().replaceAll("=", "");
            case Base32Pad:
                return b.prefix + new String(new Base32().encode(data)).toLowerCase();
            case Base32PadUpper:
                return b.prefix + new String(new Base32().encode(data));
            case Base32Upper:
                return b.prefix + new String(new Base32().encode(data)).replaceAll("=", "");
            case Base32Hex:
                return b.prefix + new String(new Base32(true).encode(data)).toLowerCase().replaceAll("=", "");
            case Base32HexPad:
                return b.prefix + new String(new Base32(true).encode(data)).toLowerCase();
            case Base32HexPadUpper:
                return b.prefix + new String(new Base32(true).encode(data));
            case Base32HexUpper:
                return b.prefix + new String(new Base32(true).encode(data)).replaceAll("=", "");
            case Base36:
                return b.prefix + Base36.encode(data);
            case Base36Upper:
                return b.prefix + Base36.encode(data).toUpperCase();
            case Base64:
                return b.prefix + Base64.encodeBase64String(data).replaceAll("=", "");
            case Base64Url:
                return b.prefix + Base64.encodeBase64URLSafeString(data).replaceAll("=", "");
            case Base64Pad:
                return b.prefix + Base64.encodeBase64String(data);
            case Base64UrlPad:
                return b.prefix + Base64.encodeBase64String(data).replaceAll("\\+", "-").replaceAll("/", "_");
            default:
                throw new IllegalStateException("Unsupported base encoding: " + b.name());
        }
    }

    public static Base encoding(String data) {
        return Base.lookup(data.charAt(0));
    }

    public static byte[] decode(String data) {
        Base b = encoding(data);
        String rest = data.substring(1);
        switch (b) {
            case Base58BTC:
                return Base58.decode(rest);
            case Base16:
                return Base16.decode(rest);
            case Base16Upper:
                return Base16.decode(rest.toLowerCase());
            case Base32:
            case Base32Pad:
                return new Base32().decode(rest);
            case Base32PadUpper:
            case Base32Upper:
                return new Base32().decode(rest.toLowerCase());
            case Base32Hex:
            case Base32HexPad:
                return new Base32(true).decode(rest);
            case Base32HexPadUpper:
            case Base32HexUpper:
                return new Base32(true).decode(rest.toLowerCase());
            case Base36:
                return Base36.decode(rest);
            case Base36Upper:
                return Base36.decode(rest.toLowerCase());
            case Base64:
            case Base64Url:
            case Base64Pad:
            case Base64UrlPad:
                return Base64.decodeBase64(rest);
            default:
                throw new IllegalStateException("Unsupported base encoding: " + b.name());
        }
    }
}
