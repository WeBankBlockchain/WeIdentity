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

package com.webank.weid.exception;

import com.webank.weid.constant.ErrorCode;

/**
 * WeIdBase Exception. Base Exception for WeIdentity Project.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class DataTypeCastException extends WeIdBaseException {


    /**
     * constructor.
     *
     * @param cause Throwable
     */
    public DataTypeCastException(Throwable cause) {
        super(ErrorCode.DATA_TYPE_CASE_ERROR.getCodeDesc(), cause);
    }

    /**
     * constructor.
     *
     * @param message message
     */
    public DataTypeCastException(String message) {
        super(ErrorCode.DATA_TYPE_CASE_ERROR.getCodeDesc());
    }
    
    /**
     * get associated error code.
     */
    public ErrorCode getErrorCode() {
        return ErrorCode.DATA_TYPE_CASE_ERROR;
    }
}
