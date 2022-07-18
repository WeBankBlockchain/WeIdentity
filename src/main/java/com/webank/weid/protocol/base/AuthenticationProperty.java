

package com.webank.weid.protocol.base;

import lombok.Data;

import com.webank.weid.constant.WeIdConstant.PublicKeyType;

/**
 * The base data structure for AuthenticationProperty.
 *
 * @author tonychen 2018.10.8
 */
@Data
public class AuthenticationProperty {

    /**
     * Required: The type.
     */
    private String type = PublicKeyType.ECDSA.getTypeName();

    /**
     * Required: The public key.
     */
    private String publicKey;

    /**
     * Required: Revoked, or not.
     */
    private Boolean revoked = false;
}
