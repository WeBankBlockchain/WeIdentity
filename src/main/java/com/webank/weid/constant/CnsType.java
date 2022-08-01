

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
    DEFAULT("allOrg", "v3.3"),

    /**
     * 共享cns定义，此cns可作为机构共享数据存储.
     */
    SHARE("share", "v3.3"),
    
    /**
     * 机构配置CNS定义.
     */
    ORG_CONFING("orgConfig", "v3.3");

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
