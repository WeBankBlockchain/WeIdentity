/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.service.fisco;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo;

import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.CnsType;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.rpc.callback.RegistCallBack;
import com.webank.weid.service.fisco.v2.WeServerV2;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.CommonCallback;
import com.webank.weid.service.impl.callback.KeyManagerCallback;
import com.webank.weid.util.PropertyUtils;

public abstract class WeServer<C> {

    /*
     * Maximum Timeout period in milliseconds.
     */
    public static final int MAX_AMOP_REQUEST_TIMEOUT = 50000;
    public static final int AMOP_REQUEST_TIMEOUT = Integer
        .valueOf(PropertyUtils.getProperty("amop.request.timeout", "5000"));

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeServer.class);

    /**
     * WeServer对象上下文.
     */
    private static ConcurrentHashMap<Integer, WeServer<?>>  weServerContext =
        new ConcurrentHashMap<>();

    /**
     * bucket地址映射Map.
     */
    private static ConcurrentHashMap<String, CnsInfo> bucketAddressMap = 
        new ConcurrentHashMap<>();
    
    /**
     * FISCO配置对象.
     */
    protected FiscoConfig fiscoConfig;

    /**
     * 获取Client对象所属的类型,此处是为了给动态加载合约使用.
     *
     * @return Client的Class
     */
    public abstract Class<?> getClientClass();

    /**
     * AMOP回调处理注册器.
     */
    //protected RegistCallBack pushCallBack;

    /**
     * 构造WeServer对象,此时仅为初始化做准备.
     *
     * @param fiscoConfig FISCO配置对象
     */
    protected WeServer(FiscoConfig fiscoConfig) {
        this.fiscoConfig = fiscoConfig;
        setDefaultCallback();
    }

    /**
     * 初始化WeServer服务,进行多线程安全保护,确保整个应用只初始化一次 并且根据配置FISCO的版本来自动初始化对应版本的服务.
     *
     * @param fiscoConfig FISCO配置对象
     * @param groupId 群组ID
     * @param <C> Client对象
     * @return 返回WeServer对象
     */
    public static synchronized <C> WeServer<C> getInstance(
        FiscoConfig fiscoConfig, 
        Integer groupId
    ) {
        WeServer<?> weServer = weServerContext.get(groupId);
        if (weServer == null) {
            synchronized (WeServer.class) {
                weServer = weServerContext.get(groupId);
                if (weServer == null) {
                    weServer = new WeServerV2(fiscoConfig);
                    weServer.initClient(groupId);
                    weServerContext.put(groupId, weServer);
                }
            }
        }
        return (WeServer<C>)weServer;
    }

    /**
     * 注册默认的callback.
     */
    /*private void registDefaultCallback() {
        pushCallBack.registAmopCallback(
            AmopMsgType.GET_ENCRYPT_KEY.getValue(),
            new KeyManagerCallback()
        );
        pushCallBack.registAmopCallback(
            AmopMsgType.COMMON_REQUEST.getValue(),
            new CommonCallback()
        );
    }*/

    protected abstract void setDefaultCallback();

    /**
     * 获取PushCallback对象，用于给使用者注册callback处理器.
     *
     * @return 返回RegistCallBack
     */
    /*public RegistCallBack getPushCallback() {
        return pushCallBack;
    }*/

    /**
     * 获取超时时间，如果超时时间非法，则返回默认的超时时间.
     *
     * @param timeOut 调用对应AMOP请求接口的超时时间,毫秒单位.
     * @return 返回正确有效的超时时间
     */
    protected int getTimeOut(int timeOut) {
        if (timeOut > MAX_AMOP_REQUEST_TIMEOUT || timeOut < 0) {
            logger.error("invalid timeOut : {}", timeOut);
            return MAX_AMOP_REQUEST_TIMEOUT;
        } else {
            return timeOut;
        }
    }

    /**
     * 获取AMOP监听的topic.
     *
     * @return 返回topic集合，目前sdk只支持单topic监听
     */
    public abstract Set<String> getTopic();

    public abstract BcosSDK getSDK();

    /**
     * 根据传入的私钥(16进制数字私钥)，进行动态创建Credentials对象.
     *
     * @param privateKey 数字私钥
     * @return 返回Credentials对象
     */
    public abstract CryptoKeyPair createCredentials(String privateKey);

    /**
     * 获取Credentials对象.
     *
     * @return 返回Credentials对象
     */
    public abstract CryptoKeyPair getCredentials();

    /**
     * 获取Client对象.
     *
     * @return 返回Client对象
     */
    public abstract C getClient();

    /**
     * 初始化Fisco client.
     *
     * @param groupId 群组Id
     */
    protected abstract void initClient(Integer groupId);

    /**
     * 发送AMOP消息.
     *
     * @param amopCommonArgs AMOP请求体
     * @param timeOut AMOP请求超时时间
     * @return 返回AMOP响应体.
     */
    public abstract void sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut, AmopResponseCallback cb);

    /**
     * 获取当前块高.
     *
     * @return 返回块高
     * @throws IOException 可能出现的异常.
     */
    public abstract int getBlockNumber() throws IOException;

    /**
     * 获取FISCO-BCOS版本.
     * 
     * @return 返回版本信息
     * @throws IOException 可能出现的异常.
     */
    public abstract String getVersion() throws IOException;

    /**
     * 查询bucketAddress.
     * 
     * @param cnsType cns类型枚举
     * @return 返回CnsInfo
     * @throws WeIdBaseException 查询合约地址异常
     */
    protected abstract CnsInfo queryCnsInfo(CnsType cnsType) throws WeIdBaseException;

    /**
     * 获取Bucket地址.
     * 
     * @param cnsType cns类型枚举
     * @return 返回bucket地址
     */
    public CnsInfo getBucketByCns(CnsType cnsType) {
        CnsInfo cnsInfo = bucketAddressMap.get(cnsType.toString());
        if (cnsInfo == null) {
            cnsInfo = this.queryCnsInfo(cnsType);
            if (cnsInfo != null) {
                bucketAddressMap.put(cnsType.toString(), cnsInfo);
            }
        }
        return cnsInfo;
    }
}
