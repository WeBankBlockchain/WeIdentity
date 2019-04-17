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

package com.webank.weid.protocol.base;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.service.impl.CredentialPojoServiceImpl;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.WeIdUtils;

/**
 * Created by Junqi Zhang on 2019/4/4.
 */
@Data
@EqualsAndHashCode
public class PresentationE implements JsonSerialize {
    
    private static final Logger logger = LoggerFactory.getLogger(PresentationE.class);
    
    private static CredentialPojoService service = new CredentialPojoServiceImpl();
    
    /**
     * Required: The context field.
     */
    private List<String> context = new ArrayList<String>();

    private List<String> type = new ArrayList<String>();

    private List<CredentialPojoWrapper> credentialList;

    private Map<String, String> proof;
    
    public String toJson() {
        return JsonUtil.objToJsonStrWithNoPretty(this);
    }

    public PresentationE create(
        List<CredentialPojoWrapper> credentialList,
        PolicyAndChellenge policyAndChellenge,
        WeIdAuthentication weIdAuthentication) {
        
        try {
            // 检查输入数据完整性
            ErrorCode errorCode = 
                validateCreateArgs(credentialList, policyAndChellenge, weIdAuthentication);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "check input error:{}-{}",
                    errorCode.getCode(),
                    errorCode.getCodeDesc()
                );
                return null;
            }
            // 处理proof数据
            processProof(policyAndChellenge.getChallenge(), weIdAuthentication);
            // 处理credentialList数据
            errorCode = processCredentialList(credentialList, policyAndChellenge.getPresentationPolicyE());
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "process credentialList error:{}-{}",
                    errorCode.getCode(),
                    errorCode.getCodeDesc()
                );
                return null;
            }
            context.add(CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT);
            type.add("VerifiableCredential");
            return this;
        } catch (Exception e) {
            logger.error("create PresentationE error", e);
            return null;
        } 
    }
    
    private ErrorCode validateCreateArgs(
        List<CredentialPojoWrapper> credentialList,
        PolicyAndChellenge policyAndChellenge,
        WeIdAuthentication weIdAuthentication) {
        
        if (credentialList == null || policyAndChellenge == null || weIdAuthentication ==null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        Challenge challenge = policyAndChellenge.getChallenge();
        if (challenge == null 
            || StringUtils.isBlank(challenge.getWeId())
            || StringUtils.isBlank(challenge.getNonce())
            || challenge.getChallegeId() == null
            || challenge.getVersion() == null) {
            return ErrorCode.CHALLENGE_IS_INVALID;
        }
        if (weIdAuthentication.getWeIdPrivateKey() == null 
            || !WeIdUtils.validatePrivateKeyWeIdMatches(
               weIdAuthentication.getWeIdPrivateKey(), weIdAuthentication.getWeId())) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }
        if (!challenge.getWeId().equals(weIdAuthentication.getWeId())) {
            return ErrorCode.CHALLENGE_WEID_MISMATCH;
        }
        return validateClaimPolicy(credentialList, policyAndChellenge.getPresentationPolicyE());
    }
    
    private ErrorCode validateClaimPolicy(
        List<CredentialPojoWrapper> credentialList,
        PresentationPolicyE presentationPolicyE) {
        if (presentationPolicyE == null || presentationPolicyE.getPolicy() == null) {
            return ErrorCode.PRESENTATION_POLICY_INVALID;
        }
        Map<Integer, ClaimPolicy> claimPolicyMap = presentationPolicyE.getPolicy();
        for (CredentialPojoWrapper credentialPojoWrapper : credentialList) {
            if (credentialPojoWrapper.getCredentialPojo() == null) {
                return ErrorCode.CHALLENGE_IS_INVALID;
            }
            ClaimPolicy claimPolicy = 
                    claimPolicyMap.get(credentialPojoWrapper.getCredentialPojo().getCptId());
            if (claimPolicy == null) {
                return ErrorCode.CLAIM_POLICY_NOT_EXISTS;
            }
        }
        return ErrorCode.SUCCESS;
    }
    
    private ErrorCode processCredentialList(
        List<CredentialPojoWrapper> credentialList, 
        PresentationPolicyE presentationPolicy) {
        
        this.credentialList = new ArrayList<CredentialPojoWrapper>();
        // 获取ClaimPolicyMap
        Map<Integer, ClaimPolicy> claimPolicyMap = presentationPolicy.getPolicy();
        // 遍历所有原始证书
        for (CredentialPojoWrapper credentialPojoWrapper : credentialList) {
            // 根据原始证书获取对应的 claimPolicy
            ClaimPolicy claimPolicy = 
                claimPolicyMap.get(credentialPojoWrapper.getCredentialPojo().getCptId());
            // 根据原始证书和claimPolicy去创建选择性披露凭证
            ResponseData<CredentialPojoWrapper>  res =
                service.createSelectiveCredential(credentialPojoWrapper, claimPolicy);
            if (res.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                this.credentialList = null;
                return ErrorCode.getTypeByErrorCode(res.getErrorCode().intValue());
            }
            this.credentialList.add(res.getResult());
        }
        return ErrorCode.SUCCESS;
    }
    
    private void processProof(Challenge challenge, WeIdAuthentication weIdAuthentication) {
       String challengeString = challenge.toString();
       Sign.SignatureData sigData = 
           DataToolUtils.signMessage(
               challengeString,
               weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
           );
       proof = new HashMap<String, String>();
       String signature =
           new String(
               DataToolUtils.base64Encode(DataToolUtils.simpleSignatureSerialization(sigData)),
               StandardCharsets.UTF_8
           );
       proof.put("signature", signature);
       proof.put("type", "RsaSingature2018");
       proof.put("nonce", challenge.getNonce());
       proof.put("challegeId", challenge.getChallegeId().toString());
       proof.put("createor", challenge.getWeId());
       proof.put("created", DateUtils.getTimestamp(new Date()));
       proof.put("version", challenge.getVersion().toString());
    }
}
