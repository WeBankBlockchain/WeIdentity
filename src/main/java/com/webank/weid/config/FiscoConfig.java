package com.webank.weid.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.util.PropertyUtils;

/**
 * FISCO-BCOS Config that loaded by java process, not Spring applicationContext anymore.
 *
 * @author chaoxinhu
 * @since 2019.6
 */
@Data
public class FiscoConfig {

    private static final Logger logger = LoggerFactory.getLogger(FiscoConfig.class);

    // Note that all keys are appended with a colon ":" to support regex auto-loading

    private String version;
    private String nodes;
    private String weIdAddress;
    private String cptAddress;
    private String issuerAddress;
    private String evidenceAddress;
    private String specificIssuerAddress;
    private String chainId;
    private String web3sdkTimeout;
    private String web3sdkCorePoolSize;
    private String web3sdkMaxPoolSize;
    private String web3sdkQueueSize;
    private String web3sdkKeepAliveSeconds;
    private String v1CaCrtPath;
    private String v1ClientCrtPassword;
    private String v1ClientKeyStorePath;
    private String v1KeyStorePassword;
    private String v2CaCrtPath;
    private String v2NodeCrtPath;
    private String v2NodeKeyPath;

    /**
     * load configuration without Spring context required.
     *
     * @return true if success, else false
     */
    public boolean load() {
        try {
            version = PropertyUtils.getProperty("bcos.version");
            nodes = PropertyUtils.getProperty("nodes");
            weIdAddress = PropertyUtils.getProperty("weId.contractaddress");
            cptAddress = PropertyUtils.getProperty("cpt.contractaddress");
            issuerAddress = PropertyUtils.getProperty("issuer.contractaddress");
            evidenceAddress = PropertyUtils.getProperty("evidence.contractaddress");
            specificIssuerAddress = PropertyUtils.getProperty("specificissuer.contractaddress");
            chainId = PropertyUtils.getProperty("chain.id");
            web3sdkTimeout = PropertyUtils
                .getProperty("web3sdkTimeout", "10000");
            web3sdkCorePoolSize = PropertyUtils
                .getProperty("web3sdk.core-pool-size", "100");
            web3sdkMaxPoolSize = PropertyUtils
                .getProperty("web3sdk.max-pool-size", "200");
            web3sdkQueueSize = PropertyUtils
                .getProperty("web3sdk.queue-capacity", "1000");
            web3sdkKeepAliveSeconds = PropertyUtils
                .getProperty("web3sdk.keep-alive-seconds", "60");
            v1CaCrtPath = PropertyUtils
                .getProperty("v1.ca-crt-path", "ca.crt");
            v1ClientCrtPassword = PropertyUtils
                .getProperty("v1.client-crt-password", "123456");
            v1ClientKeyStorePath = PropertyUtils
                .getProperty("v1.client-key-store-path", "client.keystore");
            v1KeyStorePassword = PropertyUtils
                .getProperty("v1.key-store-password", "123456");
            v2CaCrtPath = PropertyUtils
                .getProperty("v2.ca-crt-path", "./v2/ca.crt");
            v2NodeCrtPath = PropertyUtils
                .getProperty("v2.node-crt-path", "./v2/node.crt");
            v2NodeKeyPath = PropertyUtils
                .getProperty("v2.node-key-path", "./v2/node.key");
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during loading Fisco-Bcos properties: " + e.getMessage());
            return false;
        }
    }
}
