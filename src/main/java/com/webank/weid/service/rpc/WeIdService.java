

package com.webank.weid.service.rpc;

import java.util.List;

import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.ServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.WeIdListResult;



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
     * A private key is required to send transaction, but may be not matching the given public key
     *
     * @param weIdPublicKey you need to input a public key
     * @param weIdPrivateKey you need to input a private key
     * @return a data set including a WeIdentity DID and a keypair
     */
    ResponseData<String> createWeIdByPublicKey(WeIdPublicKey weIdPublicKey, WeIdPrivateKey weIdPrivateKey);

    /**
     * Create a WeIdentity DID from the provided public key.
     *
     *@param createWeIdArgs the create WeIdentity DID args
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
     * Query WeIdentity DID document metadata.
     *
     * @param weId the WeIdentity DID
     * @return weId document metadata in java object type
     */
    ResponseData<WeIdDocumentMetadata> getWeIdDocumentMetadata(String weId);

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
     * Check if the WeIdentity DID exists on chain.
     *
     * @param weId The WeIdentity DID.
     * @return true if exists, false otherwise.
     */
    ResponseData<Boolean> isWeIdExist(String weId);

    /**
     * Check if the WeIdentity DID is deactivated on chain.
     *
     * @param weId The WeIdentity DID.
     * @return true if is deactivated, false otherwise.
     */
    ResponseData<Boolean> isDeactivated(String weId);

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

    /**
     * query data according to block height, index location and search direction.
     * 
     * @param first the first index of weid in contract
     * @param last the last index of weid in contract
     * @return return the WeId List
     */
    ResponseData<List<String>> getWeIdList(
        Integer first,
        Integer last
    );
    
    /**
     * get total weId.
     *
     * @return total weid
     */
    ResponseData<Integer> getWeIdCount();

    /**
     * get WeID list by pubKey list.
     * @param pubKeyList the pubKey list
     * @return return the WeIDListResult
     */
    ResponseData<WeIdListResult> getWeIdListByPubKeyList(List<WeIdPublicKey> pubKeyList);
}
