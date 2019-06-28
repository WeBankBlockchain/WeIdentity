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

package com.webank.weid.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AmopService;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;


/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public class AmopServiceImpl extends BaseService implements AmopService {

    private static final Logger logger = LoggerFactory.getLogger(AmopServiceImpl.class);

    @Override
    public ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
        String targetOrgId,
        Integer policyId,
        String targetUserWeId
    ) {
        try {
            if (StringUtils.isBlank(fiscoConfig.getCurrentOrgId())) {
                logger.error("the orgId is null, policyId = {}", policyId);
                return new ResponseData<PolicyAndChallenge>(null, ErrorCode.ILLEGAL_INPUT);
            }
            GetPolicyAndChallengeArgs args = new GetPolicyAndChallengeArgs();
            args.setFromOrgId(fiscoConfig.getCurrentOrgId());
            args.setToOrgId(targetOrgId);
            args.setPolicyId(String.valueOf(policyId));
            args.setMessageId(super.getSeq());
            args.setTargetUserWeId(targetUserWeId);
            ResponseData<GetPolicyAndChallengeResponse> retResponse =
                this.getPolicyAndChallenge(targetOrgId, args);
            if (retResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("AMOP response fail, policyId={}, errorCode={}, errorMessage={}",
                    policyId,
                    retResponse.getErrorCode(),
                    retResponse.getErrorMessage()
                );
                return new ResponseData<PolicyAndChallenge>(
                    null,
                    ErrorCode.getTypeByErrorCode(retResponse.getErrorCode().intValue())
                );
            }
            GetPolicyAndChallengeResponse result = retResponse.getResult();
            ErrorCode errorCode =
                ErrorCode.getTypeByErrorCode(result.getErrorCode().intValue());
            return new ResponseData<PolicyAndChallenge>(result.getPolicyAndChallenge(), errorCode);
        } catch (Exception e) {
            logger.error("getPresentationPolicy failed due to system error. ", e);
            return new ResponseData<PolicyAndChallenge>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ResponseData<GetPolicyAndChallengeResponse> getPolicyAndChallenge(
        String toOrgId,
        GetPolicyAndChallengeArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetPolicyAndChallengeArgs.class,
            GetPolicyAndChallengeResponse.class,
            AmopMsgType.GET_POLICY_AND_CHALLENGE,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 发送普通消息的AMOP请求接口.
     */
    public ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            AmopCommonArgs.class,
            AmopResponse.class,
            AmopMsgType.TYPE_TRANSPORTATION,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     *  通过AMOP获取秘钥请求接口.
     */
    public ResponseData<GetEncryptKeyResponse> getEncryptKey(String toOrgId,
        GetEncryptKeyArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetEncryptKeyArgs.class,
            GetEncryptKeyResponse.class,
            AmopMsgType.GET_ENCRYPT_KEY,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 注册回调函数接口.
     */
    public void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback) {
        super.getPushCallback().registAmopCallback(directRouteMsgType, directRouteCallback);
    }
}
