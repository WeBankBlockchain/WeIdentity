package com.webank.weid.suite.persistence.redis;

import java.util.Date;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.util.PropertyUtils;


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

    private void resolveDomain() {

        if (StringUtils.isBlank(this.key)) {
            this.key = DataDriverConstant.DOMAIN_DEFAULT_INFO;
        }
        this.value = PropertyUtils.getProperty(this.key);

        if (StringUtils.isBlank(this.value)
                && DataDriverConstant.DOMAIN_DEFAULT_INFO.equals(this.key)) {
            this.tableDomain = DEFAULT_TABLE;

        } else if (StringUtils.isNotBlank(this.value)
                && this.value.split(VALUE_SPLIT_CHAR).length == 2) {
            String[] domains = this.value.split(VALUE_SPLIT_CHAR);
            this.baseDomain = domains[0];
            this.tableDomain = domains[1];
        } else {
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
            timeout =  PropertyUtils.getProperty(DataDriverConstant.DOMAIN_DEFAULT_INFO_TIMEOUT);
        }
        if (StringUtils.isNotBlank(timeout)) {
            this.timeout = Long.parseLong(timeout);
        }
    }


    public Date getExpire() {
        return new Date(System.currentTimeMillis() + this.timeout);
    }

    /**
     * get the current time.
     *
     * @return now
     */
    public Date getNow() {
        return new Date();
    }

}