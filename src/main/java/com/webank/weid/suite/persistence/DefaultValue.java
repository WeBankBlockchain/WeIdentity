package com.webank.weid.suite.persistence;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class DefaultValue implements Serializable {

    /**
     * 主键.
     */
    private String id;

    /**
     * blob主体数据.
     */
    private String data;

    /**
     * 创建时间.
     */
    private Date created;
    /**
     * 更新时间.
     */
    private Date updated;

    /**
     * 编码格式.
     */
    private String protocol;

    /**
     * 超时时间.
     */
    private Date expire;

    /**
     * 数据所属版本.
     */
    private String version;

    /**
     * 扩展字段1.
     */
    private int ext1;

    /**
     * 扩展字段2.
     */
    private int ext2;

    /**
     * 扩展字段3.
     */
    private String ext3;

    /**
     * 扩展字段4.
     */
    private String ext4;

}
