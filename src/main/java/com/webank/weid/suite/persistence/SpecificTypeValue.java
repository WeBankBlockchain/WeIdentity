package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class SpecificTypeValue {
    /**
     * 主键，type_name.
     */
    private String type_name;

    /**
     * SpecificType从属weid.
     */
    private String fellow;

    /**
     * 创建时间.
     */
    private Date created;

    /**
     * 更新时间.
     */
    private Date updated;

    /**
     * SpecificType拥有者.
     */
    private String owner;
}
