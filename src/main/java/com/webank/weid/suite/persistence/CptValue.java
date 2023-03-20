package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class CptValue {
    /**
     * 主键，cptId.
     */
    private int cptId;

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
    private int cptVersion;

    /**
     * cpt创建者.
     */
    private String publisher;

    /**
     * cpt描述.
     */
    private String description;

    /**
     * cpt对象的json序列化.
     */
    private String cptSchema;

    /**
     * cpt签名
     */
    private String cptSignature;

}
