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

package com.webank.weid.suite.persistence.sql.driver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.sql.SqlExecutor;
import com.webank.weid.util.DataToolUtils;

/**
 * mysql operations.
 *
 * @author tonychen 2019年3月18日
 */
public class MysqlDriver implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(MysqlDriver.class);
    
    private static final String CHECK_TABLE_SQL =
        "SELECT table_name data FROM information_schema.TABLES WHERE table_name ='$1'";
    
    private static final String CREATE_TABLE_SQL =
        "CREATE TABLE `$1` ("
        + "`id` varchar(128) NOT NULL COMMENT 'primary key',"
        + "`data` blob DEFAULT NULL COMMENT 'the save data', "
        + "`created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'created', "
        + "`updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'updated', "
        + "`protocol` varchar(32) DEFAULT NULL COMMENT 'protocol', "
        + "PRIMARY KEY (`id`) "
        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the data table'";

    private static final Integer FAILED_STATUS = DataDriverConstant.SQL_EXECUTE_FAILED_STATUS;
    
    private static final ErrorCode KEY_INVALID = ErrorCode.PRESISTENCE_DATA_KEY_INVALID;
    
    @Override
    public ResponseData<String> get(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->get] the id of the data is empty.");
            return new ResponseData<String>(StringUtils.EMPTY, KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);
        try {
            ResponseData<String> response = new SqlExecutor(domain)
                .executeQuery(SqlExecutor.SQL_QUERY, dataKey);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                && response.getResult() != null) {
                response.setResult(
                    new String(
                        response.getResult().getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8
                    )
                );
            }
            return response;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->get] get the data error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#save(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> save(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->save] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);
        try {
            return new SqlExecutor(domain)
                .executeSave(
                    SqlExecutor.SQL_SAVE,
                    CHECK_TABLE_SQL,
                    CREATE_TABLE_SQL,
                    dataKey,
                    data
                );
        } catch (WeIdBaseException e) {
            logger.error("[mysql->save] save the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#batchSave(java.util.List, java.util.List)
     */
    @Override
    public ResponseData<Integer> batchSave(String domain, List<String> ids, List<String> dataList) {
        List<List<String>> dataLists = new ArrayList<List<String>>();
        List<String> idHashList = new ArrayList<>();
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                logger.error("[mysql->batchSave] the id of the data is empty.");
                return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
            }
            idHashList.add(DataToolUtils.getHash(id));
        }
        dataLists.add(idHashList);
        dataLists.add(dataList);
        try {
            return new SqlExecutor(domain)
                .batchSave(
                    SqlExecutor.SQL_SAVE,
                    CHECK_TABLE_SQL,
                    CREATE_TABLE_SQL, 
                    dataLists
                );
        } catch (WeIdBaseException e) {
            logger.error("[mysql->batchSave] batchSave the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#delete(java.lang.String)
     */
    @Override
    public ResponseData<Integer> delete(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->delete] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);
        try {
            return new SqlExecutor(domain).execute(SqlExecutor.SQL_DELETE, dataKey);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->delete] delete the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#update(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> update(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->update] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);
        Date date = new Date();
        try {
            return new SqlExecutor(domain).execute(SqlExecutor.SQL_UPDATE, date, data, dataKey);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->update] update the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }
}
