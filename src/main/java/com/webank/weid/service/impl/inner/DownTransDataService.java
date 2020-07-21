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

package com.webank.weid.service.impl.inner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.amop.GetTransDataArgs;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.crypto.CryptoServiceFactory;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.entity.TransCodeBaseData;
import com.webank.weid.suite.transmission.TransmissionService;
import com.webank.weid.util.DataToolUtils;

/**
 * 根据资源获取CodeData回调处理.
 *
 * @author yanggang
 */
public class DownTransDataService extends InnerService implements TransmissionService<String> {

    private static final Logger logger = LoggerFactory.getLogger(DownTransDataService.class);

    @Override
    public ResponseData<String> service(String message) {
        try {
            GetTransDataArgs arg = DataToolUtils.deserialize(message, GetTransDataArgs.class);
            return getCodeData(arg);
        } catch (Exception e) {
            logger.error("[onPush] get CodeData has error.", e);
            ResponseData<String> codeDataRes = new ResponseData<String>();
            codeDataRes.setResult(StringUtils.EMPTY);
            codeDataRes.setErrorCode(ErrorCode.UNKNOW_ERROR);
            return codeDataRes;
        }
    }

    private ResponseData<String> getCodeData(
        GetTransDataArgs arg
    ) throws ClassNotFoundException {
        logger.info("[getCodeData] begin query data param:{}", arg);
        ResponseData<String> codeDataRes = new ResponseData<String>();
        codeDataRes.setResult(StringUtils.EMPTY);
        ResponseData<String> responseData = this.getDataDriver().get(
            DataDriverConstant.DOMAIN_RESOURCE_INFO, arg.getResourceId());
        // 数据查询出错
        if (responseData.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[getCodeData] query data has error: {} - {}.",
                responseData.getErrorCode(),
                responseData.getErrorMessage()
            );
            codeDataRes.setErrorCode(ErrorCode.getTypeByErrorCode(responseData.getErrorCode()));
            return codeDataRes;
        }
        // 数据不存在
        if (StringUtils.isBlank(responseData.getResult())) {
            logger.error("[getCodeData] the data does not exist.");
            codeDataRes.setErrorCode(ErrorCode.SQL_DATA_DOES_NOT_EXIST);
            return codeDataRes;
        }
        // 解析数据
        TransCodeBaseData codeData = (TransCodeBaseData) DataToolUtils.deserialize(
            responseData.getResult(), Class.forName(arg.getClassName())
        );
        //得到数据编解码类型(原文&密文)
        EncodeType encodeType = EncodeType.getEncodeType(codeData.getEncodeType());
        logger.info("[getCodeData] the encode is {}", encodeType.name());
        if (encodeType == EncodeType.ORIGINAL) {
            // 原文类型
            codeDataRes.setResult(responseData.getResult());
            codeDataRes.setErrorCode(ErrorCode.SUCCESS);
            logger.info("[getCodeData] query data successfully.");
            return codeDataRes;
        } else if (encodeType == EncodeType.CIPHER) { //密文类型
            // 获取密钥
            GetEncryptKeyResponse encryptKey = getEncryptKey(arg);
            if (encryptKey.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[getCodeData] query the key has error.");
                codeDataRes.setErrorCode(
                    ErrorCode.getTypeByErrorCode(encryptKey.getErrorCode().intValue()));
                return codeDataRes;
            }
            logger.info("[getCodeData] begin decrypt the data");
            String data = String.valueOf(codeData.getData());
            String value = CryptoServiceFactory
                .getCryptoService(CryptoType.AES)
                .decrypt(data, encryptKey.getEncryptKey());
            codeData.setData(value);
            codeDataRes.setResult(DataToolUtils.serialize(codeData));
            codeDataRes.setErrorCode(ErrorCode.SUCCESS);
            logger.info("[getCodeData] query data successfully.");
            return codeDataRes;
        } else {
            // 数据找不到编解码类型
            logger.error("[getCodeData] the encode type error.");
            codeDataRes.setErrorCode(ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR);
            return codeDataRes;
        }
    }

    /**
     * 获取密钥接口.
     *
     * @param arg 请求参数
     * @return 返回密钥对象
     */
    private GetEncryptKeyResponse getEncryptKey(GetTransDataArgs arg) {
        logger.info("[getEncryptKey] begin query encrypt key param:{}", arg);
        GetEncryptKeyResponse encryptResponse = new GetEncryptKeyResponse();
        ResponseData<String> keyResponse = this.getDataDriver().get(
            DataDriverConstant.DOMAIN_ENCRYPTKEY, arg.getResourceId());
        if (keyResponse.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
            && StringUtils.isBlank(keyResponse.getResult())) {
            logger.error("[getEncryptKey] the encrypt key is not exists.");
            encryptResponse.setEncryptKey(StringUtils.EMPTY);
            encryptResponse.setErrorCode(ErrorCode.ENCRYPT_KEY_NOT_EXISTS.getCode());
            encryptResponse.setErrorMessage(ErrorCode.ENCRYPT_KEY_NOT_EXISTS.getCodeDesc());
        } else {
            encryptResponse.setEncryptKey(StringUtils.EMPTY);
            if (keyResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "[getEncryptKey] the encrypt key has error: {} - {}.",
                    keyResponse.getErrorCode(),
                    keyResponse.getErrorMessage()
                );
                encryptResponse.setErrorCode(keyResponse.getErrorCode());
                encryptResponse.setErrorMessage(keyResponse.getErrorMessage());
                return encryptResponse;
            }
            try {
                Map<String, Object> keyMap = DataToolUtils.deserialize(
                    keyResponse.getResult(),
                    new HashMap<String, Object>().getClass()
                );
                if (!checkAuthority(arg, keyMap)) { // 检查是否有权限
                    encryptResponse.setErrorCode(ErrorCode.ENCRYPT_KEY_NO_PERMISSION.getCode());
                    encryptResponse.setErrorMessage(
                        ErrorCode.ENCRYPT_KEY_NO_PERMISSION.getCodeDesc());
                } else {
                    encryptResponse.setEncryptKey((String) keyMap.get(ParamKeyConstant.KEY_DATA));
                    encryptResponse.setErrorCode(ErrorCode.SUCCESS.getCode());
                    encryptResponse.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
                }
            } catch (DataTypeCastException e) {
                logger.error("[getEncryptKey]  deserialize the data error.", e);
                encryptResponse.setErrorCode(ErrorCode.ENCRYPT_KEY_INVALID.getCode());
                encryptResponse.setErrorMessage(ErrorCode.ENCRYPT_KEY_INVALID.getCodeDesc());
            }
        }
        return encryptResponse;
    }

    /**
     * 检查是否有权限获取秘钥数据.
     *
     * @param arg 请求秘钥对应的参数
     * @param keyMap 查询出来的key数据
     */
    private boolean checkAuthority(GetTransDataArgs arg, Map<String, Object> keyMap) {
        if (keyMap == null) {
            logger.error("[checkAuthority] illegal input.");
            return false;
        }
        List<String> verifiers = (ArrayList<String>) keyMap.get(ParamKeyConstant.KEY_VERIFIERS);
        // 如果verifiers为empty,或者传入的weId为空，或者weId不在指定列表中，则无权限获取秘钥数据
        if (CollectionUtils.isEmpty(verifiers)
            || StringUtils.isBlank(arg.getWeId())
            || !verifiers.contains(arg.getWeId())) {
            logger.error(
                "[checkAuthority] no access to get the data, this weid is {}.",
                arg.getWeId()
            );
            return false;
        }
        // 验证signValue
        ResponseData<WeIdDocument> domRes = this.getWeIdService().getWeIdDocument(arg.getWeId());
        if (domRes.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[checkAuthority] can not get the WeIdDocument, this weid is {}.",
                arg.getWeId()
            );
            return false;
        }
        ErrorCode errorCode = DataToolUtils.verifySecp256k1SignatureFromWeId(
            arg.getResourceId(),
            arg.getSignValue(),
            domRes.getResult(),
            null
        );
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            errorCode = DataToolUtils.verifySignatureFromWeId(
                arg.getResourceId(),
                arg.getSignValue(),
                domRes.getResult(),
                null
            );
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "[checkAuthority] the data is be changed, this weid is {}.",
                    arg.getWeId()
                );
                return false;
            }
        }
        logger.info("[checkAuthority] you have the permission to get key.");
        return true;
    }
}
