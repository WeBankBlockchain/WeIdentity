

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * The sign information of evidence's each signer's sign attempt. Used as a mapped info against each
 * individual signer.
 *
 * @author chaoxinhu 2020.2
 * @since v1.6.0
 */
@Data
public class EvidenceSignInfo {

    /**
     * The signature of the signer onto this evidence.
     */
    private String signature;

    /**
     * The timestamp at which this evidence is signed.
     */
    private String timestamp;

    /**
     * The extra value this signer records on chain.
     */
    private List<String> logs = new ArrayList<>();

    /**
     * Whether this signer revoked this evidence. This is initialized as null, and will be set
     * appropriate values upon getEvidence().
     */
    private Boolean revoked = null;

    public static EvidenceSignInfo fromBlockChain(com.webank.weid.blockchain.protocol.base.EvidenceSignInfo evidenceSign) {
        EvidenceSignInfo evidenceSignInfo = new EvidenceSignInfo();
        evidenceSignInfo.setSignature(evidenceSign.getSignature());
        evidenceSignInfo.setTimestamp(evidenceSign.getTimestamp());
        evidenceSignInfo.setRevoked(evidenceSign.getRevoked());
        evidenceSignInfo.setLogs(evidenceSign.getLogs());
        return evidenceSignInfo;
    }
}
