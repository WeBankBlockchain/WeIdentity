package com.webank.weid.protocol.amop;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.PolicyAndPreCredential;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.WeIdAuthentication;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月4日
 *
 */
@Getter
@Setter
public class RequestIssueCredentialArgs extends AmopBaseMsgArgs{

	/**
	 * user's presentation
	 */
//	private PresentationE presentation;
	
	/**
	 * policyAndPreCredential from issuer
	 */
	private PolicyAndPreCredential policyAndPreCredential;
	
	/**
	 * policy id
	 */
	private String policyId;
	
	/**
	 * user's authentication
	 */
	private WeIdAuthentication auth;
	
}
