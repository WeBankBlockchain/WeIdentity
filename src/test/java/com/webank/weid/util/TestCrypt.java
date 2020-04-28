package com.webank.weid.util;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Base64;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.crypto.KeyGenerator;
import com.webank.weid.suite.entity.Asymmetrickey;
import com.webank.weid.suite.entity.CryptType;

public class TestCrypt {
    
    private static final Logger logger = LoggerFactory.getLogger(TestCrypt.class);
    
    private static String json = "{\"claim\":{\"a\":\"a\",\"b\":\"b\"},\"cptId\":2000099,"
            + "\"expirationDate\":1903510144,\"id\":\"4d4bb35e-6335-4e81-aae9-6a3b88ca04f3\","
            + "\"issuer\":\"did:weid:101:0x110f0ed41b33b1395b9060d274247c2f9e15b29a\","
            + "\"type\":\"lite1\"}";
    
    private static final String original = "{\"name\":\"zhangsan\",age:12}";
    
    @Test
    public void testAes() {
        String key = KeyGenerator.getKey();
        logger.info("key: {}", key);
        logger.info("original: {}", original);
        String encrypt = CryptServiceFactory.getCryptService(CryptType.AES).encrypt(original, key);
        logger.info("encrypt: {}", encrypt);
        String decrypt = CryptServiceFactory.getCryptService(CryptType.AES).decrypt(encrypt, key);
        logger.info("decrypt: {}", decrypt);
        Assert.assertEquals(original, decrypt);
    }

    @Test
    public void testRsa() throws Exception {
        Asymmetrickey key = KeyGenerator.getKeyForRsa(2048);
        logger.info("pub key: {}", key.getPublicKey());
        logger.info("pri key: {}", key.getPrivavteKey());
        logger.info("original: {}", original);
        String encrypt = CryptServiceFactory.getCryptService(CryptType.RSA)
            .encrypt(original, key.getPublicKey());
        logger.info("encrypt: {}", encrypt);
        String decrypt = CryptServiceFactory.getCryptService(CryptType.RSA)
            .decrypt(encrypt, key.getPrivavteKey());
        logger.info("decrypt: {}", decrypt);
        Assert.assertEquals(original, decrypt);
    }
    
    @Test
    public void testEcies() throws Exception {
        for (int i = 0; i < 100; i++) {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            String publicKey = String.valueOf(keyPair.getPublicKey());
            String privateKey = String.valueOf(keyPair.getPrivateKey());
            logger.info("pub key: {}", publicKey);
            logger.info("pri key: {}", privateKey);
            BigInteger pub = new BigInteger(publicKey);
            BigInteger pri = new BigInteger(privateKey);
            publicKey = Base64.encodeBase64String(pub.toByteArray());
            privateKey = Base64.encodeBase64String(pri.toByteArray());
            logger.info("pub key base64: {}", publicKey);
            logger.info("pri key base64: {}", privateKey);
            String original = json;
            String encrypt = CryptServiceFactory.getCryptService(CryptType.ECIES)
                .encrypt(original, publicKey);
            logger.info("encrypt: {}", encrypt);
            String decrypt = CryptServiceFactory.getCryptService(CryptType.ECIES)
                .decrypt(encrypt, privateKey);
            logger.info("decrypt: {}", decrypt);
            Assert.assertEquals(DataToolUtils.sha3(original), DataToolUtils.sha3(decrypt));
            Assert.assertEquals(original, decrypt);
        }
        
    }
}
