package com.webank.weid.contract.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.webank.weid.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.util.DataToolUtils;

public abstract class AddressProcess {
    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(AddressProcess.class);

    protected static void writeAddressToFile(
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
    
    protected static String getAddressFromFile(
        String fileName) {
        
        BufferedReader br = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file))
                );
                String address = br.readLine();
                return address;
            }
        } catch (IOException e) {
            logger.error("writer file exception", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("io close exception", e);
                }
            }
        }
        logger.error("getAddressFromFile() the {} does not exists.", fileName);
        return StringUtils.EMPTY;
    }
    
    protected static String getAddress(String fileName) {
        String address = getAddressFromFile(fileName);
        logger.info("[getAddress] get address from {}, address {}.", fileName, address);
        return address;
    }
    
    protected static ContractConfig getContractConfig() {
        ContractConfig contractConfig = new ContractConfig();
        String weIdAddress = getAddress("weIdContract.address");
        String issuerAddress = getAddress("authorityIssuer.address");
        String specificAddress = getAddress("specificIssuer.address");
        String evidenceAddress = getAddress("evidenceController.address");
        String cptAddress = getAddress("cptController.address");

        contractConfig.setWeIdAddress(weIdAddress);
        contractConfig.setIssuerAddress(issuerAddress);
        contractConfig.setSpecificIssuerAddress(specificAddress);
        contractConfig.setEvidenceAddress(evidenceAddress);
        contractConfig.setCptAddress(cptAddress);
        return contractConfig;
    }
    
    /**
     * 根据合约地址获取hash数据.
     * @param contractConfig 合约数据
     * @return 返回hash
     */
    public static String getHashByAddress(ContractConfig contractConfig) {
        String weidAddress = contractConfig.getWeIdAddress();
        String authAddress = contractConfig.getIssuerAddress();
        String specificAddress = contractConfig.getSpecificIssuerAddress();
        String evidenceAddress = contractConfig.getEvidenceAddress();
        String cptAddress = contractConfig.getCptAddress();
        if (StringUtils.isBlank(weidAddress) 
            || StringUtils.isBlank(authAddress)
            || StringUtils.isBlank(specificAddress) 
            || StringUtils.isBlank(evidenceAddress) 
            || StringUtils.isBlank(cptAddress)) {
            throw new WeIdBaseException("can not found the address, please deploy contract.");
        }
        StringBuffer address = new StringBuffer();
        address.append(weidAddress)
            .append(authAddress)
            .append(cptAddress)
            .append(specificAddress)
            .append(evidenceAddress);
        return DataToolUtils.getHash(address.toString());
    }

    /**
     * todo 设置String groupId后是否影响hash
     */
    public static String getHashForShare(String groupId, String evidenceAddress) {
        StringBuffer address = new StringBuffer();
        address.append("share").append(groupId).append(evidenceAddress);
        return DataToolUtils.getHash(address.toString());
    }
}
