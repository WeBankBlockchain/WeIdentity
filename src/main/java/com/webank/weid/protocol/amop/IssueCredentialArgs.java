

package com.webank.weid.protocol.amop;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.PresentationE;

/**
 * args for requesting issuer to issue credential.
 *
 * @author tonychen 2019年12月4日
 */
@Getter
@Setter
public class IssueCredentialArgs extends AmopBaseMsgArgs {

    /**
     * user's credential list,including KYC credential and credential based on CPT111.
     */
    private PresentationE presentation;

    /**
     * user claim (decided by user).
     */
    private String claim;


    private String policyId;

}
