package com.webank.weid.protocol.response;

import com.webank.weid.protocol.base.PolicyAndPreCredential;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月3日
 *
 */
@Getter
@Setter
public class PolicyAndPreCredentialResponse {

	private PolicyAndPreCredential policyAndPreCredential;
	
	  /**
     * 错误码.
     */
    private Integer errorCode;

    /**
     * 错误信息.
     */
    protected String errorMessage;
}
