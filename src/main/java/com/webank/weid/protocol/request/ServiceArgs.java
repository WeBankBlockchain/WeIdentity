

package com.webank.weid.protocol.request;

import lombok.Data;

/**
 * The Arguments when setting services.
 *
 * @author tonychen 2020.4.24
 */
@Data
public class ServiceArgs {

    //用户可以指定service id，也可以由系统指定
    /**
     * Required: The service id.
     */
    private String id;

    /**
     * Required: service type.
     */
    private String type;

    /**
     * Required: service endpoint.
     */
    private String serviceEndpoint;

}
