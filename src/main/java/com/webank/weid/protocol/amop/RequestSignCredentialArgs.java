package com.webank.weid.protocol.amop;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.CredentialPojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月2日
 *
 */
@Getter
@Setter
public class RequestSignCredentialArgs extends AmopBaseMsgArgs{

	private String userWeId;
	
	/**
	 * issuer 给用户发的credential，需要转成credentialInfoMap
	 */
	private CredentialPojo credentialPojo;
	
	
}
