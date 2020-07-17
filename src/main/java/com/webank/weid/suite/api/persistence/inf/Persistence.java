/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.suite.api.persistence.inf;

import java.util.List;

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
     * @param ids list of keys
     * @param dataList list of data
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> batchAdd(String domain, List<String> ids, List<String> dataList);

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
    public ResponseData<Integer> saveOrUpdate(String domain, String id, String data);

    /**
     * add transaction to storage.
     *
     * @param transactionArgs the transaction info.
     * @return execute status of the "saveTransaction" operation.
     */
    public ResponseData<Integer> saveTransaction(TransactionArgs transactionArgs);
}
