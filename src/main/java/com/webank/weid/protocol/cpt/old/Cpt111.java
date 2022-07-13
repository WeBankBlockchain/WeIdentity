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

package com.webank.weid.protocol.cpt.old;

import lombok.Data;

/**
 * User request issuer to sign credential.
 *
 * @author tonychen
 */
@Data
//@Attributes(title = "User CPT", description = "Reserved CPT 111")
public class Cpt111 {

//    @Attributes(required = true, description = "CPT ID")
    private String cptId;
//    @Attributes(required = true, description = "credential Signature Request", minimum = 1)
    private String credentialSignatureRequest;
//    @Attributes(required = true, description = "User Nonce")
    private String userNonce;
}
