

package com.webank.weid.suite.encode;

import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.suite.entity.EncodeData;

/**
 * 编解码处理器接口.
 * @author v_wbgyang
 *
 */
public interface EncodeProcessor {

    /**
     * 编码处理方法定义.
     * @param encodeData 需要编码的实体数据
     * @return 返回编码后的数据
     * @throws EncodeSuiteException Exception
     */
    public String encode(EncodeData encodeData) throws EncodeSuiteException;
    
    /**
     * 解码处理方法定义.
     * @param encodeData 需要解码的实体数据
     * @return 返回解密后的数据
     * @throws EncodeSuiteException Exception
     */
    public String decode(EncodeData encodeData) throws EncodeSuiteException;
}
