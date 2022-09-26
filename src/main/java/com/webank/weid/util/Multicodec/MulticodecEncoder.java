package com.webank.weid.util.Multicodec;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Multicodec is part of the Multiformats collection of protocols.
 *
 * Multiformats is a collection of protocols which aim to future-proof systems, today. They do this mainly by allowing
 * data to be self-describable.
 * See: https://github.com/multiformats/multiformats
 * The Multicodec is an agreed way for encoding bytes with a prefix that specifies the type of encoding.
 * The format is therefore a portable and self describing way of expressing an encoding of bytes that does not assume
 * a specific context.
 * See: https://github.com/multiformats/multicodec
 * For example, the multicodec specification is used in the specifications for Decentralised Identifiers (DIDs) in
 * regard to the DID Method 'key' specification.
 * Therefore, any work on `did:key` implementations needs to also solve for the use of multicodecs.
 * See: https://w3c-ccg.github.io/did-method-key
 *
 */
public class MulticodecEncoder {

    /**
     * Encodes the byte array of data for the multicodec type.
     * The multicodec type bytes are unsigned varint encoded and are pre-pended to the byte array.
     * @param multicodec The Multicodec enum to encode with.
     * @param data The bytes of data to encode.
     * @return The multicodec encoding of the input bytes
     *
     */
    public static byte[] encode(Multicodec multicodec, byte[] data) {

        //Get the multicodec prefix of the encoding type as an array as some codes use multibyte prefixes.
        byte[] multicodecBytes = HexUtils.hexToBytes(multicodec.code);

        //Create a byte array pre-pended with the multicodec prefix for the multicodecType
        //Taking care that the multicodec prefix must be unsigned varint encoded...
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            //As per multicodec spec, perform unsigned varint encoding of each byte of the multicodec prefix
            for (byte b : multicodecBytes) {
                byteArrayOutputStream.write(VarInt.writeUnsignedVarInt(Byte.toUnsignedInt(b)));
            }

            //Append the data bytes
            byteArrayOutputStream.write(data);
            return byteArrayOutputStream.toByteArray();

        } catch (IOException exIO) {
            System.err.println("Unexpected error on multicodec encode: " + exIO.getMessage());
            return null;
        }
    }

    /**
     * Decodes a multicodec encoded byte array.
     * Assumption: The decoding only supports single byte codec codes.
     * Some codec codes are multibyte values and these cannot be distinguished from single byte codec codes
     * when the data follows.
     * Consider this encoding of sample data 'A1E9D3D8EC'
     * 1. cidv1 encoding starts with 0x10.
     *    - therefore sample encoding hex data is: 01A1E9D3D8EC
     * 2. udp encoding starts with 0x0111
     *    - therefore sample encoding hex data is: 0111A1E9D3D8EC
     *
     * Looking 2 it is impossible to determine if its
     *   cidv1 encoding with data '11A1E9D3D8EC',
     *   or if it is udp encoding with data 'A1E9D3D8EC'.
     * This suggests an issue in the multi codec specification for codec prefixes greater than 1 byte.
     * They cannot be 1:1 unambiguously mapped between each other.
     * @param multicodecEncodedData The multicodec encoded data
     * @return The DecodedData object
     */
    public static DecodedData decode(byte[] multicodecEncodedData) {

        DecodedData data = new DecodedData();
        //No clever shortcuts - we simply loop over the set of the codecs and find a match at the start of the string.
        //Remembering that the codec at the start will be unsigned varint encoded ...
        String multicodecEncodedDataHex = HexUtils.bytesToHex(multicodecEncodedData);

        for (Multicodec c : Multicodec.values()) {
            //as per stated assumption...
            int codeLength = HexUtils.hexToBytes(c.code).length;
            if ((codeLength == 2) && (multicodecEncodedDataHex.startsWith(c.uvarintcode))) {
                data.setCodec(c);
                data.setData(StringUtils.removeStart(multicodecEncodedDataHex, c.uvarintcode));
                break;
            }
        }
        return data;
    }

}
