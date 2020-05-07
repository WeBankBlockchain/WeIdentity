/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.suite.api.crypto.inf.CryptoService;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.util.DataToolUtils;

public class RsaCryptoService implements CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(RsaCryptoService.class);

    private static final String KEY_ALGORITHM = CryptoType.RSA.name();
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Override
    public String encrypt(String content, String key) throws EncodeSuiteException {
        logger.info("begin encrypt by RSA");
        checkForEncrypt(content, key);
        try {
            byte[] pubByte = Base64.decodeBase64(key);
            PublicKey pub = KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(pubByte));
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            logger.error("RSA encrypt error, please check the log.", e);
            throw new EncodeSuiteException();
        }
    }
    
    private void checkForEncrypt(String content, String key) {
        // 入参非空检查
        String errorMessage = null;
        if (StringUtils.isEmpty(content)) {
            errorMessage = "input content is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查content是否为utf-8
        boolean isUtf8 = Charset.forName(StandardCharsets.UTF_8.toString())
            .newEncoder().canEncode(content);
        if (!isUtf8) {
            errorMessage = "input content is not utf-8.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 入参非空检查
        if (StringUtils.isEmpty(key)) {
            errorMessage = "input publicKey is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查publicKey是否为标准base64格式
        if (!DataToolUtils.isValidBase64String(key)) {
            errorMessage = "input publicKey is not a valid Base64 string.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
    }
    
    @Override
    public String decrypt(String content, String key) throws EncodeSuiteException {
        logger.info("begin decrypt by RSA");
        checkForDecrypt(content, key);
        try {
            // 64位解码加密后的字符串
            byte[] inputByte = Base64.decodeBase64(content);
            byte[] priByte = Base64.decodeBase64(key);
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
    
    private void checkForDecrypt(String content, String key) {
        // 入参非空检查
        String errorMessage = null;
        if (StringUtils.isEmpty(content)) {
            errorMessage = "input content is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查content是否为标准base64格式
        if (!DataToolUtils.isValidBase64String(content)) {
            errorMessage = "input content is not a valid Base64 string.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 入参非空检查
        if (StringUtils.isEmpty(key)) {
            errorMessage = "input privateKey is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查privateKey是否为标准base64格式
        if (!DataToolUtils.isValidBase64String(key)) {
            errorMessage = "input privateKey is not a valid Base64 string.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        } 
    }
}
