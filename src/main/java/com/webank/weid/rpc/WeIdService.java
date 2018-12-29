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

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service interface for operations on WeIdentity DID.
 *
 * @author tonychen
 */
public interface WeIdService {

    /**
     * Create a WeIdentity DID without a keypair. SDK will generate a keypair for the caller.
     *
     * @return a data set including a WeIdentity DID and a keypair
     */
    ResponseData<CreateWeIdDataResult> createWeId();

    /**
     * Create a WeIdentity DID from the provided public key.
     *
     * @param createWeIdArgs you need to input a public key
     * @return WeIdentity DID
     */
    ResponseData<String> createWeId(CreateWeIdArgs createWeIdArgs);

    /**
     * Query WeIdentity DID document.
     *
     * @param weId the WeIdentity DID
     * @return WeIdentity document in json type
     */
    ResponseData<String> getWeIdDocumentJson(String weId);

    /**
     * Query WeIdentity DID document.
     *
     * @param weId the WeIdentity DID
     * @return weId document in java object type
     */
    ResponseData<WeIdDocument> getWeIdDocument(String weId);

    /**
     * Set public key in the WeIdentity DID Document.
     *
     * @param setPublicKeyArgs the set public key args
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> setPublicKey(SetPublicKeyArgs setPublicKeyArgs);

    /**
     * Set service properties.
     *
     * @param setServiceArgs your service name and endpoint
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> setService(SetServiceArgs setServiceArgs);

    /**
     * Set authentications in WeIdentity DID.
     *
     * @param setAuthenticationArgs A public key is needed.
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> setAuthentication(SetAuthenticationArgs setAuthenticationArgs);

    /**
     * Check if the WeIdentity DID exists on chain.
     *
     * @param weId The WeIdentity DID.
     * @return true if exists, false otherwise.
     */
    ResponseData<Boolean> isWeIdExist(String weId);
}
