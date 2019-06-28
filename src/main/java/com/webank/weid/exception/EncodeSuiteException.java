/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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
 * 编解码处理异常.
 * @author v_wbgyang
 *
 */
public class EncodeSuiteException extends WeIdBaseException {

    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode = ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR;
    
    public EncodeSuiteException(Throwable e) {
        super(ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCodeDesc(), e);
    }
    
    public EncodeSuiteException() {
        super(ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCodeDesc()); 
    }
    
    public EncodeSuiteException(ErrorCode errorCode) {
        super(errorCode.getCodeDesc());
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
