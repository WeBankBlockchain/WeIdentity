/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.protocol.base;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JsonLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.protocol.inf.JsonSerializer;

public class CredentialPojoList extends ArrayList<CredentialPojo> implements JsonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(CredentialPojoList.class);
    
    /**
    * the serial.
    */
    private static final long serialVersionUID = -6164771401868673728L;

    @Override
    public String toJson() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (CredentialPojo credential : this) {
            buffer.append(credential.toJson()).append(",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("]");
        return buffer.toString();
    }

    /**
     * 将Json转换成CredentialPojoList.
     * 
     * @param json json数据
     * @return 返回CredentialPojoList
     */
    public static CredentialPojoList fromJson(String json) {
        try {
            JsonNode jsonNode = JsonLoader.fromString(json);
            ArrayNode arrayNode = (ArrayNode)jsonNode;
            CredentialPojoList result = new CredentialPojoList();
            arrayNode.forEach(node -> result.add(CredentialPojo.fromJson(node.toString())));
            return result;
        } catch (IOException e) {
            logger.info("[fromJson] from JSON error for CredentialPojoList.", e);
            return null;
        }
    }
}
