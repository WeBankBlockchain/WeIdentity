

package com.webank.weid.suite.encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.suite.entity.EncodeData;

/**
 * 原文编解码处理器.
 * 
 * @author v_wbgyang
 *
 */
public class OriginalEncodeProcessor implements EncodeProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(OriginalEncodeProcessor.class);
    
    /**
     * 因为是原文处理，所以不做任何操作.
     */
    @Override
    public String encode(EncodeData encodeData) {
        logger.info("this is Original encode, so nothing to do.");
        return encodeData.getData();
    }

    /**
     * 因为是原文处理所以不做任何操作.
     */
    @Override
    public String decode(EncodeData encodeData) {
        logger.info("this is Original decode, so nothing to do.");
        return encodeData.getData();
    }

}
