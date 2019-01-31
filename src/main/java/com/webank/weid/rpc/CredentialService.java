/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.rpc;

import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service interface for operations on Credentials.
 *
 * @author chaoxinhu 2018.12
 */
public interface CredentialService {

    /**
     * Generate a credential for full claim content.
     *
     * @param args the args
     * @return credential
     */
    ResponseData<CredentialWrapper> createCredential(CreateCredentialArgs args);

    /**
     * Generate a credential with selected data.
     *
     * @param credential the credential.
     * @param disclosure the setting of disclosure, such as: {@code{"name":1,"gender":0,"age":1}},
     *     which means you WILL disclose "name" and "age", and "gender" WILL NOT be disclosed
     *     to others.
     * @return CredentialWrapper
     */
    ResponseData<CredentialWrapper> createSelectiveCredential(
        Credential credential,
        String disclosure
    );

    /**
     * Verify the validity of a credential. Public key will be fetched from chain.
     *
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     *     in ResponseData
     */
    ResponseData<Boolean> verify(Credential credential);

    /**
     * Verify the validity of a credential. Public key will be fetched from chain.
     *
     * @param credentialWrapper the credentialWrapper
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(CredentialWrapper credentialWrapper);

    /**
     * Verify the validity of a credential. Public key must be provided.
     *
     * @param credentialWrapper the credential wrapper.
     * @param weIdPublicKey the specified public key which used to verify signature of the
     *     credential.
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
     * @param args the args
     * @return the Credential Hash value in byte array, fixed to be 32 Bytes length
     */
    ResponseData<String> getCredentialHash(Credential args);
}
