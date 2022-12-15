

package com.webank.weid.contract.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DeployContract.
 *
 * @author tonychen
 */
public abstract class DeployEvidence {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(DeployEvidence.class);

    /**
     * The Fisco Config bundle.
     */
    /*protected static final FiscoConfig fiscoConfig;

    static {
        fiscoConfig = new FiscoConfig();
        if (!fiscoConfig.load()) {
            logger.error("[BaseService] Failed to load Fisco-BCOS blockchain node information.");
            System.exit(1);
        }
    }*/

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        // args = new String[] {"2"};

        if (args == null || args.length == 0) {
            logger.error("input param illegal");
            System.exit(1);
        }

        String groupStr = args[0];
        String groupId = groupStr;

        String privateKey = null;
        if (args != null && args.length > 1) {
            privateKey = args[1];
        }

        com.webank.weid.blockchain.deploy.DeployEvidence.deployContract(privateKey, groupId, true);
        System.exit(0);
    }
    
    /**
     * 部署evidence合约.
     * 
     * @param privateKey 私钥地址
     * @param groupId 群组编号
     * @param instantEnable 是否即时启用
     * @return 返回部署的hash值
     */
    /*public static String deployContract(String privateKey, String groupId, boolean instantEnable) {
        return DeployEvidenceV2.deployContract(fiscoConfig, privateKey, groupId, instantEnable);
    }*/
}
