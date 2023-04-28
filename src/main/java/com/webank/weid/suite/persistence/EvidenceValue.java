package com.webank.weid.suite.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class EvidenceValue {
    /**
     * 主键，hash.
     */
    private String hash;

    /**
     * signers of evidence.
     */
    private String signers;

    /**
     * signatures of evidence.
     */
    private String signatures;

    /**
     * logs of evidence.
     */
    private String logs;

    /**
     * 每个签名的时间.
     */
    private String updated;

    /**
     * revoke of each signature.
     */
    private String revoked;

    /**
     * extraKey of evidence.
     */
    private String extra_key;

    /**
     * extraData of evidence.
     */
    private String extra_data;

    /**
     * groupId of evidence.
     */
    private String group_id;

}
