/*
89 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.suite.transportation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.auth.impl.WeIdAuthImpl;
import com.webank.weid.suite.auth.inf.WeIdAuth;
import com.webank.weid.suite.auth.inf.WeIdAuthCallback;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;
import com.webank.weid.util.WeIdUtils;

public abstract class AbstractTransportation extends BaseService {

    private static final Logger logger =
        LoggerFactory.getLogger(AbstractTransportation.class);
    private static WeIdService weidService = new WeIdServiceImpl();
    private List<String> verifierWeIdList;
    private static long lasttime = System.currentTimeMillis();
    private static AtomicInteger atomicInt = new AtomicInteger(0);
    private static final int maxSize = 1000;
    private static WeIdAuth weIdAuthService;
    private static Persistence dataDriver;

    protected WeIdAuth getWeIdAuthService() {
        if (weIdAuthService == null) {
            weIdAuthService = new WeIdAuthImpl();
        }
        return weIdAuthService;
    }

    protected Persistence getDataDriver() {
        if (dataDriver == null) {
            dataDriver = new MysqlDriver();
        }
        return dataDriver;
    }
    
    /**
     * WeIdAuth回调拿到WeIdAuthentication.
     * 
     * @param weIdAuthentication 用户身份
     */
    protected void registerWeIdAuthentication(WeIdAuthentication weIdAuthentication) {
        this.getWeIdAuthService().registerCallBack(new WeIdAuthCallback() {
            
            private WeIdAuthentication authentication;
            {
                authentication = new WeIdAuthentication(
                    weIdAuthentication.getWeId(), 
                    weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
                );
            }
            
            @Override
            public WeIdAuthentication onChannelConnecting(String counterpartyWeId) {
                return authentication;
            }
            
            @Override
            public Integer onChannelConnected(WeIdAuthObj arg) {
                return null;
            }
        });
    }
    
    /**
     * 验证协议配置.
     *
     * @param property 协议配置实体
     * @return Error Code and Message
     */
    protected ErrorCode checkEncodeProperty(ProtocolProperty property) {
        if (property == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_PROPERTY_ERROR;
        }
        if (property.getEncodeType() == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR;
        }
        if (property.getTransType() == null) {
            return  ErrorCode.TRANSPORTATION_TRANSMISSION_TYPE_INVALID;
        }
        if (property.getUriType() == null) {
            return  ErrorCode.TRANSPORTATION_URI_TYPE_INVALID;
        }
        return ErrorCode.SUCCESS;
    }
    
    /**
     * 验证WeIdAuthentication有效性.
     *
     * @param weIdAuthentication 身份信息
     * @return Error Code and Message
     */
    protected ErrorCode checkWeIdAuthentication(WeIdAuthentication weIdAuthentication) {
        if (weIdAuthentication == null
                || weIdAuthentication.getWeIdPrivateKey() == null
                || weIdAuthentication.getWeId() == null) {
            return ErrorCode.WEID_AUTHORITY_INVALID;
        }
        if (!WeIdUtils.validatePrivateKeyWeIdMatches(
                weIdAuthentication.getWeIdPrivateKey(),
                weIdAuthentication.getWeId())) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }
        ResponseData<Boolean> isExists = weidService.isWeIdExist(weIdAuthentication.getWeId());
        if (!isExists.getResult()) {
            return ErrorCode.WEID_DOES_NOT_EXIST;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * 验证wrapper数据.
     *
     * @param obj wrapper数据,作为协议的rawData部分
     * @return Error Code and Message
     */
    protected ErrorCode checkProtocolData(Object obj) {
        if (obj == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID;
        }
        return ErrorCode.SUCCESS;
    }

    protected List<String> getVerifiers() {
        if (verifierWeIdList == null) {
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_NO_SPECIFYER_TO_SET); 
        }
        return verifierWeIdList;
    }

    protected void setVerifier(List<String> verifierWeIdList) {
        if (this.verifierWeIdList != null) {
            String errorMessage = ErrorCode.THIS_IS_REPEATED_CALL.getCode() + " - "
                    + ErrorCode.THIS_IS_REPEATED_CALL.getCodeDesc();
            logger.error("[specify] {}.", errorMessage);
            throw new WeIdBaseException(errorMessage);
        }
        if (CollectionUtils.isEmpty(verifierWeIdList)) {
            String errorMessage = ErrorCode.ILLEGAL_INPUT.getCode() + " - "
                    + ErrorCode.ILLEGAL_INPUT.getCodeDesc();
            logger.error("[specify] {}, the verifiers is null.", errorMessage);
            throw new WeIdBaseException(errorMessage);
        }
        for (String weid : verifierWeIdList) {
            ResponseData<Boolean> isExists = weidService.isWeIdExist(weid);
            if (!isExists.getResult()) {
                String errorMessage = ErrorCode.WEID_DOES_NOT_EXIST.getCode() + " - "
                        + ErrorCode.WEID_DOES_NOT_EXIST.getCodeDesc();
                logger.error("[specify] {} , weid = {} .", errorMessage, weid);
                throw new WeIdBaseException(errorMessage);
            }
        }
        this.verifierWeIdList = verifierWeIdList;
    }

    protected Method getFromJsonMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("fromJson")
                    && method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0] == String.class) {
                targetMethod = method;
            }
        }
        return targetMethod;
    }
    
    /**
     * 产生资源Id.
     * @return 返回资源Id
     */
    public static synchronized long nextId() {
        long time = System.currentTimeMillis();
        if (time == lasttime && atomicInt.get() == maxSize) {
            atomicInt = new AtomicInteger(0);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.error("[nextId] sleep error.");
            }
            lasttime = System.currentTimeMillis();
            return nextId();
        }
        return time * maxSize + atomicInt.getAndIncrement();
    }
}
