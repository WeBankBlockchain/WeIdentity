/*
 *       Copyright© (2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.protocol.base.EndpointInfo;

/**
 * A singleton service to handle endpoint's Create (register), Read, Update and Delete. Also
 * including send and receive (Java method level re-routing) capability.
 *
 * @author chaoxinhu 2019.8
 */
public class EndpointHandler {

    /**
     * Map structure to store requestName and its registered EndpointFunctor Impl.
     */
    public Map<String, EndpointFunctor> implMap = new ConcurrentHashMap<>();

    /**
     * Singletons.
     */
    private static volatile EndpointHandler endpointHandler;

    private EndpointHandler() {
    }

    /**
     * Obtain an Endpoint Handler instance.
     *
     * @return handler instance.
     */
    public static EndpointHandler getInstance() {
        if (endpointHandler == null) {
            synchronized (EndpointHandler.class) {
                if (endpointHandler == null) {
                    endpointHandler = new EndpointHandler();
                }
            }
        }
        return endpointHandler;
    }

    /**
     * Register an endpoint with specified impl object and host address and store in both local
     * memory and config file.
     *
     * @param requestName request name
     * @param functorImpl the implemented fuctor
     * @throws Exception save to files exception
     */
    public final void registerEndpoint(String requestName, EndpointFunctor functorImpl)
        throws Exception {
        implMap.put(requestName, functorImpl);
        EndpointInfo endpointInfo = new EndpointInfo();
        endpointInfo.setRequestName(requestName);
        endpointInfo.setDescription(functorImpl.getDescription());
        EndpointDataUtil.mergeToCentral(endpointInfo);
        EndpointDataUtil.saveEndpointsToFile();
    }

    /**
     * Remove an endpoint from local memory and config file.
     *
     * @param requestName given request name
     */
    public final void removeEndpoint(String requestName) {
        implMap.remove(requestName);
        EndpointInfo endpointInfo = new EndpointInfo();
        endpointInfo.setRequestName(requestName);
        EndpointDataUtil.removeEndpoint(endpointInfo);
    }

    /**
     * The actual execute method. Implementations must be done by caller first.
     *
     * @param requestName the request name to check in the mapping
     * @param requestBody the request body to pass in
     * @return the serialized Object
     */
    public String execute(String requestName, String requestBody) {
        EndpointFunctor functorImpl = implMap.get(requestName);
        if (functorImpl == null) {
            return StringUtils.EMPTY;
        }
        return functorImpl.execute(requestBody);
    }
}