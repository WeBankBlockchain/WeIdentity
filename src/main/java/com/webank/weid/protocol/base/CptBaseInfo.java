

package com.webank.weid.protocol.base;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The base data structure for the SDK API register CPT info.
 *
 * @author lingfenghe
 */
@Data
public class CptBaseInfo {

    /**
     * Required: The id for the CPT.
     */
    private Integer cptId;

    /**
     * Required: The version of the CPT for the same CPT id.
     */
    private Integer cptVersion;

    /**
     * transfer the CptBaseInfo from weid-blockchain.
     * @param cpt the CptBaseInfo class in weid-blockchain
     * @return WeIdDocument
     */
    public static CptBaseInfo fromBlockChain(com.webank.weid.blockchain.protocol.base.CptBaseInfo cpt) {
        CptBaseInfo cptBaseInfo = new CptBaseInfo();
        cptBaseInfo.setCptId(cpt.getCptId());
        cptBaseInfo.setCptVersion(cpt.getCptVersion());
        return cptBaseInfo;
    }
}
