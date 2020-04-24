package com.webank.weid.suite.crypto;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.suite.entity.CryptType;

public class RsaCryptService implements CryptService {

    private static final Logger logger = LoggerFactory.getLogger(RsaCryptService.class);

    private static final String KEY_ALGORITHM = CryptType.RSA.name();

    @Override
    public String encrypt(String data, String publicKey) throws EncodeSuiteException {
        try {
            byte[] pubByte = Base64.decode(publicKey);
            PublicKey pub = KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(pubByte));
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encode(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("RSA encrypt error, please check the log.", e);
            throw new EncodeSuiteException();
        }
    }

    @Override
    public String decrypt(String data, String privateKey) throws EncodeSuiteException {
        try {
            // 64位解码加密后的字符串
            byte[] inputByte = Base64.decode(data.getBytes(StandardCharsets.UTF_8));
            byte[] priByte = Base64.decode(privateKey);
            PrivateKey priKey = KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(priByte));
            // RSA解密
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("RAS decrypt error, please check the log.", e);
            throw new EncodeSuiteException();
        }
    }
}
