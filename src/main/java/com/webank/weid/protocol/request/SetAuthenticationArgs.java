

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments when setting Authentication for WeIdentity DID.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class SetAuthenticationArgs {

    /**
     * Required: The WeIdentity DID.
     */
    private String weId;

    /**
     * Required: The owner.
     */
    private String owner;

    /**
     * Required: The public key.
     */
    private String publicKey;

    /**
     * Required: The WeIdentity DID private key.
     */
    private WeIdPrivateKey userWeIdPrivateKey;
}
