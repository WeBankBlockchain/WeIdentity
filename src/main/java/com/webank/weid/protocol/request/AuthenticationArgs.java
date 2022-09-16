

package com.webank.weid.protocol.request;

import lombok.Data;

/**
 * The Arguments when setting Authentication for WeIdentity DID.
 *
 * @author afeexian 2022.9.9
 */
@Data
public class AuthenticationArgs {

    //用户可以指定verification method id，也可以由系统指定
    /**
     * Required: The method id.
     */
    private String id;

    /**
     * Required: The method controller.
     */
    private String controller;

    /**
     * Required: The public key.
     */
    private String publicKey;

}
