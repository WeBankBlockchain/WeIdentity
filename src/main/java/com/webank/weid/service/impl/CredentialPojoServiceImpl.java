package com.webank.weid.service.impl;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoWrapper;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.CredentialPojoUtils;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年4月17日
 */
@Component
public class CredentialPojoServiceImpl extends BaseService implements CredentialPojoService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialPojoServiceImpl.class);

    private static WeIdService weIdService = new WeIdServiceImpl();

    private static void generateSalt(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                generateSalt((HashMap) value);
            } else {
                String salt = DataToolUtils.getRandomSalt();
                entry.setValue(salt);
            }
        }
    }

    /**
     * 校验claim、salt和disclosureMap的格式是否一致
     */
    private static boolean validCredentialMapArgs(Map<String, Object> claim,
        Map<String, Object> salt, Map<String, Object> disclosureMap) {

        //检查是否为空
        if (claim == null || salt == null || disclosureMap == null) {
            return false;
        }

        //检查每个map里的key个数是否相同
        Set<String> claimKeys = claim.keySet();
        Set<String> saltKeys = salt.keySet();
        Set<String> disclosureKeys = disclosureMap.keySet();

        if (claimKeys.size() != saltKeys.size() || saltKeys.size() != disclosureKeys.size()) {
            return false;
        }

        //检查key值是否一致
        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            Object saltV = salt.get(k);
            Object disclosureV = disclosureMap.get(k);
            if (!salt.containsKey(k) || !disclosureMap.containsKey(k)) {
                return false;
            }
            if (v instanceof Map) {
                //递归检查
                if (!validCredentialMapArgs((HashMap) v, (HashMap) saltV, (HashMap) disclosureV)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void addSelectSalt(
        Map<String, Object> disclosureMap,
        Map<String, Object> saltMap,
        Map<String, Object> claim) {
        for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
            String claimKey = entry.getKey();
            Object value = entry.getValue();
            Object saltV = saltMap.get(claimKey);
            Object claimV = claim.get(claimKey);

            if (value instanceof Map) {
                addSelectSalt((HashMap) value, (HashMap) saltV, (HashMap) claimV);
            } else {
                if (((Integer) value)
                    .equals(CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus())) {
                    saltMap.put(claimKey, CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus());
                    String hash = CredentialPojoUtils
                        .getFieldSaltHash(String.valueOf(value), String.valueOf(saltV));
                    claim.put(claimKey, hash);
                }
            }
        }
    }

    private static ErrorCode verifyContent(CredentialPojoWrapper credentialWrapper,
        String publicKey) {
        Map<String, Object> salt = credentialWrapper.getSalt();
        CredentialPojo credentialPojo = credentialWrapper.getCredentialPojo();
//		Map<String, Object>claim = credentialPojo.getClaim();
//		String claimHash = CredentialPojoUtils.getClaimHash(credentialPojo, salt, null);
        String rawData = CredentialPojoUtils
            .getCredentialThumbprintWithoutSig(credentialPojo, salt, null);
        String issuerWeid = credentialPojo.getIssuer();
        if (StringUtils.isEmpty(publicKey)) {
            // Fetch public key from chain
            ResponseData<WeIdDocument> innerResponseData =
                weIdService.getWeIdDocument(issuerWeid);
            if (innerResponseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "Error occurred when fetching WeIdentity DID document for: {}, msg: {}",
                    issuerWeid, innerResponseData.getErrorMessage());
                return ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL;
            } else {
                WeIdDocument weIdDocument = innerResponseData.getResult();
                return DataToolUtils
                    .verifySignatureFromWeId(rawData, credentialPojo.getSignature(), weIdDocument);
            }
        } else {
            boolean result;
            try {
                result = DataToolUtils
                    .verifySignature(rawData, credentialPojo.getSignature(),
                        new BigInteger(publicKey));
            } catch (SignatureException e) {
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            }
            if (!result) {
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            }
            return ErrorCode.SUCCESS;
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#createCredential(com.webank.weid.protocol.request.CreateCredentialPojoArgs)
     */
    @Override
    public ResponseData<CredentialPojoWrapper> createCredential(CreateCredentialPojoArgs args) {

        CredentialPojoWrapper credentialPojoWrapper = new CredentialPojoWrapper();
        try {

            Object claimObject = args.getClaim();
            CredentialPojo result = new CredentialPojo();
            String context = CredentialUtils.getDefaultCredentialContext();
            result.setContext(context);
            result.setId(UUID.randomUUID().toString());
            result.setCptId(args.getCptId());
            result.setIssuer(args.getIssuer());
            result.setIssuranceDate(DateUtils.getCurrentTimeStamp());
            result.setExpirationDate(args.getExpirationDate());
            String className = "Cpt" + args.getCptId();
            if (!StringUtils.equals(className, claimObject.getClass().getSimpleName())) {
                logger.error(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCodeDesc());
                return new ResponseData<CredentialPojoWrapper>(null,
                    ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL);
            }

            String claimStr = DataToolUtils.serialize(args.getClaim());
            HashMap<String, Object> claimMap = DataToolUtils.deserialize(claimStr, HashMap.class);
//            result.setClaim(args.getClaim());
            result.setClaim(claimMap);

            Map<String, Object> saltMap = DataToolUtils.clone(claimMap);
            generateSalt(saltMap);
            credentialPojoWrapper.setSalt(saltMap);
            String rawData = CredentialPojoUtils
                .getCredentialThumbprintWithoutSig(result, saltMap, null);
            String privateKey = args.getWeIdPrivateKey().getPrivateKey();
            String signature = DataToolUtils.sign(rawData, privateKey);
            result.setSignature(signature);

            credentialPojoWrapper.setCredentialPojo(result);
            ResponseData<CredentialPojoWrapper> responseData = new ResponseData<>(
                credentialPojoWrapper,
                ErrorCode.SUCCESS
            );

            return responseData;
        } catch (Exception e) {
            logger.error("Generate Credential failed due to system error. ", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#createSelectiveCredential(com.webank.weid.protocol.base.CredentialPojoWrapper, com.webank.weid.protocol.base.ClaimPolicy)
     */
    @Override
    public ResponseData<CredentialPojoWrapper> createSelectiveCredential(
        CredentialPojoWrapper credentialPojoWrapper,
        ClaimPolicy claimPolicy) {

        if (credentialPojoWrapper == null) {
            logger.error("[createSelectiveCredential] credentialPojoWrapper is null.");
            return new ResponseData<CredentialPojoWrapper>(null, ErrorCode.CREDENTIAL_IS_NILL);
        }
        if (claimPolicy == null) {
            logger.error("[createSelectiveCredential] claimPolicy is null.");
            return new ResponseData<CredentialPojoWrapper>(null,
                ErrorCode.CREDENTIAL_CLAIM_POLICY_NOT_EXIST);
        }
        CredentialPojo credentialPojo = credentialPojoWrapper.getCredentialPojo();
        String disclosure = claimPolicy.getFieldsToBeDisclosed();
        Map<String, Object> saltMap = credentialPojoWrapper.getSalt();
        Map<String, Object> claim = credentialPojo.getClaim();

        Map<String, Object> disclosureMap = DataToolUtils.deserialize(disclosure, HashMap.class);

        if (!validCredentialMapArgs(claim, saltMap, disclosureMap)) {
            return new ResponseData<CredentialPojoWrapper>(null,
                ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM);
        }
        addSelectSalt(disclosureMap, saltMap, claim);
        credentialPojoWrapper.setSalt(saltMap);

        ResponseData<CredentialPojoWrapper> response = new ResponseData<CredentialPojoWrapper>();
        response.setResult(credentialPojoWrapper);
        response.setErrorCode(ErrorCode.SUCCESS);
        return response;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#verify(java.lang.String, com.webank.weid.protocol.base.CredentialPojoWrapper)
     */
    @Override
    public ResponseData<Boolean> verify(CredentialPojoWrapper credentialWrapper,
        String issuerWeId) {

        String issuerId = credentialWrapper.getCredentialPojo().getIssuer();
        if (!StringUtils.equals(issuerWeId, issuerId)) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_ISSUER_MISMATCH);
        }
        ErrorCode errorCode = verifyContent(credentialWrapper, null);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<Boolean>(false, errorCode);
        }
        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#verify(java.lang.String, com.webank.weid.protocol.base.PresentationPolicyE, com.webank.weid.protocol.base.Challenge, com.webank.weid.protocol.base.PresentationE)
     */
    @Override
    public ResponseData<Boolean> verify(String presenterWeId,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge, PresentationE presentationE) {
        if (StringUtils.isBlank(presenterWeId)
            || challenge == null
            || StringUtils.isBlank(challenge.getNonce())
            || !CredentialPojoUtils.checkPresentationPolicyEValid(presentationPolicyE)) {
            return new ResponseData<Boolean>(false, ErrorCode.ILLEGAL_INPUT);
        }

        ErrorCode checkPresentationE = CredentialPojoUtils.checkPresentationEValid(presentationE);
        if (checkPresentationE.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<Boolean>(false, checkPresentationE);
        }

        //verify presenterWeId
        if (StringUtils.isNotBlank(challenge.getWeId())
            && !presenterWeId.equals(challenge.getWeId())) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_PRESENTERWEID_NOTMATCH);
        }
        
        //verify challenge
        if (!challenge.getNonce().equals(presentationE.getNonce())) {
            return new ResponseData<Boolean>(false, ErrorCode.PRESENTATION_CHALLENGE_NONCE_MISMATCH);
        }
        
        //verify Signature of PresentationE
        WeIdDocument weIdDocument = weIdService.getWeIdDocument(presenterWeId).getResult();
        String signature = presentationE.getSignature();
        //remove signatureValue
        presentationE.getProof().remove(ParamKeyConstant.SIGNATUREVALUE);
        ErrorCode errorCode =
            DataToolUtils.verifySignatureFromWeId(presentationE.toRawData(), signature, weIdDocument);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[verify] verify challenge {} failed.", challenge);
            return new ResponseData<Boolean>(false, errorCode);
        }

        //verify cptId of presentationE
        List<CredentialPojoWrapper> credentialPojoWrapperlist = presentationE.getCredentialList();
        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ErrorCode verifyCptIdresult =
            this.verifyCptId(policyMap, credentialPojoWrapperlist);
        if (verifyCptIdresult.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[verify] verify cptId failed.");
            return new ResponseData<Boolean>(false, verifyCptIdresult);
        }

        for (CredentialPojoWrapper credentialPojoWrapper : credentialPojoWrapperlist) {
            //verify policy
            Integer cptId = credentialPojoWrapper.getCredentialPojo().getCptId();
            ClaimPolicy claimPolicy = policyMap.get(cptId);
            ErrorCode verifypolicyResult = this.verifyPolicy(credentialPojoWrapper, claimPolicy);
            if (verifypolicyResult.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[verify] verify policy {} failed.", policyMap);
                return new ResponseData<Boolean>(false, verifypolicyResult);
            }

            //verify credential
            ErrorCode verifyCredentialResult = verifyContent(credentialPojoWrapper, null);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "[verify] verify credential {} failed.", credentialPojoWrapper);
                return new ResponseData<Boolean>(false, verifyCredentialResult);
            }
        }

        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#verify(com.webank.weid.protocol.base.CredentialPojoWrapper, com.webank.weid.protocol.base.WeIdPublicKey)
     */
    @Override
    public ResponseData<Boolean> verify(CredentialPojoWrapper credentialWrapper,
        WeIdPublicKey issuerPublicKey) {

        String publicKey = issuerPublicKey.getPublicKey();
        if (StringUtils.isEmpty(publicKey)) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_PUBLIC_KEY_NOT_EXISTS);
        }
        ErrorCode errorCode = verifyContent(credentialWrapper, publicKey);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<Boolean>(false, errorCode);
        }
        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    private ErrorCode verifyCptId(Map<Integer, ClaimPolicy> policyMap,
        List<CredentialPojoWrapper> credentialPojoWrapperList) {
        if (policyMap.size() > credentialPojoWrapperList.size()) {
            return ErrorCode.CREDENTIAL_CPTID_NOTMATCH;
        } else {
            List<Integer> cptIdList = credentialPojoWrapperList.stream().map(
                cpwl -> cpwl.getCredentialPojo().getCptId()).collect(Collectors.toList());
            if (cptIdList == null || cptIdList.isEmpty()) {
                return ErrorCode.CREDENTIAL_CPTID_NOTMATCH;
            }
            if (!cptIdList.containsAll(policyMap.keySet())) {
                return ErrorCode.CREDENTIAL_CPTID_NOTMATCH;
            }
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyDisclosureAndSalt(Map<String, Object> disclosureMap,
        Map<String, Object> saltMap) {
        for (String disclosureK : disclosureMap.keySet()) {
            Object disclosureV = disclosureMap.get(disclosureK);
            Object saltV = saltMap.get(disclosureK);
            if (disclosureV instanceof Map) {
                this.verifyDisclosureAndSalt((HashMap) disclosureV, (HashMap) saltV);
            } else {
                if (!disclosureV.equals(
                    CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus())
                    && !disclosureV.equals(
                    CredentialFieldDisclosureValue.DISCLOSED.getStatus())) {
                    logger.error("[verify] policy disclosureValue {} illegal.", disclosureMap);
                    return ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL;
                }

                if (StringUtils.isEmpty(String.valueOf(saltV))) {
                    return ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL;
                }

                if ((disclosureV.equals(
                    CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus())
                    && String.valueOf(saltV).length() > 1)
                    || (disclosureV.equals(
                    CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus())
                    && !saltV.equals(
                    CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus()))) {
                    return ErrorCode.CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE;
                }

                if (disclosureV.equals(CredentialFieldDisclosureValue.DISCLOSED.getStatus())
                    && String.valueOf(saltV).length() <= 1) {
                    return ErrorCode.CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE;
                }
            }
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyPolicy(CredentialPojoWrapper credentialPojoWrapper,
        ClaimPolicy claimPolicy) {
        Map<String, Object> saltMap = credentialPojoWrapper.getSalt();
        String disclosure = claimPolicy.getFieldsToBeDisclosed();
        Map<String, Object> disclosureMap = DataToolUtils.deserialize(disclosure, HashMap.class);
        return this.verifyDisclosureAndSalt(disclosureMap, saltMap);
    }

    @Override
    public ResponseData<PresentationE> createPresentation(
        List<CredentialPojoWrapper> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        WeIdAuthentication weIdAuthentication) {
        
        PresentationE presentation = new PresentationE();
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
                return new ResponseData<PresentationE>(null, errorCode);
            }
            // 处理credentialList数据
            errorCode = processCredentialList(credentialList, presentationPolicyE, presentation);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "process credentialList error:{}-{}",
                    errorCode.getCode(),
                    errorCode.getCodeDesc()
                );
                return new ResponseData<PresentationE>(null, errorCode);
            }
            presentation.getContext().add(CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT);
            presentation.getType().add(WeIdConstant.DEFAULT_PRESENTATION_TYPE);
            // 处理proof数据
            generateProof(challenge, weIdAuthentication, presentation);
            return new ResponseData<PresentationE>(presentation, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("create PresentationE error", e);
            return new ResponseData<PresentationE>(null, ErrorCode.UNKNOW_ERROR);
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
        List<Integer> cptIdList = credentialList.stream().map(
                cpwl -> cpwl.getCredentialPojo().getCptId()).collect(Collectors.toList()); 
        Set<Integer> claimPolicyCptSet = presentationPolicyE.getPolicy().keySet();
        if (!cptIdList.containsAll(claimPolicyCptSet)) {
            return ErrorCode.PRESENTATION_CREDENTIALLIST_MISMATCH_CLAIM_POLICY;
        }
        return ErrorCode.SUCCESS;
    }
    
    private ErrorCode processCredentialList(
        List<CredentialPojoWrapper> credentialList, 
        PresentationPolicyE presentationPolicy,
        PresentationE presentation) {
        
        List<CredentialPojoWrapper> newcredentialList = new ArrayList<>();
        // 获取ClaimPolicyMap
        Map<Integer, ClaimPolicy> claimPolicyMap = presentationPolicy.getPolicy();
        // 遍历所有原始证书
        for (CredentialPojoWrapper credentialPojoWrapper : credentialList) {
            // 根据原始证书获取对应的 claimPolicy
            ClaimPolicy claimPolicy = 
                claimPolicyMap.get(credentialPojoWrapper.getCredentialPojo().getCptId());
            if (claimPolicy == null) {
                continue;
            }
            // 根据原始证书和claimPolicy去创建选择性披露凭证
            ResponseData<CredentialPojoWrapper>  res =
                this.createSelectiveCredential(credentialPojoWrapper, claimPolicy);
            if (res.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return ErrorCode.getTypeByErrorCode(res.getErrorCode().intValue());
            }
            newcredentialList.add(res.getResult());
        }
        presentation.setCredentialList(newcredentialList);
        return ErrorCode.SUCCESS;
    }
    
    private void generateProof(
        Challenge challenge, 
        WeIdAuthentication weIdAuthentication,
        PresentationE presentation) {
       
       Map<String, String> proof = new HashMap<String, String>();
       proof.put(ParamKeyConstant.TYPE, "EcdsaSignature");
       proof.put(ParamKeyConstant.CREATED, DateUtils.getTimestamp(new Date()));
       proof.put(ParamKeyConstant.VERIFICATION_METHOD, weIdAuthentication.getWeIdPublicKeyId());
       proof.put(ParamKeyConstant.NONCE, challenge.getNonce());
       presentation.setProof(proof);
       String signature = 
           DataToolUtils.sign(
               presentation.toRawData(), 
               weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
           );
       proof.put(ParamKeyConstant.SIGNATUREVALUE, signature);
    }
}
