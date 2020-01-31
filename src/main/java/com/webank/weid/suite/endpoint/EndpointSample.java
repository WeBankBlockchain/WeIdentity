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

import java.util.concurrent.ConcurrentHashMap;

/**
 * A sample class showing how to register a EndpointFunctor and register a handler with it.
 * Developer can also register his/her own service methods with a similar approach: firstly
 * implement the execute(), and then implement getDescription() by writing your own descriptions.
 * Finally, register it via EndpointHandler.
 *
 * @author chaoxinhu 2019.8
 */
public class EndpointSample {

    private static class DuplicateFunctor implements EndpointFunctor {

        @Override
        public String execute(String arg) {
            return arg + arg + arg;
        }

        @Override
        public String getDescription() {
            return "A sample method to duplicate the input";
        }
    }

    private static class DataAuthorizationFunctor implements EndpointFunctor {

        // The pass-in argument is a resource ID in authToken Credential. This functor must provide
        // a search mechanism with key being the resouece ID. Here, we use ConcurrentHashMap for
        // ease. It's recommended to use a DB / config / props based method for online-update.
        ConcurrentHashMap<String, String> resourceMap = new ConcurrentHashMap<>();

        @Override
        public String execute(String resourceId) {
            if (!resourceMap.containsKey(resourceId)) {
                return "Cannot find this resource: " + resourceId;
            }
            return resourceMap.get(resourceId);
        }

        @Override
        public String getDescription() {
            return "A sample method to fetch authorized data";
        }
    }

    /**
     * This piece of code is a simple sample showing how to register and boot-up the RPC server.
     *
     * @param args input args (omitted)
     * @throws Exception interrupted exception
     */
    public static void main(String[] args) throws Exception {
        // Firstly, clear the existing endpoints
        EndpointDataUtil.clearProps();

        // Register a duplicate endpoint
        EndpointFunctor functor1 = new DuplicateFunctor();
        String requestName1 = "duplicate-input";
        RpcServer.registerEndpoint(requestName1, functor1, null);

        // Register a data-authorization fetch endpoint
        EndpointFunctor functor2 = new DataAuthorizationFunctor();
        /*
         Here the registered endpoint name MUST be the PATH segment of the serviceUrl, and
         the HOST:PORT segment of the serviceUrl must be registered beforehand, and must be
         strictly same as in the REST-service side's application.properties.
         See an example:
         Suppose the serviceUrl is http://10.173.176.98/fetch-data, then endpoint name here must
         be registered as "fetch-data", and "10.173.176.98:6010" must be put in REST-service
         (suppose your endpoint service SDK side occupied port 6010). So, the workflow is like:
          1) SDK side calls registerEndpoint() and puts 6010 in weidentity.properties
          2) endpoint REST side puts 10.173.176.98:6010 in application.properteis
          3) Boot up both sides
          4) endpoint REST side fetches endpoints (including "fetch-data") from SDK
             side (101.73.176.98:6010)
          5) endpoint REST side receives an authToken credential with claimed serviceUrl as
             http://10.173.176.98/fetch-data
          6) endpoint REST side will extract HOST:PORT (10.173.176.98), and search the term (i.e.
             10.173.176.98:6010) in local endpoints info. Note: if a different port than 6010 is
             used, serviceUrl must clearly specify it e.g. http://10.173.176.98:6011/fetch-data
          7) endpoint REST side will also search the requestName with key "fetch-data" in its local
             endpoints info.
          8) endpoint REST side finds the endpoint, so an RPC call is sent to 10.173.176.98:6010,
             piggybacking the resourceId.
          9) the SDK side receives the RPC call and invoke the execute() to return authorized data.
        */
        RpcServer.registerEndpoint("fetch-data", functor2, null);

        // Start the RPC server instance
        RpcServer.main(null);
    }
}
