


package com.webank.weid.protocol.amop;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * args for GetPolicyAndPreCredential.
 *
 * @author tonychen 2019年12月3日
 */
@Getter
@Setter
public class GetPolicyAndPreCredentialArgs extends AmopBaseMsgArgs {

    /**
     * the id of the policy.
     */
    private String policyId;

    /**
     * the user whom the policy will send to.
     */
    private String targetUserWeId;

    /**
     * the cpt id.
     */
    private Integer cptId;

    /**
     * user's claim data.
     */
    private String claim;

    /**
     * extra data.
     */
    private Map<String, String> extra;
}
