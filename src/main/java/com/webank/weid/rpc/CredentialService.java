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

import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service interface for operations on Credentials.
 *
 * @author chaoxinhu 2018.10
 */
public interface CredentialService {

    /**
     * Generate a credential.
     *
     * @param args the args
     * @return credential
     */
    ResponseData<Credential> createCredential(CreateCredentialArgs args);

    /**
     * Verify the validity of a credential. Public key will be fetched from chain.
     *
     * @param args the args
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     *      in ResponseData
     */
    ResponseData<Boolean> verifyCredential(Credential args);

    /**
     * Verify the validity of a credential. Public key must be provided.
     *
     * @param args the args
     * @return the verification result. True if yes, false otherwise with exact verify error codes
     *      in ResponseData
     */
    ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(VerifyCredentialArgs args);
}
