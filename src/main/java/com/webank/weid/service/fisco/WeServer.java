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

package com.webank.weid.service.fisco;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.rpc.callback.RegistCallBack;
import com.webank.weid.service.fisco.v1.WeServerV1;
import com.webank.weid.service.fisco.v2.WeServerV2;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.KeyManagerCallback;
import com.webank.weid.util.PropertyUtils;

public abstract class WeServer<W, C, S> {

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
     * WeServer对象.
     */
    private static WeServer<?, ?, ?> weServer;

    /**
     * FISCO配置对象.
     */
    protected FiscoConfig fiscoConfig;

    /**
     * AMOP回调处理注册器.
     */
    protected RegistCallBack pushCallBack;

    /**
     * 构造WeServer对象,此时仅为初始化做准备.
     *
     * @param fiscoConfig FISCO配置对象
     * @param pushCallBack 默认的AMOP回调处理类对象
     */
    protected WeServer(FiscoConfig fiscoConfig, RegistCallBack pushCallBack) {
        this.fiscoConfig = fiscoConfig;
        this.pushCallBack = pushCallBack;
        registDefaultCallback();
    }

    /**
     * 初始化WeServer服务,进行多线程安全保护,确保整个应用只初始化一次 并且根据配置FISCO的版本来自动初始化对应版本的服务.
     *
     * @param fiscoConfig FISCO配置对象
     * @return 返回WeServer对象
     */
    public static synchronized <W, C, S> WeServer<W, C, S> init(FiscoConfig fiscoConfig) {
        if (weServer == null) {
            synchronized (WeServer.class) {
                if (weServer == null) {
                    if (fiscoConfig.getVersion()
                        .startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
                        weServer = new WeServerV1(fiscoConfig);
                    } else {
                        weServer = new WeServerV2(fiscoConfig);
                    }
                    weServer.initWeb3j();
                }
            }
        }
        return (WeServer<W, C, S>) weServer;
    }

    /**
     * 注册默认的callback.
     */
    private void registDefaultCallback() {
        pushCallBack.registAmopCallback(
            AmopMsgType.GET_ENCRYPT_KEY.getValue(),
            new KeyManagerCallback()
        );
    }

    /**
     * 获取PushCallback对象，用于给使用者注册callback处理器.
     *
     * @return 返回RegistCallBack
     */
    public RegistCallBack getPushCallback() {
        return pushCallBack;
    }

    /**
     * 初始化Web3sdk线程池信息.
     *
     * @return 返回线程池对象
     */
    protected ThreadPoolTaskExecutor initializePool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setBeanName("web3sdk");
        pool.setCorePoolSize(Integer.valueOf(fiscoConfig.getWeb3sdkCorePoolSize()));
        pool.setMaxPoolSize(Integer.valueOf(fiscoConfig.getWeb3sdkMaxPoolSize()));
        pool.setQueueCapacity(Integer.valueOf(fiscoConfig.getWeb3sdkQueueSize()));
        pool.setKeepAliveSeconds(Integer.valueOf(fiscoConfig.getWeb3sdkKeepAliveSeconds()));
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
        pool.initialize();
        return pool;
    }

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
     * 获取Web3j对象.
     *
     * @return 返回Web3j对象
     */
    public abstract W getWeb3j();

    /**
     * 获取Web3j对象所属的类型,此处是为了给动态加载合约使用.
     */
    public abstract Class<?> getWeb3jClass();

    /**
     * 获取Service对象.
     *
     * @return 返回Service对象
     */
    public abstract S getService();

    /**
     * 获取Credentials对象.
     *
     * @return 返回Credentials对象
     */
    public abstract C getCredentials();

    /**
     * 根据传入的私钥(10进制数字私钥)，进行动态创建Credentials对象.
     *
     * @return 返回Credentials对象
     */
    public abstract C createCredentials(String privateKey);

    /**
     * 初始化Web3j.
     */
    protected abstract void initWeb3j();

    /**
     * 发送AMOP消息.
     *
     * @param amopCommonArgs AMOP请求体
     * @param timeOut AMOP请求超时时间
     * @return 返回AMOP响应体.
     */
    public abstract AmopResponse sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut);

    /**
     * 获取当前块高.
     *
     * @return 返回块高
     * @throws IOException 可能出现的异常.
     */
    public abstract int getBlockNumber() throws IOException;
}
