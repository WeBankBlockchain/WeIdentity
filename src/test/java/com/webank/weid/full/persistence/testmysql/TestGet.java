package com.webank.weid.full.persistence.testmysql;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.transportation.TestBaseTransportation;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.persistence.mysql.driver.MysqlDriver;

public class TestGet extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestGet.class);

    private Persistence persistence = null;

    private static final String domain = "domain.default";
    private static final String id = "123456";
    private static final String data = "data123456";

    @Override
    public synchronized void testInit() {
        //super.mockMysqlDriver();
        if (persistence == null) {
            persistence = new MysqlDriver();
        }
        persistence.delete(domain, id);
        ResponseData<Integer> response = persistence.add(domain, id, data);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(1, response.getResult().intValue());
    }

    /**
     * case:test get.
     */
    public void testGet_success() {

        ResponseData<String> res = persistence.get(
            domain, id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(data, res.getResult());
    }

    /**
     * case:test database is not exist.
     */
    public void testGet_databaseNotExist() {

        ResponseData<String> res = persistence.get("sourcedata9999:sdk_all_data", id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
            ErrorCode.PRESISTENCE_DOMAIN_INVALID.getCode(), res.getErrorCode().intValue());
    }

    /**
     * case:test database is not exist.
     */
    public void testGet_domainIsNull() {

        ResponseData<String> res = persistence.get(null, id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(data, res.getResult());
    }

    /**
     * case:test database is not exist.
     */
    public void testGet_domainIsBlank() {

        ResponseData<String> res = persistence.get("", id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(data, res.getResult());
    }

    /**
     * case:test table is not exist.
     */
    public void testGet_TableNotExist() {
        String dataSource = domain.split(":")[0];
        ResponseData<String> res = persistence.get(
            dataSource + ":table_not_exist", "123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SQL_EXECUTE_FAILED.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:test id is not exist.
     */
    public void testGet_idNotExist() {

        ResponseData<String> res = persistence.get(domain, id + Math.random());
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertNull(res.getResult());
    }

}
