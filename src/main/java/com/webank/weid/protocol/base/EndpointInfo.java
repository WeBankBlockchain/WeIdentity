

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * An EndpointInfo contains a requestName with an N:1 relationship - N in, 1 requestName. SDK
 * endpoint handler will handle Endpoint related services rerouting based this stub.
 *
 * @author chaoxinhu 2019.7
 */
@Data
public class EndpointInfo {

    private String requestName;
    private List<String> inAddr = new ArrayList<>();
    private String description;
}
