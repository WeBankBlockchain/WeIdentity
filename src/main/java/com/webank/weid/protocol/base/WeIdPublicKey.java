

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data wrapper to handle WeIdentity DID Public Key info.
 *
 * <p>Created by Junqi Zhang on 18/10/2018.
 */
@Data
public class WeIdPublicKey {

    /**
     * Required: The public key.
     */
    private String publicKey;
}
