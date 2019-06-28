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
    private String groupId;
    private String encryptType;
    private String v1CaCrtPath;
    private String v1ClientCrtPassword;
    private String v1ClientKeyStorePath;
    private String v1KeyStorePassword;
    private String v2CaCrtPath;
    private String v2NodeCrtPath;
    private String v2NodeKeyPath;
    private String currentOrgId;

    /**
     * load configuration without Spring context required.
     *
     * @return true if success, else false
     */
    public boolean load() {
        try {
            // node info is obtained from weidentity.properties.tpl
            nodes = PropertyUtils.getProperty("nodes");

            version = PropertyUtils.getProperty("bcos.version");
            weIdAddress = PropertyUtils.getProperty("weId.contractaddress");
            cptAddress = PropertyUtils.getProperty("cpt.contractaddress");
            issuerAddress = PropertyUtils.getProperty("issuer.contractaddress");
            evidenceAddress = PropertyUtils.getProperty("evidence.contractaddress");
            specificIssuerAddress = PropertyUtils.getProperty("specificissuer.contractaddress");
            chainId = PropertyUtils.getProperty("chain.id");
            web3sdkTimeout = PropertyUtils.getProperty("web3sdk.timeout");
            web3sdkCorePoolSize = PropertyUtils.getProperty("web3sdk.core-pool-size");
            web3sdkMaxPoolSize = PropertyUtils.getProperty("web3sdk.max-pool-size");
            web3sdkQueueSize = PropertyUtils.getProperty("web3sdk.queue-capacity");
            web3sdkKeepAliveSeconds = PropertyUtils.getProperty("web3sdk.keep-alive-seconds");
            groupId = PropertyUtils.getProperty("group.id");
            encryptType = PropertyUtils.getProperty("encrypt.type");
            v1CaCrtPath = PropertyUtils.getProperty("v1.ca-crt-path");
            v1ClientCrtPassword = PropertyUtils.getProperty("v1.client-crt-password");
            v1ClientKeyStorePath = PropertyUtils.getProperty("v1.client-key-store-path");
            v1KeyStorePassword = PropertyUtils.getProperty("v1.key-store-password");
            v2CaCrtPath = PropertyUtils.getProperty("v2.ca-crt-path");
            v2NodeCrtPath = PropertyUtils.getProperty("v2.node-crt-path");
            v2NodeKeyPath = PropertyUtils.getProperty("v2.node-key-path");
            currentOrgId = PropertyUtils.getProperty("blockchain.orgid");
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during loading Fisco-Bcos properties: " + e.getMessage());
            return false;
        }
    }
}