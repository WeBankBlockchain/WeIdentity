

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * The Arguments when setting services.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class SetServiceArgs {

    /**
     * Required: user's WeIdentity DID.
     */
    private String weId;

    /**
     * Required: service type.
     */
    private String type;

    /**
     * Required: service endpoint.
     */
    private String serviceEndpoint;

    /**
     * Required: WeIdentity DID private key.
     */
    private WeIdPrivateKey userWeIdPrivateKey;
}
