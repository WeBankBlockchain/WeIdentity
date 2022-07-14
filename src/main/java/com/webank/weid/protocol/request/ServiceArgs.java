

package com.webank.weid.protocol.request;

import lombok.Data;

/**
 * The Arguments when setting services.
 *
 * @author tonychen 2020.4.24
 */
@Data
public class ServiceArgs {

    /**
     * Required: service type.
     */
    private String type;

    /**
     * Required: service endpoint.
     */
    private String serviceEndpoint;

}
