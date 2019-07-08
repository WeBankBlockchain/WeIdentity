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

import com.webank.weid.protocol.base.Challenge;

/**
 * Credential for authentication. The answer to meet the challenge. We package the answer into a
 * Credential(CPT104) so the verifier can verify this answer.
 *
 * @author Created by Junqi Zhang on 2019/4/9.
 */
@Data
@Attributes(title = "Authentication Answer", description = "Answer to meet the challenge")
public class Cpt103 {

    @Attributes(required = true, description = "The entity's weidentity did")
    private String id;
    @Attributes(required = true, description = "The challenge")
    private Challenge challenge;
    @Attributes(required = true, description = "The proof")
    private String proof;
}
