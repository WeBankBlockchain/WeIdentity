package com.webank.weid.full;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import mockit.Mock;
import mockit.MockUp;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.persistence.driver.MysqlDriver;

public interface MockMysqlDriver {
    /**
     * mock DB for storage data.
     */
    public static final Map<String, String> mockDbMap = new HashMap<String, String>();
    
    public static final String ISMOCKKEY = "isMock";
    
    public default void mockMysqlDriver() {
        String vlaue = mockDbMap.get(ISMOCKKEY);
        if (StringUtils.isNotBlank(vlaue)){
            return;
        }
        new MockUp<MysqlDriver>() {
            @Mock
            public ResponseData<Integer> save(String domain, String id, String data) {
                mockDbMap.put(id, data);
                return new ResponseData<Integer>(1,ErrorCode.SUCCESS);
            }
            
            @Mock
            public ResponseData<String> get(String domain, String id) {
                return new ResponseData<String>(mockDbMap.get(id),ErrorCode.SUCCESS);
            }
        };
        mockDbMap.put(ISMOCKKEY, ISMOCKKEY);
    }
    
}
