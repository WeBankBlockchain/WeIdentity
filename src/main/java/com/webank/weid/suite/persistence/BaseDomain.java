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
