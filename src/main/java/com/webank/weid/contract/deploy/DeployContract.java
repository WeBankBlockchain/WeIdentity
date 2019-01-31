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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.bcos.channel.client.Service;
import org.bcos.contract.tools.ToolConf;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.GenCredential;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.contract.AuthorityIssuerData;
import com.webank.weid.contract.CommitteeMemberController;
import com.webank.weid.contract.CommitteeMemberData;
import com.webank.weid.contract.CptController;
import com.webank.weid.contract.CptData;
import com.webank.weid.contract.EvidenceFactory;
import com.webank.weid.contract.RoleController;
import com.webank.weid.contract.WeIdContract;

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
     * The context.
     */
    protected static final ApplicationContext context;

    /**
     * The credentials.
     */
    private static Credentials credentials;

    /**
     * web3j object.
     */
    private static Web3j web3j;

    static {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
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
     * @return the web3j instance
     */
    protected static Web3j getWeb3j() {
        if (null == web3j) {
            loadConfig();
        }
        return web3j;
    }

    private static void deployContract() {
        String weIdContractAddress = deployWeIdContract();
        String authorityIssuerDataAddress = deployAuthorityIssuerContracts();
        deployCptContracts(authorityIssuerDataAddress, weIdContractAddress);
        deployEvidenceContracts();
    }

    private static String deployWeIdContract() {
        if (null == web3j) {
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
        String authorityIssuerDataAddress, String weIdContractAddress) {
        if (null == web3j) {
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
            //        writeAddressToFile("CptData", cptDataAddress);

            Future<CptController> f2 =
                CptController.deploy(
                    web3j,
                    credentials,
                    WeIdConstant.GAS_PRICE,
                    WeIdConstant.GAS_LIMIT,
                    WeIdConstant.INILITIAL_VALUE,
                    new Address(cptDataAddress),
                    new Address(weIdContractAddress));
            CptController cptController =
                f2.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            String cptControllerAddress = cptController.getContractAddress();
            writeAddressToFile(cptControllerAddress, "cptController.address");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("CptController deploy exception", e);
        }

        return StringUtils.EMPTY;
    }

    private static String deployAuthorityIssuerContracts() {
        if (null == web3j) {
            loadConfig();
        }

        // Step 1: Deploy RoleController sol => [addr1]
        String authorityIssuerDataAddress = StringUtils.EMPTY;
        Future<RoleController> f1 =
            RoleController.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE);

        // Step 2: Deploy CommitteeMemberData sol => [addr1]
        Future<CommitteeMemberData> f2 = null;
        String roleControllerAddress = StringUtils.EMPTY;
        try {
            RoleController roleController =
                f1.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            roleControllerAddress = roleController.getContractAddress();
            f2 = CommitteeMemberData.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(roleControllerAddress));

        } catch (Exception e) {
            logger.error("RoleController deployment error:", e);
            return authorityIssuerDataAddress;
        }

        // Step 3: Deploy CommitteeMemberController sol => [addr1]
        try {
            CommitteeMemberData committeeMemberData =
                f2.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            String committeeMemberDataAddress = committeeMemberData.getContractAddress();

            CommitteeMemberController.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(committeeMemberDataAddress),
                new Address(roleControllerAddress));

        } catch (Exception e) {
            logger.error("CommitteeMemberData deployment error:", e);
            return authorityIssuerDataAddress;
        }

        // Step 4: Deploy AuthorityIssuerData sol => [addr1]
        Future<AuthorityIssuerData> f4 = null;
        try {
            f4 = AuthorityIssuerData.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(roleControllerAddress));

        } catch (Exception e) {
            logger.error("CommitteeMemberController deployment error:", e);
            return authorityIssuerDataAddress;
        }

        // Step 5: Deploy AuthorityIssuerController sol => [addr1]
        Future<AuthorityIssuerController> f5 = null;
        try {
            AuthorityIssuerData authorityIssuerData =
                f4.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            authorityIssuerDataAddress = authorityIssuerData.getContractAddress();
            f5 = AuthorityIssuerController.deploy(
                web3j,
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT,
                WeIdConstant.INILITIAL_VALUE,
                new Address(authorityIssuerDataAddress),
                new Address(roleControllerAddress));

        } catch (Exception e) {
            logger.error("AuthorityIssuerData deployment error:", e);
            return authorityIssuerDataAddress;
        }

        // Step 6: Write [addrress] Into File
        try {
            AuthorityIssuerController authorityIssuerController =
                f5.get(DEFAULT_DEPLOY_CONTRACTS_TIMEOUT_IN_SECONDS,
                    TimeUnit.SECONDS);
            String authorityIssuerControllerAddress =
                authorityIssuerController.getContractAddress();
            writeAddressToFile(authorityIssuerControllerAddress, "authorityIssuer.address");
            return authorityIssuerControllerAddress;
        } catch (Exception e) {
            logger.error("AuthorityIssuerController deployment error:", e);
        }
        return authorityIssuerDataAddress;
    }

    private static String deployEvidenceContracts() {
        if (null == web3j) {
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
            ow = new OutputStreamWriter(new FileOutputStream(fileName, true), WeIdConstant.UTF_8);
            String content = new StringBuffer().append(contractAddress).toString();
            ow.write(content);
            ow.close();
        } catch (IOException e) {
            logger.error("writer file exception", e);
        } finally {
            if (null != ow) {
                try {
                    ow.close();
                } catch (IOException e) {
                    logger.error("io close exception", e);
                }
            }
        }
    }
}
