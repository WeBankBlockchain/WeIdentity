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

package com.webank.weid.protocol.response;

import lombok.Data;

import com.webank.weid.constant.ErrorCode;

/**
 * The internal base response result class.
 *
 * @param <T> the generic type
 * @author tonychen 2018.9.29
 */
@Data
public class ResponseData<T> {

    private T result;
    private Integer errorCode = ErrorCode.SUCCESS.getCode();
    private String errorMessage = ErrorCode.SUCCESS.getCodeDesc();

    /**
     * Instantiates a new response data.
     */
    public ResponseData() {
    }

    /**
     * Instantiates a new response data.
     *
     * @param result the result
     * @param errorCode the return code
     */
    public ResponseData(T result, ErrorCode errorCode) {
        this.result = result;
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getCodeDesc();
    }

    /**
     * Instantiates a new response data.
     *
     * @param result the result
     * @param errorCode the return code
     * @param errorMessage the return message
     */
    public ResponseData(T result, Integer errorCode, String errorMessage) {
        this.result = result;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
