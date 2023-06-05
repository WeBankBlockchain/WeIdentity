

package com.webank.weid.protocol.base;

import java.util.Map;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.protocol.inf.Hashable;
import com.webank.weid.util.CredentialUtils;

/**
 * Credential response.
 *
 * @author tonychen 2019年1月24日
 */
@Data
public class CredentialWrapper implements Hashable {

    /**
     * Required: The Credential.
     */
    private Credential credential;

    /**
     * Required: key is the credential field, and value "1" for disclosure to the third party, "0"
     * for no disclosure.
     */
    private Map<String, Object> disclosure;

    /**
     * Generate the unique hash of this CredentialWrapper.
     *
     * @return hash value
     */
    public String getHash() {
        if (this == null) {
            return StringUtils.EMPTY;
        }
        if (this.getDisclosure() == null || this.getDisclosure().size() == 0) {
            return this.getCredential().getHash();
        }
        Credential credential = this.getCredential();
        if (CredentialUtils.isCredentialValid(credential) != ErrorCode.SUCCESS) {
            return StringUtils.EMPTY;
        }
        return CredentialUtils.getCredentialWrapperHash(this);
    }

    /**
     * Directly extract the signature value from credential.
     *
     * @return signature value
     */
    public String getSignature() {
        return credential.getSignature();
    }

    /**
     * Get the signature thumbprint for re-signing.
     *
     * @return thumbprint
     */
    public String getSignatureThumbprint() {
        return CredentialUtils.getCredentialThumbprint(this.getCredential(), this.getDisclosure());
    }
}
