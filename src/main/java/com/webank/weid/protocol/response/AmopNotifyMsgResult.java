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

import com.webank.weid.protocol.inf.IResult;
import lombok.Data;

/**
 * Created by junqizhang on 15/08/2017.
 */
@Data
public class AmopNotifyMsgResult implements IResult {

    /*
     * 错误信息
     */
    protected String message;
    /*
     * 错误码：返回0表明成功收到通知；其他表明异常情况.
     */
    private Integer errorCode;
}
