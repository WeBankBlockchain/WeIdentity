

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data structure for the SDK API register CPT info.
 *
 * @author junqizhang
 */
@Data
public class ClaimPolicyBaseInfo {

    /**
     * Required: The id for the CPT.
     */
    private Integer claimPolicyId;

    /**
     * Required: The version of the CPT for the same CPT id.
     */
    private Integer claimPolicyVersion;
}
