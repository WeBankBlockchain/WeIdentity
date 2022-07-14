

package com.webank.weid.config;

import lombok.Data;

/**
 * contract address config.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class ContractConfig {

    /**
     * The WeIdentity DID Contract address.
     */
    private String weIdAddress;

    /**
     * The CPT Contract address.
     */
    private String cptAddress;

    /**
     * The AuthorityIssuerController Contract address.
     */
    private String issuerAddress;

    /**
     * The EvidenceController Contract address.
     */
    private String evidenceAddress;

    /**
     * The Specific issuer Contract address.
     */
    private String specificIssuerAddress;
}
