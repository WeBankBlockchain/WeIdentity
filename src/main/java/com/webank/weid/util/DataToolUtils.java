/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.reinert.jjschema.v1.JsonSchemaV4Factory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Hash;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.bcos.web3j.utils.Numeric;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.RsvSignature;

/**
 * 数据工具类.
 *
 * @author tonychen 2019年4月23日
 */
public final class DataToolUtils {

    private static final Logger logger = LoggerFactory.getLogger(DataToolUtils.class);
    private static final String SEPARATOR_CHAR = "-";
    //private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String CHARSET = StandardCharsets.UTF_8.toString();

    private static final String FORMAT_NAME = "JPG";

    /**
     * default salt length.
     */
    private static final String DEFAULT_SALT_LENGTH = "5";

    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;

    // LOGO宽度
    private static final int LOGO_WIDTH = 60;

    // LOGO高度
    private static final int LOGO_HEIGHT = 60;


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    //private static final ObjectWriter OBJECT_WRITER;
    //private static final ObjectReader OBJECT_READER;
    private static final ObjectWriter OBJECT_WRITER_UN_PRETTY_PRINTER;

    static {
        // sort by letter
        OBJECT_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        // when map is serialization, sort by key
        OBJECT_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // ignore mismatched fields
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // use field for serialize and deSerialize
        OBJECT_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        OBJECT_WRITER_UN_PRETTY_PRINTER = OBJECT_MAPPER.writer();

        //OBJECT_WRITER = OBJECT_MAPPER.writer().withDefaultPrettyPrinter();
        //OBJECT_READER = OBJECT_MAPPER.reader();
    }

    /**
     * Keccak-256 hash function.
     *
     * @param utfString the utfString
     * @return hash value as hex encoded string
     */
    public static String sha3(String utfString) {
        return Numeric.toHexString(sha3(utfString.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Sha 3.
     *
     * @param input the input
     * @return the byte[]
     */
    public static byte[] sha3(byte[] input) {
        return Hash.sha3(input, 0, input.length);
    }

    public static String getHash(String hexInput) {
        return sha3(hexInput);
    }

    /**
     * generate random string.
     *
     * @return random string
     */
    public static String getRandomSalt() {

        String length = PropertyUtils.getProperty("salt.length", DEFAULT_SALT_LENGTH);
        int saltLength = Integer.valueOf(length);
        String salt = RandomStringUtils.random(saltLength, true, true);
        return salt;
    }

    /**
     * serialize a class instance to Json String.
     *
     * @param object the class instance to serialize
     * @param <T> the type of the element
     * @return JSON String
     */
    public static <T> String serialize(T object) {
        Writer write = new StringWriter();
        try {
            OBJECT_MAPPER.writeValue(write, object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException when serialize object to json", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException when serialize object to json", e);
        } catch (IOException e) {
            logger.error("IOException when serialize object to json", e);
        }
        return write.toString();
    }

    /**
     * deserialize a JSON String to an class instance.
     *
     * @param json json string
     * @param clazz Class.class
     * @param <T> the type of the element
     * @return class instance
     */
    public static <T> T deserialize(String json, Class<T> clazz) {
        Object object = null;
        try {
            object = OBJECT_MAPPER.readValue(json, TypeFactory.rawClass(clazz));
        } catch (JsonParseException e) {
            logger.error("JsonParseException when serialize object to json", e);
            throw new DataTypeCastException(e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException when serialize object to json", e);
            new DataTypeCastException(e);
        } catch (IOException e) {
            logger.error("IOException when serialize object to json", e);
        }
        return (T) object;
    }

    /**
     * deserialize a JSON String to List.
     *
     * @param json json string
     * @param clazz Class.class
     * @param <T> the type of the element
     * @return class instance
     */
    public static <T> List<T> deserializeToList(String json, Class<T> clazz) {
        List<T> object = null;
        try {
            JavaType javaType =
                OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(ArrayList.class, TypeFactory.rawClass(clazz));
            object = OBJECT_MAPPER.readValue(json, javaType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException when serialize object to json", e);
            throw new DataTypeCastException(e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException when serialize object to json", e);
            new DataTypeCastException(e);
        } catch (IOException e) {
            logger.error("IOException when serialize object to json", e);
        }
        return object;
    }

    /**
     * Object to Json String.
     *
     * @param obj Object
     * @return String
     */
    public static String objToJsonStrWithNoPretty(Object obj) {

        try {
            return OBJECT_WRITER_UN_PRETTY_PRINTER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new DataTypeCastException(e);
        }
    }

    /**
     * Convert a Map to compact Json output, with keys ordered. Use Jackson JsonNode toString() to
     * ensure key order and compact output.
     *
     * @param map input map
     * @return JsonString
     * @throws Exception IOException
     */
    public static String mapToCompactJson(Map<String, Object> map) throws Exception {
        return OBJECT_MAPPER.readTree(serialize(map)).toString();
    }

    /**
     * Convert a POJO to Map.
     *
     * @param object POJO
     * @return Map
     * @throws Exception IOException
     */
    public static Map<String, Object> objToMap(Object object) throws Exception {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(serialize(object));
        return (HashMap<String, Object>) OBJECT_MAPPER.convertValue(jsonNode, HashMap.class);
    }

    /**
     * 对象深度复制(对象必须是实现了Serializable接口).
     *
     * @param obj pojo
     * @param <T> the type of the element
     * @return Object clonedObj
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            logger.error("clone object has error.", e);
        }
        return clonedObj;
    }

    /**
     * Load Json Object. Can be used to return both Json Data and Json Schema.
     *
     * @param jsonString the json string
     * @return JsonNode
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static JsonNode loadJsonObject(String jsonString) throws IOException {
        return JsonLoader.fromString(jsonString);
    }

    /**
     * Validate Json Data versus Json Schema.
     *
     * @param jsonData the json data
     * @param jsonSchema the json schema
     * @return true if yes, false otherwise
     * @throws Exception the exception
     */
    public static boolean isValidateJsonVersusSchema(String jsonData, String jsonSchema)
        throws Exception {
        JsonNode jsonDataNode = loadJsonObject(jsonData);
        JsonNode jsonSchemaNode = loadJsonObject(jsonSchema);
        JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(jsonSchemaNode);

        ProcessingReport report = schema.validate(jsonDataNode);
        if (report.isSuccess()) {
            logger.info(report.toString());
            return true;
        } else {
            Iterator<ProcessingMessage> it = report.iterator();
            StringBuffer errorMsg = new StringBuffer();
            while (it.hasNext()) {
                errorMsg.append(it.next().getMessage());
            }
            logger.error("Json schema validator failed, error: {}", errorMsg.toString());
            return false;
        }
    }

    /**
     * Validate Json Schema format validity.
     *
     * @param jsonSchema the json schema
     * @return true if yes, false otherwise
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean isValidJsonSchema(String jsonSchema) throws IOException {
        return JsonSchemaFactory
            .byDefault()
            .getSyntaxValidator()
            .schemaIsValid(loadJsonObject(jsonSchema));
    }

    /**
     * validate Cpt Json Schema validity .
     *
     * @param cptJsonSchema the cpt json schema
     * @return true, if is cpt json schema valid
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean isCptJsonSchemaValid(String cptJsonSchema) throws IOException {
        return StringUtils.isNotEmpty(cptJsonSchema)
            && isValidJsonSchema(cptJsonSchema)
            && cptJsonSchema.length() <= WeIdConstant.JSON_SCHEMA_MAX_LENGTH;
    }

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
     */
    public static Sign.SignatureData signMessage(String message, ECKeyPair keyPair) {
        return Sign.signMessage(sha3(message.getBytes(StandardCharsets.UTF_8)), keyPair);
    }

    /**
     * Sign a message based on the given privateKey in Decimal String BigInt. The message passed in
     * WILL BE HASHED.
     *
     * @param message the message
     * @param privateKeyString the private key string
     * @return SignatureData
     */
    public static Sign.SignatureData signMessage(
        String message,
        String privateKeyString) {

        BigInteger privateKey = new BigInteger(privateKeyString);
        ECKeyPair keyPair = new ECKeyPair(privateKey, publicKeyFromPrivate(privateKey));
        return Sign.signMessage(sha3(message.getBytes(StandardCharsets.UTF_8)), keyPair);
    }

    /**
     * Sign a object based on the given privateKey in Decimal String BigInt.
     *
     * @param rawData this rawData to be signed,
     * @param privateKeyString the private key string
     * @return String the data after signature
     */
    public static String sign(
        String rawData,
        String privateKeyString) {

        Sign.SignatureData sigData = signMessage(rawData, privateKeyString);
        return new String(
            base64Encode(simpleSignatureSerialization(sigData)),
            StandardCharsets.UTF_8
        );
    }

    /**
     * Extract the Public Key from the message and the SignatureData.
     *
     * @param message the message
     * @param signatureData the signature data
     * @return publicKey
     * @throws SignatureException Signature is the exception.
     */
    public static BigInteger signatureToPublicKey(
        String message,
        Sign.SignatureData signatureData)
        throws SignatureException {

        return Sign.signedMessageToKey(sha3(message.getBytes(StandardCharsets.UTF_8)),
            signatureData);
    }

    /**
     * Verify whether the message and the Signature matches the given public Key.
     *
     * @param message This should be from the same plain-text source with the signature Data.
     * @param signatureData This must be in SignatureData. Caller should call deserialize.
     * @param publicKey This must be in BigInteger. Callseer should convert it to BigInt.
     * @return true if yes, false otherwise
     * @throws SignatureException Signature is the exception.
     */
    public static boolean verifySignature(
        String message,
        Sign.SignatureData signatureData,
        BigInteger publicKey)
        throws SignatureException {

        BigInteger extractedPublicKey = signatureToPublicKey(message, signatureData);
        return extractedPublicKey.equals(publicKey);
    }

    /**
     * Verify whether the message and the Signature matches the given public Key.
     *
     * @param message This should be from the same plain-text source with the signature Data.
     * @param signature this is a signature string of Base64.
     * @param publicKey This must be in BigInteger. Caller should convert it to BigInt.
     * @return true if yes, false otherwise
     * @throws SignatureException Signature is the exception.
     */
    public static boolean verifySignature(
        String message,
        String signature,
        BigInteger publicKey)
        throws SignatureException {

        Sign.SignatureData signatureData = convertBase64StringToSignatureData(signature);
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
    public static Sign.SignatureData rawSignatureDeserializationV2(int v, byte[] r, byte[] s) {
    	byte valueByte = (byte) v;
        return new Sign.SignatureData(valueByte, r, s);
    }

    /**
     * Verify a signature based on the provided raw data, and the WeID Document from chain. This
     * will traverse each public key in the WeID Document and fetch all keys which belongs to the
     * authentication list. Then, verify signature to each one; return true if anyone matches. This
     * is used for object checking.
     *
     * @param rawData the rawData to be verified
     * @param signature the Signature Data
     * @param weIdDocument the WeIdDocument to be extracted
     * @return true if yes, false otherwise with exact error codes
     */
    public static ErrorCode verifySignatureFromWeId(
        String rawData,
        String signature,
        WeIdDocument weIdDocument) {

        Sign.SignatureData signatureData = convertBase64StringToSignatureData(signature);
        return verifySignatureFromWeId(rawData, signatureData, weIdDocument);
    }

    /**
     * Verify a signature based on the provided raw data, and the WeID Document from chain. This
     * will traverse each public key in the WeID Document and fetch all keys which belongs to the
     * authentication list. Then, verify signature to each one; return true if anyone matches. This
     * is used in CredentialService and EvidenceService.
     *
     * @param rawData the rawData to be verified
     * @param signatureData the Signature Data structure
     * @param weIdDocument the WeIdDocument to be extracted
     * @return true if yes, false otherwise with exact error codes
     */
    public static ErrorCode verifySignatureFromWeId(
        String rawData,
        Sign.SignatureData signatureData,
        WeIdDocument weIdDocument) {
        List<String> publicKeysListToVerify = new ArrayList<String>();

        // Traverse public key list indexed Authentication key list
        for (AuthenticationProperty authenticationProperty : weIdDocument
            .getAuthentication()) {
            String index = authenticationProperty.getPublicKey();
            for (PublicKeyProperty publicKeyProperty : weIdDocument.getPublicKey()) {
                if (publicKeyProperty.getId().equalsIgnoreCase(index)) {
                    publicKeysListToVerify.add(publicKeyProperty.getPublicKey());
                }
            }
        }
        try {
            boolean result = false;
            for (String publicKeyItem : publicKeysListToVerify) {
                if (StringUtils.isNotEmpty(publicKeyItem)) {
                    result =
                        result
                            || verifySignature(
                            rawData, signatureData, new BigInteger(publicKeyItem));
                }
            }
            if (!result) {
                return ErrorCode.CREDENTIAL_ISSUER_MISMATCH;
            }
        } catch (SignatureException e) {
            logger.error("some exceptions occurred in signature verification", e);
            return ErrorCode.CREDENTIAL_EXCEPTION_VERIFYSIGNATURE;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Convert SignatureData to blockchain-ready RSV format.
     *
     * @param signatureData the signature data
     * @return rsvSignature the rsv signature structure
     */
    public static RsvSignature convertSignatureDataToRsv(
        SignatureData signatureData) {
        Uint8 v = intToUnt8(Integer.valueOf(signatureData.getV()));
        Bytes32 r = bytesArrayToBytes32(signatureData.getR());
        Bytes32 s = bytesArrayToBytes32(signatureData.getS());
        RsvSignature rsvSignature = new RsvSignature();
        rsvSignature.setV(v);
        rsvSignature.setR(r);
        rsvSignature.setS(s);
        return rsvSignature;
    }

    /**
     * Convert an off-chain Base64 signature String to signatureData format.
     *
     * @param base64Signature the signature string in Base64
     * @return signatureData structure
     */
    public static SignatureData convertBase64StringToSignatureData(String base64Signature) {
        return simpleSignatureDeserialization(
            base64Decode(base64Signature.getBytes(StandardCharsets.UTF_8))
        );
    }

    /**
     * Get the UUID and remove the '-'.
     *
     * @return return the UUID of the length is 32
     */
    public static String getUuId32() {
        return UUID.randomUUID().toString().replaceAll(SEPARATOR_CHAR, StringUtils.EMPTY);
    }

    /**
     * Compress JSON String.
     *
     * @param arg the compress string
     * @return return the value of compressed
     * @throws IOException IOException
     */
    public static String compress(String arg) throws IOException {
        if (null == arg || arg.length() <= 0) {
            return arg;
        }
        ByteArrayOutputStream out = null;
        GZIPOutputStream gzip = null;
        try {
            out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(arg.getBytes(StandardCharsets.UTF_8.toString()));
            close(gzip);
            String value = out.toString(StandardCharsets.ISO_8859_1.toString());
            return value;
        } finally {
            close(out);
        }
    }

    /**
     * Decompression of String data.
     *
     * @param arg String data with decompression
     * @return return the value of decompression
     * @throws IOException IOException
     */
    public static String unCompress(String arg) throws IOException {
        if (null == arg || arg.length() <= 0) {
            return arg;
        }
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        GZIPInputStream gzip = null;
        try {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(arg.getBytes(StandardCharsets.ISO_8859_1.toString()));
            gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n = 0;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            String value = out.toString(StandardCharsets.UTF_8.toString());
            return value;
        } finally {
            close(gzip);
            close(in);
            close(out);
        }
    }

    private static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                logger.error("close OutputStream error", e);
            }
        }
    }

    private static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                logger.error("close InputStream error", e);
            }
        }
    }

    private static BufferedImage createImage(
        String content,
        String imgPath,
        ErrorCorrectionLevel errorCorrectionLevel,
        boolean needCompress)
        throws WriterException, IOException {

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix =
            new MultiFormatWriter()
                .encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (StringUtils.isBlank(imgPath)) {
            return image;
        }
        // 插入图片
        insertImage(image, imgPath, needCompress);
        return image;
    }

    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress)
        throws IOException {

        File file = new File(imgPath);
        if (!file.exists()) {
            logger.error("imgPath:[{}] is not exists.", imgPath);
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > LOGO_WIDTH) {
                width = LOGO_WIDTH;
            }
            if (height > LOGO_HEIGHT) {
                height = LOGO_HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    private static void qrCodeEncode(
        String content,
        String imgPath,
        String destPath,
        ErrorCorrectionLevel errorCorrectionLevel,
        boolean needCompress)
        throws WriterException, IOException {

        BufferedImage image = createImage(content, imgPath, errorCorrectionLevel, needCompress);
        ImageIO.write(image, FORMAT_NAME, new File(destPath));
    }

    /**
     * 生成不带LOGO的二维码并保存到指定文件中.
     *
     * @param content 二维码字符串
     * @param destPath 二维码图片保存文件路径
     * @param errorCorrectionLevel 容错级别
     * @return code of ErrorCode
     */
    public static Integer generateQrCode(
        String content,
        ErrorCorrectionLevel errorCorrectionLevel,
        String destPath) {

        try {
            qrCodeEncode(content, null, destPath, errorCorrectionLevel, false);
            return ErrorCode.SUCCESS.getCode();
        } catch (WriterException e) {
            logger.error("generateQrCode into file WriterException.", e);
        } catch (IOException e) {
            logger.error("generateQrCode into file IOException.", e);
        }
        return ErrorCode.UNKNOW_ERROR.getCode();
    }

    /**
     * 生成不带LOGO的二维码并将二维码的字节输入到字节输出流中.
     *
     * @param content 二维码字符串
     * @param errorCorrectionLevel 容错级别
     * @param stream 字节输出流
     * @return code of ErrorCode
     */
    public static Integer generateQrCode(
        String content,
        ErrorCorrectionLevel errorCorrectionLevel,
        OutputStream stream) {

        try {
            BufferedImage image = createImage(content, null, errorCorrectionLevel, false);
            ImageIO.write(image, FORMAT_NAME, stream);
            return ErrorCode.SUCCESS.getCode();
        } catch (WriterException e) {
            logger.error("generateQrCode into OutputStream WriterException.", e);
        } catch (IOException e) {
            logger.error("generateQrCode into OutputStream IOException.", e);
        }
        return ErrorCode.UNKNOW_ERROR.getCode();
    }


    /**
     * Bytes array to bytes 32.
     *
     * @param byteValue the byte value
     * @return the bytes 32
     */
    public static Bytes32 bytesArrayToBytes32(byte[] byteValue) {

        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    /**
     * String to bytes 32.
     *
     * @param string the string
     * @return the bytes 32
     */
    public static Bytes32 stringToBytes32(String string) {

        byte[] byteValueLen32 = new byte[32];
        if (StringUtils.isEmpty(string)) {
            return new Bytes32(byteValueLen32);
        }
        byte[] byteValue = string.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);

        return new Bytes32(byteValueLen32);
    }

    /**
     * Bytes 32 to bytes array.
     *
     * @param bytes32 the bytes 32
     * @return the byte[]
     */
    public static byte[] bytes32ToBytesArray(Bytes32 bytes32) {

        byte[] bytesArray = new byte[32];
        byte[] bytes32Value = bytes32.getValue();
        System.arraycopy(bytes32Value, 0, bytesArray, 0, 32);
        return bytesArray;
    }

    /**
     * Convert a Byte32 data to Java String. IMPORTANT NOTE: Byte to String is not 1:1 mapped. So -
     * Know your data BEFORE do the actual transform! For example, Deximal Bytes, or ASCII Bytes are
     * OK to be in Java String, but Encrypted Data, or raw Signature, are NOT OK.
     *
     * @param bytes32 the bytes 32
     * @return String
     */
    public static String bytes32ToString(Bytes32 bytes32) {

        return new String(bytes32.getValue(), StandardCharsets.UTF_8).trim();
    }

    /**
     * string to byte.
     *
     * @param value stringData
     * @return byte[]
     */
    public static byte[] stringToByteArray(String value) {
        if (StringUtils.isBlank(value)) {
            return new byte[1];
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * string to byte32.
     *
     * @param value stringData
     * @return byte[]
     */
    public static byte[] stringToByte32Array(String value) {
        if (StringUtils.isBlank(value)) {
            return new byte[32];
        }

        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] newBytes = new byte[32];

        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        return newBytes;
    }

    /**
     * string to byte32List.
     *
     * @param data stringData
     * @param size size of byte32List
     */
    public static List<byte[]> stringToByte32ArrayList(String data, int size) {
        List<byte[]> byteList = new ArrayList<>();

        if (StringUtils.isBlank(data)) {
            for (int i = 0; i < size; i++) {
                byteList.add(new byte[32]);
            }
            return byteList;
        }

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        if (dataBytes.length <= WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH) {
            byte[] newBytes = new byte[32];
            System.arraycopy(dataBytes, 0, newBytes, 0, dataBytes.length);
            byteList.add(newBytes);
        } else {
            byteList = splitBytes(dataBytes, size);
        }

        if (byteList.size() < size) {
            List<byte[]> addList = new ArrayList<>();
            for (int i = 0; i < size - byteList.size(); i++) {
                addList.add(new byte[32]);
            }
            byteList.addAll(addList);
        }
        return byteList;
    }

    private static synchronized List<byte[]> splitBytes(byte[] bytes, int size) {
        List<byte[]> byteList = new ArrayList<>();
        double splitLength =
            Double.parseDouble(WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH + "");
        int arrayLength = (int) Math.ceil(bytes.length / splitLength);
        byte[] result = new byte[arrayLength];

        int from = 0;
        int to = 0;

        for (int i = 0; i < arrayLength; i++) {
            from = (int) (i * splitLength);
            to = (int) (from + splitLength);

            if (to > bytes.length) {
                to = bytes.length;
            }

            result = Arrays.copyOfRange(bytes, from, to);
            if (result.length < size) {
                byte[] newBytes = new byte[32];
                System.arraycopy(result, 0, newBytes, 0, result.length);
                byteList.add(newBytes);
            } else {
                byteList.add(result);
            }
        }
        return byteList;
    }
    /**
     * convert bytesArrayList to Bytes32ArrayList.
     * @param list byte size
     * @param size size
     * @return result
     */
    public static List<byte[]> bytesArrayListToBytes32ArrayList(List<byte[]> list, int size) {
        List<byte[]> bytesList = new ArrayList<>();

        if (list.isEmpty()) {
            for (int i = 0; i < size; i++) {
                bytesList.add(new byte[32]);
            }
            return bytesList;
        }

        for (byte[] bytes : list) {
            if (bytes.length <= WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH) {
                byte[] newBytes = new byte[32];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                bytesList.add(newBytes);
            }
        }

        if (bytesList.size() < size) {
            List<byte[]> addList = new ArrayList<>();
            for (int i = 0; i < size - bytesList.size(); i++) {
                addList.add(new byte[32]);
            }
            bytesList.addAll(addList);
        }
        return bytesList;
    }

    /**
     * Bytes 32 to string without trim.
     *
     * @param bytes32 the bytes 32
     * @return the string
     */
    public static String bytes32ToStringWithoutTrim(Bytes32 bytes32) {

        byte[] strs = bytes32.getValue();
        return new String(strs, StandardCharsets.UTF_8);
    }

    /**
     * Int to uint 256.
     *
     * @param value the value
     * @return the uint 256
     */
    public static Uint256 intToUint256(int value) {
        return new Uint256(new BigInteger(String.valueOf(value)));
    }

    /**
     * Uint 256 to int.
     *
     * @param value the value
     * @return the int
     */
    public static int uint256ToInt(Uint256 value) {
        return value.getValue().intValue();
    }

    /**
     * String to dynamic bytes.
     *
     * @param input the input
     * @return the dynamic bytes
     */
    public static DynamicBytes stringToDynamicBytes(String input) {

        return new DynamicBytes(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Dynamic bytes to string.
     *
     * @param input the input
     * @return the string
     */
    public static String dynamicBytesToString(DynamicBytes input) {
        return new String(input.getValue(), StandardCharsets.UTF_8);
    }

    /**
     * Int to int 256.
     *
     * @param value the value
     * @return the int 256
     */
    public static Int256 intToInt256(int value) {
        return new Int256(value);
    }

    /**
     * Int 256 to int.
     *
     * @param value the value
     * @return the int
     */
    public static int int256ToInt(Int256 value) {
        return value.getValue().intValue();
    }

    /**
     * Int to unt 8.
     *
     * @param value the value
     * @return the uint 8
     */
    public static Uint8 intToUnt8(int value) {
        return new Uint8(value);
    }

    /**
     * Uint 8 to int.
     *
     * @param value the value
     * @return the int
     */
    public static int uint8ToInt(Uint8 value) {
        return value.getValue().intValue();
    }

    /**
     * Long to int 256.
     *
     * @param value the value
     * @return the int 256
     */
    public static Int256 longToInt256(long value) {
        return new Int256(value);
    }

    /**
     * Int 256 to long.
     *
     * @param value the value
     * @return the long
     */
    public static long int256ToLong(Int256 value) {
        return value.getValue().longValue();
    }

    /**
     * Long array to int 256 static array.
     *
     * @param longArray the long array
     * @return the static array
     */
    public static StaticArray<Int256> longArrayToInt256StaticArray(long[] longArray) {
        List<Int256> int256List = new ArrayList<Int256>();
        for (int i = 0; i < longArray.length; i++) {
            int256List.add(longToInt256(longArray[i]));
        }
        StaticArray<Int256> in256StaticArray = new StaticArray<Int256>(int256List);
        return in256StaticArray;
    }

    /**
     * String array to bytes 32 static array.
     *
     * @param stringArray the string array
     * @return the static array
     */
    public static StaticArray<Bytes32> stringArrayToBytes32StaticArray(String[] stringArray) {

        List<Bytes32> bytes32List = new ArrayList<Bytes32>();
        for (int i = 0; i < stringArray.length; i++) {
            if (StringUtils.isNotEmpty(stringArray[i])) {
                bytes32List.add(stringToBytes32(stringArray[i]));
            } else {
                bytes32List.add(stringToBytes32(StringUtils.EMPTY));
            }
        }
        StaticArray<Bytes32> bytes32StaticArray = new StaticArray<Bytes32>(bytes32List);
        return bytes32StaticArray;
    }

    /**
     * String array to bytes 32 static array.
     *
     * @param addressArray the string array
     * @return the static array
     */
    public static StaticArray<Address> addressArrayToAddressStaticArray(Address[] addressArray) {

        List<Address> addressList = new ArrayList<>();
        for (int i = 0; i < addressArray.length; i++) {
            addressList.add(addressArray[i]);
        }
        StaticArray<Address> addressStaticArray = new StaticArray<Address>(addressList);
        return addressStaticArray;
    }

    /**
     * Bytes 32 dynamic array to string array without trim.
     *
     * @param bytes32DynamicArray the bytes 32 dynamic array
     * @return the string[]
     */
    public static String[] bytes32DynamicArrayToStringArrayWithoutTrim(
        DynamicArray<Bytes32> bytes32DynamicArray) {

        List<Bytes32> bytes32List = bytes32DynamicArray.getValue();
        String[] stringArray = new String[bytes32List.size()];
        for (int i = 0; i < bytes32List.size(); i++) {
            stringArray[i] = bytes32ToStringWithoutTrim(bytes32List.get(i));
        }
        return stringArray;
    }

    /**
     * Int 256 dynamic array to long array.
     *
     * @param int256DynamicArray the int 256 dynamic array
     * @return the long[]
     */
    public static long[] int256DynamicArrayToLongArray(DynamicArray<Int256> int256DynamicArray) {

        List<Int256> int256list = int256DynamicArray.getValue();
        long[] longArray = new long[int256list.size()];
        for (int i = 0; i < int256list.size(); i++) {
            longArray[i] = int256ToLong(int256list.get(i));
        }
        return longArray;
    }

    /**
     * convert list to BigInteger list.
     * @param list BigInteger list
     * @param size size
     * @return result
     */
    public static List<BigInteger> listToListBigInteger(List<BigInteger> list, int size) {
        List<BigInteger> bigIntegerList = new ArrayList<>();
        for (BigInteger bs : list) {
            bigIntegerList.add(bs);
        }

        List<BigInteger> addList = new ArrayList<>();
        if (bigIntegerList.size() < size) {
            for (int i = 0; i < size - bigIntegerList.size(); i++) {
                addList.add(BigInteger.ZERO);
            }
            bigIntegerList.addAll(addList);
        }
        return bigIntegerList;
    }
    
    /**
     * Generate Default CPT Json Schema based on a given CPT ID.
     *
     * @param cptId the CPT ID
     * @return CPT Schema in Json String
     */
    public static String generateDefaultCptJsonSchema(Integer cptId) {
        String cptClassStr = "com.webank.weid.protocol.cpt.Cpt" + cptId;
        try {
            return generateDefaultCptJsonSchema(Class.forName(cptClassStr));
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Generate Default CPT Json Schema based on a given class, to support external invocation.
     *
     * @param myClass the CPT ID
     * @return CPT Schema in Json String
     */
    public static String generateDefaultCptJsonSchema(Class myClass) {
        try {
            com.github.reinert.jjschema.v1.JsonSchemaFactory schemaFactory
                = new JsonSchemaV4Factory();
            schemaFactory.setAutoPutDollarSchema(true);
            JsonNode cptSchema = schemaFactory.createSchema(myClass);
            return DataToolUtils.objToJsonStrWithNoPretty(cptSchema);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Generate unformatted CPT which allows any format, to support external invocation.
     *
     * @return CPT Schema in Json String
     */
    public static String generateUnformattedCptJsonSchema() {
        List<Map<String, String>> anyStringList = new ArrayList<>();
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("type", "string");
        anyStringList.add(stringMap);
        Map<String, String> nullMap = new HashMap<>();
        nullMap.put("type", "null");
        anyStringList.add(nullMap);
        Map<String, Object> anyMap = new LinkedHashMap<>();
        anyMap.put("anyOf", anyStringList);
        Map<String, Object> patternMap = new LinkedHashMap<>();
        patternMap.put("^.*$", anyMap);
        Map<String, Object> cptSchemaMap = new LinkedHashMap<>();
        cptSchemaMap.put(JsonSchemaConstant.SCHEMA_KEY, JsonSchemaConstant.SCHEMA_VALUE);
        cptSchemaMap.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_OBJECT);
        cptSchemaMap.put("title", "Unformatted CPT");
        cptSchemaMap.put("description", "Universal unformatted CPT template");
        cptSchemaMap.put("patternProperties", patternMap);
        return DataToolUtils.objToJsonStrWithNoPretty(cptSchemaMap);
    }
    
    
    /**
     * convert byte32List to String.
     * @param bytesList list
     * @param size size
     * @return reuslt
     */
    public static synchronized String byte32ListToString(List<byte[]> bytesList, int size) {
        if (bytesList.isEmpty()) {
            return "";
        }

        int zeroCount = 0;
        for (int i = 0; i < bytesList.size(); i++) {
            for (int j = 0; j < bytesList.get(i).length; j++) {
                if (bytesList.get(i)[j] == 0) {
                    zeroCount++;
                }
            }
        }

        if (WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH * size - zeroCount == 0) {
            return "";
        }

        byte[] newByte = new byte[WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH * size - zeroCount];
        int index = 0;
        for (int i = 0; i < bytesList.size(); i++) {
            for (int j = 0; j < bytesList.get(i).length; j++) {
                if (bytesList.get(i)[j] != 0) {
                    newByte[index] = bytesList.get(i)[j];
                    index++;
                }
            }
        }

        return (new String(newByte)).toString();
    }
    
    /**
     * Get the current timestamp as the param "created". May be called elsewhere.
     *
     * @return the StaticArray
     */
    public static List<BigInteger> getParamCreatedList(int length) {
        long created = System.currentTimeMillis();
        List<BigInteger> createdList = new ArrayList<>();
        createdList.add(BigInteger.valueOf(created));
        return createdList;
    }
}

