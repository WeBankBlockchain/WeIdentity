

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data structure to handle WeIdentity DID authority info.
 *
 * @author afeexian
 */
@Data
public class WeIdAuthentication {

    /**
     * Required: The weIdentity DID.
     */
    private String weId;
    
    /**
     * the method Id.
     */
    private String authenticationMethodId;

    /**
     * Required: The private key or The weIdentity DID.
     */
    private WeIdPrivateKey weIdPrivateKey;
    
    public WeIdAuthentication() {
        super();
    }
    
    /**
     * Constructor with weId and privateKey. 
     * @param weId the weId
     * @param privateKey the privateKey
     */
    public WeIdAuthentication(String weId, String privateKey) {
        this.weId = weId;
        this.weIdPrivateKey = new WeIdPrivateKey();
        this.weIdPrivateKey.setPrivateKey(privateKey);
    }
    
    /**
     * Constructor with weId, privateKey and weIdPublicKeyId. 
     * @param weId the weId
     * @param privateKey the privateKey
     * @param authenticationMethodId the weIdPublicKeyId
     */
    public WeIdAuthentication(String weId, String privateKey, String authenticationMethodId) {
        this(weId, privateKey);
        this.authenticationMethodId = authenticationMethodId;
    }
}
