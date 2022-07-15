

package com.webank.weid.protocol.base;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.inf.JsonSerializer;

/**
 * policy and pre-credential.
 *
 * @author tonychen 2019年12月3日
 */
@Getter
@Setter
public class PolicyAndPreCredential implements JsonSerializer {

    /**
     * serial Version UID.
     */
    private static final long serialVersionUID = -5224072665022845706L;

    /**
     * policy and challenge.
     */
    private PolicyAndChallenge policyAndChallenge;

    /**
     * credential based on CPT 110.
     */
    private CredentialPojo preCredential;

    /**
     * extra data.
     */
    private Map<String, String> extra;
}
