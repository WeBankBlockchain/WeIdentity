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

package com.webank.weid.protocol.base;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.util.DataToolUtils;

/**
 * The base data structure to handle Credential info.
 *
 * @author junqizhang 2019.04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PresentationPolicyE extends Version implements JsonSerializer {
    
    /**
     * the serialVersionUID.
     */
    private static final long serialVersionUID = 3607089314849566766L;

    private static final Logger logger = LoggerFactory.getLogger(PresentationPolicyE.class);

    /**
     * Policy ID.
     */
    private Integer id;

    /**
     * represent who publish this presentation policy.
     */
    private String orgId;

    /**
     * represent who publish this presentation policy.
     */
    private String policyPublisherWeId;

    /**
     * specify which properties in which credential are needed.
     */
    private Map<Integer, ClaimPolicy> policy;

    /**
     * extra data which policy presenter can use it store some specific business data.
     */
    private Map<String, String> extra;

    /**
     * close the constructor.
     */
    private PresentationPolicyE() {
    }

    /**
     * create the PresentationPolicyE with policyFileName, 
     * please make sure the JSON file in your classPath.
     * 
     * @param policyFileName the policyFileName
     * @return the PresentationPolicyE
     */
    public static PresentationPolicyE create(String policyFileName) {
        PresentationPolicyE policy = null;
        try {
            JsonNode jsonNode = null;
            //获取policyJson文件 转换成JsonNode
            File file = new File(policyFileName);
            if (file.exists()) {
                jsonNode = JsonLoader.fromFile(file);
            } else {
                jsonNode = JsonLoader.fromResource("/" + policyFileName);
            }
           
            if (jsonNode == null) {
                logger.error("can not find the {} file in your classpath.", policyFileName);
                return policy;
            }
            policy = fromJson(jsonNode.toString());
        } catch (IOException e) {
            logger.error("create PresentationPolicyE has error, please check the log.", e);
        }
        return policy;
    }
    
    /**
     * create the PresentationPolicyE with JSON String.
     * 
     * @param json the JSON String
     * @return the PresentationPolicyE
     */
    public static PresentationPolicyE fromJson(String json) {
        PresentationPolicyE policy = null;
        try {
            //将Json转换成Map
            HashMap<String, Object> policyMap = 
                DataToolUtils.deserialize(json, HashMap.class);
            //获取policyJson中的policy 转换成Map
            HashMap<Integer, Object> claimMap = 
                (HashMap<Integer, Object>)policyMap.get(CredentialConstant.CLAIM_POLICY_FIELD);
            //遍历claimMap
            Iterator<Integer> it = claimMap.keySet().iterator();
            while (it.hasNext()) {
                //得到每一个claim
                HashMap<String, Object> claim = (HashMap<String, Object>)claimMap.get(it.next());
                //得到fieldsToBeDisclosed转换成Map
                HashMap<String, Object> disclosedMap = 
                    (HashMap<String, Object>)claim.get(
                        CredentialConstant.CLAIM_POLICY_DISCLOSED_FIELD
                    );
                //覆盖原来的fieldsToBeDisclosed为字符串
                claim.put(
                    CredentialConstant.CLAIM_POLICY_DISCLOSED_FIELD,
                    DataToolUtils.serialize(disclosedMap)
                );
            }
            //重新序列化为policyJson
            String value = DataToolUtils.serialize(policyMap);
            //反序列化policyJson为PresentationPolicyE
            return DataToolUtils.deserialize(value, PresentationPolicyE.class);
        } catch (Exception e) {
            logger.error("create PresentationPolicyE has error, please check the log.", e);
        }
        return policy;
    }
    
    @Override
    public String toJson() {
        String jsonString = DataToolUtils.serialize(this);
        HashMap<String, Object> policyEMap = DataToolUtils.deserialize(jsonString, HashMap.class);
        Map<String, Object> policy1 = 
            (HashMap<String, Object>)policyEMap.get(CredentialConstant.CLAIM_POLICY_FIELD);
        for (Map.Entry<String, Object> entry : policy1.entrySet()) {
            HashMap<String, Object> claimPolicyMap = (HashMap<String, Object>)entry.getValue();
            HashMap<String, Object> disclosureMap = 
                DataToolUtils.deserialize(
                    (String)claimPolicyMap.get(CredentialConstant.CLAIM_POLICY_DISCLOSED_FIELD),
                    HashMap.class
                );
            claimPolicyMap.put(CredentialConstant.CLAIM_POLICY_DISCLOSED_FIELD, disclosureMap);
        }
        return DataToolUtils.serialize(policyEMap);
    }
}
