

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments for SDK RegisterAuthorityIssuer.
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class RegisterAuthorityIssuerArgs {

    /**
     * Required: The authority issuer information.
     */
    private AuthorityIssuer authorityIssuer;

    /**
     * Required: The WeIdentity DID private key for sending transaction.
     */
    private WeIdPrivateKey weIdPrivateKey;
}
