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

package com.webank.weid.contract.deploy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.bcos.channel.client.Service;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.GenCredential;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.contract.AuthorityIssuerData;
import com.webank.weid.contract.CommitteeMemberController;
import com.webank.weid.contract.CommitteeMemberData;
import com.webank.weid.contract.CptController;
import com.webank.weid.contract.CptData;
import com.webank.weid.contract.EvidenceFactory;
import com.webank.weid.contract.RoleController;
import com.webank.weid.contract.SpecificIssuerController;
import com.webank.weid.contract.SpecificIssuerData;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * The Class DeployContract.
 *
 * @author tonychen
 */
public class DeployContract {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(DeployContract.class);

    /**
     * The Constant for default deploy contracts timeout.
     */
    private static final Integer DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS = 15;

    /**
     * The Fisco Config bundle.
     */
    protected static final FiscoConfig fiscoConfig;

    /**
     * The credentials.
     */
    private static Credentials credentials;

    /**
     * web3j object.
     */
    private static Web3j web3j;

    static {
        fiscoConfig = new FiscoConfig();
        if (!fiscoConfig.load()) {
            logger.error("[BaseService] Failed to load Fisco-BCOS blockchain node information.");
        }
        loadConfig();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        deployContract();
        System.exit(0);
    }

    /**
     * Load config.
     *
     * @return true, if successful
     */
    private static boolean loadConfig() {
        return (initWeb3j() && initCredentials());
    }

    private static boolean initWeb3j() {
        logger.info("[BaseService] begin to init web3j instance..");
        Service service = TransactionUtils.buildFiscoBcosService(fiscoConfig);
        try {
            service.run();
        } catch (Exception e) {
            logger.error("[BaseService] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }

        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        web3j = Web3j.build(channelEthereumService);
        if (web3j == null) {
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
        logger.info("[BaseService] begin to init credentials..");
        credentials = GenCredential.create();

        if (credentials == null) {
            logger.error("[BaseService] credentials init failed. ");
            return false;
        }
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString();
        writeAddressToFile(privateKey, "privateKey.txt");
        return true;
    }

    /**
     * Gets the web3j.
     *
     * @return the web3j instance
     */
    protected static Web3j getWeb3j() {
        if (web3j == null) {
            loadConfig();
        }
        return web3j;
    }

    private static void deployContract() {
        String weIdContractAddress = deployWeIdContract();
        String roleControllerAddress = deployRoleControllerContracts();
        Map<String, String> addrList = deployIssuerContracts(roleControllerAddress);
        if (addrList.containsKey("AuthorityIssuerData")) {
            String authorityIssuerDataAddress = addrList.get("AuthorityIssuerData");
            deployCptContracts(
                authorityIssuerDataAddress,
                weIdContractAddress,
                roleControllerAddress
            );
        }
        deployEvidenceContracts();
    }

    private static String deployWeIdContract() {
        if (web3j == null) {
            loadConfig();
        }
        Future<WeIdContract> f =
            WeIdContract.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE);

        try {
            WeIdContract weIdContract =
                f.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            String contractAddress = weIdContract.getContractAddress();
            writeAddressToFile(contractAddress, "weIdContract.address");
            return contractAddress;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("WeIdContract deploy exception", e);
        }
        return StringUtils.EMPTY;
    }

    private static String deployCptContracts(
        String authorityIssuerDataAddress,
        String weIdContractAddress,
        String roleControllerAddress) {
        if (web3j == null) {
            loadConfig();
        }

        try {
            Future<CptData> f1 =
                CptData.deploy(
                    web3j,
                    credentials,
                    WeIdConstant.GAS_PRICE,
                    WeIdConstant.GAS_LIMIT,
                    WeIdConstant.INILITIAL_VALUE,
                    new Address(authorityIssuerDataAddress));
            CptData cptData = f1.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            String cptDataAddress = cptData.getContractAddress();

            Future<CptController> f2 =
                CptController.deploy(
                    web3j,
                    credentials,
                    WeIdConstant.GAS_PRICE,
                    WeIdConstant.GAS_LIMIT,
                    WeIdConstant.INILITIAL_VALUE,
                    new Address(cptDataAddress),
                    new Address(weIdContractAddress)
                );
            CptController cptController =
                f2.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            String cptControllerAddress = cptController.getContractAddress();
            writeAddressToFile(cptControllerAddress, "cptController.address");

            Future<TransactionReceipt> f3 = cptController
                .setRoleController(new Address(roleControllerAddress));
            f3.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("CptController deploy exception", e);
        }

        return StringUtils.EMPTY;
    }

    private static String deployRoleControllerContracts() {
        if (web3j == null) {
            loadConfig();
        }

        try {
            Future<RoleController> f1 =
                RoleController.deploy(
                    web3j,
                    credentials,
                    WeIdConstant.GAS_PRICE,
                    WeIdConstant.GAS_LIMIT,
                    WeIdConstant.INILITIAL_VALUE);
            RoleController roleController =
                f1.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            return roleController.getContractAddress();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("RoleController deploy exception", e);
            return StringUtils.EMPTY;
        }
    }

    private static Map<String, String> deployIssuerContracts(String roleControllerAddress) {
        if (web3j == null) {
            loadConfig();
        }
        Map<String, String> issuerAddressList = new HashMap<>();

        Future<CommitteeMemberData> f2;
        String committeeMemberDataAddress;
        try {
            f2 = CommitteeMemberData.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(roleControllerAddress));
            CommitteeMemberData committeeMemberData =
                f2.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            committeeMemberDataAddress = committeeMemberData.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(committeeMemberDataAddress))) {
                issuerAddressList.put("CommitteeMemberData", committeeMemberDataAddress);
            }
        } catch (Exception e) {
            logger.error("CommitteeMemberData deployment error:", e);
            return issuerAddressList;
        }

        Future<CommitteeMemberController> f3;
        String committeeMemberControllerAddress;
        try {
            f3 = CommitteeMemberController.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(committeeMemberDataAddress),
                new Address(roleControllerAddress)
            );
            CommitteeMemberController committeeMemberController =
                f3.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            committeeMemberControllerAddress = committeeMemberController.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(committeeMemberControllerAddress))) {
                issuerAddressList
                    .put("CommitteeMemberController", committeeMemberControllerAddress);
            }
        } catch (Exception e) {
            logger.error("CommitteeMemberController deployment error:", e);
            return issuerAddressList;
        }

        Future<AuthorityIssuerData> f4;
        String authorityIssuerDataAddress;
        try {
            f4 = AuthorityIssuerData.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(roleControllerAddress)
            );
            AuthorityIssuerData authorityIssuerData =
                f4.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            authorityIssuerDataAddress = authorityIssuerData.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(authorityIssuerDataAddress))) {
                issuerAddressList.put("AuthorityIssuerData", authorityIssuerDataAddress);
            }
        } catch (Exception e) {
            logger.error("AuthorityIssuerData deployment error:", e);
            return issuerAddressList;
        }

        Future<AuthorityIssuerController> f5;
        String authorityIssuerControllerAddress;
        try {
            f5 = AuthorityIssuerController.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(authorityIssuerDataAddress),
                new Address(roleControllerAddress));
            AuthorityIssuerController authorityIssuerController =
                f5.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
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
            Future<SpecificIssuerData> f6 = SpecificIssuerData.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE
            );
            SpecificIssuerData specificIssuerData = f6.get(
                DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS,
                TimeUnit.SECONDS
            );
            specificIssuerDataAddress = specificIssuerData.getContractAddress();
            if (!WeIdUtils.isEmptyAddress(new Address(specificIssuerDataAddress))) {
                issuerAddressList.put("SpecificIssuerData", specificIssuerDataAddress);
            }
        } catch (Exception e) {
            logger.error("SpecificIssuerData deployment error:", e);
        }

        try {
            Future<SpecificIssuerController> f7 = SpecificIssuerController.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(specificIssuerDataAddress),
                new Address(roleControllerAddress)
            );
            SpecificIssuerController specificIssuerController = f7.get(
                DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS,
                TimeUnit.SECONDS
            );
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

    private static String deployEvidenceContracts() {
        if (web3j == null) {
            loadConfig();
        }

        try {
            Future<EvidenceFactory> f =
                EvidenceFactory.deploy(
                    web3j,
                    credentials,
                    WeIdConstant.GAS_PRICE,
                    WeIdConstant.GAS_LIMIT,
                    WeIdConstant.INILITIAL_VALUE
                );
            EvidenceFactory evidenceFactory = f
                .get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            String evidenceFactoryAddress = evidenceFactory.getContractAddress();
            writeAddressToFile(evidenceFactoryAddress, "evidenceController.address");
            return evidenceFactoryAddress;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("EvidenceFactory deploy exception", e);
        }
        return StringUtils.EMPTY;
    }

    private static void writeAddressToFile(
        String contractAddress,
        String fileName) {

        OutputStreamWriter ow = null;
        try {
            boolean flag = true;
            File file = new File(fileName);
            if (file.exists()) {
                flag = file.delete();
            }
            if (!flag) {
                logger.error("writeAddressToFile() delete file is fail.");
                return;
            }
            ow = new OutputStreamWriter(
                new FileOutputStream(fileName, true),
                StandardCharsets.UTF_8
            );
            String content = new StringBuffer().append(contractAddress).toString();
            ow.write(content);
            ow.close();
        } catch (IOException e) {
            logger.error("writer file exception", e);
        } finally {
            if (ow != null) {
                try {
                    ow.close();
                } catch (IOException e) {
                    logger.error("io close exception", e);
                }
            }
        }
    }
}
