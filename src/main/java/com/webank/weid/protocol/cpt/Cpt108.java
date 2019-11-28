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
import com.webank.weid.protocol.base.CredentialPojo;
import java.util.List;
import lombok.Data;

/**
 * Trusted timestamping envelope.
 *
 * @author junqizhang 2019.11
 */

@Data
@Attributes(title = "Trusted Timestamping", description = "Trusted timestamping envelope")
public class Cpt108 {

    /**
     * ID of timestamp authority.
     */
    String TimestampAuthorityId;

    /**
     * information about timestamp authority.
     */
    String TimestampAuthority;

    /**
     * caculate the hash from the credentials.
     */
    String claimHash;

    /**
     * trusted timestamping provided by the trusted third party or by the consensus of each node
     * in the consortium chain.
     */
    Long timestamp;

    /**
     * signed by Timestamp authority.
     * authoritySignature = sign( hashKey )
     */
    String authoritySignature;

    @Attributes(required = true, description = "Original credential list to be signed")
    List<CredentialPojo> credentialList;
}
