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

package com.webank.weid.suite.entity;

public enum QrCodeVersion {
    
    V1(1);
    
    private int code;
    
    QrCodeVersion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
    public String toString() {
        return String.valueOf(this.code);
    }
    
    /**
     * get MetaVersion by code.
     * @param value code value
     * @return QrCodeVersion
     */
    public static QrCodeVersion getObject(String value) {
        for (QrCodeVersion version : QrCodeVersion.values()) {
            if (String.valueOf(version.getCode()).equals(value)) {
                return version;
            }
        }
        return null;
    }
}
