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
 * The interface which caller must implement this in their own integrations.
 *
 * @author chaoxinhu 2019.8
 */
public interface EndpointFunctor {

    /**
     * Execute the function via its String-typed responseBody (argument). First, use ``` to separate
     * the arguments, and convert them to your ideal POJO type. Then, call the functions. Lastly,
     * use any serialization function to return.
     *
     * @param arg argument String, separated by ```
     * @return any serialized object String
     */
    String execute(String arg);

    /**
     * Return the basic description of this endpoint.
     *
     * @return description message String
     */
    String getDescription();
}
