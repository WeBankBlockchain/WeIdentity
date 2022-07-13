

package com.webank.weid.protocol.base;

import java.util.Map;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.protocol.inf.Hashable;
import com.webank.weid.util.CredentialUtils;

/**
 * The base data structure to handle Credential info.
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class Credential implements Hashable {

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
    private Map<String, String> proof;

    /**
     * Directly extract the signature value from credential.
     *
     * @return signature value
     */
    public String getSignature() {
        return getValueFromProof(ParamKeyConstant.CREDENTIAL_SIGNATURE);
    }

    /**
     * Directly extract the proof type from credential.
     *
     * @return proof type
     */
    public String getProofType() {
        return getValueFromProof(ParamKeyConstant.PROOF_TYPE);
    }

    private String getValueFromProof(String key) {
        if (proof != null) {
            return proof.get(key);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Get the unique hash value of this Credential.
     *
     * @return hash value
     */
    public String getHash() {
        if (CredentialUtils.isCredentialValid(this) != ErrorCode.SUCCESS) {
            return StringUtils.EMPTY;
        }
        return CredentialUtils.getCredentialHash(this);
    }

    /**
     * Get the signature thumbprint for re-signing.
     *
     * @return thumbprint
     */
    public String getSignatureThumbprint() {
        return CredentialUtils.getCredentialThumbprint(this, null);
    }
}
