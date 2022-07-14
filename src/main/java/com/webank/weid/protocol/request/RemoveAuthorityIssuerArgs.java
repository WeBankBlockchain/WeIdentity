

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments for SDK API RemoveAuthorityIssuer.
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class RemoveAuthorityIssuerArgs {

    /**
     * Required: WeIdentity DID.
     */
    private String weId;

    /**
     * Required: WeIdentity DID private key.
     */
    private WeIdPrivateKey weIdPrivateKey;
}
