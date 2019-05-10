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

import com.webank.weid.protocol.amop.JsonSerializer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.service.impl.CredentialPojoServiceImpl;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Created by Junqi Zhang on 2019/4/4.
 */
@Data
@EqualsAndHashCode
public class PresentationE implements JsonSerializer {
    
    private static final Logger logger = LoggerFactory.getLogger(PresentationE.class);
    
    private static CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
    
    /**
     * Required: The context field.
     */
    private List<String> context = new ArrayList<String>();

    private List<String> type = new ArrayList<String>();

    private List<CredentialPojoWrapper> credentialList;

    private Map<String, String> proof;

    public PresentationE create(
        List<CredentialPojoWrapper> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        WeIdAuthentication weIdAuthentication) {
        
        try {
            // 检查输入数据完整性
            ErrorCode errorCode = 
                validateCreateArgs(credentialList, presentationPolicyE, challenge, weIdAuthentication);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "check input error:{}-{}",
                    errorCode.getCode(),
                    errorCode.getCodeDesc()
                );
                return null;
            }
            // 处理proof数据
            generateProof(challenge, weIdAuthentication);
            // 处理credentialList数据
            errorCode = processCredentialList(credentialList, presentationPolicyE);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "process credentialList error:{}-{}",
                    errorCode.getCode(),
                    errorCode.getCodeDesc()
                );
                return null;
            }
            context.add(CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT);
            type.add(CredentialConstant.DEFAULT_CREDENTIAL_TYPE);
            return this;
        } catch (Exception e) {
            logger.error("create PresentationE error", e);
            return null;
        } 
    }
    
    private ErrorCode validateCreateArgs(
        List<CredentialPojoWrapper> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        WeIdAuthentication weIdAuthentication) {
        
        if (challenge == null || weIdAuthentication == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (StringUtils.isBlank(challenge.getNonce())
            || challenge.getVersion() == null) {
            return ErrorCode.PRESENTATION_CHALLENGE_INVALID;
        }
        if (weIdAuthentication.getWeIdPrivateKey() == null 
            || !WeIdUtils.validatePrivateKeyWeIdMatches(
               weIdAuthentication.getWeIdPrivateKey(), weIdAuthentication.getWeId())) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }
        if (!StringUtils.isBlank(challenge.getWeId()) 
            && !challenge.getWeId().equals(weIdAuthentication.getWeId())) {
            return ErrorCode.PRESENTATION_CHALLENGE_WEID_MISMATCH;
        }
        if (StringUtils.isBlank(weIdAuthentication.getWeIdPublicKeyId())) {
            return ErrorCode.PRESENTATION_WEID_PUBLICKEY_ID_INVALID;
        }
        return validateClaimPolicy(credentialList, presentationPolicyE);
    }
    
    private ErrorCode validateClaimPolicy(
        List<CredentialPojoWrapper> credentialList,
        PresentationPolicyE presentationPolicyE) {
        if (credentialList == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (presentationPolicyE == null || presentationPolicyE.getPolicy() == null) {
            return ErrorCode.PRESENTATION_POLICY_INVALID;
        }
        Set<Integer> cptSet = new HashSet<>();
        for (CredentialPojoWrapper credentialPojoWrapper : credentialList) {
            cptSet.add(credentialPojoWrapper.getCredentialPojo().getCptId());
        } 
        Set<Integer> claimPolicyCptSet = presentationPolicyE.getPolicy().keySet();
        if (!cptSet.containsAll(claimPolicyCptSet)) {
            return ErrorCode.PRESENTATION_CREDENTIALLIST_MISMATCH_CLAIM_POLICY;
        }
        return ErrorCode.SUCCESS;
    }
    
    private ErrorCode processCredentialList(
        List<CredentialPojoWrapper> credentialList, 
        PresentationPolicyE presentationPolicy) {
        
        this.credentialList = new ArrayList<>();
        // 获取ClaimPolicyMap
        Map<Integer, ClaimPolicy> claimPolicyMap = presentationPolicy.getPolicy();
        // 遍历所有原始证书
        for (CredentialPojoWrapper credentialPojoWrapper : credentialList) {
            // 根据原始证书获取对应的 claimPolicy
            ClaimPolicy claimPolicy = 
                claimPolicyMap.get(credentialPojoWrapper.getCredentialPojo().getCptId());
            if (claimPolicy == null) {
                this.credentialList.add(credentialPojoWrapper);
                continue;
            }
            // 根据原始证书和claimPolicy去创建选择性披露凭证
            ResponseData<CredentialPojoWrapper>  res =
                credentialPojoService.createSelectiveCredential(credentialPojoWrapper, claimPolicy);
            if (res.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                this.credentialList = null;
                return ErrorCode.getTypeByErrorCode(res.getErrorCode().intValue());
            }
            this.credentialList.add(res.getResult());
        }
        return ErrorCode.SUCCESS;
    }
    
    private void generateProof(Challenge challenge, WeIdAuthentication weIdAuthentication) {
        
       String signature = 
           DataToolUtils.sign(
               challenge.toRawData(), 
               weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
           );
       proof = new HashMap<String, String>();
       proof.put(ParamKeyConstant.TYPE, WeIdConstant.DEFAULT_SIGN_TYPE);
       proof.put(ParamKeyConstant.CREATED, DateUtils.getTimestamp(new Date()));
       proof.put(ParamKeyConstant.VERIFICATION_METHOD, weIdAuthentication.getWeIdPublicKeyId());
       proof.put(ParamKeyConstant.NONCE, challenge.getNonce());
       proof.put(ParamKeyConstant.SIGNATUREVALUE, signature);
    }
    
    /**
     * 获取公钥Id，用于验证的时候识别publicKey.
     * @return
     */
    public String getVerificationMethod(){
        return getValueFromProof(ParamKeyConstant.VERIFICATION_METHOD);
    }
    
    /**
     * 获取签名值Signature.
     * @return 返回签名字符串Signature.
     */
    public String getSignature() {
        return getValueFromProof(ParamKeyConstant.SIGNATUREVALUE);
    }
    
    /**
     * 获取challenge随机值.
     * @return 返回challenge随机值.
     */
    public String getNonce() {
        return getValueFromProof(ParamKeyConstant.NONCE);
    }
    
    private String getValueFromProof(String key){
        if (proof != null) {
            return proof.get(key);
        }
        return StringUtils.EMPTY; 
    }
}
