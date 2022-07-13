

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data structure to handle WeIdentity DID authority info.
 *
 * @author darwindu
 */
@Data
public class WeIdAuthentication {

    /**
     * Required: The weIdentity DID.
     */
    private String weId;
    
    /**
     * the public key Id.
     */
    private String weIdPublicKeyId;

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
     * @param weIdPublicKeyId the weIdPublicKeyId
     */
    public WeIdAuthentication(String weId, String privateKey, String weIdPublicKeyId) {
        this(weId, privateKey);
        this.weIdPublicKeyId = weIdPublicKeyId;
    }
}
