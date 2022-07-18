

package com.webank.weid.contract.deploy;

import com.webank.weid.contract.deploy.v3.DeployContractV3;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.v2.DeployContractV2;
import com.webank.weid.exception.WeIdBaseException;

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
        //此处初始化读取main resource的配置文件，可修改chainId为101。并修改fisco.properties:profile.active=prd101
//        String chainId = "101";
        String chainId = args[0];
        String privateKey = null;
        if (args != null && args.length > 2) {
            privateKey = args[1];
        }
        if (StringUtils.isBlank(privateKey)) {
            privateKey = AddressProcess.getAddressFromFile("ecdsa_key");
        }
        fiscoConfig.setChainId(chainId);
        try {
            deployContract(privateKey, true);
        } catch (WeIdBaseException e) {
            if (e.getErrorCode().getCode() == ErrorCode.CNS_NO_PERMISSION.getCode()) {
                System.out.println("deploy fail, Maybe your private key is incorrect. Please make "
                    + "sure that the root directory of the private key file ecdsa_key that "
                    + "you deployed for the first time exists in the root directory.");
            }
            throw e;
        }
        System.exit(0);
    }
    
    public static void deployContract(String privateKey, boolean instantEnable) {
        if (fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            throw new WeIdBaseException(ErrorCode.THIS_IS_UNSUPPORTED);
        } else if (fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_2_X_VERSION_PREFIX)) {
            DeployContractV2.deployContract(privateKey, fiscoConfig, instantEnable);
        } else if (fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_3_X_VERSION_PREFIX)) {
            DeployContractV3.deployContract(privateKey, fiscoConfig, instantEnable);
        }
    }
}
