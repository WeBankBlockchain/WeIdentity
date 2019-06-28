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

package com.webank.weid.protocol.cpt;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;

/**
 * Api endpoint address disclosure.
 *
 * @author Created by Junqi Zhang on 2019/4/9.
 */
@Data
@Attributes(title = "API Endpoint", description = "API Endpoint address disclosure")
public class Cpt105 {
    @Attributes(required = true, description = "The WeIdentity DID of owner")
    private String ownerWeId;
    @Attributes(required = true, description = "The URL")
    private String url;
    @Attributes(required = true, description = "The port")
    private String port;
    @Attributes(required = true, description = "The policy ID")
    private Integer policyId;
    @Attributes(required = true, description = "The org ID")
    private String orgId;
}
