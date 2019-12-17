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

package com.webank.weid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.collections4.CollectionUtils;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.persistence.sql.SqlDomain;
import com.webank.weid.suite.persistence.sql.SqlExecutor;

public abstract class MockMysqlDriver {

    /**
     * mock DB for storage data.
     */
    public static final Map<String, Object> mockDbMap = new HashMap<String, Object>();
    
    public static final Set<String> mockTableSet = new HashSet<String>();

    /**
     * the default method for mock mySqlDriver.
     */
    public static void mockMysqlDriver() {

        if (!mockTableSet.contains("default_data")) {
            mockTableSet.add("default_data");
        }
        new MockUp<SqlExecutor>() {
            
            //SqlExecutor executor;
            SqlDomain sqlDomain; 
            @Mock
            public void $init(Invocation invocation, SqlDomain sqlDomain) {
                //this.executor = invocation.getInvokedInstance();
                //Deencapsulation
                this.sqlDomain = sqlDomain;
            }
            
            @Mock
            public ResponseData<Integer> execute(String sql, Object... data) {
                String tableDomain = sqlDomain.getTableName();
                if (sql.startsWith("insert")) {
                    if (mockDbMap.containsKey(data[0].toString())) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SQL_EXECUTE_FAILED
                        );
                    }
                    mockDbMap.put(data[0].toString(), data[1]);
                } else if (sql.startsWith("delete")) {
                    if (!mockTableSet.contains(tableDomain)) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SQL_EXECUTE_FAILED
                        );
                    }
                    if (!mockDbMap.containsKey(data[0].toString())) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SUCCESS
                        );
                    }
                    mockDbMap.remove(data[0].toString());
                } else if (sql.startsWith("update")) {
                    if (!mockTableSet.contains(tableDomain)) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SQL_EXECUTE_FAILED
                        );
                    }
                    if (!mockDbMap.containsKey(data[3].toString())) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SUCCESS
                        );
                    }
                    mockDbMap.put(data[3].toString(), data[1]);
                } else if (sql.startsWith("CREATE")) {
                    if (mockTableSet.contains(tableDomain)) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SQL_EXECUTE_FAILED
                        );
                    }
                    mockTableSet.add(tableDomain);
                }
                return new ResponseData<Integer>(1, ErrorCode.SUCCESS);
            }

            @Mock
            public ResponseData<Map<String, String>> executeQuery(String sql, Object... data) {
                String tableName = sqlDomain.getTableName();
                Map<String, String> map = new HashMap<String, String>();
                if (mockTableSet.contains(tableName)) {
                    if (data != null && data.length > 0) {
                        map.put(DataDriverConstant.SQL_COLUMN_DATA, (String)mockDbMap.get(data[0]));
                        return new ResponseData<Map<String, String>>(map, ErrorCode.SUCCESS);
                    }
                    map.put(DataDriverConstant.SQL_COLUMN_DATA, tableName);
                    return new ResponseData<Map<String, String>>(map, ErrorCode.SUCCESS);
                }
                return new ResponseData<Map<String, String>>(null, ErrorCode.SQL_EXECUTE_FAILED);
            }
            
            @Mock
            public ResponseData<Integer> batchSave(String sql, List<List<Object>> dataList) {
                List<Object> values = dataList.get(dataList.size() - 1);
                for (List<Object> list : dataList) {
                    if (CollectionUtils.isEmpty(list) || list.size() != values.size()) {
                        return 
                            new ResponseData<Integer>(
                                DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                                ErrorCode.PRESISTENCE_BATCH_SAVE_DATA_MISMATCH
                            );  
                    }
                }
                List<Object> idList = dataList.get(0);
                int saveCount = 0;
                for (int i = 0; i < idList.size(); i++) {
                    if (mockDbMap.containsKey(idList.get(i))) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SQL_EXECUTE_FAILED
                        );
                    } else {
                        mockDbMap.put(idList.get(i).toString(), dataList.get(1).get(i).toString()); 
                        saveCount++;
                    }
                }
                return new ResponseData<Integer>(saveCount, ErrorCode.SUCCESS);
            }
            
            @Mock
            public void resolveTableDomain(String checkTableSql, String createTableSql) {
                mockTableSet.add(sqlDomain.getTableName());
            }
        };
    }
}
