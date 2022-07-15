

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments when creating WeIdentity DID.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class CreateWeIdArgs {

    /**
     * Required: Public Key.
     */
    private String publicKey;

    /**
     * Required: WeIdentity DID private key.
     */
    private WeIdPrivateKey weIdPrivateKey;
}
