/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.constant;

/**
 * credential field disclosure status.
 * @author tonychen
 */
public enum CredentialFieldDisclosureValue {

    /**
     * the field is disclosed.
     */
    DISCLOSED(1),

    /**
     * the field is not disclosed.
     */
    NOT_DISCLOSED(0);

    /**
     * disclosure status.
     */
    private Integer status;

    CredentialFieldDisclosureValue(Integer status) {
        this.status = status;
    }

    /**
     * get field disclosure status.
     * @return disclosure status of the field.
     */
    public Integer getStatus() {
        return status;
    }
}
