/*
 *       Copyright© (2019-2020) WeBank Co., Ltd.
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
 * CPT for data authorization.
 *
 * @author chaoxinhu 2020.2
 */
@Data
@Attributes(title = "Data Authorization Token",
    description = "Authorize data between WeIDs via the exposed Service Endpoint")
public class Cpt101 {

    @Attributes(required = true, description = "Authorize from this WeID")
    private String fromWeId;
    @Attributes(required = true, description = "Authorize to this WeID")
    private String toWeId;
    @Attributes(required = true, description = "Service Endpoint URL")
    private String serviceUrl;
    @Attributes(required = true, description = "Authorized Resource ID")
    private String resourceId;
    @Attributes(required = true, description = "Duration of Validity in seconds")
    private Long duration;
}
