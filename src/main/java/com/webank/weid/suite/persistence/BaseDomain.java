

package com.webank.weid.suite.persistence;

import java.util.Date;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.util.PropertyUtils;

/**
 * Persistence通用domain类.
 *
 * @author karenli
 */

@Getter
public class BaseDomain {

    /**
     * the split for value.
     */
    public static final String VALUE_SPLIT_CHAR = ":";

    /**
     * default table name.
     */
    public static final String DEFAULT_TABLE = "default_info";

    /**
     * the domain key.
     */
    public String key;

    /**
     * the domain value.
     */
    public String value;

    /**
     * the database domain.
     */
    public String baseDomain;

    /**
     * the table domain.
     */
    public String tableDomain;

    /**
     * the domain timeout.
     */
    public long timeout = 86400000L;

    /**
     * resolve Domain Timeout.
     *
     */
    public void resolveDomainTimeout() {
        String timeout = PropertyUtils.getProperty(this.key + ".timeout");
        if (StringUtils.isBlank(timeout)) {
            timeout =  PropertyUtils.getProperty(DataDriverConstant.DOMAIN_DEFAULT_INFO_TIMEOUT);
        }
        if (StringUtils.isNotBlank(timeout)) {
            this.timeout = Long.parseLong(timeout);
        }
    }

    /**
     * get the expire time.
     *
     * @return date
     */
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
