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

package com.webank.weid.protocol.cpt.old;

import java.util.List;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;

/**
 * Api endpoint address disclosure.
 *
 * @author junqizhang, chaoxinhu 2019.8
 */
@Data
@Attributes(title = "API Endpoint", description = "API Endpoint address disclosure")
public class Cpt105 {

    @Attributes(required = true, description = "Owner WeIdentity DID")
    private String id;
    @Attributes(required = true, description = "Network host and port")
    private String hostport;
    @Attributes(required = true, description = "Endpoint name")
    private String endpointName;
    @Attributes(required = true, description = "Description")
    private String description;
    @Attributes(required = true, description = "API Version")
    private String version;
    @Attributes(required = true, description = "Argument types in sequence")
    private List<String> argType;
}
