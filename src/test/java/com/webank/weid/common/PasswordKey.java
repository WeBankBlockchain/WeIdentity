

package com.webank.weid.common;

/**
 * public and private key object class.
 *
 * @author v_wbgyang
 */
public class PasswordKey {

    /**
     * the key of privateKey.
     */
    private String privateKey;

    /**
     * the key of publicKey.
     */
    private String publicKey;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
