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
 * CPT type enum.
 *
 * @author tonychen 2020年2月17日
 */
public enum CptType {

    /**
     * original type, used to create original type credential.
     */
    ORIGINAL(0, "original"),

    /**
     * zkp type, used to create zkp type credential.
     */
    ZKP(1, "zkp");

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
     * @param code cpt type code
     * @param name cpt type name
     */
    CptType(Integer code, String name) {
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
