

package com.webank.weid.constant;

/**
 * Constants used in Endpoint Service Integration-side.
 *
 * @author chaoxinhu 2019.8
 */
public class EndpointServiceConstant {

    /**
     * The separator to divide each *item* in the RPC message - between requestName and requestBody
     * and requestUUID.
     */
    public static final String EPS_SEPARATOR = "|||";

    /**
     * The separator to divide each *argument* in the requestBody item.
     */
    public static final String PARAM_SEPARATOR = "```";

    /**
     * The default function name for "fetch" function.
     */
    public static final String FETCH_FUNCTION = "auto-fetch";
}
