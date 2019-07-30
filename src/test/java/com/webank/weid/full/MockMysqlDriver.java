package com.webank.weid.full;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.persistence.sql.SqlExecutor;

public interface MockMysqlDriver {

    /**
     * mock DB for storage data.
     */
    public static final Map<String, Object> mockDbMap = new HashMap<String, Object>();
    
    public static final Set<String> mockTableSet = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
            {
                add("sdk_all_data");
            }
        };

    public static final String ISMOCKKEY = "isMock";

    /**
     * the default method for mock mySqlDriver.
     */
    public default void mockMysqlDriver() {
        String vlaue = (String)mockDbMap.get(ISMOCKKEY);
        if (StringUtils.isNotBlank(vlaue)) {
            return;
        }
        new MockUp<SqlExecutor>() {
            
            SqlExecutor executor;
            
            @Mock
            public void $init(Invocation invocation, String domain) {
                executor = invocation.getInvokedInstance();
                Deencapsulation.invoke(
                    executor, "resolveDomain", new Class[]{String.class}, domain);
            }
            
            @Mock
            public ResponseData<Integer> execute(String sql, Object... data) {
                String tableDomain = (String)Deencapsulation.getField(executor, "tableDomain");
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
                    if (!mockDbMap.containsKey(data[2].toString())) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SUCCESS
                        );
                    }
                    mockDbMap.put(data[2].toString(), data[1]);
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
            public ResponseData<String> executeQuery(String sql, String... data) {
                String tableDomain = (String)Deencapsulation.getField(executor, "tableDomain");
                if (mockTableSet.contains(tableDomain)) {
                    if (data != null && data.length > 0) {
                        return new ResponseData<String>(
                            (String)mockDbMap.get(data[0]), ErrorCode.SUCCESS);
                    }
                    return new ResponseData<String>(tableDomain, ErrorCode.SUCCESS);
                }
                return new ResponseData<String>(null, ErrorCode.SQL_EXECUTE_FAILED);
            }
            
            @Mock
            public ResponseData<Integer> batchSave(String sql, List<List<String>> dataList) {
                List<String> values = dataList.get(dataList.size() - 1);
                for (List<String> list : dataList) {
                    if (CollectionUtils.isEmpty(list) || list.size() != values.size()) {
                        return 
                            new ResponseData<Integer>(
                                DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                                ErrorCode.PRESISTENCE_BATCH_SAVE_DATA_MISMATCH
                            );  
                    }
                }
                List<String> idList = dataList.get(0);
                int saveCount = 0;
                for (int i = 0; i < idList.size(); i++) {
                    if (mockDbMap.containsKey(idList.get(i))) {
                        return new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.SQL_EXECUTE_FAILED
                        );
                    } else {
                        mockDbMap.put(idList.get(i), dataList.get(1).get(i)); 
                        saveCount++;
                    }
                }
                return new ResponseData<Integer>(saveCount, ErrorCode.SUCCESS);
            }
        };
        mockDbMap.put(ISMOCKKEY, ISMOCKKEY);
    }
}
