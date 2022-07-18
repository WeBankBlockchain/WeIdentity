

package com.webank.weid.protocol.cpt.old;

import lombok.Data;

/**
 * CPT for data authorization.
 *
 * @author chaoxinhu 2020.2
 */
@Data
//@Attributes(title = "Data Authorization Token", description = "Authorize data between WeIDs via the exposed Service Endpoint")
public class Cpt101 {

//    @Attributes(required = true, description = "Authorize from this WeID")
    private String fromWeId;
//    @Attributes(required = true, description = "Authorize to this WeID")
    private String toWeId;
//    @Attributes(required = true, description = "Service Endpoint URL")
    private String serviceUrl;
//    @Attributes(required = true, description = "Authorized Resource ID")
    private String resourceId;
//    @Attributes(required = true, description = "Duration of Validity in seconds")
    private Long duration;
}
