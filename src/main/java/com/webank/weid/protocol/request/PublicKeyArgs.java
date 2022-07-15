

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.constant.WeIdConstant.PublicKeyType;

/**
 * The Arguments when setting Public Key for WeIdentity DID.
 *
 * @author tonychen 2020.4.24
 */
@Data
public class PublicKeyArgs {

    /**
     * Required: The type.
     */
    private PublicKeyType type = PublicKeyType.SECP256K1;

    /**
     * Required: The owner.
     */
    private String owner;

    /**
     * Required: The public key.
     */
    private String publicKey;

    /**
     * nothing to do.
     *
     * @param type the public key type
     */
    public void setType(PublicKeyType type) {
        this.type = type;
    }

    public PublicKeyArgs() {
        super();
    }

    public PublicKeyArgs(String publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKeyArgs(String owner, String publicKey) {
        this.owner = owner;
        this.publicKey = publicKey;
    }
}
