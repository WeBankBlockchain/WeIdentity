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

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.AmopCommonArgs;
import com.webank.weid.protocol.base.PolicyAndChellenge;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.MessageService;
import com.webank.weid.service.BaseService;
import com.webank.weid.suite.transportation.json.JsonTransportation;
import com.webank.weid.suite.transportation.json.JsonTransportationService;

/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public class MessageServiceImpl extends BaseService implements MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private JsonTransportation jsonTransportationService = new JsonTransportationService();

    @Override
    public ResponseData<PolicyAndChellenge> getPresentationPolicy(String orgId, Integer policyId) {
        try {
            if (StringUtils.isBlank(orgId)) {
                logger.error("the orgId is null, policyId = {}", policyId);
                return new ResponseData<PolicyAndChellenge>(null, ErrorCode.ILLEGAL_INPUT);
            }
            AmopCommonArgs args = new AmopCommonArgs();
            args.setFromOrgId(fromOrgId);
            args.setToOrgId(orgId);
            args.setMessage(String.valueOf(policyId));
            args.setMessageId(getService().newSeq());
            ResponseData<AmopResponse> retResponse = super.request(orgId, args);
            if (retResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("AMOP response fail, policyId={}, errorCode={}, errorMessage={}",
                    policyId,
                    retResponse.getErrorCode(),
                    retResponse.getErrorMessage()
                );
                return new ResponseData<PolicyAndChellenge>(
                    null, 
                    ErrorCode.getTypeByErrorCode(retResponse.getErrorCode().intValue())
                );
            }
            ResponseData<PolicyAndChellenge> policyResponse = jsonTransportationService.deserialize(
                retResponse.getResult().getResult(),
                PolicyAndChellenge.class
            );
            return policyResponse;
        } catch (Exception e) {
            logger.error("getPresentationPolicy failed due to system error. ", e);
            return new ResponseData<PolicyAndChellenge>(null, ErrorCode.UNKNOW_ERROR);
        }
    }
}
