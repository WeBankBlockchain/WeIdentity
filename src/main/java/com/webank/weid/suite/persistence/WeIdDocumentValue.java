package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class WeIdDocumentValue {
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
     * 数据版本.
     */
    private int version;

    /**
     * 是否被注销.
     */
    private int deactivated;

    /**
     * WeIdDocument对象的json序列化.
     */
    private String document_schema;

}
