

package com.webank.weid.protocol.cpt.old;

import lombok.Data;

/**
 * User request issuer to sign credential.
 *
 * @author tonychen
 */
@Data
//@Attributes(title = "metadata CPT", description = "Reserved CPT 110")
public class Cpt110 {

//    @Attributes(required = true, description = "CPT ID")
    private Integer cptId;

    /**
     * credential id.
     */
//    @Attributes(required = true, description = "credential ID")
    private String credentialId;

    /**
     * Required: The context field.
     */
//    @Attributes(required = true, description = "context")
    private String context;

    /**
     * Required: The issuer WeIdentity DID.
     */
//    @Attributes(required = true, description = "issuer weid")
    private String issuer;

    /**
     * Required: The create date.
     */
//    @Attributes(required = true, description = "issuanceDate")
    private Long issuanceDate;

    /**
     * Required: The expire date.
     */
//    @Attributes(required = true, description = "expirationDate")
    private Long expirationDate;
}
