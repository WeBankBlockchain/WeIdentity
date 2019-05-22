package com.webank.weid.protocol.amop;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年5月7日
 *
 */

@Getter
@Setter
public class GetPolicyAndChallengeArgs extends AmopBaseMsgArgs {

	private String policyId;
	
	private String targetUserWeId;
}
