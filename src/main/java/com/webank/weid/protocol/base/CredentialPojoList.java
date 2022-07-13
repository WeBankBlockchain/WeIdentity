

package com.webank.weid.protocol.base;

import com.webank.weid.util.DataToolUtils;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
            JsonNode jsonNode = DataToolUtils.loadJsonObject(json);
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
