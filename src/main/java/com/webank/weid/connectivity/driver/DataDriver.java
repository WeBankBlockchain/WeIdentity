/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.connectivity.driver;

import java.util.List;

import com.webank.weid.protocol.response.ResponseData;

/**
 * Data access driver.
 *
 * @author tonychen 2019年3月18日
 */
public interface DataDriver {

    /**
     * save data to storage.
     *
     * @param id the key of the data.
     * @param data which you want to store to the storage.
     * @return execute status of the "save" operation.
     */
    public ResponseData<Integer> save(String id, String data);

    /**
     * batch save data to storage.
     *
     * @param ids list of keys
     * @param dataList list of data
     * @return execute status of the "save" operation.
     */
    public ResponseData<Integer> batchSave(List<String> ids, List<String> dataList);

    /**
     * query data from storage by id.
     *
     * @param id the key of the data.
     * @return the data you stored.
     */
    public ResponseData<String> getData(String id);

    /**
     * delete data by id.
     * 
     * @param id the key of the data.
     * @return the data you stored.
     */
    public ResponseData<Integer> delete(String id);

    /**
     * update data by id.
     * @param id the key you store with.
     * @param data the data you want to update into.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> update(String id, String data);
}
