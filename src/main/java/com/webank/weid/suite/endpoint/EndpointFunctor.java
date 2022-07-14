

package com.webank.weid.suite.endpoint;

/**
 * The interface which caller must implement this in their own integrations.
 *
 * @author chaoxinhu 2019.8
 */
public interface EndpointFunctor {

    /**
     * Execute the callback function via its String-typed responseBody (argument). First, use ```
     * to separate arguments and convert them to your ideal POJO type. Then, call the functions.
     * Lastly, use any serialization function to return.
     *
     * @param arg argument String, separated by ```
     * @return any serialized object String
     */
    String callback(String arg);

    /**
     * Return the basic description of this endpoint.
     *
     * @return description message String
     */
    String getDescription();
}
