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

package com.webank.weid.constant;

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.util.PropertyUtils;

/**
 * cns 类型定义.
 * 
 * @author v_wbgyang
 *
 */
public enum CnsType {

    /**
     * 默认的cns定义，此cns用于weid主合约存储.
     */
    DEFAULT("allOrg", "v2.1"),

    /**
     * 共享cns定义，此cns可作为机构共享数据存储.
     */
    SHARE("share", "v2.1"),
    
    /**
     * 机构配置CNS定义.
     */
    ORG_CONFING("orgConfig", "v2.1");

    private static final String SPLIT_CHAR = "/";

    private String name;

    private String version;

    CnsType(String name, String version) {
        this.name = getCnsName(name);
        this.version = version;
    }

    private String getCnsName(String name) {
        String profile = PropertyUtils.getProperty("cns.profile.active");
        if (StringUtils.isNotBlank(profile)) {
            return profile + SPLIT_CHAR + name;
        }
        return name;
    }

    public String toString() {
        return name + SPLIT_CHAR + version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
