

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data wrapper to handle WeIdentity DID Private Key info.
 *
 * <p>Created by Junqi Zhang on 17/10/2018.
 */
@Data
public class WeIdPrivateKey {

    /**
     * Required: The private key.
     */
    private String privateKey;

    public WeIdPrivateKey() {
        super();
    }

    public WeIdPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
