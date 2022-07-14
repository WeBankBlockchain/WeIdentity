


package com.webank.weid.protocol.amop;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * the request body for get PolicyAndChallenge.
 * 
 * @author tonychen 2019年5月7日
 *
 */

@Getter
@Setter
public class GetPolicyAndChallengeArgs extends AmopBaseMsgArgs {

    private String policyId;

    private String targetUserWeId;
}
