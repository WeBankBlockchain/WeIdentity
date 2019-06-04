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
