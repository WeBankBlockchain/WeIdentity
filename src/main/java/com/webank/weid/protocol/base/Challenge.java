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

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Junqi Zhang on 2019/4/9.
 */
@Getter
@Setter
public class Challenge extends Version implements RawSerializer {

    private String challegeId;

    /**
     * Specify who you want to challenge.
     */
    private String weId;

    /**
     * Specify a random alphanumeric nonce and WeIdentity DID owner will sign a credential which
     * include the nonce to prove the ownership of this WeIdentity DID. The relying party should
     * include a random alphanumeric (i.e. nonce) in the challenge, to prevent replay attacks. This
     * is also known as dynamic challenge.
     *
     */
    private String nonce;
    
    @Override
    public String toRawData() {
        return this.nonce;
    }
}
