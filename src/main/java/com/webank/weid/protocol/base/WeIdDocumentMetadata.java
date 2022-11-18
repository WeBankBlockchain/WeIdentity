

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

    public static WeIdDocumentMetadata fromBlockChain(com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata metadata) {
        WeIdDocumentMetadata weIdDocumentMetadata = new WeIdDocumentMetadata();
        weIdDocumentMetadata.setCreated(metadata.getCreated());
        weIdDocumentMetadata.setUpdated(metadata.getUpdated());
        weIdDocumentMetadata.setDeactivated(metadata.isDeactivated());
        weIdDocumentMetadata.setVersionId(metadata.getVersionId());
        return weIdDocumentMetadata;
    }
}
