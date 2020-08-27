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

public class TestRedisDelete extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestRedisDelete.class);

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
