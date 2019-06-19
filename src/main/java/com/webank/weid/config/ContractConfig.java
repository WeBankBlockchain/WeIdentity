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

package com.webank.weid.config;

import lombok.Data;

/**
 * contract address config.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class ContractConfig {

    /**
     * The WeIdentity DID Contract address.
     */
    private String weIdAddress;

    /**
     * The CPT Contract address.
     */
    private String cptAddress;

    /**
     * The AuthorityIssuerController Contract address.
     */
    private String issuerAddress;

    /**
     * The EvidenceController Contract address.
     */
    private String evidenceAddress;

    /**
     * The Specific issuer Contract address.
     */
    private String specificIssuerAddress;
}
