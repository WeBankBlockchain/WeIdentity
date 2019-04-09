/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import java.util.Map;

import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoWrapper;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operations on Credentials.
 *
 * @author chaoxinhu 2018.12
 */
public interface CredentialPojoService {

    /**
     * Generate a credential for full claim content.
     *
     * @param args the args
     * @return credential wrapper
     */
    ResponseData<CredentialPojoWrapper> createCredential(CreateCredentialPojoArgs args);

    /**
     * Generate a credential with selected data.
     *
     * @param credentialPojoWrapper the credential
     * @param claimPolicy
     * @return CredentialWrapper
     */
    ResponseData<CredentialPojoWrapper> createSelectiveCredential(
        CredentialPojoWrapper credentialPojoWrapper,
        ClaimPolicy claimPolicy
    );

    /**
     * Verify the validity of a credential. Public key will be fetched from chain.
     *
     * @param credentialWrapper the credentialWrapper
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(CredentialPojoWrapper credentialWrapper);

    /**
     * Verify the validity of a credential. Public key must be provided.
     *
     * @param credentialWrapper the credential wrapper
     * @param weIdPublicKey the specified public key which used to verify credential signature
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(
        CredentialPojoWrapper credentialWrapper,
        WeIdPublicKey weIdPublicKey
    );

    /**
     * Get the full hash value of a Credential. All fields in the Credential will be included. This
     * method should be called when creating and verifying the Credential Evidence.
     *
     * @param credential the args
     * @return the Credential Hash value in byte array, fixed to be 32 Bytes length
     */
    ResponseData<String> getCredentialHash(Credential credential);

    /**
     * Get the Json String of a Credential. All fields in the Credential will be included. This also
     * supports the selectively disclosed Credential.
     *
     * @param credential the credential
     * @return the Credential Json value in String
     */
    ResponseData<String> getCredentialJson(Credential credential);

}
