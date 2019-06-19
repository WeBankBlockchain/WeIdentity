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

package com.webank.weid.suite.persistence.driver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.DbBase;
import com.webank.weid.util.DataToolUtils;

/**
 * mysql operations.
 *
 * @author tonychen 2019年3月18日
 */
public class MysqlDriver extends DbBase implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(MysqlDriver.class);

    /**
     * sql for query.
     */
    private static final String SQL_QUERY = "select id,data,created from sdk_all_data where id =?";

    /**
     * sql for save.
     */
    private static final String SQL_SAVE = "insert into sdk_all_data(id, data) values(?,?)";

    /**
     * sql for update.
     */
    private static final String SQL_UPDATE = "update sdk_all_data set data = ?, updated=now() "
        + "where id = ?";

    /**
     * sql for delete.
     */
    private static final String SQL_DELETE = "delete from sdk_all_data where id = ?";

    /**
     * 根据驱动类型构造对象.
     */
    public MysqlDriver() {
        super(DataDriverConstant.JDBC_MYSQL_DRIVER_CLASS_NAME);
    }

    @Override
    public ResponseData<String> get(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->get] the id of the data is empty.");
            return 
                new ResponseData<String>(
                    StringUtils.EMPTY,
                    ErrorCode.PRESISTENCE_DATA_KEY_INVALID
                );
        }
        String dataKey = DataToolUtils.getHash(id);
        return super.executeQuery(SQL_QUERY, dataKey);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#save(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> save(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->save] the id of the data is empty.");
            return 
                new ResponseData<Integer>(
                    DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                    ErrorCode.PRESISTENCE_DATA_KEY_INVALID
                );
        }
        String dataKey = DataToolUtils.getHash(id);
        return super.execute(SQL_SAVE, dataKey, data);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#batchSave(java.util.List, java.util.List)
     */
    @Override
    public ResponseData<Integer> batchSave(String domain, List<String> ids, List<String> dataList) {
        List<List<String>> dataLists = new ArrayList<List<String>>();
        List<String> idHashList = new ArrayList<>();
        for (String id : ids) {
            idHashList.add(DataToolUtils.getHash(id));
        }
        dataLists.add(idHashList);
        dataLists.add(dataList);
        return super.batchSave(SQL_SAVE, dataLists);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#delete(java.lang.String)
     */
    @Override
    public ResponseData<Integer> delete(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->delete] the id of the data is empty.");
            return 
                new ResponseData<Integer>(
                    DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                    ErrorCode.PRESISTENCE_DATA_KEY_INVALID
                );
        }
        String dataKey = DataToolUtils.getHash(id);
        return super.execute(SQL_DELETE, dataKey);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#update(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> update(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->update] the id of the data is empty.");
            return 
                new ResponseData<Integer>(
                    DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                    ErrorCode.PRESISTENCE_DATA_KEY_INVALID
                );
        }
        String dataKey = DataToolUtils.getHash(id);
        return super.execute(SQL_UPDATE, data, dataKey);
    }
}
