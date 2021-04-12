/*
 *       Copyright© (2018-2021) WeBank Co., Ltd.
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

package com.webank.weid.protocol.base;

import lombok.Data;

@Data
public class IssuerType {
    /**
     * Required: The Issuer Type Name.
     */
    private String typeName;

    /**
     * Required: The The Issuer Type Owner..
     */
    private String owner;

    /**
     * Required: The create date of the Authority Issuer, in timestamp (Long) format.
     */
    private Long created;
}
