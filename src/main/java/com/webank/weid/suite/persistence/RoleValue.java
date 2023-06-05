package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class RoleValue {
    /**
     * 主键，weid.
     */
    private String weid;

    /**
     * 创建时间.
     */
    private Date created;

    /**
     * 更新时间.
     */
    private Date updated;

    /**
     * 是否是authority_role.
     */
    private int authority_role;

    /**
     * 是否是committee_role.
     */
    private int committee_role;

    /**
     * 是否是admin_role.
     */
    private int admin_role;
}
