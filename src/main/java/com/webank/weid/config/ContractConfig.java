/*
 *       Copyright© (2018) WeBank Co., Ltd.
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * contract address config.
 * @author tonychen 2018.9.29
 */
@Component("ContractConfig")
@Data
public class ContractConfig {

    /**
     * The WeIdentity DID Contract address.
     */
    @Value("${weId.contractaddress}")
    private String weIdAddress;

    /**
     * The CPT Contract address.
     */
    @Value("${cpt.contractaddress}")
    private String cptAddress;

    /**
     * The AuthorityIssuerController Contract address.
     */
    @Value("${issuer.contractaddress}")
    private String issuerAddress;
}
