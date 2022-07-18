

package com.webank.weid.protocol.amop;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PolicyAndPreCredential;
import com.webank.weid.protocol.base.WeIdAuthentication;

/**
 * args for RequestIssueCredential.
 *
 * @author tonychen 2019年12月4日
 */
@Getter
@Setter
public class RequestIssueCredentialArgs extends AmopBaseMsgArgs {

    /**
     * policyAndPreCredential from issuer.
     */
    private PolicyAndPreCredential policyAndPreCredential;

    /**
     * user's credential list.
     */
    private List<CredentialPojo> credentialList;

    /**
     * user's claim.
     */
    private String claim;

    /**
     * user's authentication.
     */
    private WeIdAuthentication auth;

}
