

package com.webank.weid.protocol.base;

import lombok.Data;

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
}
