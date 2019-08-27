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

    /**
     * This piece of code is a simple sample showing how to register, call and remove.
     *
     * @param args input args (omitted)
     * @throws Exception interrupted exception
     */
    public static void main(String[] args) throws Exception {
        // Firstly clear the existing endpoints
        EndpointDataUtil.clearProps();
        EndpointFunctor functor = new DuplicateFunctor();
        String requestName = "duplicate-input";
        RpcServer.registerEndpoint(requestName, functor);
        RpcServer.main(null);
    }
}
