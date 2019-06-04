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

package com.webank.weid.suite.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.suite.encode.CipherEncodeProcessor;
import com.webank.weid.suite.entity.CryptType;

/**
 * AES加解密处理类.
 * @author v_wbgyang
 *
 */
public class AesCryptService implements CryptService {
    
    private static final Logger logger = LoggerFactory.getLogger(CipherEncodeProcessor.class);
    
    private static final String KEY_ALGORITHM = CryptType.AES.name();
    
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"; 
    
    private static final int KEY_LEN = 128;
    
    @Override
    public String encrypt(String content, String password) throws EncodeSuiteException {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8.toString());
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(result);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            logger.error("AES encrypt error, please check the log.", e);
            throw new EncodeSuiteException();
        } 
    }
    
    @Override
    public String decrypt(String content, String password) throws EncodeSuiteException {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, StandardCharsets.UTF_8.toString());
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            logger.error("AES decrypt error, please check the log.", e);
            throw new EncodeSuiteException();
        } 
    }
    
    /**
     * 根据秘钥获取秘钥对象.
     * @param password 秘钥字符串
     * @return 返回秘钥对象
     * @throws NoSuchAlgorithmException 不存在的Algorithm异常
     * @throws UnsupportedEncodingException 不支持的encode异常
     */
    private static SecretKeySpec getSecretKey(
        final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes(StandardCharsets.UTF_8.toString()));
        kg.init(KEY_LEN, random);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }
}
