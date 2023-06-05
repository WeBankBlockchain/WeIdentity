
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

    /**
     * transfer the IssuerType from weid-blockchain.
     * @param type the IssuerType class in weid-blockchain
     * @return WeIdDocument
     */
    public static IssuerType fromBlockChain(com.webank.weid.blockchain.protocol.base.IssuerType type) {
        IssuerType issuerType = new IssuerType();
        issuerType.setTypeName(type.getTypeName());
        issuerType.setCreated(type.getCreated());
        issuerType.setOwner(type.getOwner());
        return issuerType;
    }
}
