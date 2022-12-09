

package com.webank.weid.service.rpc;

import java.util.List;

import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operations on Credentials.
 *
 * @author chaoxinhu 2018.12
 */
public interface CredentialService {

    /**
     * Generate a credential for full claim content.
     *
     * @param args the args
     * @return credential wrapper
     */
    ResponseData<CredentialWrapper> createCredential(CreateCredentialArgs args);

    /**
     * WARNING: To be deprecated in near future. We strongly suggest to use CredentialPojo for
     * multi-signature purpose. This can add an extra signer and signature to a Credential. Multiple
     * signatures will be appended in an embedded manner.
     *
     * @param credentialList original credential
     * @param weIdPrivateKey the passed-in privateKey to add sign
     * @return the modified CredentialWrapper
     */
    @Deprecated
    ResponseData<Credential> addSignature(
        List<Credential> credentialList,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Generate a credential with selected data. Embedded multi-signed Credential are not allowed.
     *
     * @param credential the credential
     * @param disclosure the setting of disclosure, such as: {@code{"name":1,"gender":0,"age":1}},
     *     which means you WILL disclose "name" and "age", and "gender" WILL NOT be disclosed
     * @return CredentialWrapper
     */
    ResponseData<CredentialWrapper> createSelectiveCredential(
        Credential credential,
        String disclosure
    );

    /**
     * Verify the validity of a credential. Public key will be fetched from chain. If the credential
     * is multi-signed, it will verify each signature in an embedded manner.
     *
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(Credential credential);

    /**
     * Verify the validity of a credential. Public key will be fetched from chain. If the credential
     * * is multi-signed, it will verify each signature in an embedded manner.
     *
     * @param credentialWrapper the credentialWrapper
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(CredentialWrapper credentialWrapper);

    /**
     * Verify the validity of a credential. Public key must be provided. Embedded multi-signed
     * Credential are not allowed.
     *
     * @param credentialWrapper the credential wrapper
     * @param weIdPublicKey the specified public key which used to verify credential signature
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(
        CredentialWrapper credentialWrapper,
        WeIdPublicKey weIdPublicKey
    );

    /**
     * Get the full hash value of a Credential. All fields in the Credential will be included. This
     * method should be called when creating and verifying the Credential Evidence.
     *
     * @param credential the args
     * @return the Credential Hash value
     */
    ResponseData<String> getCredentialHash(Credential credential);

    /**
     * Get the full hash value of a Credential with its selectively-disclosure map. All fields in
     * the Credential will be included. This method should be called when creating and verifying the
     * Credential Evidence and the result is selectively-disclosure irrelevant.
     *
     * @param credential the args
     * @return the Credential Hash value
     */
    ResponseData<String> getCredentialHash(CredentialWrapper credential);

    /**
     * WARNING: To be deprecated in near future - use DataToolUtils.serialize() instead! Get the
     * Json String of a Credential. All fields in the Credential will be included. This also
     * supports the selectively disclosed Credential.
     *
     * @param credential the credential
     * @return the Credential Json value in String
     */
    @Deprecated
    ResponseData<String> getCredentialJson(Credential credential);

}
