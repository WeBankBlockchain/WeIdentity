package com.webank.weid.full.persistence.TestRedis;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.transportation.TestBaseTransportation;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.persistence.redis.driver.RedisDriver;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDelete extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestDelete.class);

    private Persistence persistence = null;

    private static final String domain = "domain.defaultInfo";

    private  static final String id = "123456";

    private  static final String data = "data123456";

    @Override
    public synchronized void testInit() {
        //super.mockMysqlDriver();
        if (persistence == null) {
//            persistence = new MysqlDriver();
            persistence = new RedisDriver();
        }
        save_data();
    }

    @Test
    /**
     * case:test add.
     */
    public void save_data() {

        ResponseData<Integer> res = persistence.delete(domain, id);
        LogUtil.info(logger, "persistence", res);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<Integer> result = persistence.add(domain, id, data);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), result.getErrorCode().intValue());
        Assert.assertEquals(1, result.getResult().intValue());
    }

    @Test
    /**
     * case:id is not exist.
     */
    public void testDelete_idNotExist() {

        ResponseData<Integer> res = persistence.delete(domain, "id_is_not_exist");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(0, res.getResult().intValue());
    }

    @Test
    /**
     * case:test id is null.
     */
    public void testDelete_idNull() {

        ResponseData<Integer> res = persistence.delete(domain, null);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.PRESISTENCE_DATA_KEY_INVALID.getCode(),
            res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:test id is blank.
     */
    public void testDelete_idBlank() {

        ResponseData<Integer> res = persistence.delete(domain, "");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.PRESISTENCE_DATA_KEY_INVALID.getCode(),
            res.getErrorCode().intValue());
    }

}
