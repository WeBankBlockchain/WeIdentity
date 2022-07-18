

package com.webank.weid.suite.api.crypto.inf;

import com.webank.weid.exception.EncodeSuiteException;

/**
 * 秘钥加解密接口.
 * @author v_wbgyang
 *
 */
public interface CryptoService {

    /**
     * 加密方法.
     * @param content 待加密字符串
     * @param key 秘钥
     * @return 返回加密后的字符串数据
     */
    public String encrypt(String content, String key) throws EncodeSuiteException;
    
    /**
     * 解密方法.
     * @param content 待解密字符串
     * @param key 秘钥
     * @return 返回解密后的字符串数据
     */
    public String decrypt(String content, String key) throws EncodeSuiteException;
}
