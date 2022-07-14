

package com.webank.weid.protocol.base;

import lombok.Data;

@Data
public class WeIdBaseInfo {
    /**
     * Required: The id.
     */
    private String id;

    /**
     * Required: The created.
     */
    private Long created;
}
