

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * The base data structure to handle Credential EvidenceInfo info.
 *
 * @author chaoxinhu 2019.1
 */
@Data
public class EvidenceInfo {

    /**
     * Required: full Credential hash.
     */
    private String credentialHash;

    /**
     * Required: sign info mapping (key: signer WeID, value: evidenceSignInfo).
     */
    private Map<String, EvidenceSignInfo> signInfo = new HashMap<>();

    /**
     * Get all signers info.
     *
     * @return signers list
     */
    public List<String> getSigners() {
        List<String> signers = new ArrayList<>();
        for (Map.Entry<String, EvidenceSignInfo> entry : signInfo.entrySet()) {
            signers.add(entry.getKey());
        }
        return signers;
    }

    /**
     * Get all signatures info.
     *
     * @return signatures list
     */
    public List<String> getSignatures() {
        List<String> signatures = new ArrayList<>();
        for (Map.Entry<String, EvidenceSignInfo> entry : signInfo.entrySet()) {
            signatures.add(entry.getValue().getSignature());
        }
        return signatures;
    }

    /**
     * change EvidenceInfo from weid-blockchain.
     * @param evidence the weid-blockchain evidence class
     * @return EvidenceInfo
     */
    public static EvidenceInfo fromBlockChain(com.webank.weid.blockchain.protocol.base.EvidenceInfo evidence) {
        EvidenceInfo evidenceInfo = new EvidenceInfo();
        evidenceInfo.setCredentialHash(evidence.getCredentialHash());
        Map<String, EvidenceSignInfo> signInfoMap = new HashMap<>();
        for(Map.Entry<String, com.webank.weid.blockchain.protocol.base.EvidenceSignInfo> entry  : evidence.getSignInfo().entrySet()){
            EvidenceSignInfo evidenceSignInfo = EvidenceSignInfo.fromBlockChain(entry.getValue());
            signInfoMap.put(entry.getKey(), evidenceSignInfo);
        }
        evidenceInfo.setSignInfo(signInfoMap);
        return evidenceInfo;
    }
}
