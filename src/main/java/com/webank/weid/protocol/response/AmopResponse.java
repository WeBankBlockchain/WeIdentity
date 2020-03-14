/*
 * Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.protocol.response;

import lombok.Data;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.inf.IResult;

/**
 * the AMOP response.
 * @author tonychen 2019.04.16
 */
@Data
public class AmopResponse implements IResult {

    /**
     * 返回的消息.
     */
    private String result;

    /**
     * 业务类型.
     */
    protected String serviceType;

    /**
     * 错误码.
     */
    private Integer errorCode;

    /**
     * 错误信息.
     */
    protected String errorMessage;

    /**
     * 消息编号.
     */
    protected String messageId;

    /**
     * 无参构造器.
     */
    public AmopResponse() {
        super();
    }

    /**
     * ErrorCode造器.
     * @param errorCode 错误码
     */
    public AmopResponse(ErrorCode errorCode) {
        this();
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getCodeDesc();
    }
}
