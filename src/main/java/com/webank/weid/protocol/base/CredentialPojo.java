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

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.inf.IProof;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.util.CredentialPojoUtils;
import com.webank.weid.util.DataToolUtils;


/**
 * The base data structure to handle Credential info.
 *
 * @author junqizhang 2019.04
 */
@Data
public class CredentialPojo implements IProof, JsonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(CredentialPojo.class);

    /**
     * the serialVersionUID.
     */
    private static final long serialVersionUID = 8197843857223846978L;

    /**
     * Required: The context field.
     */
    private String context;

    /**
     * Required: The ID.
     */
    private String id;

    /**
     * Required: The CPT type in standard integer format.
     */
    private Integer cptId;

    /**
     * Required: The issuer WeIdentity DID.
     */
    private String issuer;

    /**
     * Required: The create date.
     */
    private Long issuanceDate;

    /**
     * Required: The expire date.
     */
    private Long expirationDate;

    /**
     * Required: The claim data.
     */
    private Map<String, Object> claim;

    /**
     * Required: The credential proof data.
     */
    private Map<String, Object> proof;
    
    /**
     * Required: The credential type default is VerifiableCredential.
     */
    private List<String> type;
    
    /**
     * 添加type.
     * @param typeValue the typeValue
     */
    public void addType(String typeValue) {
        if (type == null) {
            type = new ArrayList<String>();
        }
        type.add(typeValue);
    }

    /**
     * Directly extract the signature value from credential.
     *
     * @return signature value
     */
    public String getSignature() {
        return getValueFromProof(proof, ParamKeyConstant.PROOF_SIGNATURE).toString();
    }

    /**
     * Directly extract the proof type from credential.
     *
     * @return proof type
     */
    public String getProofType() {
        return getValueFromProof(proof, ParamKeyConstant.PROOF_TYPE).toString();
    }
    
    /**
     * Directly extract the salt from credential.
     *
     * @return salt
     */
    public Map<String, Object> getSalt() {
        return (Map<String, Object>)getValueFromProof(proof, ParamKeyConstant.PROOF_SALT);
    }
    
    /**
     * put the salt into proof.
     * 
     * @param salt map of salt
     */
    public void setSalt(Map<String, Object> salt) {
        putProofValue(ParamKeyConstant.PROOF_SALT, salt);
    }

    /**
     *  put the key-value into proof.
     * @param key the key of proof
     * @param value the value of proof
     */
    public void putProofValue(String key, Object value) {
        if (proof == null) {
            proof = new HashMap<>(); 
        }
        proof.put(key, value);
    }
    
    /**
     * convert CredentialPojo to JSON String.
     * @return CredentialPojo
     */
    @Override
    public String toJson() {     
        String json = DataToolUtils.convertTimestampToUtc(DataToolUtils.serialize(this));
        return DataToolUtils.addTagFromToJson(json);
    }
    
    /**
     * create CredentialPojo with JSON String.
     * @param credentialJson the CredentialPojo JSON String
     * @return CredentialPojo
     */
    public static CredentialPojo fromJson(String credentialJson) {
        if (StringUtils.isBlank(credentialJson)) {
            logger.error("create credential with JSON String failed, "
                + "the credential JSON String is null");
            throw new DataTypeCastException("the credential JSON String is null");
        }
        String credentialString = credentialJson;
        if (DataToolUtils.isValidFromToJson(credentialJson)) {
            credentialString = DataToolUtils.removeTagFromToJson(credentialJson);
        }
        CredentialPojo credentialPojo = DataToolUtils.deserialize(
            DataToolUtils.convertUtcToTimestamp(credentialString), 
            CredentialPojo.class
        );
        ErrorCode checkResp = CredentialPojoUtils.isCredentialPojoValid(credentialPojo);
        if (ErrorCode.SUCCESS.getCode() != checkResp.getCode()) {
            logger.error("create CredentialPojo with JSON String failed, {}", 
                checkResp.getCodeDesc());
            throw new DataTypeCastException(checkResp.getCodeDesc());
        }
        if (!CredentialPojoUtils.validClaimAndSaltForMap(
            credentialPojo.getClaim(), 
            credentialPojo.getSalt())) {
            logger.error("create PresentationE with JSON String failed, claim and salt of "
                + "credentialPojo not match.");
            throw new DataTypeCastException("claim and salt of credentialPojo not match.");
        }
        return credentialPojo;
    }
}
