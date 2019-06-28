/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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
