

package com.webank.weid.suite.api.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.api.persistence.inf.Persistence;
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

    private static Persistence newMysqlDriver() {
        return new MysqlDriver();
    }

    private static Persistence newRedisDriver() {
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
                return newMysqlDriver();
            case Redis:
                return newRedisDriver();
            default:
                logger.error("the type = {} unsupported.", persistenceType.name());
                throw new WeIdBaseException(ErrorCode.THIS_IS_UNSUPPORTED);
        }
    }
}
