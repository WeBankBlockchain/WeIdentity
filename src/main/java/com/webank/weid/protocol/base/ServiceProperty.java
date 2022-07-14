

package com.webank.weid.protocol.base;

import lombok.Data;

/**
 * The base data structure for Service Properties.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class ServiceProperty {

    /**
     * Required: The type.
     */
    private String type;

    /**
     * Required: The service endpoint.
     */
    private String serviceEndpoint;
}
