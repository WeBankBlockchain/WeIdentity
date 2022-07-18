

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data structure for public key properties.
 *
 * @author tonychen 2018.10.8
 */
@Data
public class PublicKeyProperty {

    /**
     * Required: The id.
     */
    private String id;

    /**
     * Required: The type.
     */
    private String type;

    /**
     * Required: The owner.
     */
    private String owner;

    /**
     * Required: The public key.
     */
    private String publicKey;

    /**
     * Required: Revoked or not.
     */
    private Boolean revoked = false;
}
