

package com.webank.weid.protocol.response;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.base.PolicyAndPreCredential;

/**
 * response for PolicyAndPreCredential.
 *
 * @author tonychen 2019年12月3日
 */
@Getter
@Setter
public class PolicyAndPreCredentialResponse {

    /**
     * error message.
     */
    protected String errorMessage;

    /**
     * policy, challenge and pre-credential based on CPT 110.
     */
    private PolicyAndPreCredential policyAndPreCredential;

    /**
     * error code.
     */
    private Integer errorCode;
}
