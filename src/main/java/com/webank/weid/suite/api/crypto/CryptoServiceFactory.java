

package com.webank.weid.suite.api.crypto;

import java.util.HashMap;
import java.util.Map;

import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.suite.api.crypto.inf.CryptoService;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.suite.crypto.AesCryptoService;
import com.webank.weid.suite.crypto.EciesCryptoService;
import com.webank.weid.suite.crypto.RsaCryptoService;

/**
 * 秘钥对象工厂, 根据不同类型秘钥得到相应的秘钥处理对象.
 * @author v_wbgyang
 *
 */
public class CryptoServiceFactory {
    
    /**
     * 支持加密类型的配置Map，目前支持仅支持AES.
     */
    private static final Map<String, CryptoService> cryptoServiceMap =
        new HashMap<String, CryptoService>();
    
    static {
        cryptoServiceMap.put(CryptoType.AES.name(), new AesCryptoService());
        cryptoServiceMap.put(CryptoType.RSA.name(), new RsaCryptoService());
        cryptoServiceMap.put(CryptoType.ECIES.name(), new EciesCryptoService());
    }

    /**
     * 通过秘钥枚举类型获取秘钥对象.
     * @param cryptoType 秘钥枚举类型
     * @return 秘钥加解密处理对象
     */
    public static CryptoService getCryptoService(CryptoType cryptoType) {
        CryptoService service = cryptoServiceMap.get(cryptoType.name());
        if (service == null) {
            throw new EncodeSuiteException();
        }
        return service;
    }
}
