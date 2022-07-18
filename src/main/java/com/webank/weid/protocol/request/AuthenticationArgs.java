

package com.webank.weid.protocol.request;

import lombok.Data;

/**
 * The Arguments when setting Authentication for WeIdentity DID.
 *
 * @author tonychen 2020.4.24
 */
@Data
public class AuthenticationArgs {

    /**
     * Required: The owner.
     */
    private String owner;

    /**
     * Required: The public key.
     */
    private String publicKey;

}
