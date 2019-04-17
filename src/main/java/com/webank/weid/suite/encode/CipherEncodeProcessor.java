/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.suite.encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.connectivity.driver.DataDriver;
import com.webank.weid.connectivity.driver.MysqlDriver;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.protocol.amop.AmopCommonArgs;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;
import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.crypto.KeyGenerator;
import com.webank.weid.suite.entity.CryptType;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.util.UuIdUtils;

/**
 * 密文编解码处理器.
 * 
 * @author v_wbgyang
 *
 */
public class CipherEncodeProcessor extends BaseService implements EncodeProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(CipherEncodeProcessor.class);
    
    private DataDriver dataDriver = new MysqlDriver();
    
    /**
     * 密文编码处理：先进行压缩，然后进行AES加密.
     */
    @Override
    public String encode(EncodeData encodeData) throws EncodeSuiteException {
        logger.info("cipher encode process, encryption with AES.");
        try {
            String key = KeyGenerator.getKey();
            //将数据进行AES加密处理
            String value = 
                CryptServiceFactory
                    .getCryptService(CryptType.AES)
                    .encrypt(encodeData.getData(), key);
            //保存秘钥
            ResponseData<Integer> response = dataDriver.save(encodeData.getId(), key);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                throw new EncodeSuiteException(
                    ErrorCode.getTypeByErrorCode(response.getErrorCode().intValue())
                );
            }
            logger.info("cipher encode process finished.");
            return value;
        } catch (EncodeSuiteException e) {
            logger.error("encode processor has some error.", e);
            throw e;
        } catch (Exception e) {
            logger.error("encode processor has unknow error.", e);
            throw new EncodeSuiteException(e);
        }  
    }

    /**
     * 密文解码处理：先进行AES解密， 然后进行解压.
     */
    @Override
    public String decode(EncodeData encodeData) throws EncodeSuiteException {
        logger.info("cipher decode process, decryption with AES.");
        try {
            //获取秘钥，
            AmopCommonArgs args = new AmopCommonArgs();
            args.setMessage(encodeData.getId());
            args.setMessageId(UuIdUtils.getUuId32());
            args.setToOrgId(encodeData.getOrgId());
            args.setFromOrgId(fromOrgId);
            ResponseData<AmopResponse> retResponse = super.request(encodeData.getOrgId(), args);
            if (retResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("AMOP response fail, dataId={}, errorCode={}, errorMessage={}",
                    encodeData.getId(),
                    retResponse.getErrorCode(),
                    retResponse.getErrorMessage()
                );
                throw new EncodeSuiteException();
            }
            String key = retResponse.getResult().getResult();
            //将数据进行AES解密
            String value = 
                CryptServiceFactory
                    .getCryptService(CryptType.AES)
                    .decrypt(encodeData.getData(), key);
            //数据进行解压
            logger.info("cipher decode process finished.");
            return value;
        } catch (EncodeSuiteException e) {
            logger.error("encode processor has some error.", e);
            throw e;
        } catch (Exception e) {
            logger.error("decode processor has unknow error.", e);
            throw new EncodeSuiteException(e);
        }  
    }
}
