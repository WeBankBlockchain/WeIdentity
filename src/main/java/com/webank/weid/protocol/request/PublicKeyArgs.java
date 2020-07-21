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

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.constant.WeIdConstant.PublicKeyType;

/**
 * The Arguments when setting Public Key for WeIdentity DID.
 *
 * @author tonychen 2020.4.24
 */
@Data
public class PublicKeyArgs {

    /**
     * Required: The type.
     */
    private PublicKeyType type = PublicKeyType.SECP256K1;

    /**
     * Required: The owner.
     */
    private String owner;

    /**
     * Required: The public key.
     */
    private String publicKey;

    /**
     * nothing to do.
     *
     * @param type the public key type
     */
    public void setType(PublicKeyType type) {
        this.type = type;
    }

    public PublicKeyArgs() {
        super();
    }

    public PublicKeyArgs(String publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKeyArgs(String owner, String publicKey) {
        this.owner = owner;
        this.publicKey = publicKey;
    }
}
