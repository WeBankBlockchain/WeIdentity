/*
 *       Copyright© (2018) WeBank Co., Ltd.
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
     * the split for value.
     */
    private static final String VALUE_SPLIT_CHAR = ":";

    /**
     * default table name.
     */
    private static final String DEFAULT_TABLE = "default_info";

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