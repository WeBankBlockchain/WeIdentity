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

import java.util.List;

import lombok.Data;

/**
 * The base data structure to handle Credential EvidenceInfo info.
 *
 * @author chaoxinhu 2019.1
 */
@Data
public class EvidenceInfo {

    /**
     * Required: The full Credential hash.
     */
    private String credentialHash;

    /**
     * Required: The signers of this Credential.
     */
    private List<String> signers;

    /**
     * Required: The signatures of each signers with the same order.
     * In JavaBean object, the signatures will be encoded in Base64.
     * On the blockchain, the signatures will be stored in its r, s, v.
     */
    private List<String> signatures;
}
