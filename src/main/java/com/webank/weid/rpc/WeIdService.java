/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.PublicKeyArgs;
import com.webank.weid.protocol.request.ServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;


/**
 * Service inf for operations on WeIdentity DID.
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
     * Create a WeIdentity DID from the provided public key.
     *
     * @param publicKey the public key to create a weid
     * @param weIdAuthentication your private key
     * @return WeIdentity DID
     */
    ResponseData<String> delegateCreateWeId(
        WeIdPublicKey publicKey,
        WeIdAuthentication weIdAuthentication
    );

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
     * Add a public key in the WeIdentity DID Document. If this key is already revoked, then it will
     * be un-revoked.
     *
     * @param weId the WeID to add public key to
     * @param publicKeyArgs the public key args
     * @param privateKey the private key to send blockchain transaction
     * @return the public key ID, -1 if any error occurred
     */
    ResponseData<Integer> addPublicKey(String weId, PublicKeyArgs publicKeyArgs,
        WeIdPrivateKey privateKey);

    /**
     * Add a public key in the WeIdentity DID Document by other delegate caller (currently it must
     * be admin / committee). If this key is already revoked, then it will be un-revoked.
     *
     * @param weId the WeID to add public key to
     * @param publicKeyArgs the set public key args
     * @param delegateAuth the delegate's auth
     * @return the public key ID, -1 if any error occurred
     */
    ResponseData<Integer> delegateAddPublicKey(
        String weId,
        PublicKeyArgs publicKeyArgs,
        WeIdPrivateKey delegateAuth
    );

    /**
     * Set service properties.
     *
     * @param weId the WeID to set service to
     * @param serviceArgs your service name and endpoint
     * @param privateKey the private key
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> setService(String weId, ServiceArgs serviceArgs,
        WeIdPrivateKey privateKey);

    /**
     * Set service properties.
     *
     * @param weId the WeID to set service to
     * @param serviceArgs your service name and endpoint
     * @param delegateAuth the delegate's auth
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> delegateSetService(
        String weId,
        ServiceArgs serviceArgs,
        WeIdPrivateKey delegateAuth
    );

    /**
     * Set authentications in WeIdentity DID.
     *
     * @param weId the WeID to set auth to
     * @param authenticationArgs A public key is needed
     * @param privateKey the private key
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> setAuthentication(
        String weId,
        AuthenticationArgs authenticationArgs,
        WeIdPrivateKey privateKey);

    /**
     * Set authentications in WeIdentity DID.
     *
     * @param weId the WeID to set auth to
     * @param authenticationArgs A public key is needed.
     * @param delegateAuth the delegate's auth
     * @return true if the "set" operation succeeds, false otherwise.
     */
    ResponseData<Boolean> delegateSetAuthentication(
        String weId,
        AuthenticationArgs authenticationArgs,
        WeIdPrivateKey delegateAuth
    );

    /**
     * Check if the WeIdentity DID exists on chain.
     *
     * @param weId The WeIdentity DID.
     * @return true if exists, false otherwise.
     */
    ResponseData<Boolean> isWeIdExist(String weId);

    /**
     * Remove a public key enlisted in WeID document together with the its authentication.
     *
     * @param weId the WeID to delete public key from
     * @param publicKeyArgs the public key args
     * @param privateKey the private key to send blockchain transaction
     * @return true if succeeds, false otherwise
     */
    ResponseData<Boolean> revokePublicKeyWithAuthentication(
        String weId,
        PublicKeyArgs publicKeyArgs,
        WeIdPrivateKey privateKey);

    /**
     * Remove an authentication tag in WeID document only - will not affect its public key.
     *
     * @param weId the WeID to remove auth from
     * @param authenticationArgs A public key is needed
     * @param privateKey the private key
     * @return true if succeeds, false otherwise
     */
    ResponseData<Boolean> revokeAuthentication(
        String weId,
        AuthenticationArgs authenticationArgs,
        WeIdPrivateKey privateKey);
}
