
package com.webank.weid.protocol.base;

import lombok.Data;

@Data
public class IssuerType {
    /**
     * Required: The Issuer Type Name.
     */
    private String typeName;

    /**
     * Required: The The Issuer Type Owner..
     */
    private String owner;

    /**
     * Required: The create date of the Authority Issuer, in timestamp (Long) format.
     */
    private Long created;
}
