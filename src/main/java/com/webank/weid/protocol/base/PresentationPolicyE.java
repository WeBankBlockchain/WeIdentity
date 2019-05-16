/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.protocol.base;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.webank.weid.protocol.inf.JsonSerializer;

/**
 * The base data structure to handle Credential info.
 *
 * @author junqizhang 2019.04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PresentationPolicyE extends Version implements JsonSerializer {

    /**
     * Policy ID.
     */
    private Integer id;

    /**
     * represent who publish this presentation policy.
     */
    private String orgId;

    /**
     * represent who publish this presentation policy.
     */
    private String weId;

    /**
     * specify which properties in which credential are needed.
     */
    private Map<Integer, ClaimPolicy> policy;

    /**
     * extra data which policy presenter can use it store some specific business data.
     */
    private Map<String, String> extra;
}
