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
 * User request issuer to sign credential.
 *
 * @author tonychen
 */
@Data
@Attributes(title = "metadata CPT", description = "Reserved CPT 110")
public class Cpt110 {

    @Attributes(required = true, description = "CPT ID")
    private Integer cptId;

    /**
     * credential id.
     */
    @Attributes(required = true, description = "credential ID")
    private String id;

    /**
     * Required: The context field.
     */
    @Attributes(required = true, description = "context")
    private String context;

    /**
     * Required: The issuer WeIdentity DID.
     */
    @Attributes(required = true, description = "issuer weid")
    private String issuer;

    /**
     * Required: The create date.
     */
    @Attributes(required = true, description = "issuanceDate")
    private Long issuanceDate;

    /**
     * Required: The expire date.
     */
    @Attributes(required = true, description = "expirationDate")
    private Long expirationDate;
}
