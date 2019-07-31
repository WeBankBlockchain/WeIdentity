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
