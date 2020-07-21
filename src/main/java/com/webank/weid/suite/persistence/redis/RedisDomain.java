package com.webank.weid.suite.persistence.redis;

import java.util.Date;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.MysqlDriverConstant;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.persistence.mysql.ConnectionPool;
import com.webank.weid.util.PropertyUtils;

/**
 * @author karenli
 * @program: weid-java-sdk
 * @description:
 * @date 2020-07-09 17:43:46
 */
@Getter
public class RedisDomain {

    private static final Logger logger = LoggerFactory.getLogger(RedisDomain.class);

    /**
     * the split for key.
     */
    public static final String KEY_SPLIT_CHAR = ".";

    /**
     * the split for value.
     */
    private static final String VALUE_SPLIT_CHAR = ":";

    /**
     * the domain prefix.
     */
    public static final String PREFIX = "domain.";

    /**
     * default table name.
     */
    private static final String DEFAULT_TABLE = "default_info";

    /**
     * 表的默认前缀.
     */
    private static final String DEFAULT_TABLE_PREFIX = "weidentity";

    /**
     * 机构编码.
     */
    private static final String ORG_ID = PropertyUtils.getProperty("blockchain.orgid");

    /**
     * 表名分隔符.
     */
    private static final String TABLE_SPLIT_CHAR = "_";

    /**
     * the domain key.
     */
    private String key;

    /**
     * the domain value.
     */
    private String value;

    /**
     * the database domain.
     */
    private String baseDomain;

    /**
     * the table domain.
     */
    private String tableDomain;

    /**
     * the domain timeout.
     */
    private long timeout = 86400000L;


    public RedisDomain() {
        resolveDomain();
    }

    public RedisDomain(String domainkey) {
        this.key = domainkey;
        resolveDomain();
    }

    //检查Domain，并且设置好tableDomain以及baseDomain
    private void resolveDomain() {

        //当配置key为空，默认为default_info
        if (StringUtils.isBlank(this.key)) {
            this.key = MysqlDriverConstant.DOMAIN_DEFAULT_INFO;
        }
        this.value = PropertyUtils.getProperty(this.key);

        //当value为空并且key为default_info时（即default_info没有在properties中）
        // baseDomain和table_Domain是默认配置
        if (StringUtils.isBlank(this.value)
                && MysqlDriverConstant.DOMAIN_DEFAULT_INFO.equals(this.key)) {
            this.baseDomain = ConnectionPool.getFirstDataSourceName();
            this.tableDomain = DEFAULT_TABLE;

            //当properties中有相应的domain，给baseDomain和tabledomain赋值
        } else if (StringUtils.isNotBlank(this.value)
                && this.value.split(VALUE_SPLIT_CHAR).length == 2) {
            String[] domains = this.value.split(VALUE_SPLIT_CHAR);
            //比如datasource1:encrypt_key_info，则baseDomain=datasource1  tableDomain=encrypt_key_info
            this.baseDomain = domains[0];
            this.tableDomain = domains[1];
            //如果连接池中没有baseDomain（即没有该数据库），报错为xxx数据库不存在
            if (!ConnectionPool.checkDataSourceName(this.baseDomain)) {
                logger.error(
                        "[resolveDomain] the domain {{}:{}} is invalid, {} is not exists.",
                        this.key,
                        this.value,
                        this.baseDomain
                );
                throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_INVALID);
            }
        } else {
            //domain不为空但是形式不为xx：xxxx，（比如domain：default：info）报错domain不合法
            logger.error("[resolveDomain] the domain {{}:{}} is illegal.",
                    this.key,
                    this.value
            );
            throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL);
        }
        resolveDomainTimeout();
    }

    private void resolveDomainTimeout() {
        String timeout = PropertyUtils.getProperty(this.key + ".timeout");
        if (StringUtils.isBlank(timeout)) {
            timeout =  PropertyUtils.getProperty(MysqlDriverConstant.DOMAIN_DEFAULT_INFO_TIMEOUT);
        }
        if (StringUtils.isNotBlank(timeout)) {
            this.timeout = Long.parseLong(timeout);
        }
    }


    public Date getExpire() {
        return new Date(System.currentTimeMillis() + this.timeout);
    }

    /**
     * get the currenttime.
     *
     * @return now
     */
    public Date getNow() {
        return new Date();
    }

}