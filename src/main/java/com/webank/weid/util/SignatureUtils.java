/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.crypto.Sign;
import org.bouncycastle.util.encoders.Base64;

import com.webank.weid.constant.WeIdConstant;

/**
 * The Signature related Utils class. Based on ECDSA Asymmetric Encryption + SHA256 Hash Algorithm.
 *
 * <p>Two types of Objects are taken care of in this class: 1. Key-pair (pubkey and privkey). 2.
 * Signature. This class provides the following functionalities to them above: 1. Creation of
 * Key-Pairs and Signatures. 2. Verification of Key-Pairs and Signatures. 3. Serialization and
 * De-serializations. This Util class also takes care of: 1. Base64 and Hex encoding styles. It is
 * worth noting that we suggest to add encode/decode instead of plain serial/de-serializations. Most
 * implementations are re-factors or wrappers based on FISCO-BCOS web3j and Ethereumj.
 *
 * <p>Future support of SM2/SM3 is under construction.
 *
 * @author chaoxinhu 2018.10
 */
public class SignatureUtils {

    /**
     * Generate a new Key-pair.
     *
     * @return the ECKeyPair
     * @throws InvalidAlgorithmParameterException Invalid algorithm.
     * @throws NoSuchAlgorithmException No such algorithm.
     * @throws NoSuchProviderException No such provider.
     */
    public static ECKeyPair createKeyPair() 
        throws InvalidAlgorithmParameterException, 
        NoSuchAlgorithmException, 
        NoSuchProviderException {
        return Keys.createEcKeyPair();
    }

    /**
     * Sign a message based on the given key-pairs. The message passed in WILL BE HASHED.
     *
     * @param message the message
     * @param keyPair the key pair
     * @return SignatureData
     * @throws UnsupportedEncodingException If the named charset is not supported.
     */
    public static Sign.SignatureData signMessage(String message, ECKeyPair keyPair)
        throws UnsupportedEncodingException {
        return Sign.signMessage(HashUtils.sha3(message.getBytes(WeIdConstant.UTF_8)), keyPair);
    }

    /**
     * Sign a message based on the given privateKey in Decimal String BigInt. The message passed in
     * WILL BE HASHED.
     *
     * @param message the message
     * @param privateKeyString the private key string
     * @return SignatureData
     * @throws UnsupportedEncodingException If the named charset is not supported.
     */
    public static Sign.SignatureData signMessage(
        String message,
        String privateKeyString)
        throws UnsupportedEncodingException {

        BigInteger privateKey = new BigInteger(privateKeyString);
        ECKeyPair keyPair = new ECKeyPair(privateKey, publicKeyFromPrivate(privateKey));
        return Sign.signMessage(HashUtils.sha3(message.getBytes(WeIdConstant.UTF_8)), keyPair);
    }

    /**
     * Extract the Public Key from the message and the SignatureData.
     *
     * @param message the message
     * @param signatureData the signature data
     * @return publicKey
     * @throws SignatureException Signature is the exception.
     * @throws UnsupportedEncodingException If the named charset is not supported.
     */
    public static BigInteger signatureToPublicKey(
        String message,
        Sign.SignatureData signatureData)
        throws SignatureException, UnsupportedEncodingException {

        return Sign.signedMessageToKey(HashUtils.sha3(message.getBytes(WeIdConstant.UTF_8)),
                signatureData);
    }

    /**
     * Verify whether the message and the Signature matches the given public Key.
     *
     * @param message This should be from the same plain-text source with the signature Data.
     * @param signatureData This must be in SignatureData. Caller should call
     *      impleSignatureDeserialization.
     * @param publicKey This must be in BigInteger. Caller should convert it to BigInt.
     * @return true if yes, false otherwise
     * @throws SignatureException Signature is the exception.
     * @throws UnsupportedEncodingException If the named charset is not supported.
     */
    public static boolean verifySignature(
        String message, 
        Sign.SignatureData signatureData, 
        BigInteger publicKey)
        throws SignatureException, UnsupportedEncodingException {
        
        BigInteger extractedPublicKey = signatureToPublicKey(message, signatureData);
        return extractedPublicKey.equals(publicKey);
    }

    /**
     * Obtain the PublicKey from given PrivateKey.
     *
     * @param privateKey the private key
     * @return publicKey
     */
    public static BigInteger publicKeyFromPrivate(BigInteger privateKey) {
        return Sign.publicKeyFromPrivate(privateKey);
    }

    /**
     * Obtain the WeIdPrivateKey from given PrivateKey.
     *
     * @param privateKey the private key
     * @return WeIdPrivateKey
     */
    public static ECKeyPair createKeyPairFromPrivate(BigInteger privateKey) {
        return ECKeyPair.create(privateKey);
    }

    /**
     * The Base64 encode/decode class.
     *
     * @param base64Bytes the base 64 bytes
     * @return the byte[]
     */
    public static byte[] base64Decode(byte[] base64Bytes) {
        return Base64.decode(base64Bytes);
    }

    /**
     * Base 64 encode.
     *
     * @param nonBase64Bytes the non base 64 bytes
     * @return the byte[]
     */
    public static byte[] base64Encode(byte[] nonBase64Bytes) {
        return Base64.encode(nonBase64Bytes);
    }

    /**
     * Checks if is valid base 64 string.
     *
     * @param string the string
     * @return true, if is valid base 64 string
     */
    public static boolean isValidBase64String(String string) {
        return org.apache.commons.codec.binary.Base64.isBase64(string);
    }

    /**
     * The Serialization class of Signatures. This is simply a concatenation of bytes of the v, r,
     * and s. Ethereum uses a similar approach with a wrapping from Base64.
     * https://www.programcreek.com/java-api-examples/index.php?source_dir=redPandaj-master/src/org/redPandaLib/crypt/ECKey.java
     * uses a DER-formatted serialization, but it does not entail the v tag, henceforth is more
     * complex and computation hungry.
     *
     * @param signatureData the signature data
     * @return the byte[]
     */
    public static byte[] simpleSignatureSerialization(Sign.SignatureData signatureData) {
        byte[] serializedSignatureData = new byte[65];
        serializedSignatureData[0] = signatureData.getV();
        System.arraycopy(signatureData.getR(), 0, serializedSignatureData, 1, 32);
        System.arraycopy(signatureData.getS(), 0, serializedSignatureData, 33, 32);
        return serializedSignatureData;
    }

    /**
     * The De-Serialization class of Signatures. This is simply a de-concatenation of bytes of the
     * v, r, and s.
     *
     * @param serializedSignatureData the serialized signature data
     * @return the sign. signature data
     */
    public static Sign.SignatureData simpleSignatureDeserialization(
        byte[] serializedSignatureData) {
        byte v = serializedSignatureData[0];
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        System.arraycopy(serializedSignatureData, 1, r, 0, 32);
        System.arraycopy(serializedSignatureData, 33, s, 0, 32);
        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
        return signatureData;
    }

    /**
     * The De-Serialization class of Signatures accepting raw values of v, r, and s. Note: due to
     * the non 1:1 mapping between default encoded Java String and Byte Array, all the parameters
     * derived from Byte Array should either be STILL IN Byte Array or Base-64.
     *
     * @param v the v
     * @param r the r
     * @param s the s
     * @return the sign. signature data
     */
    public static Sign.SignatureData rawSignatureDeserialization(int v, byte[] r, byte[] s) {
        byte valueByte = (byte) v;
        return new Sign.SignatureData(valueByte, r, s);
    }
}
