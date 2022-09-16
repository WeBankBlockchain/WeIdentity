

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.constant.WeIdConstant.PublicKeyType;
import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments when setting Public Key for WeIdentity DID.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class SetPublicKeyArgs {

    /**
     * Required: The WeIdentity DID.
     */
    private String weId;

    /**
     * Required: The type.
     */
    private PublicKeyType type = PublicKeyType.ECDSA;

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
    
    /**
     * nothing to do.
     * @param type the public key type
     */
    public void setType(PublicKeyType type) {
        this.type = type;
    }
}
