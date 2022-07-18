

package com.webank.weid.suite.api.persistence.inf;

import java.util.Map;

import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.protocol.response.ResponseData;

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
}
