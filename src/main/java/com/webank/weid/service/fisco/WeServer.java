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

public abstract class WeServer<W,C,S> {
    
    /*
     * Maximum Timeout period in milliseconds.
     */
    public static final int MAX_AMOP_REQUEST_TIMEOUT = 50000;
    public static final int AMOP_REQUEST_TIMEOUT = Integer
        .valueOf(PropertyUtils.getProperty("amop.request.timeout", "5000"));

    private static final Logger logger = LoggerFactory.getLogger(WeServer.class);
    
    protected FiscoConfig fiscoConfig;
    
    protected  RegistCallBack pushCallBack;
    
    protected WeServer(FiscoConfig fiscoConfig, RegistCallBack pushCallBack){
        this.fiscoConfig = fiscoConfig;
        this.pushCallBack = pushCallBack;
        registDefaultCallback();
    }
    
    private void registDefaultCallback() {
        pushCallBack.registAmopCallback(
            AmopMsgType.GET_ENCRYPT_KEY.getValue(),
            new KeyManagerCallback()
        );
    }
    
    public RegistCallBack getPushCallback(){
        return pushCallBack;
    }
    
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
    
    protected int getTimeOut(int timeOut) {
        if (timeOut > MAX_AMOP_REQUEST_TIMEOUT || timeOut < 0) {
            logger.error("invalid timeOut : {}", timeOut);
            return MAX_AMOP_REQUEST_TIMEOUT;
        } else {
           return timeOut;
        }
    }
    
    public static <W,C,S> WeServer<W,C,S> init(FiscoConfig fiscoConfig) {
        WeServer<?,?,?> weService = null;
        if (fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            weService = new WeServerV1(fiscoConfig);
        } else {
            weService = new WeServerV2(fiscoConfig);
        }
        weService.initWeb3j();
        return (WeServer<W,C,S>)weService;
    }
    
    public abstract W getWeb3j();
    
    public abstract Class<?> getWeb3jClass();
    
    public abstract S getService();
    
    public abstract C getCredentials();
    
    public abstract Object createCredentials(String privateKey);

    protected abstract void initWeb3j();
    
    public abstract AmopResponse sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut);
    
    public abstract int getBlockNumber() throws IOException;
}
