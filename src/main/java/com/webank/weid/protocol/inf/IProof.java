/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.protocol.inf;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * proof接口.
 * 
 * @author v_wbgyang
 *
 */
public interface IProof {
    
    /**
     * 从proof中获取key对应value.
     * @param proof proofMap数据
     * @param key 要获取的数据的key
     * @return 返回key的数据
     */
    public default Object getValueFromProof(Map<String, Object> proof, String key) {
        if (proof != null) {
            Object value = proof.get(key);
            return value != null ? value : StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }
}
