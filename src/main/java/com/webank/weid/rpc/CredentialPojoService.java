/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.rpc;

import java.util.List;

import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.cpt.Cpt101;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operations on Credentials.
 *
 * @author tonychen
 */
public interface CredentialPojoService {

    /**
     * Generate a credential for full claim content.
     *
     * @param args the args
     * @return CredentialPojo
     */
    ResponseData<CredentialPojo> createCredential(CreateCredentialPojoArgs args);

    /**
     * user make credential from issuer's pre-credential.
     *
     * @param preCredential issuer's pre-credential
     * @param claimJson user claim
     * @param weIdAuthentication auth
     * @return credential based on CPT 111
     */
    ResponseData<CredentialPojo> prepareZkpCredential(
        CredentialPojo preCredential,
        String claimJson,
        WeIdAuthentication weIdAuthentication
    );

    /**
     * Generate a selective disclosure credential with specified claim policy.
     *
     * @param credential the credential
     * @param claimPolicy describe which fields in credential should be disclosed.
     * @return CredentialPojo
     */
    ResponseData<CredentialPojo> createSelectiveCredential(
        CredentialPojo credential,
        ClaimPolicy claimPolicy
    );

    /**
     * Add an extra signer and signature to a Credential. Multiple signatures will be appended in an
     * embedded manner.
     *
     * @param credentialList original credential list
     * @param callerAuth the passed-in privateKey and WeID bundle to sign
     * @return the modified CredentialWrapper
     */
    ResponseData<CredentialPojo> addSignature(
        List<CredentialPojo> credentialList,
        WeIdAuthentication callerAuth
    );

    /**
     * Get the full hash value of a CredentialPojo. All fields in the CredentialPojo will be
     * included. This method should be called when creating and verifying the Credential Evidence
     * and the result is selectively-disclosure irrelevant.
     *
     * @param credentialPojo the args
     * @return the Credential Hash value
     */
    ResponseData<String> getCredentialPojoHash(CredentialPojo credentialPojo);

    /**
     * Verify the validity of a credential. Public key will be fetched from chain.
     *
     * @param issuerWeId the issuer WeID
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(String issuerWeId, CredentialPojo credential);

    /**
     * Verify the validity of a credential with public key fetched from chain. Here you can specify
     * the public key ID from the WeID Document if you well know the ID (you can pass the whole
     * string, or just the ID).
     *
     * @param issuerWeId the issuer WeID
     * @param weIdPublicKeyId the public key
     * @param credential the credential
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> verify(
        String issuerWeId,
        String weIdPublicKeyId,
        CredentialPojo credential
    );

    /**
     * Verify the validity of a credential. Public key must be provided.
     *
     * @param issuerPublicKey the specified public key which used to verify credential signature
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(WeIdPublicKey issuerPublicKey, CredentialPojo credential);

    /**
     * verify the presentation with presenter's weid and policy.
     *
     * @param presenterWeId the presenter's weid
     * @param presentationPolicyE policy of the presentation
     * @param challenge challenge
     * @param presentationE the presentation
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(
        String presenterWeId,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        PresentationE presentationE
    );

    /**
     * Verify the validity of a credential. Public key must be provided. This is offline interface.
     * 
     * @param issuerPublicKey the specified public key which used to verify credential signature
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verifyOffline(WeIdPublicKey issuerPublicKey, CredentialPojo credential);

    /**
     * verify the presentation and pdf information.
     * @param pdfTemplatePath path of pdf template
     * @param serializePdf byte[] of serialize by pdf transportation
     * @param presenterWeId the presenter's weid
     * @param presentationPolicyE policy of the presentation
     * @param challenge challenge
     * @param presentationE the presentation
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verifyPresentationFromPdf(
        String pdfTemplatePath,
        byte[] serializePdf,
        String presenterWeId,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        PresentationE presentationE
    );

    /**
     * packing according to original vouchers and disclosure strategies.
     *
     * @param credentialList original credential list
     * @param presentationPolicyE the disclosure strategies.
     * @param challenge used for authentication
     * @param weIdAuthentication owner information
     * @return PresentationE presentationE
     */
    ResponseData<PresentationE> createPresentation(
        List<CredentialPojo> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        WeIdAuthentication weIdAuthentication
    );

    /**
     * Create a trusted timestamp credential.
     *
     * @param credentialList the credentialPojo list to be signed
     * @param weIdAuthentication caller authentication
     * @return the embedded timestamp in credentialPojo
     */
    ResponseData<CredentialPojo> createTrustedTimestamp(
        List<CredentialPojo> credentialList,
        WeIdAuthentication weIdAuthentication
    );

    /**
     * Create a data authorization token.
     *
     * @param authInfo the authorization info in CPT101 format
     * @param weIdAuthentication the caller (authorization issuer) authentication
     * @return the data authorization token in credentialPojo
     */
    ResponseData<CredentialPojo> createDataAuthToken(
        Cpt101 authInfo,
        WeIdAuthentication weIdAuthentication
    );
}

