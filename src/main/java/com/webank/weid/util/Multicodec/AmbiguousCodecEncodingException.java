package com.webank.weid.util.Multicodec;

/**
 * Some codec codes are multibyte values and these cannot be distinguished from single byte codec codes when the data follows.
 * <br/>
 * Consider this encoding of sample data 'A1E9D3D8EC'<br/>
 * <pre>
 *  1. cidv1 encoding starts with 0x10.
 *     - therefore sample encoding hex data is: 01A1E9D3D8EC
 *  2. udp encoding starts with 0x0111
 *     - therefore sample encoding hex data is: 0111A1E9D3D8EC
 *
 * Looking 2 it is impossible to determine if its cidv1 encoding with data '11A1E9D3D8EC' or if it is udp encoding with data 'A1E9D3D8EC'.
 * </pre>
 * <p>
 * This reveals an issue in the multi codec specification for codec prefixes greater than 1 byte, as they cannot
 * be 1:1 unambiguously mapped between each other.
 * </p>
 *
 */
public class AmbiguousCodecEncodingException extends Exception {

    AmbiguousCodecEncodingException(String message) {
        super(message);
    }
}
