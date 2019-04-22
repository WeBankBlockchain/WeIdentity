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

import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
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
     * Generate a selective disclosure credential with specified claim policy.
     *
     * @param credentialPojoWrapper the credential
     * @param claimPolicy describe which fields in credential should be disclosed.
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
    ResponseData<Boolean> verify(
        CredentialPojoWrapper credentialWrapper,
        String issuerWeId
    );

    /**
     * Verify the validity of a credential. Public key must be provided.
     *
     * @param credentialWrapper the credential wrapper
     * @param issuerPublicKey the specified public key which used to verify credential signature
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(
        CredentialPojoWrapper credentialWrapper,
        WeIdPublicKey issuerPublicKey
    );

    ResponseData<Boolean> verify(
        PresentationE presentationE,
        String presenterWeId,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge
    );
}
