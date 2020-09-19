/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.suite.persistence.mysql;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.persistence.BaseDomain;
import com.webank.weid.util.PropertyUtils;

@Getter
public class SqlDomain extends BaseDomain {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlDomain.class);
    
    /**
     * the split for key.
     */
    public static final String KEY_SPLIT_CHAR = ".";
    
    /**
     * the domain prefix.
     */
    public static final String PREFIX = "domain.";
    
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
    
    public SqlDomain() {
        resolveDomain();
    }
    
    public SqlDomain(String domainKey) {
        this.key = domainKey;
        resolveDomain();
    }

    private void resolveDomain() {
        if (StringUtils.isBlank(this.key)) {
            this.key = DataDriverConstant.DOMAIN_DEFAULT_INFO;
        }
        this.value = PropertyUtils.getProperty(this.key);
        if (StringUtils.isBlank(this.value) 
            && DataDriverConstant.DOMAIN_DEFAULT_INFO.equals(this.key)) {
            this.baseDomain = ConnectionPool.getFirstDataSourceName();
            this.tableDomain = DEFAULT_TABLE;
        } else if (StringUtils.isNotBlank(this.value) 
            && this.value.split(VALUE_SPLIT_CHAR).length == 2) {
            String[] domains = this.value.split(VALUE_SPLIT_CHAR);
            this.baseDomain = domains[0];
            this.tableDomain = domains[1];
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
            logger.error("[resolveDomain] the domain {{}:{}} is illegal.", 
                this.key, 
                this.value
            );
            throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL);
        }
        resolveDomainTimeout();
    }
    
    /**
     * get the table name.
     * @return the tableName
     */
    public String getTableName() {
        if (StringUtils.isBlank(ORG_ID)) {
            logger.error("[getTableName] the orgid is blank.");
            throw new WeIdBaseException(ErrorCode.ORG_ID_IS_NULL);
        }
        return new StringBuffer(DEFAULT_TABLE_PREFIX)
            .append(TABLE_SPLIT_CHAR)
            .append(ORG_ID)
            .append(TABLE_SPLIT_CHAR)
            .append(this.tableDomain).toString();
    }
    
    /**
     * 设置数据源名称, 目前接口暂时不开放.
     * @param baseDomain 数据源名称
     */
    void setBaseDomain(String baseDomain) {
        this.baseDomain = baseDomain;
    }
}
