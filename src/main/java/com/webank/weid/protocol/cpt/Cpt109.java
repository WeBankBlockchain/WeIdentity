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
 * Trusted timestamping envelope.
 *
 * @author junqizhang 2019.11
 */

@Data
@Attributes(title = "Trusted Timestamp", 
    description = "Trusted Timestamp from authorized 3rd-party, or chain consensus")
public class Cpt109 {

    /**
     * caculate the hash from the entire list rather than from any single credential.
     */
    String claimHash;

    /**
     * trusted timestamping provided by the trusted third party or by the consensus of each node in
     * the consortium chain.
     */
    Long timestamp;

    /**
     * hashKey = hash(claimHash + timestamp) hashKey will be the key in the smart contract.
     */
    String hashKey;

    /**
     * signed by Timestamp authority signature = sign( hashKey ).
     */
    String signatureList;
}
