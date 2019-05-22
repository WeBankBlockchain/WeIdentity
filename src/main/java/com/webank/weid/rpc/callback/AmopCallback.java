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

package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.service.impl.base.AmopCommonArgs;

/**
 * Created by junqizhang on 08/07/2017. 
 * 业务方需要继承DirectRouteCallback，并实现需要实现的方法.
 */
public class AmopCallback implements PushNotifyAllCallback {

    private static final String MSG_HEALTH = "I am alive!";
    private static final String ERROR_MSG_NO_OVERRIDE =
        "server side have not handle this type of message!";

    @Override
    public AmopNotifyMsgResult onPush(CheckAmopMsgHealthArgs arg) {

        AmopNotifyMsgResult result = new AmopNotifyMsgResult();
        result.setMessage(MSG_HEALTH);
        result.setErrorCode(ErrorCode.SUCCESS.getCode());
        result.setMessage(ErrorCode.SUCCESS.getCodeDesc());
        return result;
    }

    /**
     * 默认针对TYPE_TRANSPORTATION消息的回调处理.
     * @param arg AMOP请求参数
     * @return AMOP相应体
     */
    public AmopResponse onPush(AmopCommonArgs arg) {

        AmopResponse result = new AmopResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取秘钥的回调处理.
     * @param arg 获取秘钥需要的参数
     * @return 返回秘钥的响应体
     */
    public GetEncryptKeyResponse onPush(GetEncryptKeyArgs arg) {

        GetEncryptKeyResponse result = new GetEncryptKeyResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取PolicyAndChallenge的回调处理.
     * @param arg 获取PolicyAndChallenge需要的参数
     * @return 返回PolicyAndChallenge的响应体
     */
    public GetPolicyAndChallengeResponse onPush(GetPolicyAndChallengeArgs arg) {

        GetPolicyAndChallengeResponse result = new GetPolicyAndChallengeResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }
}
