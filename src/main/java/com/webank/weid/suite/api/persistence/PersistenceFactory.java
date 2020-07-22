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


package com.webank.weid.suite.api.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.api.persistence.inf.MysqlPersistence;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.api.persistence.inf.RedisPersistence;
import com.webank.weid.suite.api.persistence.params.PersistenceType;
import com.webank.weid.suite.persistence.mysql.driver.MysqlDriver;
import com.webank.weid.suite.persistence.redis.driver.RedisDriver;

/**
 * 数据库工厂, 根据不同类型的数据库得到相应的数据库.
 * @author karenli
 *
 */
public class PersistenceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceFactory.class);

    public static MysqlPersistence newMysqlDriver() {
        return new MysqlDriver();
    }

    public static RedisPersistence newRedisDriver() {
        return new RedisDriver();
    }

    /**
     * 根据封装类型实例化对应的实例对象, 此方法目前支持Mysql, Redis.
     * @param persistenceType 封装类型枚举
     * @return 返回具体处理类型
     */
    public static Persistence build(PersistenceType persistenceType) {
        switch (persistenceType) {
            case Mysql:
                return new MysqlDriver();
            case Redis:
                return new RedisDriver();
            default:
                logger.error("the type = {} unsupported.", persistenceType.name());
                throw new WeIdBaseException(ErrorCode.THIS_IS_UNSUPPORTED);
        }
    }
}
