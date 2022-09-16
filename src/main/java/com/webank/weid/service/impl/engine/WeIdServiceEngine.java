

package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.protocol.base.WeIdPojo;
import com.webank.weid.protocol.response.ResponseData;

/**
 * this service engine calls the contract methods and process blockchain response data on FISCO BCOS
 * 1.3.x or on FISCO BCOS 2.0.
 *
 * @author tonychen 2019年6月21日
 */
public interface WeIdServiceEngine extends ReloadStaticContract {

    /**
     * call weid contract to create a new weid.
     *
     * @param weAddress identity on blockchain
     * @param publicKey public key of the identity
     * @param privateKey privateKey identity's private key
     * @return result
     */
    ResponseData<Boolean> createWeId(
        String weAddress,
        String publicKey,
        String privateKey
    );

    /**
     * call weid contract to deactivate a weid.
     *
     * @param weAddress address of the identity
     * @param privateKey privateKey identity's private key
     * @return result
     */
    ResponseData<Boolean> deactivateWeId(
            String weAddress,
            String privateKey
    );

    /**
     * call weid contract to update the weid document.
     *
     * @param weIdDocument identity on blockchain
     * @param weAddress address of the identity
     * @param privateKey privateKey identity's private key
     * @return result
     */
    ResponseData<Boolean> updateWeId(
            WeIdDocument weIdDocument,
            String weAddress,
            String privateKey
    );

    /**
     * check if the weid exists on blockchain.
     *
     * @param weId the weid of the entity
     * @return result
     */
    ResponseData<Boolean> isWeIdExist(String weId);

    /**
     * check if the weid deactivated on blockchain.
     *
     * @param weId the weid of the entity
     * @return result
     */
    ResponseData<Boolean> isDeactivated(String weId);

    /**
     * get weid document from blockchain.
     *
     * @param weId the entity's weid
     * @return weid document
     */
    ResponseData<WeIdDocument> getWeIdDocument(String weId);

    /**
     * get weid document metadata from blockchain.
     *
     * @param weId the entity's weid
     * @return weid document metadata
     */
    ResponseData<WeIdDocumentMetadata> getWeIdDocumentMetadata(String weId);

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
}
