

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
     * Required: The id.
     */
    private String id;

    /**
     * Required: The type.
     */
    private String type;

    /**
     * Required: The service endpoint.
     */
    private String serviceEndpoint;

    public String toString() {
        return this.id + ',' + this.type + ',' + this.serviceEndpoint;
    }

    public static ServiceProperty fromString(String serviceString) {
        String[] result = serviceString.split(",");
        ServiceProperty serviceProperty = new ServiceProperty();
        serviceProperty.setId(result[0]);
        serviceProperty.setType(result[1]);
        serviceProperty.setServiceEndpoint(result[2]);
        return serviceProperty;
    }
}
