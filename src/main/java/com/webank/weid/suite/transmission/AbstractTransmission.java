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

package com.webank.weid.suite.transmission;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.crypto.CryptoServiceFactory;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.suite.auth.impl.WeIdAuthImpl;
import com.webank.weid.suite.auth.inf.WeIdAuth;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;
import com.webank.weid.util.DataToolUtils;

/**
 * 传输公共处理类.
 * 
 * @author yanggang
 *
 */
public abstract class AbstractTransmission implements Transmission {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTransmission.class);

    private static WeIdAuth weIdAuthService;

    private WeIdAuth getWeIdAuthService() {
        if (weIdAuthService == null) {
            weIdAuthService = new WeIdAuthImpl();
        }
        return weIdAuthService;
    }

    /**
     * 认证处理.
     * 
     * @param <T> 请求实例中具体数据类型
     * @param request 用于做weAuth验证的用户身份信息
     * @return 返回加密后的数据对象
     */
    protected <T> TransmissionlRequestWarp<T> authTransmission(TransmissionRequest<T> request) {
        logger.info("[AbstractTransmission.auth] begin auth the transmission.");
        ResponseData<WeIdAuthObj> authResponse = getWeIdAuthService().createAuthenticatedChannel(
            request.getAmopId(), request.getWeIdAuthentication());
        if (authResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            //认证失败
            logger.error("[AbstractTransmission.auth] auth fail:{}-{}.",
                authResponse.getErrorCode(),
                authResponse.getErrorMessage());
            throw new WeIdBaseException(ErrorCode.getTypeByErrorCode(authResponse.getErrorCode()));
        }
        logger.info("[AbstractTransmission.auth] auth the transmission successfully.");
        WeIdAuthObj weIdAuth = authResponse.getResult();
        TransmissionlRequestWarp<T> reqeustWarp = new TransmissionlRequestWarp<T>(
            request, weIdAuth);
        String encodeData = encryptData(getOriginalData(request.getArgs()), weIdAuth);
        reqeustWarp.setEncodeData(encodeData);
        return reqeustWarp;
    }

    /**
     * 解密数据.
     * 
     * @param encodeData 密文数据
     * @param weIdAuth 通道协议对象
     * @return 返回明文数据
     */
    protected String decryptData(String encodeData, WeIdAuthObj weIdAuth) {
        return CryptoServiceFactory
            .getCryptoService(CryptoType.AES)
            .decrypt(encodeData, weIdAuth.getSymmetricKey());
    }

    /**
     * 加密传输数据.
     * 
     * @param originalData 原文
     * @param weIdAuth 通道协议对象
     * @return 返回加密后的数据
     */
    protected String encryptData(String originalData, WeIdAuthObj weIdAuth) {
        return CryptoServiceFactory
            .getCryptoService(CryptoType.AES)
            .encrypt(originalData, weIdAuth.getSymmetricKey());
    }

    protected <T> String getOriginalData(T args) {
        String originalData = null;
        if (args instanceof String) {
            originalData = (String)args;
        } else {
            originalData = DataToolUtils.serialize(args);
        }
        return originalData;
    }

    @Data
    protected class TransmissionlRequestWarp<T> {
        
        TransmissionRequest<T> request;
        WeIdAuthObj weIdAuthObj;
        String encodeData;
        
        TransmissionlRequestWarp(TransmissionRequest<T> request, WeIdAuthObj weIdAuthObj) {
            this.request = request;
            this.weIdAuthObj = weIdAuthObj;
        }
    }
}
