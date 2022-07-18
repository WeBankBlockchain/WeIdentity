

package com.webank.weid.protocol.request;

import java.util.Map;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments for the following SDK API: createCredential().
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class CreateCredentialArgs {

    /**
     * Required: The CPT type in standard integer format.
     */
    private Integer cptId;

    /**
     * Required: The issuer WeIdentity DID.
     */
    private String issuer;

    /**
     * Required: The expire date.
     */
    private Long expirationDate;

    /**
     * Required: The claim data.
     */
    private Map<String, Object> claim;

    /**
     * Required: The private key structure used for signing.
     */
    private WeIdPrivateKey weIdPrivateKey;

    /**
     * Optional: The issuance date of the credential.
     */
    private Long issuanceDate = null;
}
