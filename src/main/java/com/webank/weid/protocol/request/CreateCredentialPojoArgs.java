

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.constant.CredentialType;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.util.CredentialUtils;

/**
 * The Arguments for the following SDK API: createCredential().
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class CreateCredentialPojoArgs<T> {

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
    private T claim;

    /**
     * Required: The private key structure used for signing.
     */
    private WeIdAuthentication weIdAuthentication;

    /**
     * Optional: The issuance date of the credential.
     */
    private Long issuanceDate = null;

    /**
     * 新增字段，issuer提前生成好的credential ID，对应credentialPojo里的ID.
     */
    private String id = null;

    /**
     * Optional:credential context.
     */
    private String context = CredentialUtils.getDefaultCredentialContext();

    /**
     * credential type.
     */
    private CredentialType type = CredentialType.ORIGINAL;
}
