package com.webank.weid.protocol.response;

import com.webank.weid.protocol.base.PolicyAndChellenge;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年5月7日
 *
 */

@Getter
@Setter
public class GetPolicyAndChallengeResponse {


	/**
	 * 返回的消息
	 */
	private PolicyAndChellenge policyAndChellenge;
	
	/**
	 * 错误码
	 */
	private Integer errorCode;
	
	/**
     * 错误信息
     */
    protected String errorMessage;
}
