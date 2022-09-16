

package com.webank.weid.suite.encode;

import java.util.HashMap;
import java.util.Map;

import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.suite.api.transportation.params.EncodeType;

/**
 * 编解码工厂,用于获取编解码处理器.
 * @author v_wbgyang
 *
 */
public class EncodeProcessorFactory {
    
    /**
     * 编解码处理器配置Map,目前支持原文和密文.
     */
    private static final Map<String, EncodeProcessor> encode_processor_map =
        new HashMap<String, EncodeProcessor>();
    
    static { 
        encode_processor_map.put(EncodeType.ORIGINAL.name(), new OriginalEncodeProcessor());
        encode_processor_map.put(EncodeType.CIPHER.name(), new CipherEncodeProcessor());
    }

    /**
     * 根据编解码枚举获取编解码处理器.
     * @param encodeType 编解码枚举
     * @return 编解码处理器
     */
    public static EncodeProcessor getEncodeProcessor(EncodeType encodeType) {
        EncodeProcessor encodeProcessor = encode_processor_map.get(encodeType.name());
        if (encodeProcessor == null) {
            throw new EncodeSuiteException();
        }
        return encodeProcessor;
    }
}
