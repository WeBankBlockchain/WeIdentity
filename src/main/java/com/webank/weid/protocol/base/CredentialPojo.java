

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.inf.Hashable;
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
public class CredentialPojo implements IProof, JsonSerializer, Hashable {

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
     * create CredentialPojo with JSON String.
     *
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
        Map<String, Object> credentialMap = DataToolUtils
            .deserialize(credentialString, HashMap.class);

        Object type = credentialMap.get(ParamKeyConstant.PROOF_TYPE);

        //lite1类型的credential要做特殊处理，将廋身过的json还原成可验证的lite credential对象
        if (type instanceof String && StringUtils
            .equals(String.valueOf(type), CredentialType.LITE1.getName())) {
            //result.addType(CredentialConstant.DEFAULT_CREDENTIAL_TYPE);
            String signature = String.valueOf(credentialMap.get(ParamKeyConstant.PROOF));
            credentialMap.remove(ParamKeyConstant.PROOF_TYPE);
            credentialMap.remove(ParamKeyConstant.PROOF);
            CredentialPojo credentialPojo;
            try {
                credentialPojo = DataToolUtils.mapToObj(
                    credentialMap,
                    CredentialPojo.class
                );
                credentialPojo.putProofValue(ParamKeyConstant.PROOF_SIGNATURE, signature);
                //credentialPojo.setIssuanceDate(0L);
                credentialPojo.addType(CredentialConstant.DEFAULT_CREDENTIAL_TYPE);
                credentialPojo.addType(CredentialType.LITE1.getName());
                return credentialPojo;
            } catch (Exception e) {
                logger.error("[fromJson] deserialize failed. error message:{}", e);
                return null;
            }

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

    /**
     * 添加type.
     *
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
        return toString(getValueFromProof(proof, ParamKeyConstant.PROOF_SIGNATURE));
    }

    /**
     * Directly extract the proof type from credential.
     *
     * @return proof type
     */
    public String getProofType() {
        return toString(getValueFromProof(proof, ParamKeyConstant.PROOF_TYPE));
    }

    /**
     * Directly extract the salt from credential.
     *
     * @return salt
     */
    public Map<String, Object> getSalt() {
        return (Map<String, Object>) getValueFromProof(proof, ParamKeyConstant.PROOF_SALT);
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
     * put the key-value into proof.
     *
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
     *
     * @return CredentialPojo
     */
    @Override
    public String toJson() {

        //如果是LITE1类型的credential，则需要廋身
        if (type.contains(CredentialType.LITE1.getName())) {
            try {
                String signature = this.getSignature();
                Map<String, Object> credMap = DataToolUtils.objToMap(this);
                //credMap.remove(ParamKeyConstant.ISSUANCE_DATE);
                credMap.remove(ParamKeyConstant.CONTEXT);
                credMap.remove(ParamKeyConstant.PROOF);
                credMap.put(ParamKeyConstant.PROOF_TYPE, CredentialType.LITE1.getName());

                credMap.put(ParamKeyConstant.PROOF, signature);
                return DataToolUtils.serialize(credMap);
            } catch (Exception e) {
                logger.error("[CredentialPojo:toJson] failed, error message:{}", e);
                return StringUtils.EMPTY;
            }
        }
        String json = DataToolUtils.convertTimestampToUtc(DataToolUtils.serialize(this));
        return DataToolUtils.addTagFromToJson(json);
    }

    /**
     * Generate the unique hash of this CredentialPojo.
     *
     * @return hash value
     */
    public String getHash() {
        if (type.contains(CredentialType.LITE1.getName())) {
            return CredentialPojoUtils.getLiteCredentialPojoHash(this);
        }
        if (CredentialPojoUtils.isCredentialPojoValid(this) != ErrorCode.SUCCESS) {
            return StringUtils.EMPTY;
        }
        return CredentialPojoUtils.getCredentialPojoHash(this, null);
    }

    /**
     * Get the signature thumbprint for re-signing.
     *
     * @return thumbprint
     */
    public String getSignatureThumbprint() {
        return CredentialPojoUtils.getCredentialThumbprintWithoutSig(this, this.getSalt(), null);
    }

    /**
     * Get the CredentialType.
     * @return the CredentialType
     */
    public CredentialType getCredentialType() {
        if (this.type == null) {
            logger.warn("[getCredentialType] the type is null.");
            return null;
        }
        if (this.type.contains(CredentialType.ZKP.getName())) {
            return CredentialType.ZKP;
        } else if (this.type.contains(CredentialType.LITE1.getName())) {
            return CredentialType.LITE1;
        } else  if (this.type.contains(CredentialType.ORIGINAL.getName())) {
            return CredentialType.ORIGINAL;
        } else {
            logger.warn("[getCredentialType] the type does not contain default CredentialType.");
            return null;
        }
    }
}
