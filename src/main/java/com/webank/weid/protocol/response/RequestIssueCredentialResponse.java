package com.webank.weid.protocol.response;

import com.webank.weid.protocol.base.CredentialPojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月4日
 *
 */
@Getter
@Setter
public class RequestIssueCredentialResponse {

	private CredentialPojo credentialPojo;
	
	private String credentialSignature;
	
	private String issuerNonce;
	
    /**
     * 错误码.
     */
    private Integer errorCode;

    /**
     * 错误信息.
     */
    protected String errorMessage;
}
