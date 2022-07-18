

package com.webank.weid.protocol.cpt.old;

import lombok.Data;

/**
 * Trusted timestamping envelope.
 *
 * @author junqizhang 2019.11
 */

@Data
//@Attributes(title = "Trusted Timestamp", description = "Trusted Timestamp from authorized 3rd-party, or chain consensus")
public class Cpt109 {

    /**
     * calculate the hash from the entire list rather than from any single credential.
     */
    String claimHash;

    /**
     * trusted timestamp provided by the trusted third party or by the consensus of each node in
     * the consortium chain.
     */
    Long timestamp;

    /**
     * hashKey = hash(claimHash + timestamp) hashKey will be the key in the smart contract.
     */
    String hashKey;

    /**
     * signed by Timestamp authority signature = sign( hashKey ).
     */
    String signatureList;
}
