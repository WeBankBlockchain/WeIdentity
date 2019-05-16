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

import java.util.List;

import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
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
     * @return CredentialPojo
     */
    ResponseData<CredentialPojo> createCredential(CreateCredentialPojoArgs args);

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
     * Verify the validity of a credential. Public key will be fetched from chain.
     *
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(String issuerWeId, CredentialPojo credential);

    /**
     * Verify the validity of a credential. Public key must be provided.
     *
     * @param issuerPublicKey the specified public key which used to verify credential signature
     * @param credential the credential
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     */
    ResponseData<Boolean> verify(WeIdPublicKey issuerPublicKey, CredentialPojo credential);

    ResponseData<Boolean> verify(
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
     * @return
     */
    public ResponseData<PresentationE> createPresentation(
        List<CredentialPojo> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        WeIdAuthentication weIdAuthentication
    );

}
