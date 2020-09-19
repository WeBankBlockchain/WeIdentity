/*
 *       Copyright© (2020) WeBank Co., Ltd.
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

package com.webank.weid.full.persistence.testredis;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.transportation.TestBaseTransportation;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.PersistenceFactory;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.api.persistence.params.PersistenceType;
import com.webank.weid.util.PropertyUtils;

public class TestRedisGet extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestRedisGet.class);

    private Persistence persistence = null;

    private static final String domain = "domain.defaultInfo";

    private static final String id = "123456";

    private static final String data = "data123456";

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
        persistence.delete(domain, id);
        ResponseData<Integer> response = persistence.add(domain, id, data);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(1, response.getResult().intValue());
    }

    @Test
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

    @Test
    /**
     * case:test database is not exist.
     */
    public void testGet_databaseNotExist() {

        ResponseData<String> res = persistence.get("sourcedata9999:sdk_all_data", id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
            ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:test database is not exist.
     */
    public void testGet_domainIsNull() {

        ResponseData<String> res = persistence.get(null, id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(data, res.getResult());
    }

    @Test
    /**
     * case:test database is not exist.
     */
    public void testGet_domainIsBlank() {

        ResponseData<String> res = persistence.get("", id);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals(data, res.getResult());
    }

    @Test
    /**
     * case:test table is not exist.
     */
    public void testGet_TableNotExist() {
        String dataSource = domain.split(":")[0];
        ResponseData<String> res = persistence.get(
            dataSource + ":table_not_exist", "123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(),
                res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:test id is not exist.
     */
    //expected null, but was:<>
    public void testGet_idNotExist() {

        ResponseData<String> res = persistence.get(domain, id + Math.random());
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertEquals("", res.getResult());
    }


}
