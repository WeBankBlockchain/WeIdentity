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

package com.webank.weid.contract.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.v1.DeployContractV1;
import com.webank.weid.contract.deploy.v2.DeployContractV2;

/**
 * The Class DeployContract.
 *
 * @author tonychen
 */
public abstract class DeployContract {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(DeployContract.class);

    /**
     * The Fisco Config bundle.
     */
    protected static final FiscoConfig fiscoConfig;

    static {
        fiscoConfig = new FiscoConfig();
        if (!fiscoConfig.load()) {
            logger.error("[BaseService] Failed to load Fisco-BCOS blockchain node information.");
            System.exit(1);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        
        String privateKey = null;
        if (args != null && args.length > 0) {
            privateKey = args[0];
        }
        deployContract(privateKey);
        System.exit(0);
    }
    
    public static void deployContract(String privateKey) {
        if (fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            DeployContractV1.deployContract(privateKey);
        } else {
            DeployContractV2.deployContract(privateKey);
        } 
    }
}
