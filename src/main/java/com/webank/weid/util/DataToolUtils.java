
package com.webank.weid.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.cpt.RawCptSchema;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.response.RsvSignature;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 数据工具类.
 *
 * @author tonychen 2019年4月23日
 */
public final class DataToolUtils {

    private static final Logger logger = LoggerFactory.getLogger(DataToolUtils.class);
    private static final String SEPARATOR_CHAR = "-";
    //private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * default salt length.
     */
    private static final String DEFAULT_SALT_LENGTH = "5";

    private static final int SERIALIZED_SIGNATUREDATA_LENGTH = 65;

    private static final int radix = 10;

    private static final String TO_JSON = "toJson";

    private static final String FROM_JSON = "fromJson";

    private static final String KEY_CREATED = "created";

    private static final String KEY_ISSUANCEDATE = "issuanceDate";

    private static final String KEY_EXPIRATIONDATE = "expirationDate";

    private static final String KEY_CLAIM = "claim";

    private static final String KEY_FROM_TOJSON = "$from";

    private static final List<String> CONVERT_UTC_LONG_KEYLIST = new ArrayList<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    //private static final ObjectWriter OBJECT_WRITER;
    //private static final ObjectReader OBJECT_READER;
    private static final ObjectWriter OBJECT_WRITER_UN_PRETTY_PRINTER;

    private static final com.networknt.schema.JsonSchemaFactory JSON_SCHEMA_FACTORY;

    public static final String deployStyle = PropertyUtils.getProperty("deploy.style");

    //Todo:后面把两个配置文件的cryptoType合成一个，只放在weidentity.properties文件
    public static int cryptoType = Integer.parseInt(PropertyUtils.getProperty("crypto.type"));

    public static final CryptoSuite cryptoSuite = new CryptoSuite(Integer.parseInt(PropertyUtils.getProperty("crypto.type")));
    /**
     * use this to create key pair of v2 or v3
     * WARN: create keyPair must use BigInteger of privateKey or decimal String of privateKey
     */
    static {
        // sort by letter
        OBJECT_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        // when map is serialization, sort by key
        OBJECT_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // ignore mismatched fields
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        // use field for serialize and deSerialize
        OBJECT_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        OBJECT_WRITER_UN_PRETTY_PRINTER = OBJECT_MAPPER.writer();

        CONVERT_UTC_LONG_KEYLIST.add(KEY_CREATED);
        CONVERT_UTC_LONG_KEYLIST.add(KEY_ISSUANCEDATE);
        CONVERT_UTC_LONG_KEYLIST.add(KEY_EXPIRATIONDATE);

        //OBJECT_WRITER = OBJECT_MAPPER.writer().withDefaultPrettyPrinter();
        //OBJECT_READER = OBJECT_MAPPER.reader();

        JSON_SCHEMA_FACTORY = com.networknt.schema.JsonSchemaFactory.getInstance(VersionFlag.V4);

        if (deployStyle.equals("blockchain")) {
            cryptoType = com.webank.weid.blockchain.util.DataToolUtils.cryptoType;
        }

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
     * Sha 3.
     *
     * @param input the input
     * @return the string
     */
    public static String hash(String input) {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.hash(input);
        } else {
            // default database
            return Numeric.toHexString(hash(input.getBytes(StandardCharsets.UTF_8)));
        }
    }

    /**
     * Sha 3.
     *
     * @param input the input
     * @return the byte[]
     */
    public static byte[] hash(byte[] input) {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.hash(input);
        } else {
            // default database
            return cryptoSuite.hash(input);
        }
    }

    public static String getHash(String hexInput) {
        return hash(hexInput);
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
     * Check whether the String is a valid hash.
     *
     * @param hashValue hash in String
     * @return true if yes, false otherwise
     */
    public static boolean isValidHash(String hashValue) {
        return !StringUtils.isEmpty(hashValue)
                && Pattern.compile(WeIdConstant.HASH_VALUE_PATTERN).matcher(hashValue).matches();
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
            if (isValidFromToJson(json)) {
                logger.error("this jsonString is converted by toJson(), "
                        + "please use fromJson() to deserialize it");
                throw new DataTypeCastException("deserialize json to Object error");
            }
            object = OBJECT_MAPPER.readValue(json, TypeFactory.rawClass(clazz));
        } catch (JsonParseException e) {
            logger.error("JsonParseException when deserialize json to object", e);
            throw new DataTypeCastException(e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException when deserialize json to object", e);
            throw new DataTypeCastException(e);
        } catch (IOException e) {
            logger.error("IOException when deserialize json to object", e);
            throw new DataTypeCastException(e);
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
     * readLocalJson
     * @return {@link Object}
     * @throws IOException ioexception
     */
    public static  Map<String, String> readFromLocal(String path) throws IOException {
        try {
            File jsonFile = new File(path);
            return OBJECT_MAPPER.readValue(jsonFile, new TypeReference<Map<String,String>>() {
            });
        }catch (FileNotFoundException e){
            Map<String,String> map = new HashMap<>();
            String[] parts = path.split("/");
            map.put("tableName",parts[5]);
            writeInLocal(path,map);
            readFromLocal(path);
        }
        return null;

    }

    /**
     * @param path 路径
     * @param obj  obj
     * @return int
     * @throws IOException ioexception
     */
    public static int writeInLocal(String path,Object obj) throws IOException {
        File jsonFile = new File(path);
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(jsonFile, obj);
        return 0;
    }

    public static String bytesToStr(byte[] bytes) throws IOException {
        return OBJECT_MAPPER.readTree(bytes).toPrettyString();
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
     * Convert a Map to compact Json output, with keys ordered. Use Jackson JsonNode toString() to
     * ensure key order and compact output.
     *
     * @param map input map
     * @return JsonString
     * @throws Exception IOException
     */
    public static String stringMapToCompactJson(Map<String, String> map) throws Exception {
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
     * Convert a MAP to POJO.
     *
     * @param map the input data
     * @param <T> the type of the element
     * @param clazz the output class type
     * @return object in T type
     * @throws Exception IOException
     */
    public static <T> T mapToObj(Map<String, Object> map, Class<T> clazz) throws Exception {
        final T pojo = (T) OBJECT_MAPPER.convertValue(map, clazz);
        return pojo;
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
     * @throws JsonProcessingException parse json fail
     */
    public static JsonNode loadJsonObject(String jsonString) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(jsonString);

    }

    /**
     * load json from resource
     * @param path class path file path
     * @return json node
     * @throws IOException load error
     */
    public static JsonNode loadJsonObjectFromResource(String path) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new DataTypeCastException("open path to inputStream get null!");
            }
            return OBJECT_MAPPER.readTree(inputStream);
        }
    }

    /**
     * load json from file
     * @param file file of json
     * @return json node
     * @throws IOException file not found
     */
    public static JsonNode loadJsonObjectFromFile(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return OBJECT_MAPPER.readTree(inputStream);
        } catch (FileNotFoundException e) {
            logger.error("file not found when load jsonObject:{}", file.getPath());
            throw new DataTypeCastException(e);
        }
    }


    /**
     * Validate Json Data versus Json Schema.
     *
     * @param jsonData the json data
     * @param jsonSchema the json schema
     * @return empty if yes, not empty otherwise
     * @throws Exception the exception
     */
    public static Set<ValidationMessage> checkJsonVersusSchema(String jsonData, String jsonSchema)
            throws Exception {
        JsonNode jsonDataNode = loadJsonObject(jsonData);
        JsonNode jsonSchemaNode = loadJsonObject(jsonSchema);
        // use new validator
        com.networknt.schema.JsonSchema schema = JSON_SCHEMA_FACTORY.getSchema(jsonSchemaNode);
        Set<ValidationMessage> report = schema.validate(jsonDataNode);
        if (report.size() == 0) {
            logger.info(report.toString());
        } else {
            Iterator<ValidationMessage> it = report.iterator();
            StringBuffer errorMsg = new StringBuffer();
            while (it.hasNext()) {
                ValidationMessage msg = it.next();
                errorMsg.append(msg.getCode()).append(":").append(msg.getMessage());
            }
            logger.error("Json schema validator failed, error: {}", errorMsg.toString());
        }
        return report;
//        JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(jsonSchemaNode);
//        ProcessingReport report = schema.validate(jsonDataNode);
//        if (report.isSuccess()) {
//            logger.info(report.toString());
//        } else {
//            Iterator<ProcessingMessage> it = report.iterator();
//            StringBuffer errorMsg = new StringBuffer();
//            while (it.hasNext()) {
//                errorMsg.append(it.next().getMessage());
//            }
//            logger.error("Json schema validator failed, error: {}", errorMsg.toString());
//        }
//        return report;
    }

    /**
     * Validate Json Schema format validity.
     *
     * @param jsonSchema the json schema
     * @return true if yes, false otherwise
     * @throws IOException Signals that an I/O exception has occurred.
     */
//    public static boolean isValidJsonSchema(String jsonSchema) {
//        return JsonSchemaFactory
//            .byDefault()
//            .getSyntaxValidator()
//            .schemaIsValid(loadJsonObject(jsonSchema));
//    }

    /**
     * validate Cpt Json Schema validity .
     *
     * @param cptJsonSchema the cpt json schema
     * @return true, if is cpt json schema valid
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean isCptJsonSchemaValid(String cptJsonSchema) throws IOException {
        return StringUtils.isNotEmpty(cptJsonSchema)
//            && isValidJsonSchema(cptJsonSchema)
                && cptJsonSchema.length() <= WeIdConstant.JSON_SCHEMA_MAX_LENGTH;
    }

    /**
     * Check if this json string is in valid format.
     *
     * @param json Json string
     * @return true if yes, false otherwise
     */
    public static boolean isValidJsonStr(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * create new cpt json schema.
     *
     * @param args Map
     * @return String
     */
    public static String cptSchemaToString(CptMapArgs args) {

        Map<String, Object> cptJsonSchema = args.getCptJsonSchema();
        Map<String, Object> cptJsonSchemaNew = new HashMap<String, Object>();
        cptJsonSchemaNew.put(JsonSchemaConstant.SCHEMA_KEY, JsonSchemaConstant.SCHEMA_VALUE);
        cptJsonSchemaNew.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_OBJECT);
        cptJsonSchemaNew.putAll(cptJsonSchema);
        String cptType = args.getCptType().getName();
        cptJsonSchemaNew.put(CredentialConstant.CPT_TYPE_KEY, cptType);
        return DataToolUtils.serialize(cptJsonSchemaNew);
    }


    /**
     * Secp256k1 sign to Signature.
     *
     * @param rawData original raw data
     * @param privateKey decimal
     * @return SignatureData for signature value
     */
    public static RsvSignature signToRsvSignature(String rawData, String privateKey) {
        if (deployStyle.equals("blockchain")) {
            return RsvSignature.fromBlockChain(com.webank.weid.blockchain.util.DataToolUtils.signToRsvSignature(rawData, privateKey));
        } else {
            // default database
            String messageHash = hash(rawData);
            return sign(messageHash, privateKey);
        }
    }

    public static RsvSignature sign(String messageHash, String privateKey) {
        CryptoKeyPair cryptoKeyPair =  cryptoSuite.getKeyPairFactory().createKeyPair(new BigInteger(privateKey));
        RsvSignature rsvSignature = new RsvSignature();
        SignatureResult signatureResult = cryptoSuite.sign(messageHash, cryptoKeyPair);
        Bytes32 R = new Bytes32(signatureResult.getR());
        rsvSignature.setR(R);
        Bytes32 S = new Bytes32(signatureResult.getS());
        rsvSignature.setS(S);
        if(cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE){
            ECDSASignatureResult ecdsaSignatureResult = new ECDSASignatureResult(signatureResult.convertToString());
            rsvSignature.setV(new Uint8(BigInteger.valueOf(ecdsaSignatureResult.getV())));
        } else {
            rsvSignature.setV(new Uint8(0));
        }
        return rsvSignature;
    }

    /**
     * Serialize secp256k1 signature into base64 encoded, in R, S, V (0, 1) format.
     *
     * @param sigData secp256k1 signature (v = 0,1)
     * @return base64 string
     */
    public static String SigBase64Serialization(
            RsvSignature sigData) {
        byte[] sigBytes = new byte[65];
        sigBytes[64] = sigData.getV().getValue().byteValue();
        System.arraycopy(sigData.getR().getValue(), 0, sigBytes, 0, 32);
        System.arraycopy(sigData.getS().getValue(), 0, sigBytes, 32, 32);
        return new String(base64Encode(sigBytes), StandardCharsets.UTF_8);
    }

    /**
     * De-Serialize secp256k1 signature base64 encoded string, in R, S, V (0, 1) format.
     *
     * @param signature signature base64 string
     * @return secp256k1 signature (v = 0,1)
     */
    public static RsvSignature SigBase64Deserialization(String signature) {
        byte[] sigBytes = base64Decode(signature.getBytes(StandardCharsets.UTF_8));
        if (SERIALIZED_SIGNATUREDATA_LENGTH != sigBytes.length) {
            throw new WeIdBaseException("signature data illegal");
        }
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        System.arraycopy(sigBytes, 0, r, 0, 32);
        System.arraycopy(sigBytes, 32, s, 0, 32);
        RsvSignature rsvSignature = new RsvSignature();
        rsvSignature.setR(new Bytes32(r));
        rsvSignature.setS(new Bytes32(s));
        rsvSignature.setV(new Uint8(sigBytes[64]));
        return rsvSignature;
    }

    /**
     * Verify secp256k1 signature.
     *
     * @param rawData original raw data
     * @param signatureBase64 signature string
     * @param publicKey in BigInteger format
     * @return return boolean result, true is success and false is fail
     */
    public static boolean verifySignature(
            String rawData,
            String signatureBase64,
            BigInteger publicKey
    ) {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.verifySignature(rawData, signatureBase64, publicKey);
        } else {
            // default database
            try {
                if (rawData == null) {
                    return false;
                }
                RsvSignature rsvSignature = SigBase64Deserialization(signatureBase64);
                String messageHash = hash(rawData);
                return verifySignature(publicKey.toString(16), messageHash, rsvSignature);
            } catch (Exception e) {
                logger.error("Error occurred during secp256k1 sig verification: {}", e);
                return false;
            }
        }
    }

    /**
     * Verify secp256k1 signature.
     *
     * @param hexPublicKey publicKey in hex string
     * @param messageHash hash of original raw data
     * @param rsvSignature signature value
     * @return return boolean result, true is success and false is fail
     */
    public static boolean verifySignature(
            String hexPublicKey,
            String messageHash,
            RsvSignature rsvSignature
    ) {
        if(cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE) {
            ECDSASignatureResult signatureResult = new ECDSASignatureResult(
                    rsvSignature.getV().getValue().byteValueExact(),
                    rsvSignature.getR().getValue(),
                    rsvSignature.getS().getValue());
            return cryptoSuite.verify(hexPublicKey, messageHash, signatureResult.convertToString());
        } else {
//                byte[] sigBytes = new byte[64];
//                System.arraycopy(rsvSignature.getR(), 0, sigBytes, 0, 32);
//                System.arraycopy(rsvSignature.getS(), 0, sigBytes, 32, 32);
            SM2SignatureResult signatureResult = new SM2SignatureResult(
                    Numeric.hexStringToByteArray(hexPublicKey), //todo pub of sm2 sig
                    rsvSignature.getR().getValue(),
                    rsvSignature.getS().getValue());
            return cryptoSuite.verify(hexPublicKey, messageHash, signatureResult.convertToString());
        }
    }

    /**
     * encrypt the data. todo
     *
     * @param data the data to encrypt
     * @param publicKey public key
     * @return decrypt data
     * @throws Exception encrypt exception
     */
    public static byte[] encrypt(String data, String publicKey) throws Exception {
        /*
        cryptoSuite.ECCEncrypt encrypt = new ECCEncrypt(new BigInteger(publicKey));
            return encrypt.encrypt(data.getBytes());

         */
        return data.getBytes();
    }


    /**
     * decrypt the data. todo
     *
     * @param data the data to decrypt
     * @param privateKey private key
     * @return original data
     * @throws Exception decrypt exception
     */
    public static byte[] decrypt(byte[] data, String privateKey) throws Exception {

        /*ECCDecrypt decrypt = new ECCDecrypt(new BigInteger(privateKey));
        return decrypt.decrypt(data);*/
        return data;
    }


    /**
     * hexString of pub or private key convert to decimal string
     * @param keyInHex private key or pub key in hex string
     * @return decimal string
     */
    public static String hexStr2DecStr(String keyInHex) {
        byte[] keyBytes = Numeric.hexStringToByteArray(keyInHex);
        return new BigInteger(1, keyBytes).toString(10);
    }

    /**
     * generate private key
     * @return decimal private key
     */
    public static String generatePrivateKey() {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.generatePrivateKey();
        } else {
            // default database
            return hexStr2DecStr(cryptoSuite.getKeyPairFactory().generateKeyPair().getHexPrivateKey());
        }
    }

    /**
     * Obtain the PublicKey from given PrivateKey.
     *
     * @param privateKey the private key
     * @return publicKey
     */
    public static BigInteger publicKeyFromPrivate(BigInteger privateKey) {
        return new BigInteger(publicKeyStrFromPrivate(privateKey));
    }

    /**
     * Obtain the PublicKey from given PrivateKey.
     *
     * @param privateKey the private key
     * @return publicKey decimal
     */
    public static String publicKeyStrFromPrivate(BigInteger privateKey) {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.publicKeyStrFromPrivate(privateKey);
        } else {
            // default database
            return hexStr2DecStr(cryptoSuite.getKeyPairFactory().createKeyPair(privateKey).getHexPublicKey());
        }
    }

    /**
     * Obtain the PublicKey from given PrivateKey.
     *
     * @param privateKey the private key
     * @return publicKey
     */
    public static String addressFromPrivate(BigInteger privateKey) {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.addressFromPrivate(privateKey);
        } else {
            // default database
            return cryptoSuite.getKeyPairFactory().createKeyPair(privateKey).getAddress();
        }
    }

    /**
     * Obtain the PublicKey from given PrivateKey.
     *
     * @param publicKey the public key
     * @return publicKey
     */
    public static String addressFromPublic(BigInteger publicKey) {
        if (deployStyle.equals("blockchain")) {
            return com.webank.weid.blockchain.util.DataToolUtils.addressFromPublic(publicKey);
        } else {
            // default database
            return Numeric.toHexString(cryptoSuite.getKeyPairFactory().getAddress(publicKey));
        }
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
     * Verify a signature (base64).
     *
     * @param rawData the rawData to be verified
     * @param signature the Signature Data in Base64 style
     * @param weIdDocument the WeIdDocument to be extracted
     * @param methodId the WeID public key ID
     * @return true if yes, false otherwise with exact error codes
     */
    public static ErrorCode verifySignatureFromWeId(
            String rawData,
            String signature,
            WeIdDocument weIdDocument,
            String methodId) {

        String foundMatchingMethodId = StringUtils.EMPTY;
        try {
            boolean result = false;
            for (AuthenticationProperty authenticationProperty : weIdDocument.getAuthentication()) {
                if (StringUtils.isNotEmpty(authenticationProperty.getPublicKey())) {
                    boolean currentResult = verifySignature(
                            rawData, signature, new BigInteger(authenticationProperty.getPublicKey()));
                    result = currentResult || result;
                    if (currentResult) {
                        foundMatchingMethodId = authenticationProperty.getId();
                        break;
                    }
                }
            }
            if (!result) {
                return ErrorCode.CREDENTIAL_VERIFY_FAIL;
            }
        } catch (Exception e) {
            logger.error("some exceptions occurred in signature verification", e);
            return ErrorCode.CREDENTIAL_EXCEPTION_VERIFYSIGNATURE;
        }
        if (!StringUtils.isEmpty(methodId)
                && !foundMatchingMethodId.equalsIgnoreCase(methodId)) {
            return ErrorCode.CREDENTIAL_VERIFY_SUCCEEDED_WITH_WRONG_PUBLIC_KEY_ID;
        }
        return ErrorCode.SUCCESS;
    }


    /**
     * Convert an off-chain Base64 signature String to signatureData format.
     *
     * @param base64Signature the signature string in Base64
     * @return signatureData structure
     */
    //public static SignatureData convertBase64StringToSignatureData(String base64Signature) {
    /*public static ECDSASignatureResult convertBase64StringToSignatureData(String base64Signature) {
        return simpleSignatureDeserialization(
            base64Decode(base64Signature.getBytes(StandardCharsets.UTF_8))
        );
    }*/

    /**
     * Get the UUID and remove the '-'.
     *
     * @return return the UUID of the length is 32
     */
    public static String getUuId32() {
        return UUID.randomUUID().toString().replaceAll(SEPARATOR_CHAR, StringUtils.EMPTY);
    }

    /**
     * convert byte array to string.
     *
     * @param bytearray byte[]
     * @return String
     */
    public static String byteToString(byte[] bytearray) {
        String result = "";
        char temp;

        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
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
     //     * Generate Default CPT Json Schema based on a given CPT ID.
     * get Default CPT Json Schema from default constant class by given CPT ID
     * @param cptId the CPT ID
     * @return CPT Schema in Json String
     */
    public static String generateDefaultCptJsonSchema(Integer cptId) {
        try {
            return RawCptSchema.getCptSchema(cptId);
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
     * Check if the byte array is empty.
     *
     * @param byteArray the byte[]
     * @return true if empty, false otherwise
     */
    public static boolean isByteArrayEmpty(byte[] byteArray) {
        for (int index = 0; index < byteArray.length; index++) {
            if (byteArray[index] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the current timestamp as the param "created". May be called elsewhere.
     *
     * @param length length
     * @return the StaticArray
     */
    public static List<BigInteger> getParamCreatedList(int length) {
        long created = DateUtils.getNoMillisecondTimeStamp();
        List<BigInteger> createdList = new ArrayList<>();
        createdList.add(BigInteger.ZERO);
        createdList.add(BigInteger.valueOf(created));
        return createdList;
    }

    /**
     * convert timestamp to UTC of json string.
     *
     * @param jsonString json string
     * @return timestampToUtcString
     */
    public static String convertTimestampToUtc(String jsonString) {
        String timestampToUtcString;
        try {
            timestampToUtcString = dealNodeOfConvertUtcAndLong(
                    loadJsonObject(jsonString),
                    CONVERT_UTC_LONG_KEYLIST,
                    TO_JSON
            ).toString();
        } catch (IOException e) {
            logger.error("replaceJsonObj exception.", e);
            throw new DataTypeCastException(e);
        }
        return timestampToUtcString;
    }

    /**
     * convert UTC Date to timestamp of Json string.
     *
     * @param jsonString presentationJson
     * @return presentationJson after convert
     */
    public static String convertUtcToTimestamp(String jsonString) {
        String utcToTimestampString;
        try {
            utcToTimestampString = dealNodeOfConvertUtcAndLong(
                    loadJsonObject(jsonString),
                    CONVERT_UTC_LONG_KEYLIST,
                    FROM_JSON
            ).toString();
        } catch (IOException e) {
            logger.error("replaceJsonObj exception.", e);
            throw new DataTypeCastException(e);
        }
        return utcToTimestampString;
    }

    private static JsonNode dealNodeOfConvertUtcAndLong(
            JsonNode jsonObj,
            List<String> list,
            String type) {
        if (jsonObj.isObject()) {
            return dealObjectOfConvertUtcAndLong((ObjectNode) jsonObj, list, type);
        } else if (jsonObj.isArray()) {
            return dealArrayOfConvertUtcAndLong((ArrayNode) jsonObj, list, type);
        } else {
            return jsonObj;
        }
    }

    private static JsonNode dealObjectOfConvertUtcAndLong(
            ObjectNode jsonObj,
            List<String> list,
            String type) {
        ObjectNode resJson = OBJECT_MAPPER.createObjectNode();
        jsonObj.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode obj = entry.getValue();
            if (obj.isObject()) {
                //JSONObject
                if (key.equals(KEY_CLAIM)) {
                    resJson.set(key, obj);
                } else {
                    resJson.set(key, dealObjectOfConvertUtcAndLong((ObjectNode) obj, list, type));
                }
            } else if (obj.isArray()) {
                //JSONArray
                resJson.set(key, dealArrayOfConvertUtcAndLong((ArrayNode) obj, list, type));
            } else {
                if (list.contains(key)) {
                    if (TO_JSON.equals(type)) {
                        if (isValidLongString(obj.asText())) {
                            resJson.put(
                                    key,
                                    DateUtils.convertNoMillisecondTimestampToUtc(
                                            Long.parseLong(obj.asText())));
                        } else {
                            resJson.set(key, obj);
                        }
                    } else {
                        if (DateUtils.isValidDateString(obj.asText())) {
                            resJson.put(
                                    key,
                                    DateUtils.convertUtcDateToNoMillisecondTime(obj.asText()));
                        } else {
                            resJson.set(key, obj);
                        }
                    }
                } else {
                    resJson.set(key, obj);
                }
            }
        });
        return resJson;
    }

    private static JsonNode dealArrayOfConvertUtcAndLong(
            ArrayNode jsonArr,
            List<String> list,
            String type) {
        ArrayNode resJson = OBJECT_MAPPER.createArrayNode();
        for (int i = 0; i < jsonArr.size(); i++) {
            JsonNode jsonObj = jsonArr.get(i);
            if (jsonObj.isObject()) {
                resJson.add(dealObjectOfConvertUtcAndLong((ObjectNode) jsonObj, list, type));
            } else if (jsonObj.isArray()) {
                resJson.add(dealArrayOfConvertUtcAndLong((ArrayNode) jsonObj, list, type));
            } else {
                resJson.add(jsonObj);
            }
        }
        return resJson;
    }

    /**
     * valid string is a long type.
     *
     * @param str string
     * @return result
     */
    public static boolean isValidLongString(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        long result = 0;
        int i = 0;
        int len = str.length();
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;

        char firstChar = str.charAt(0);
        if (firstChar <= '0') {
            return false;
        }
        multmin = limit / radix;
        while (i < len) {
            digit = Character.digit(str.charAt(i++), radix);
            if (digit < 0) {
                return false;
            }
            if (result < multmin) {
                return false;
            }
            result *= radix;
            if (result < limit + digit) {
                return false;
            }
            result -= digit;
        }
        return true;
    }

    /**
     * valid the json string is converted by toJson().
     *
     * @param json jsonString
     * @return result
     */
    public static boolean isValidFromToJson(String json) {
        if (StringUtils.isBlank(json)) {
            logger.error("input json param is null.");
            return false;
        }
        JsonNode jsonObject = null;
        try {
            jsonObject = loadJsonObject(json);
        } catch (IOException e) {
            logger.error("convert jsonString to JSONObject failed." + e);
            return false;
        }
        return jsonObject.has(KEY_FROM_TOJSON);
    }

    /**
     * add tag which the json string is converted by toJson().
     *
     * @param json jsonString
     * @return result
     */
    public static String addTagFromToJson(String json) {
        JsonNode jsonObject;
        try {
            jsonObject = loadJsonObject(json);
            if (!jsonObject.has(KEY_FROM_TOJSON)) {
                ((ObjectNode) jsonObject).put(KEY_FROM_TOJSON, TO_JSON);
            }
        } catch (IOException e) {
            logger.error("addTagFromToJson fail." + e);
            return json;
        }
        return jsonObject.toString();
    }

    /**
     * remove tag which the json string is converted by toJson().
     *
     * @param json jsonString
     * @return result
     */
    public static String removeTagFromToJson(String json) {
        JsonNode jsonObject;
        try {
            jsonObject = loadJsonObject(json);
            if (jsonObject.has(KEY_FROM_TOJSON)) {
                ((ObjectNode) jsonObject).remove(KEY_FROM_TOJSON);
            }
        } catch (IOException e) {
            logger.error("removeTag fail." + e);
            return json;
        }
        return jsonObject.toString();
    }

    /**
     * Check whether a URL String is a valid endpoint.
     *
     * @param url the endpoint url
     * @return true if yes, false otherwise
     */
    public static boolean isValidEndpointUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        String hostname;
        Integer port;
        String endpointName;
        try {
            URI uri = new URI(url);
            hostname = uri.getHost();
            port = uri.getPort();
            String path = uri.getPath();
            if (StringUtils.isEmpty(hostname) || StringUtils.isEmpty(path) || port < 0) {
                logger.error("Service URL illegal: {}", url);
                return false;
            }
            // Truncate the first slash
            endpointName = path.substring(1);
            if (StringUtils.isEmpty(endpointName)) {
                return false;
            }
        } catch (Exception e) {
            logger.error("Service URL format check failed: {}", url);
            return false;
        }
        return true;
    }

    /**
     * check if the input string is Uft-8.
     *
     * @param string input
     * @return true, otherwise false
     */
    public static boolean isUtf8String(String string) {
        try {
            string.getBytes("UTF-8");
            return true;
        } catch (UnsupportedEncodingException e) {
            logger.error("Passed-in String is not a valid UTF-8 String.");
        }
        return false;
    }

    /**
     * Check whether the address is local address.
     *
     * @param host host string
     * @return true if yes, false otherwise
     */
    public static boolean isLocalAddress(String host) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            logger.error("Unkown host: " + host);
            return false;
        }
        // Check if the address is a valid special local or loop back
        if (addr.isSiteLocalAddress() || addr.isAnyLocalAddress() || addr.isLoopbackAddress()
                || addr.isLinkLocalAddress()) {
            return true;
        }
        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (SocketException e) {
            return false;
        }
    }

    /**
     * Convert a hash string (0x[64Bytes]) into a byte array with 32 bytes length by compressing
     * each two nearby characters into one.
     *
     * @param hash hash String
     * @return byte array
     */
    public static byte[] convertHashStrIntoHashByte32Array(String hash) {
        if (!isValidHash(hash)) {
            return null;
        }
        byte[] originHashByte = hash.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[WeIdConstant.BYTES32_FIXED_LENGTH];
        for (int i = 0; i < WeIdConstant.BYTES32_FIXED_LENGTH; i++) {
            String hex = new String(
                    new byte[]{originHashByte[2 + i * 2], originHashByte[3 + i * 2]});
            int val = Integer.parseInt(hex, 16);
            result[i] = (byte) val;
        }
        return result;
    }

    /**
     * Convert a byte array with 32 bytes into a hash String by stretching the two halfs of a hex
     * byte into two separate hex string. Padding with zeros must be kept in mind.
     *
     * @param hash hash byte array
     * @return hash String
     */
    public static String convertHashByte32ArrayIntoHashStr(byte[] hash) {
        StringBuilder convertedBackStr = new StringBuilder().append(WeIdConstant.HEX_PREFIX);
        for (int i = 0; i < WeIdConstant.BYTES32_FIXED_LENGTH; i++) {
            String hex = Integer
                    .toHexString(((int) hash[i]) >= 0 ? ((int) hash[i]) : ((int) hash[i]) + 256);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            convertedBackStr.append(hex);
        }
        return convertedBackStr.toString();
    }

    /**
     * An intermediate fix to convert Bytes32 Object List from web3sdk 2.x into a real String list.
     *
     * @param byteList Bytes32 Object list
     * @return hash String list
     */
    public static List<String> convertBytes32ObjectListToStringHashList(
            List<Bytes32> byteList) {
        List<String> strList = new ArrayList<>();
        for (int i = 0; i < byteList.size(); i++) {
            strList.add(DataToolUtils.convertHashByte32ArrayIntoHashStr(
                    //((org.fisco.bcos.web3j.abi.datatypes.generated.Bytes32) (byteList.toArray()[i]))
                    ((Bytes32) (byteList.toArray()[i]))
                            .getValue()));
        }
        return strList;
    }

    /**
     * Strictly check two lists' elements existence whether items in src exists in dst list or not.
     *
     * @param src source list
     * @param dst dest list
     * @return boolean list, each true / false indicating existing or not.
     */
    public static List<Boolean> strictCheckExistence(List<String> src, List<String> dst) {
        List<Boolean> result = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < src.size(); i++) {
            if (src.get(i).equalsIgnoreCase(dst.get(index))) {
                result.add(true);
                index++;
            } else {
                result.add(false);
            }
        }
        return result;
    }
}

