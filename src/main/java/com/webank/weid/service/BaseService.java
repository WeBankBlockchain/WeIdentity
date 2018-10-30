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

package com.webank.weid.service;

import com.webank.weid.constant.WeIdConstant;
import java.lang.reflect.Method;
import java.math.BigInteger;
import org.bcos.channel.client.Service;
import org.bcos.contract.tools.ToolConf;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.GenCredential;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.bcos.web3j.tx.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The BaseService for other RPC classes.
 *
 * @author tonychen
 */
public abstract class BaseService {

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected static ApplicationContext context;

    protected static Credentials credentials;

    private static Web3j web3j;

    static {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    /**
     * Load config.
     *
     * @return true, if successful
     */
    static boolean loadConfig() {

        return (initWeb3j() && initCredentials());
    }

    private static boolean initWeb3j() {
        Service service = context.getBean(Service.class);
        try {
            service.run();
        } catch (Exception e) {
            logger.error("[BaseService] Service init failed. ", e);
        }
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        web3j = Web3j.build(channelEthereumService);
        if (null == web3j) {
            logger.error("[BaseService] web3j init failed. ");
            return false;
        }
        return true;
    }

    /**
     * Inits the credentials.
     *
     * @return true, if successful
     */
    private static boolean initCredentials() {
        ToolConf toolConf = context.getBean(ToolConf.class);

        logger.info("begin init credentials");
        credentials = GenCredential.create(toolConf.getPrivKey());

        if (null == credentials) {
            logger.error("[BaseService] credentials init failed. ");
            return false;
        }
        return true;
    }

    /**
     * Gets the web3j.
     *
     * @return the web3j
     */
    protected static Web3j getWeb3j() {
        if (null == web3j) {
            initWeb3j();
        }
        return web3j;
    }

    /**
     * Reload contract.
     *
     * @param contractAddress the contract address
     * @param credentials the credentials
     * @param cls the class
     * @return the contract
     */
    protected static Contract reloadContract(
        String contractAddress, Credentials credentials, Class<?> cls) {

        Object contract = null;
        if (null == web3j) {
            initWeb3j();
        }
        try {
            // load contract
            Method method =
                cls.getMethod(
                    "load",
                    String.class,
                    Web3j.class,
                    Credentials.class,
                    BigInteger.class,
                    BigInteger.class);

            contract =
                method.invoke(
                    null,
                    contractAddress,
                    web3j,
                    credentials,
                    WeIdConstant.GAS_PRICE,
                    WeIdConstant.GAS_LIMIT);

            logger.info(cls.getSimpleName() + " init succ");

        } catch (Exception e) {
            logger.error("load contract :{} failed. Error message is :{}", cls.getSimpleName(), e);
            throw new RuntimeException("load contract failed." + e.getMessage());
        }

        if (contract == null) {
            throw new RuntimeException(cls.getSimpleName() + " init fail");
        }
        return (Contract) contract;
    }

    /**
     * Gets the contract service.
     *
     * @param contractAddress the contract address
     * @param cls the class
     * @return the contract service
     */
    protected static Contract getContractService(String contractAddress, Class<?> cls) {

        Object contract = null;
        if (null == web3j || null == credentials) {
            loadConfig();
        }
        try {
            // load contract
            Method method =
                cls.getMethod(
                    "load",
                    String.class,
                    Web3j.class,
                    Credentials.class,
                    BigInteger.class,
                    BigInteger.class);

            contract = method.invoke(
                null,
                contractAddress,
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT
            );

            logger.info(cls.getSimpleName() + " init succ");

        } catch (Exception e) {
            logger.error("load contract :{} failed. Error message is :{}", cls.getSimpleName(), e);
            throw new RuntimeException("load contract failed." + e.getMessage());
        }

        if (contract == null) {
            throw new RuntimeException(cls.getSimpleName() + " init fail");
        }
        return (Contract) contract;
    }
}
