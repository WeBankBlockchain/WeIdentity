package com.webank.weid.util;

import java.security.NoSuchAlgorithmException;

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

    @Test
    public void testAes() {
        String key = KeyGenerator.getKey();
        logger.info("key: {}", key);
        String original = "{\"name\":\"zhangsan\",age:12}";
        logger.info("original: {}", original);
        String encrypt = CryptServiceFactory.getCryptService(CryptType.AES).encrypt(original, key);
        logger.info("encrypt: {}", encrypt);
        String decrypt = CryptServiceFactory.getCryptService(CryptType.AES).decrypt(encrypt, key);
        logger.info("decrypt: {}", decrypt);
        Assert.assertEquals(original, decrypt);
    }
    
    @Test
    public void testRsa() throws NoSuchAlgorithmException {
        Asymmetrickey key = KeyGenerator.getKeyForRsa();
        logger.info("pub key: {}", key.getPublicKey());
        logger.info("pri key: {}", key.getPrivavteKey());
        String original = "{\"name\":\"zhangsan\",age:12}";
        logger.info("original: {}", original);
        String encrypt = CryptServiceFactory.getCryptService(CryptType.RSA)
            .encrypt(original, key.getPublicKey());
        logger.info("encrypt: {}", encrypt);
        String decrypt = CryptServiceFactory.getCryptService(CryptType.RSA)
            .decrypt(encrypt, key.getPrivavteKey());
        logger.info("decrypt: {}", decrypt);
        Assert.assertEquals(original, decrypt);
    }
}
