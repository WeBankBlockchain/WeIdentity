package com.webank.weid.protocol.base;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月3日
 *
 */
@Getter
@Setter
public class PolicyAndPreCredential {


	/**
	 * policy and challenge
	 */
	private PolicyAndChallenge policyAndChallenge;
	
	/**
     * 传CPT110的credential
     */
    private CredentialPojo preCredential;
    
    
    private String claim;
    /**
     * extra
     */
    private Map<String, String> extra;
}
