

package com.webank.weid.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.WeIdBaseException;
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
    private static final Validator validator = new Validator();
    // 配置topic
    public static String topic;
    
    // Note that all keys are appended with a colon ":" to support regex auto-loading

    @NotNull(message = "the bcos.version is undefined")
    @NotEmpty(message = "the value of bcos.version is null")
    @MatchPattern(pattern = "[2,3]+(\\.\\w*)?", message = "the value of bcos.version is invalid, only support 2,3")
    private String version;
    
    @NotNull(message = "the nodes is undefined")
    @NotEmpty(message = "the value of nodes is null")
    private String nodes;
    
    private String weIdAddress;
    private String cptAddress;
    private String issuerAddress;
    private String evidenceAddress;
    private String specificIssuerAddress;
    private String chainId;
    
    @NotNull(message = "the web3sdk.timeout is undefined")
    @NotEmpty(message = "the value of web3sdk.timeout is null")
    @MatchPattern(pattern = "\\d+", message = "the value of web3sdk.timeout is invalid")
    @Min(value = 5, message = "the the minimum value of web3sdk.timeout is 5")
    private String web3sdkTimeout;
    
    @NotNull(message = "the web3sdk.core-pool-size is undefined")
    @NotEmpty(message = "the value of web3sdk.core-pool-size is null")
    @MatchPattern(pattern = "\\d+", message = "the value of web3sdk.core-pool-size is invalid")
    private String web3sdkCorePoolSize;
    
    @NotNull(message = "the web3sdk.max-pool-size is undefined")
    @NotEmpty(message = "the value of web3sdk.max-pool-size is null")
    @MatchPattern(pattern = "\\d+", message = "the value of web3sdk.max-pool-size is invalid")
    private String web3sdkMaxPoolSize;
    
    @NotNull(message = "the web3sdk.queue-capacity is undefined")
    @NotEmpty(message = "the value of web3sdk.queue-capacity is null")
    @MatchPattern(pattern = "\\d+", message = "the value of web3sdk.queue-capacity is invalid")
    private String web3sdkQueueSize;
    
    @NotNull(message = "the web3sdk.keep-alive-seconds is undefined")
    @NotEmpty(message = "the value of web3sdk.keep-alive-seconds is null")
    @MatchPattern(pattern = "\\d+", message = "the value of web3sdk.keep-alive-seconds is invalid")
    private String web3sdkKeepAliveSeconds;

    /**
     * 3.0后groupId支持为string
     */
    @NotNull(message = "the group.id is undefined")
    @NotEmpty(message = "the value of group.id is null")
    private String groupId;

    @NotNull(message = "the sdk.sm-crypto is undefined")
    @NotEmpty(message = "the value of sdk.sm-crypto is null")
    private String sdkSMCrypto;

    @NotNull(message = "the sdk.cert-path is undefined")
    @NotEmpty(message = "the value of sdk.cert-path is null")
    private String sdkCertPath;

    @NotNull(message = "the amop.pub-path is undefined")
    @NotEmpty(message = "the value of amop.pub-path is null")
    private String amopPubPath;

    @NotNull(message = "the amop.pri-path is undefined")
    @NotEmpty(message = "the value of amop.pri-path is null")
    private String amopPriPath;

    @NotNull(message = "the amop.p12-password is undefined")
    @NotEmpty(message = "the value of amop.p12-password is null")
    private String amopP12Password;

    @NotNull(message = "the blockchain.orgid is undefined")
    @NotEmpty(message = "the value of blockchain.orgid is null")
    private String currentOrgId;
    
    @NotNull(message = "the amop.id is undefined")
    @NotEmpty(message = "the value of amop.id is null")
    private String amopId;
    
    private String cnsContractFollow;

    private String privateKey;

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
            sdkSMCrypto = PropertyUtils.getProperty("sdk.sm-crypto");
            sdkCertPath = PropertyUtils.getProperty("sdk.cert-path");
            amopPubPath = PropertyUtils.getProperty("amop.pub-path");
            amopPriPath = PropertyUtils.getProperty("amop.pri-path");
            amopP12Password = PropertyUtils.getProperty("amop.p12-password");
            currentOrgId = PropertyUtils.getProperty("blockchain.orgid");
            amopId = PropertyUtils.getProperty("amop.id");
            privateKey = PropertyUtils.getProperty("amop.privateKey");
            cnsContractFollow = PropertyUtils.getProperty("cns.contract.follow");
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during loading Fisco-Bcos properties: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * check the attribute value for FiscoConfig.
     */
    public void check() {
        List<ConstraintViolation> constraintViolationSet = validator.validate(this);
        List<String> messageList = new ArrayList<String>();
        constraintViolationSet.forEach(violationInfo -> {
            messageList.add(violationInfo.getMessage());
        });
        if (messageList.size() > 0) {
            logger.error("[FiscoConfig.check] message: {}", messageList.get(0));
            throw new WeIdBaseException(messageList.get(0));
        }
    }
    
    /**
     * check the contract address.
     * @return 返回地址是否存在
     */
    public boolean checkAddress() {
        return StringUtils.isNotBlank(this.getWeIdAddress()) 
            && StringUtils.isNotBlank(this.getIssuerAddress())
            && StringUtils.isNotBlank(this.getSpecificIssuerAddress()) 
            && StringUtils.isNotBlank(this.getEvidenceAddress()) 
            && StringUtils.isNotBlank(this.getCptAddress());
    }
}