

package com.webank.weid.suite.persistence;

import java.util.List;
import java.util.Map;

import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.WeIdDocument;
import com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * Data access driver.
 *
 * @author tonychen 2019年3月18日
 */
public interface Persistence {

    /**
     * add data to storage.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @param data which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> add(String domain, String id, String data);

    /**
     * batch add data to storage.
     *
     * @param domain the domain of the data.
     * @param keyValueList list of id-data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> batchAdd(String domain, Map<String, String> keyValueList);

    /**
     * query data from storage by id.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @return the data you stored.
     */
    public ResponseData<String> get(String domain, String id);

    /**
     * delete data by id.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @return the data you stored.
     */
    public ResponseData<Integer> delete(String domain, String id);

    /**
     * update data by id.
     *
     * @param domain the domain of the data.
     * @param id the key you store with.
     * @param data the data you want to update into.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> update(String domain, String id, String data);

    /**
     * add data to storage if not exist, others for update.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @param data which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addOrUpdate(String domain, String id, String data);

    /**
     * add transaction to storage.
     *
     * @param transactionArgs the transaction info.
     * @return execute status of the "addTransaction" operation.
     */
    public ResponseData<Integer> addTransaction(TransactionArgs transactionArgs);
    /**
     * add data to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param documentSchema which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addWeId(String domain, String weId, String documentSchema);
    /**
     * add data to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param documentSchema which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> updateWeId(String domain, String weId, String documentSchema);
    /**
     * query WeIdDocumentMetadata from storage by id.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return the data you stored.
     */
    public ResponseData<WeIdDocument> getWeIdDocument(String domain, String weId);
    /**
     * query WeIdDocumentMetadata from storage by id.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return the data you stored.
     */
    public ResponseData<WeIdDocumentMetadata> getMeta(String domain, String weId);

    /**
     * deactivateWeId by id.
     *
     * @param domain the domain of the data.
     * @param weId the weId you want to deactivate.
     * @param state the state you want to change.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> deactivateWeId(String domain, String weId, Boolean state);

    /**
     * get several weId.
     *
     * @param domain the domain of the data.
     * @param first the first index of weId.
     * @param last the last index of weId.
     * @return execute status of the "update" operation.
     */
    public ResponseData<List<String>> getWeIdList(String domain, Integer first, Integer last);

    /**
     * get total amounts of weId.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getWeIdCount(String domain);

    /**
     * query Cpt from storage by cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @return the data you stored.
     */
    public ResponseData<CptValue> getCpt(String domain, int cptId);
    /**
     * save Cpt to storage with cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @param publisher the publisher of the cpt.
     * @param description the description of the cpt.
     * @param cptSchema the cptSchema of the cpt.
     * @param cptSignature the cptSignature of the cpt.
     * @return the data you stored.
     */
    public ResponseData<CptBaseInfo> addCpt(String domain, int cptId, String publisher, String description, String cptSchema, String cptSignature);
}
