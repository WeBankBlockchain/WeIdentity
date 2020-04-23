/*
 * Copyright© (2018-2020) WeBank Co., Ltd.
 *
 * This file is part of weid-java-sdk.
 *
 * weid-java-sdk is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * weid-java-sdk is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * weid-java-sdk. If not, see <https://www.gnu.org/licenses/>.
 */


package com.webank.weid.constant;

/**
 * Credential type.
 * @author tonychen 2020年4月22日
 */
public enum CredentialType {

    /**
     * original type, used to create original type credential.
     */
    ORIGINAL(0, "original"),

    /**
     * zkp type, used to create zkp type credential.
     */
    ZKP(1, "zkp"),

    /**
     * lite1 type, used to create lite1 type credential.
     */
    LITE1(2, "lite1");

    /**
     * type code.
     */
    private Integer code;
    /**
     * type name.
     */
    private String name;

    /**
     * constructor.
     *
     * @param code credential type code
     * @param name credential type name
     */
    CredentialType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * get type code.
     *
     * @return type code
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * get type name.
     *
     * @return type name
     */
    public String getName() {
        return this.name;
    }
}
