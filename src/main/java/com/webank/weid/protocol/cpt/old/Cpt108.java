

package com.webank.weid.protocol.cpt.old;

import com.webank.weid.protocol.base.CredentialPojo;
import java.util.List;
import lombok.Data;

/**
 * Trusted timestamping envelope.
 *
 * @author junqizhang 2019.11
 */

@Data
//@Attributes(title = "Trusted Timestamping", description = "Trusted timestamping envelope")
public class Cpt108 {

    /**
     * information about timestamp authority.
     */
//    @Attributes(required = true, description = "Timestamp Authority")
    String timestampAuthority;

    /**
     * caculate the hash from the credentials.
     */
//    @Attributes(required = true, description = "Claim Hash")
    String claimHash;

    /**
     * trusted timestamping provided by the trusted third party or by the consensus of each node in
     * the consortium chain.
     */
//    @Attributes(required = true, description = "Timestamp")
    Long timestamp;

    /**
     * signed by Timestamp authority. authoritySignature = sign( hashKey )
     */
//    @Attributes(required = true, description = "Signature value from Authority")
    String authoritySignature;

    /**
     * The credential list to be signed with timestamp.
     */
//    @Attributes(required = true, description = "Original credential list to be signed")
    List<CredentialPojo> credentialList;
}
