package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorityIssuerInfo {
    /**
     * 主键，weid.
     */
    private String weid;

    /**
     * m名字
     */
    private String name;

    /**
     * 描述.
     */
    private String description;

    /**
     * 创建时间.
     */
    private Date created;

    /**
     * 更新时间.
     */
    private Date updated;

    /**
     * 是否已经被确认.
     */
    private int recognize;

    /**
     * acc_value.
     */
    private String acc_value;

    /**
     * 附属描述.
     */
    private String extra_str;

    /**
     * 附属描述.
     */
    private String extra_int;
}
