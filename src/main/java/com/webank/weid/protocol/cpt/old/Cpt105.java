

package com.webank.weid.protocol.cpt.old;

import java.util.List;
import lombok.Data;

/**
 * Api endpoint address disclosure.
 *
 * @author junqizhang, chaoxinhu 2019.8
 */
@Data
//@Attributes(title = "API Endpoint", description = "API Endpoint address disclosure")
public class Cpt105 {

//    @Attributes(required = true, description = "Owner WeIdentity DID")
    private String id;
//    @Attributes(required = true, description = "Network host and port")
    private String hostport;
//    @Attributes(required = true, description = "Endpoint name")
    private String endpointName;
//    @Attributes(required = true, description = "Description")
    private String description;
//    @Attributes(required = true, description = "API Version")
    private String version;
//    @Attributes(required = true, description = "Argument types in sequence")
    private List<String> argType;
}
