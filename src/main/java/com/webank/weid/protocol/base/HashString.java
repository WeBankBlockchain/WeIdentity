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
