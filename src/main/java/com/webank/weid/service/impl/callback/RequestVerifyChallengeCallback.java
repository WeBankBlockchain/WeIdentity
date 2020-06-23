/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.service.impl.callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.RequestVerifyChallengeArgs;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.RequestVerifyChallengeResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.suite.auth.impl.WeIdAuthImpl;
import com.webank.weid.suite.auth.inf.WeIdAuth;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;
import com.webank.weid.util.DataToolUtils;


/**
 * amop callback for verifying challenge.
 *
 * @author tonychen 2020年3月10日
 */
public class RequestVerifyChallengeCallback extends AmopCallback {

    private static final Logger logger = LoggerFactory
        .getLogger(RequestVerifyChallengeCallback.class);

    private WeIdService weIdService = new WeIdServiceImpl();

    private WeIdAuth weIdAuthService = new WeIdAuthImpl();

    /**
     * 默认获取weIdAuthObj回调.
     *
     * @param args 获取weIdAuthObj需要的参数
     * @return 返回weIdAuthObj的响应体
     */
    public RequestVerifyChallengeResponse onPush(RequestVerifyChallengeArgs args) {

        RequestVerifyChallengeResponse result = new RequestVerifyChallengeResponse();
        String signData = args.getSignData();
        String weId = args.getSelfWeId();
        String channelId = args.getChannelId();
        Challenge challenge = args.getChallenge();
        WeIdAuthObj weidAuth = weIdAuthService.getWeIdAuthObjByChannelId(channelId);
        if (!StringUtils.equals(weidAuth.getCounterpartyWeId(), weId)) {

            logger.error("[RequestVerifyChallengeCallback] the weId :{} has no permission.", weId);
            result.setErrorCode(ErrorCode.WEID_AUTH_NO_PERMISSION.getCode());
            result.setErrorMessage(ErrorCode.WEID_AUTH_NO_PERMISSION.getCodeDesc());
            return result;
        }
        String rawData = challenge.toJson();
        ResponseData<WeIdDocument> weIdDocResp = weIdService.getWeIdDocument(weId);
        ErrorCode errorCode = DataToolUtils
            .verifySecp256k1SignatureFromWeId(rawData, signData, weIdDocResp.getResult(), null);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            errorCode = DataToolUtils
                .verifySignatureFromWeId(rawData, signData, weIdDocResp.getResult(), null);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[RequestVerifyChallengeCallback] verify challenge signature failed.");
                result.setErrorCode(errorCode.getCode());
                result.setErrorMessage(errorCode.getCodeDesc());
                return result;
            }
        }

        result.setErrorCode(ErrorCode.SUCCESS.getCode());
        result.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
        return result;
    }
}
