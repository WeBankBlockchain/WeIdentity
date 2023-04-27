package com.webank.weid.suite.persistence;

import lombok.Data;

@Data
public class PresentationValue {
    /**
     * 主键，presentationId.
     */
    private int presentation_id;

    /**
     * presentation创建者.
     */
    private String creator;

    /**
     * claim policies列表
     */
    private String claim_policies;
}
