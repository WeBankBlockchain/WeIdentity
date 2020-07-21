/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import lombok.Data;

import com.webank.weid.constant.WeIdConstant.PublicKeyType;

/**
 * The base data structure for AuthenticationProperty.
 *
 * @author tonychen 2018.10.8
 */
@Data
public class AuthenticationProperty {

    /**
     * Required: The type.
     */
    private String type = PublicKeyType.SECP256K1.getTypeName();

    /**
     * Required: The public key.
     */
    private String publicKey;

    /**
     * Required: Revoked, or not.
     */
    private Boolean revoked = false;
}
