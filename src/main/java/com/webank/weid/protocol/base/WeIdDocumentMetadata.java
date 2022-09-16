

package com.webank.weid.protocol.base;

import lombok.Data;

@Data
public class WeIdDocumentMetadata {

    /**
     * Required: The created.
     */
    private Long created;

    /**
     * Required: The updated.
     */
    private Long updated;

    /**
     * Required: The deactivated.
     */
    private boolean deactivated;

    /**
     * Required: The versionId.
     */
    private int versionId;
}
