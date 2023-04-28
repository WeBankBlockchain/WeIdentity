package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class CptValue {
    /**
     * 主键，cptId.
     */
    private int cpt_id;

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
    private int cpt_version;

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
    private String cpt_schema;

    /**
     * cpt签名
     */
    private String cpt_signature;

    /**
     * credential验证公钥
     */
    private String credential_publicKey;

    /**
     * credential证明
     */
    private String credential_proof;
    /**
     * claim policies列表
     */
    private String claim_policies;

}
