

package com.webank.weid.protocol.cpt.old;

import lombok.Data;

/**
 * User request issuer to sign credential.
 *
 * @author tonychen
 */
@Data
//@Attributes(title = "User CPT", description = "Reserved CPT 111")
public class Cpt111 {

//    @Attributes(required = true, description = "CPT ID")
    private String cptId;
//    @Attributes(required = true, description = "credential Signature Request", minimum = 1)
    private String credentialSignatureRequest;
//    @Attributes(required = true, description = "User Nonce")
    private String userNonce;
}
