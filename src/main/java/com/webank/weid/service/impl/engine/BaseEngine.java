package com.webank.weid.service.impl.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.LoadContractException;
import com.webank.weid.service.BaseService;

public abstract class BaseEngine extends BaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseEngine.class);   
    
    private static <T> T loadContract(
        String contractAddress,
        Object credentials,
        Class<T> cls) throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException {
        Object contract;
        Method method = cls.getMethod(
            "load",
            String.class,
            getWeb3jClass(),
            credentials.getClass(),
            BigInteger.class,
            BigInteger.class
        );

        contract = method.invoke(
            null,
            contractAddress,
            getWeb3j(),
            credentials,
            WeIdConstant.GAS_PRICE,
            WeIdConstant.GAS_LIMIT
        );
        return (T) contract;
    }

    /**
     * Reload contract.
     *
     * @param contractAddress the contract address
     * @param privateKey the privateKey of the sender
     * @param cls the class
     * @return the contract
     */
    protected static <T> T reloadContract(
        String contractAddress,
        String privateKey,
        Class<T> cls) {
        
        if (weServer == null) {
            init();
        }

        T contract = null;
        try {
            // load contract
            contract = loadContract(contractAddress, weServer.createCredentials(privateKey), cls);
            logger.info(cls.getSimpleName() + " init succ");
        } catch (Exception e) {
            logger.error("load contract :{} failed. Error message is :{}",
                cls.getSimpleName(), e);
            throw new LoadContractException();
        }

        if (contract == null) {
            throw new LoadContractException();
        }
        return contract;
    }

    /**
     * Gets the contract service.
     *
     * @param contractAddress the contract address
     * @param cls the class
     * @return the contract service
     */
    protected static <T> T getContractService(String contractAddress, Class<T> cls) {

        T contract = null;
        try {
            // load contract
            if (weServer == null) {
                init();
            }
            contract = loadContract(contractAddress, weServer.getCredentials(), cls);
            logger.info(cls.getSimpleName() + " init succ");

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            logger.error("load contract :{} failed. Error message is :{}",
                cls.getSimpleName(), e);
            throw new LoadContractException();
        } catch (Exception e) {
            logger.error("load contract Exception:{} failed. Error message is :{}",
                cls.getSimpleName(), e);
            throw new LoadContractException();
        }

        if (contract == null) {
            throw new LoadContractException();
        }
        return  contract;
    }
}
