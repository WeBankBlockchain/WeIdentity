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

package com.webank.weid.exception;

import com.webank.weid.constant.ErrorCode;

/**
 * InitWeb3sdk Exception.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class InitWeb3jException extends WeIdBaseException {

    public InitWeb3jException(Throwable cause) {
        super(ErrorCode.LOAD_WEB3J_FAILED.getCodeDesc(), cause);
    }

    public InitWeb3jException() {
        super(ErrorCode.LOAD_WEB3J_FAILED.getCodeDesc());
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.LOAD_WEB3J_FAILED;
    }
}
