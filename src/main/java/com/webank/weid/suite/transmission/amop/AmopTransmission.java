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

package com.webank.weid.suite.transmission.amop;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.suite.transmission.AbstractTransmission;
import com.webank.weid.suite.transmission.Transmission;
import com.webank.weid.suite.transmission.TransmissionlRequest;

/**
 * AMOP传输处理器.
 * 
 * @author yanggang
 *
 */
public class AmopTransmission extends AbstractTransmission implements Transmission {
    
    private static final Logger logger = LoggerFactory.getLogger(AmopTransmission.class);
    
    private static AmopTransmissionPoxy amopTransmissionPoxy;
    
    private void initAmopChannelPoxy() {
        if (amopTransmissionPoxy == null) {
            amopTransmissionPoxy = new AmopTransmissionPoxy();
        }
    }
    
    @Override
    public ResponseData<String> send(TransmissionlRequest<?> request) {
        logger.info(
            "[AmopTransmission.send] this is amop transmission and the service type is: {}", 
            request.getServiceType());
        try {
            initAmopChannelPoxy();
            //如果请求机构和目标机构为同机构，则走本地模式，不加密
            if (amopTransmissionPoxy.getCurrentOrgId().equals(request.getOrgId())) {
                return sendLocal(request);
            } else {
                return sendAmop(request);
            } 
        } catch (WeIdBaseException e) {
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
        }
    }
    
    private ResponseData<String> sendLocal(TransmissionlRequest<?> request) {
        logger.info("[AmopTransmission.sendLocal] request to local.");
        AmopResponse response = amopTransmissionPoxy.sendLocal(buildAmopCommonArgs(request));
        logger.info("[AmopTransmission.sendLocal] the amop response: {}", response);
        return new ResponseData<String>(response.getResult(), 
            response.getErrorCode(), response.getErrorMessage());
    }
    
    private ResponseData<String> sendAmop(TransmissionlRequest<?> request) {
        logger.info("[AmopTransmission.sendAmop] request by AMOP.");
        super.auth(request);
        ResponseData<AmopResponse> response = amopTransmissionPoxy.send(
            buildAmopCommonArgs(request));
        logger.info("[AmopTransmission.sendAmop] the amop response: {}", response);
        return processResult(response);
    }
    
    private ResponseData<String> processResult(ResponseData<AmopResponse> response) {
        if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<String>(StringUtils.EMPTY, 
                response.getErrorCode(), response.getErrorMessage());
        }
        AmopResponse amopResponse = response.getResult();
        return new ResponseData<String>(amopResponse.getResult(), 
            amopResponse.getErrorCode(), amopResponse.getErrorMessage());
    }
    
    private AmopCommonArgs buildAmopCommonArgs(TransmissionlRequest<?> request) {
        AmopCommonArgs args = new AmopCommonArgs();
        args.setServiceType(request.getServiceType());
        args.setFromOrgId(amopTransmissionPoxy.getCurrentOrgId());
        args.setMessage(super.getData(request));
        return args;
    }
}
