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

package com.webank.weid.contract.deploy.v2;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.AddressProcess;
import com.webank.weid.contract.v2.AuthorityIssuerController;
import com.webank.weid.contract.v2.AuthorityIssuerData;
import com.webank.weid.contract.v2.CommitteeMemberController;
import com.webank.weid.contract.v2.CommitteeMemberData;
import com.webank.weid.contract.v2.CptController;
import com.webank.weid.contract.v2.CptData;
import com.webank.weid.contract.v2.EvidenceContract;
import com.webank.weid.contract.v2.EvidenceFactory;
import com.webank.weid.contract.v2.RoleController;
import com.webank.weid.contract.v2.SpecificIssuerController;
import com.webank.weid.contract.v2.SpecificIssuerData;
import com.webank.weid.contract.v2.WeIdContract;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.WeIdUtils;

/**
 * The Class DeployContract.
 *
 * @author tonychen
 */
public class DeployContractV2 extends AddressProcess {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(DeployContractV2.class);

    /**
     * The credentials.
     */
    private static Credentials credentials;

    /**
     * web3j object.
     */
    private static Web3j web3j;

    /**
     * Inits the credentials.
     *
     * @return true, if successful
     */
    private static boolean initCredentials(String inputPrivateKey) {
        if (StringUtils.isNotBlank(inputPrivateKey)) {
            logger.info("[DeployContractV2] begin to init credentials by privateKey..");
            credentials = GenCredential.create(new BigInteger(inputPrivateKey).toString(16));
        } else {
            logger.info("[DeployContractV2] begin to init credentials..");
            credentials = GenCredential.create();
        }

        if (credentials == null) {
            logger.error("[DeployContractV2] credentials init failed. ");
            return false;
        }
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString();
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString();
        writeAddressToFile(publicKey, "ecdsa_key.pub");
        writeAddressToFile(privateKey, "ecdsa_key");
        return true;
    }

    /**
     * Inits the web3j.
     */
    protected static void initWeb3j() {
        if (web3j == null) {
            web3j = (Web3j) BaseService.getWeb3j();
        }
    }

    /**
     * depoly contract on FISCO BCOS 2.0.
     * @param privateKey the private key
     * @param fiscoConfig 配置信息
     * @param instantEnable 是否即时启用机构配置，源码安装以及build-tool命令版本安装即时启用，
     *      build-tools-web 版本点击启用时候启用
     */
    public static void deployContract(
        String privateKey, 
        FiscoConfig fiscoConfig,
        boolean instantEnable
    ) {
        initWeb3j();
        initCredentials(privateKey);
        String roleControllerAddress = deployRoleControllerContracts();
        String weIdContractAddress = deployWeIdContract(roleControllerAddress);
        Map<String, String> addrList = deployIssuerContracts(roleControllerAddress);
        if (addrList.containsKey("AuthorityIssuerData")) {
            String authorityIssuerDataAddress = addrList.get("AuthorityIssuerData");
            deployCptContracts(
                authorityIssuerDataAddress,
                weIdContractAddress,
                roleControllerAddress
            );
        }
        deployEvidenceContractsNew();
        // cns处理
        registerToCns(fiscoConfig, instantEnable);
    }

    private static void registerToCns(
        FiscoConfig fiscoConfig,
        boolean instantEnable
    ) {
        String privateKey = AddressProcess.getAddressFromFile("ecdsa_key");
        WeIdPrivateKey weIdPrivate = new WeIdPrivateKey();
        weIdPrivate.setPrivateKey(privateKey);
        registerAddress(weIdPrivate, fiscoConfig, instantEnable);
    }


    private static String deployRoleControllerContracts() {
        if (web3j == null) {
            initWeb3j();
        }
        RoleController roleController = null;
        try {
            roleController =
                RoleController.deploy(
                    web3j,
                    credentials,
                    new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)
                ).send();
            return roleController.getContractAddress();
        } catch (Exception e) {
            logger.error("RoleController deploy exception", e);
            return StringUtils.EMPTY;
        }
    }

    private static String deployWeIdContract(String roleControllerAddress) {
        if (web3j == null) {
            initWeb3j();
        }

        WeIdContract weIdContract = null;
        try {
            weIdContract = WeIdContract.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                roleControllerAddress)
                .send();
        } catch (Exception e) {
            logger.error("WeIdContract deploy error.", e);
            return StringUtils.EMPTY;
        }

        String contractAddress = weIdContract.getContractAddress();
        writeAddressToFile(contractAddress, "weIdContract.address");
        return contractAddress;

    }

    private static String deployCptContracts(
        String authorityIssuerDataAddress,
        String weIdContractAddress,
        String roleControllerAddress) {
        if (web3j == null) {
            initWeb3j();
        }

        try {
            CptData cptData =
                CptData.deploy(
                    web3j,
                    credentials,
                    new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                    authorityIssuerDataAddress).send();
            String cptDataAddress = cptData.getContractAddress();

            CptController cptController =
                CptController.deploy(
                    web3j,
                    credentials,
                    new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                    cptDataAddress,
                    weIdContractAddress
                ).send();
            String cptControllerAddress = cptController.getContractAddress();
            writeAddressToFile(cptControllerAddress, "cptController.address");

            TransactionReceipt receipt =
                cptController.setRoleController(roleControllerAddress).send();
            if (receipt == null) {
                logger.error("CptController deploy exception");
            }
        } catch (Exception e) {
            logger.error("CptController deploy exception", e);
        }

        return StringUtils.EMPTY;
    }

    private static Map<String, String> deployIssuerContracts(String roleControllerAddress) {
        if (web3j == null) {
            initWeb3j();
        }
        Map<String, String> issuerAddressList = new HashMap<>();

        String committeeMemberDataAddress;
        try {
            CommitteeMemberData committeeMemberData = CommitteeMemberData.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                roleControllerAddress).send();
            committeeMemberDataAddress = committeeMemberData.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(committeeMemberDataAddress))) {
                issuerAddressList.put("CommitteeMemberData", committeeMemberDataAddress);
            }
        } catch (Exception e) {
            logger.error("CommitteeMemberData deployment error:", e);
            return issuerAddressList;
        }

        String committeeMemberControllerAddress;
        try {
            CommitteeMemberController committeeMemberController = CommitteeMemberController.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                committeeMemberDataAddress,
                roleControllerAddress
            ).send();
            committeeMemberControllerAddress = committeeMemberController.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(committeeMemberControllerAddress))) {
                issuerAddressList
                    .put("CommitteeMemberController", committeeMemberControllerAddress);
            }
        } catch (Exception e) {
            logger.error("CommitteeMemberController deployment error:", e);
            return issuerAddressList;
        }

        String authorityIssuerDataAddress;
        try {
            AuthorityIssuerData authorityIssuerData = AuthorityIssuerData.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                roleControllerAddress
            ).send();
            authorityIssuerDataAddress = authorityIssuerData.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(authorityIssuerDataAddress))) {
                issuerAddressList.put("AuthorityIssuerData", authorityIssuerDataAddress);
            }
        } catch (Exception e) {
            logger.error("AuthorityIssuerData deployment error:", e);
            return issuerAddressList;
        }

        String authorityIssuerControllerAddress;
        try {
            AuthorityIssuerController authorityIssuerController = AuthorityIssuerController.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                authorityIssuerDataAddress,
                roleControllerAddress).send();
            authorityIssuerControllerAddress = authorityIssuerController.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(authorityIssuerControllerAddress))) {
                issuerAddressList
                    .put("AuthorityIssuerController", authorityIssuerControllerAddress);
            }
        } catch (Exception e) {
            logger.error("AuthorityIssuerController deployment error:", e);
            return issuerAddressList;
        }

        try {
            writeAddressToFile(authorityIssuerControllerAddress, "authorityIssuer.address");
        } catch (Exception e) {
            logger.error("Write error:", e);
        }

        String specificIssuerDataAddress = StringUtils.EMPTY;
        try {
            SpecificIssuerData specificIssuerData = SpecificIssuerData.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)
            ).send();
            specificIssuerDataAddress = specificIssuerData.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(specificIssuerDataAddress))) {
                issuerAddressList.put("SpecificIssuerData", specificIssuerDataAddress);
            }
        } catch (Exception e) {
            logger.error("SpecificIssuerData deployment error:", e);
        }

        try {
            SpecificIssuerController specificIssuerController = SpecificIssuerController.deploy(
                web3j,
                credentials,
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT),
                specificIssuerDataAddress,
                roleControllerAddress
            ).send();
            String specificIssuerControllerAddress = specificIssuerController.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(specificIssuerControllerAddress))) {
                issuerAddressList.put("SpecificIssuerController", specificIssuerControllerAddress);
            }
            try {
                writeAddressToFile(specificIssuerControllerAddress, "specificIssuer.address");
            } catch (Exception e) {
                logger.error("Write error:", e);
            }
        } catch (Exception e) {
            logger.error("SpecificIssuerController deployment error:", e);
        }
        return issuerAddressList;
    }

    @Deprecated
    private static String deployEvidenceContracts() {
        if (web3j == null) {
            initWeb3j();
        }
        try {
            EvidenceFactory evidenceFactory =
                EvidenceFactory.deploy(
                    web3j,
                    credentials,
                    new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)
                ).send();
            String evidenceFactoryAddress = evidenceFactory.getContractAddress();
            writeAddressToFile(evidenceFactoryAddress, "evidenceController.address");
            return evidenceFactoryAddress;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("EvidenceFactory deploy exception", e);
        } catch (Exception e) {
            logger.error("EvidenceFactory deploy exception", e);
        }
        return StringUtils.EMPTY;
    }

    private static String deployEvidenceContractsNew() {
        if (web3j == null) {
            initWeb3j();
        }
        try {
            EvidenceContract evidenceContract =
                EvidenceContract.deploy(
                    web3j,
                    credentials,
                    new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)
                ).send();
            String evidenceContractAddress = evidenceContract.getContractAddress();
            writeAddressToFile(evidenceContractAddress, "evidenceController.address");
            return evidenceContractAddress;
        } catch (Exception e) {
            logger.error("EvidenceFactory deploy exception", e);
        }
        return StringUtils.EMPTY;
    }
    
    /**
     * 根据私钥将合约地址注册到cns中.
     * @param privateKey 私钥信息
     * @param fiscoConfig 配置信息
     * @param instantEnable 是否即时启用
     */
    public static void registerAddress(
        WeIdPrivateKey privateKey, 
        FiscoConfig fiscoConfig,
        boolean instantEnable
    ) {
        CnsType  cnsType = CnsType.DEFAULT;
        // 先進行cns注冊
        RegisterAddressV2.registerAllCns(privateKey);
        // 获取地址hash
        ContractConfig contractConfig = getContractConfig();
        String hash = getHashByAddress(contractConfig);
        logger.info("[registerAddress] contract hash = {}.", hash);
        RegisterAddressV2.registerAddress(
            cnsType,
            hash, 
            contractConfig.getWeIdAddress(), 
            WeIdConstant.CNS_WEID_ADDRESS, 
            privateKey
        );

        RegisterAddressV2.registerAddress(
            cnsType,
            hash, 
            contractConfig.getIssuerAddress(),
            WeIdConstant.CNS_AUTH_ADDRESS, 
            privateKey
        );

        RegisterAddressV2.registerAddress(
            cnsType,
            hash,
            contractConfig.getSpecificIssuerAddress(), 
            WeIdConstant.CNS_SPECIFIC_ADDRESS,
            privateKey
        );

        RegisterAddressV2.registerAddress(
            cnsType,
            hash, 
            contractConfig.getEvidenceAddress(), 
            WeIdConstant.CNS_EVIDENCE_ADDRESS, 
            privateKey
        );

        RegisterAddressV2.registerAddress(
            cnsType,
            hash, 
            contractConfig.getCptAddress(), 
            WeIdConstant.CNS_CPT_ADDRESS, 
            privateKey
        );
        
        RegisterAddressV2.registerAddress(
            cnsType,
            hash, 
            fiscoConfig.getChainId(), 
            WeIdConstant.CNS_CHAIN_ID, 
            privateKey
        );
        
        writeAddressToFile(hash, "hash");
        
        //如果为即时启用
        if (instantEnable) {
            putGlobalValue(fiscoConfig, contractConfig, privateKey);
            // 合约上也启用hash
            RegisterAddressV2.enableHash(cnsType, hash, privateKey);
        }
    }
    
    public static void putGlobalValue(
        FiscoConfig fiscoConfig, 
        ContractConfig contract, 
        WeIdPrivateKey privateKey
    ) {
        String hash = getHashByAddress(contract);
        // 将weid合约地址写入全局配置中
        putGlobalValue(WeIdConstant.CNS_WEID_ADDRESS, contract.getWeIdAddress(), privateKey);
        // 将issuer合约地址写入全局配置中
        putGlobalValue(WeIdConstant.CNS_AUTH_ADDRESS, contract.getIssuerAddress(), privateKey);
        // 将SpecificIssuer合约地址写入全局配置中
        putGlobalValue(
            WeIdConstant.CNS_SPECIFIC_ADDRESS, 
            contract.getSpecificIssuerAddress(), 
            privateKey
        );
        // 将Evidence合约地址写入全局配置中
        putGlobalValue(
            WeIdConstant.CNS_EVIDENCE_ADDRESS, 
            contract.getEvidenceAddress(), 
            privateKey
        );
        // 将Cpt合约地址写入全局配置中
        putGlobalValue(WeIdConstant.CNS_CPT_ADDRESS, contract.getCptAddress(), privateKey);
        // 将ChainId写入全局配置中
        putGlobalValue(WeIdConstant.CNS_CHAIN_ID, fiscoConfig.getChainId(), privateKey);
        // 将主Hash写入全局配置中
        putGlobalValue(WeIdConstant.CNS_MAIN_HASH, hash, privateKey);
    }
    
    private static void putGlobalValue(String key, String value, WeIdPrivateKey privateKey) {
        // 存放数据的cns块
        CnsType cnsType = CnsType.ORG_CONFING;
        // 存放全局配置的key
        String module = WeIdConstant.CNS_GLOBAL_KEY;
        RegisterAddressV2.registerAddress(
            cnsType,
            module, 
            value, 
            key, 
            privateKey
        );
    }
}