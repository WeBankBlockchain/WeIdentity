

package com.webank.weid.full.persistence.testmysql;

import com.webank.weid.common.LogUtil;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.full.persistence.TestBaseTransportation;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.util.PropertyUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMysqlDelete extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestMysqlDelete.class);

    private Persistence persistence = null;

    private static final String domain = "domain.defaultInfo";

    private  static final String id = "123456";

    private  static final String data = "data123456";

    private static PersistenceType persistenceType = null;

    @Override
    public synchronized void testInit() {

        String type = PropertyUtils.getProperty("persistence_type");
        if (type.equals("mysql")) {
            persistenceType = PersistenceType.Mysql;
        } else if (type.equals("redis")) {
            persistenceType = PersistenceType.Redis;
        }
        persistence = PersistenceFactory.build(persistenceType);
        save_data();
    }


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


    /**
     * case:test delete database not exist.
     */
    public void testDelete_databaseNotExist() {
        String table = domain.split("\\.")[1];

        ResponseData<Integer> res = persistence.delete(
                "9999999." + table, "123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:test database is null.
     */
    public void testDelete_databaseNull() {
        String table = domain.split("\\.")[1];
        ResponseData<Integer> res = persistence.delete(
                "null." + table, id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:test database is blank.
     */
    public void testDelete_databaseBlank() {

        String table = domain.split("\\.")[1];
        ResponseData<Integer> res = persistence.delete("." + table, id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:test table is not exist.
     */
    public void testDelete_tableNotExist() {
        String dataSource = domain.split("\\.")[0];
        ResponseData<Integer> res = persistence.delete(
                dataSource + ".table_not_exist", id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(),
                res.getErrorCode().intValue());
    }


    /**
     * case:test table is null.
     */
    public void testDelete_tableNull() {
        String dataSource = domain.split("\\.")[0];
        ResponseData<Integer> res = persistence.delete(dataSource + ".", id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:id is not exist.
     */
    public void testDelete_idNotExist() {

        ResponseData<Integer> res = persistence.delete(domain, "id_is_not_exist");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(0, res.getResult().intValue());
    }


    /**
     * case:test id is null.
     */
    public void testDelete_idNull() {

        ResponseData<Integer> res = persistence.delete(domain, null);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.PRESISTENCE_DATA_KEY_INVALID.getCode(),
                res.getErrorCode().intValue());
    }


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
