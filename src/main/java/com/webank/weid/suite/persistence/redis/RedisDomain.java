

package com.webank.weid.suite.persistence.redis;

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
public class RedisDomain extends BaseDomain {

    private static final Logger logger = LoggerFactory.getLogger(RedisDomain.class);

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
        } else if (this.key.equals(DataDriverConstant.DOMAIN_OFFLINE_TRANSACTION_INFO)) {
            this.tableDomain = DataDriverConstant.DOMAIN_OFFLINE_TRANSACTION_INFO;
        } else {
            logger.error("[resolveDomain] the domain {{}:{}} is illegal.",
                    this.key,
                    this.value
            );
            throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL);
        }
        resolveDomainTimeout();
    }
}
