/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * The sign information of evidence's each signer's sign attempt. Used as a mapped info against each
 * individual signer.
 *
 * @author chaoxinhu 2020.2
 * @since v1.6.0
 */
@Data
public class EvidenceSignInfo {

    /**
     * The signature of the signer onto this evidence.
     */
    private String signature;

    /**
     * The timestamp at which this evidence is signed.
     */
    private String timestamp;

    /**
     * The extra value this signer records on chain.
     */
    private List<String> logs = new ArrayList<>();

    /**
     * Whether this signer revoked this evidence. This is initialized as null, and will be set
     * appropriate values upon getEvidence().
     */
    private Boolean revoked = null;
}
