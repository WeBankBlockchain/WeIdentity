

package com.webank.weid.protocol.base;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.inf.Hashable;

/**
 * The base data structure to represent a Hash value.
 *
 * @author chaoxinhu 2019.9
 */
public class HashString implements Hashable {

    private String hash = StringUtils.EMPTY;

    public HashString(String hash) {
        setHash(hash);
    }

    @Override
    public String getHash() {
        return hash;
    }

    /**
     * Set hash value with a validity check.
     *
     * @param hash hash value
     */
    public void setHash(String hash) {
        if (StringUtils.isEmpty(hash)
            || !Pattern.compile(WeIdConstant.HASH_VALUE_PATTERN).matcher(hash).matches()) {
            return;
        }
        this.hash = hash;
    }
}
