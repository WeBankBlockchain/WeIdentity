package com.webank.weid.contract.deploy.v2;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.precompile.cns.CnsService;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.AddressProcess;
import com.webank.weid.contract.v2.DataBucket;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.CnsResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.engine.DataBucketServiceEngine;
import com.webank.weid.service.impl.engine.fiscov2.DataBucketServiceEngineV2;
import com.webank.weid.util.DataToolUtils;

public class RegisterAddressV2 extends AddressProcess {
    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegisterAddressV2.class);
    
    public static DataBucketServiceEngine bucket;
    
    private static Credentials credentials;
    
    private static Credentials getCredentials() {
        if (credentials == null) {
            credentials = GenCredential.create();
        }
        return credentials;
    }
    
    private static DataBucketServiceEngine getBucket() {
        if (bucket == null) {
            bucket = new DataBucketServiceEngineV2();
        }
        return bucket;
    }
    
    /**
     * 根据私钥将合约地址注册到cns中.
     * @param privateKey 私钥信息
     */
    public static void registerAddress(WeIdPrivateKey privateKey) {
        //先進行cns注冊
        registerBucketToCns();
        //获取地址hash
        ContractConfig contractConfig = getContractConfig();
        String hash = getHashByAddress(contractConfig);
        logger.info("[registerAddress] contract hash = {}.", hash);
        registerAddress(
            hash, 
            contractConfig.getWeIdAddress(), 
            WeIdConstant.CNS_WEID_ADDRESS, 
            privateKey
        );
        
        registerAddress(
            hash, 
            contractConfig.getIssuerAddress(),
            WeIdConstant.CNS_AUTH_ADDRESS, 
            privateKey
        );
        
        registerAddress(
            hash,
            contractConfig.getSpecificIssuerAddress(), 
            WeIdConstant.CNS_SPECIFIC_ADDRESS,
            privateKey
        );
        
        registerAddress(
            hash, 
            contractConfig.getEvidenceAddress(), 
            WeIdConstant.CNS_EVIDENCE_ADDRESS, 
            privateKey
        );
        
        registerAddress(
            hash, 
            contractConfig.getCptAddress(), 
            WeIdConstant.CNS_CPT_ADDRESS, 
            privateKey
        );
        writeAddressToFile(hash, "hash");
    }
    
    /**
     * 根据hash，合约地址，key进行地址注册到cns中.
     * @param hash 合约地址的hash值
     * @param address 合约地址
     * @param key 合约地址的key
     * @param privateKey 私钥信息
     */
    public static void registerAddress(
        String hash, 
        String address, 
        String key, 
        WeIdPrivateKey privateKey) {
        
        logger.info("[registerAddress] begin register address = {}.", address);
        if (StringUtils.isBlank(address)) {
            logger.error("[registerAddress] can not find the address.");
            throw new WeIdBaseException("register address fail.");
        }
        ResponseData<Boolean> result = getBucket().put(hash, key, address, privateKey);
        if (!result.getResult()) {
            logger.error("[registerAddress] register address fail, please check the log.");
            throw new WeIdBaseException("register address fail.");
        }
        logger.info("[registerAddress] register address successfully.");
    }
    
    /**
     * 注册全局bucket地址.
     * @throws WeIdBaseException 注册过程中的异常
     */
    public static void registerBucketToCns() throws WeIdBaseException {
        logger.info("[registerBucketToCns] begin register bucket to CNS.");
        String bucketAddr = BaseService.getBucketAddress();
        //如果地址是为空则说明是首次注册
        if (StringUtils.isNotBlank(bucketAddr)) {
            logger.info("[registerBucketToCns] the bucket is registed, it is no need to regist.");
            return;
        }
        try {
            //先进行地址部署
            bucketAddr = deployBucket();
            String resultJson = new CnsService((Web3j)BaseService.getWeb3j(), getCredentials())
                .registerCns(WeServer.CNS_NAME, WeServer.CNS_VERSION, bucketAddr, DataBucket.ABI);
            CnsResponse result = DataToolUtils.deserialize(resultJson, CnsResponse.class);
            if (result.getCode() != 0) {
                throw new WeIdBaseException(result.getCode() + "-" + result.getMsg());
            }
            logger.info("[registerBucketToCns] the bucket register successfully.");
        } catch (WeIdBaseException e) {
            logger.error("[registerBucketToCns] the bucket register fail,{}.", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[registerBucketToCns] register bucket has error.", e);
            throw new WeIdBaseException("register bucket has error.", e);
        }
    }
    
    private static String deployBucket() throws Exception {
        logger.info("[deployBucket] begin deploy bucket.");
        //先进行地址部署
        DataBucket dataBucket = DataBucket.deploy(
            (Web3j)BaseService.getWeb3j(),
            getCredentials(), 
            new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)).send();
        return dataBucket.getContractAddress();
    }
    
    private static ContractConfig getContractConfig() {
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
}
