package com.webank.weid.service.console;

import com.networknt.schema.ValidationMessage;
import com.webank.wedpr.selectivedisclosure.VerifierClient;
import com.webank.wedpr.selectivedisclosure.VerifierResult;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.CredentialType;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.cpt.Cpt101;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class CredentialServiceConsole {
    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(CredentialServiceConsole.class);
    private static final String NOT_DISCLOSED =
            CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus().toString();
    private static final String DISCLOSED =
            CredentialFieldDisclosureValue.DISCLOSED.getStatus().toString();
    private static final String EXISTED =
            CredentialFieldDisclosureValue.EXISTED.getStatus().toString();
    private static final WeIdServiceConsole weIdServiceConsole = new WeIdServiceConsole();

    //创建普通Credential
    public ResponseData<CredentialPojo> createCredential(CreateCredentialPojoArgs args) {

        try {
            ErrorCode innerResponseData =
                    CredentialPojoUtils.isCreateCredentialPojoArgsValid(args);
            if (ErrorCode.SUCCESS.getCode() != innerResponseData.getCode()) {
                logger.error("Create Credential Args illegal: {}",
                        innerResponseData.getCodeDesc());
                return new ResponseData<>(null, innerResponseData);
            }
            CredentialPojo result = new CredentialPojo();
            String context = CredentialUtils.getDefaultCredentialContext();
            result.setContext(context);
            if (StringUtils.isBlank(args.getId())) {
                result.setId(UUID.randomUUID().toString());
            } else {
                result.setId(args.getId());
            }
            result.setCptId(args.getCptId());
            Long issuanceDate = args.getIssuanceDate();
            if (issuanceDate == null) {
                result.setIssuanceDate(DateUtils.getNoMillisecondTimeStamp());
            } else {
                Long newIssuanceDate =
                        DateUtils.convertToNoMillisecondTimeStamp(args.getIssuanceDate());
                if (newIssuanceDate == null) {
                    logger.error("Create Credential Args illegal.");
                    return new ResponseData<>(null, ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL);
                } else {
                    result.setIssuanceDate(newIssuanceDate);
                }
            }
            // Comment these lines out since we now support multi-public-keys in WeID document
            // if (!WeIdUtils.validatePrivateKeyWeIdMatches(
            //     args.getWeIdAuthentication().getWeIdPrivateKey(),
            //     args.getIssuer())) {
            //     logger.error("Create Credential, private key does not match the current weid.");
            //     return new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            // }
            result.setIssuer(args.getIssuer());
            Long newExpirationDate =
                    DateUtils.convertToNoMillisecondTimeStamp(args.getExpirationDate());
            if (newExpirationDate == null) {
                logger.error("Create Credential Args illegal.");
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL);
            } else {
                result.setExpirationDate(newExpirationDate);
            }
            result.addType(CredentialConstant.DEFAULT_CREDENTIAL_TYPE);
            result.addType(args.getType().getName());

            Object claimObject = args.getClaim();
            String claimStr = null;
            if (!(claimObject instanceof String)) {
                claimStr = DataToolUtils.serialize(claimObject);
            } else {
                claimStr = (String) claimObject;
            }

            HashMap<String, Object> claimMap = DataToolUtils.deserialize(claimStr, HashMap.class);
            result.setClaim(claimMap);

            String privateKey = args.getWeIdAuthentication().getWeIdPrivateKey().getPrivateKey();
            if (StringUtils.equals(args.getType().getName(), CredentialType.LITE1.getName())) {
                return createLiteCredential(result, privateKey);
            }

            Map<String, Object> saltMap = DataToolUtils.clone(claimMap);
            generateSalt(saltMap, null);
            String rawData = CredentialPojoUtils
                    .getCredentialThumbprintWithoutSig(result, saltMap, null);

            //String signature = DataToolUtils.secp256k1Sign(rawData, new BigInteger(privateKey));
            String signature = DataToolUtils.SigBase64Serialization(
                    DataToolUtils.signToRsvSignature(rawData, privateKey)
            );
            result.putProofValue(ParamKeyConstant.PROOF_CREATED, result.getIssuanceDate());

            String creator = args.getWeIdAuthentication().getWeId();
            result.putProofValue(ParamKeyConstant.PROOF_CREATOR, creator);
            //TODO：目前CredentialProofType只有ECDSA类型，需要添加SM2
            String proofType = CredentialConstant.CredentialProofType.ECDSA.getTypeName();
            result.putProofValue(ParamKeyConstant.PROOF_TYPE, proofType);
            result.putProofValue(ParamKeyConstant.PROOF_SIGNATURE, signature);
            result.setSalt(saltMap);
            ResponseData<CredentialPojo> responseData = new ResponseData<>(
                    result,
                    ErrorCode.SUCCESS
            );

            return responseData;
        } catch (Exception e) {
            logger.error("Generate Credential failed due to system error. ", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    //创建基于hash连接的选择性披露Credential
    public ResponseData<CredentialPojo> createSelectiveCredential(
            CredentialPojo credential,
            ClaimPolicy claimPolicy) {

        if (credential == null) {
            logger.error("[createSelectiveCredential] input credential is null");
            return new ResponseData<CredentialPojo>(null, ErrorCode.ILLEGAL_INPUT);
        }
        if (credential.getType() != null
                && (credential.getType().contains(CredentialType.LITE1.getName())
                || credential.getType().contains(CredentialType.ZKP.getName()))) {
            logger.error(
                    "[createSelectiveCredential] Lite Credential and ZKP Credential DO NOT support "
                            + "this function(createSelectiveCredential), type = {}.", credential.getType());
            return new ResponseData<CredentialPojo>(null,
                    ErrorCode.CREDENTIAL_NOT_SUPPORT_SELECTIVE_DISCLOSURE);
        }
        try {
            CredentialPojo credentialClone = DataToolUtils.clone(credential);
            ErrorCode checkResp = CredentialPojoUtils.isCredentialPojoValid(credentialClone);
            if (ErrorCode.SUCCESS.getCode() != checkResp.getCode()) {
                return new ResponseData<CredentialPojo>(null, checkResp);
            }
            if (credentialClone.getCptId()
                    .equals(CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT)) {
                return new ResponseData<>(null, ErrorCode.CPT_ID_ILLEGAL);
            }
            if (claimPolicy == null) {
                logger.error("[createSelectiveCredential] claimPolicy is null.");
                return new ResponseData<CredentialPojo>(null,
                        ErrorCode.CREDENTIAL_CLAIM_POLICY_NOT_EXIST);
            }
            if (CredentialPojoUtils.isSelectivelyDisclosed(credential.getSalt())) {
                return new ResponseData<CredentialPojo>(null, ErrorCode.CREDENTIAL_RE_DISCLOSED);
            }
            String disclosure = claimPolicy.getFieldsToBeDisclosed();
            Map<String, Object> saltMap = credentialClone.getSalt();
            Map<String, Object> claim = credentialClone.getClaim();

            Map<String, Object> disclosureMap = DataToolUtils
                    .deserialize(disclosure, HashMap.class);

            if (!validCredentialMapArgs(claim, saltMap, disclosureMap)) {
                logger.error(
                        "[createSelectiveCredential] create failed. message is {}",
                        ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM.getCodeDesc()
                );
                return new ResponseData<CredentialPojo>(null,
                        ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM);
            }
            // 补 policy
            addKeyToPolicy(disclosureMap, claim);
            // 加盐处理
            addSelectSalt(disclosureMap, saltMap, claim, false);
            credentialClone.setSalt(saltMap);

            ResponseData<CredentialPojo> response = new ResponseData<CredentialPojo>();
            response.setResult(credentialClone);
            response.setErrorCode(ErrorCode.SUCCESS);
            return response;
        } catch (DataTypeCastException e) {
            logger.error("Generate SelectiveCredential failed, "
                    + "credential disclosure data type illegal. ", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_DISCLOSURE_DATA_TYPE_ILLEGAL);
        } catch (WeIdBaseException e) {
            logger.error("Generate SelectiveCredential failed, "
                    + "policy disclosurevalue illegal. ", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL);
        } catch (Exception e) {
            logger.error("Generate SelectiveCredential failed due to system error. ", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ResponseData<CredentialPojo> createLiteCredential(CredentialPojo credentialPojo,
                                                              String privateKey) {

        String rawData = CredentialPojoUtils.getLiteCredentialThumbprintWithoutSig(credentialPojo);

        // For Lite CredentialPojo, we begin to use Secp256k1 format signature to fit external type
        //替换国密
        //String signature = DataToolUtils.secp256k1Sign(rawData, new BigInteger(privateKey, 10));
        String signature = DataToolUtils.SigBase64Serialization(
                DataToolUtils.signToRsvSignature(rawData, privateKey)
        );
        String proofType = CredentialConstant.CredentialProofType.ECDSA.getTypeName();
        credentialPojo.putProofValue(ParamKeyConstant.PROOF_TYPE, proofType);
        credentialPojo.putProofValue(ParamKeyConstant.PROOF_SIGNATURE, signature);
        ResponseData<CredentialPojo> responseData = new ResponseData<>(
                credentialPojo,
                ErrorCode.SUCCESS
        );
        return responseData;
    }

    /**
     * Salt generator. Automatically fillin the map structure in a recursive manner.
     *
     * @param map the passed map (claim, salt or alike)
     * @param fixed fixed value if required to use
     */
    public static void generateSalt(Map<String, Object> map, Object fixed) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                generateSalt((HashMap) value, fixed);
            } else if (value instanceof List) {
                boolean isMapOrList = generateSaltFromList((ArrayList<Object>) value, fixed);
                if (!isMapOrList) {
                    if (fixed == null) {
                        addSalt(entry);
                    } else {
                        entry.setValue(fixed);
                    }
                }
            } else {
                if (fixed == null) {
                    addSalt(entry);
                } else {
                    entry.setValue(fixed);
                }
            }
        }
    }

    private static void addSalt(Map.Entry<String, Object> entry) {
        String salt = DataToolUtils.getRandomSalt();
        entry.setValue(salt);
    }

    private static boolean generateSaltFromList(List<Object> objList, Object fixed) {
        List<Object> list = (List<Object>) objList;
        for (Object obj : list) {
            if (obj instanceof Map) {
                generateSalt((HashMap) obj, fixed);
            } else if (obj instanceof List) {
                boolean result = generateSaltFromList((ArrayList<Object>) obj, fixed);
                if (!result) {
                    return result;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验claim、salt和disclosureMap的格式是否一致.
     */
    private static boolean validCredentialMapArgs(Map<String, Object> claim,
                                                  Map<String, Object> salt, Map<String, Object> disclosureMap) {

        //检查是否为空
        if (claim == null || salt == null || disclosureMap == null) {
            return false;
        }

        //检查每个map里的key个数是否相同
        if (!claim.keySet().equals(salt.keySet())) {
            return false;
        }

        //检查key值是否一致
        for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            //如果disclosureMap中的key在claim中没有则返回false
            if (!claim.containsKey(k)) {
                return false;
            }
            Object saltV = salt.get(k);
            Object claimV = claim.get(k);
            if (v instanceof Map) {
                //递归检查
                if (!validCredentialMapArgs((HashMap) claimV, (HashMap) saltV, (HashMap) v)) {
                    return false;
                }
            } else if (v instanceof List) {
                if (!validCredentialListArgs(
                        (ArrayList<Object>) claimV,
                        (ArrayList<Object>) saltV,
                        (ArrayList<Object>) v
                )) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean validCredentialListArgs(
            List<Object> claimList,
            List<Object> saltList,
            List<Object> disclosureList) {
        //检查是否为空
        if (claimList == null || saltList == null || disclosureList == null) {
            return false;
        }
        if (claimList.size() != saltList.size()) {
            return false;
        }
        for (int i = 0; i < disclosureList.size(); i++) {
            Object disclosureObj = disclosureList.get(i);
            Object claimObj = claimList.get(i);
            Object saltObj = saltList.get(i);
            if (disclosureObj instanceof Map) {
                boolean result =
                        validCredentialListArgs(
                                claimList,
                                saltList,
                                (HashMap) disclosureObj
                        );
                if (!result) {
                    return result;
                }
            } else if (disclosureObj instanceof List) {
                boolean result =
                        validCredentialListArgs(
                                (ArrayList<Object>) claimObj,
                                (ArrayList<Object>) saltObj,
                                (ArrayList<Object>) disclosureObj
                        );
                if (!result) {
                    return result;
                }
            }
        }
        return true;
    }

    private static boolean validCredentialListArgs(
            List<Object> claimList,
            List<Object> saltList,
            Map<String, Object> disclosure
    ) {

        if (claimList == null || saltList == null || saltList.size() != claimList.size()) {
            return false;
        }

        for (int i = 0; i < claimList.size(); i++) {
            Object claim = claimList.get(i);
            Object salt = saltList.get(i);
            boolean result = validCredentialMapArgs((HashMap) claim, (HashMap) salt, disclosure);
            if (!result) {
                return result;
            }
        }
        return true;
    }

    //向policy中补充缺失的key
    private static void addKeyToPolicy(
            Map<String, Object> disclosureMap,
            Map<String, Object> claimMap
    ) {
        for (Map.Entry<String, Object> entry : claimMap.entrySet()) {
            String claimK = entry.getKey();
            Object claimV = entry.getValue();
            if (claimV instanceof Map) {
                HashMap claimHashMap = (HashMap) claimV;
                if (!disclosureMap.containsKey(claimK)) {
                    disclosureMap.put(claimK, new HashMap());
                }
                HashMap disclosureHashMap = (HashMap) disclosureMap.get(claimK);
                addKeyToPolicy(disclosureHashMap, claimHashMap);
            } else if (claimV instanceof List) {
                ArrayList claimList = (ArrayList) claimV;
                //判断claimList中是否包含Map结构，还是单一结构
                boolean isSampleList = isSampleListForClaim(claimList);
                if (isSampleList) {
                    if (!disclosureMap.containsKey(claimK)) {
                        disclosureMap.put(claimK, Integer.parseInt(NOT_DISCLOSED));
                    }
                } else {
                    if (!disclosureMap.containsKey(claimK)) {
                        disclosureMap.put(claimK, new ArrayList());
                    }
                    ArrayList disclosureList = (ArrayList) disclosureMap.get(claimK);
                    addKeyToPolicyList(disclosureList, claimList);
                }
            } else {
                if (!disclosureMap.containsKey(claimK)) {
                    disclosureMap.put(claimK, Integer.parseInt(NOT_DISCLOSED));
                }
            }
        }
    }

    private static void addKeyToPolicyList(
            ArrayList disclosureList,
            ArrayList claimList
    ) {
        for (int i = 0; i < claimList.size(); i++) {
            Object claimObj = claimList.get(i);
            if (claimObj instanceof Map) {
                Object disclosureObj = disclosureList.size() == 0 ? null : disclosureList.get(0);
                if (disclosureObj == null) {
                    disclosureList.add(new HashMap());
                }
                HashMap disclosureHashMap = (HashMap) disclosureList.get(0);
                addKeyToPolicy(disclosureHashMap, (HashMap) claimObj);
                break;
            } else if (claimObj instanceof List) {
                Object disclosureObj = disclosureList.get(i);
                if (disclosureObj == null) {
                    disclosureList.add(new ArrayList());
                }
                ArrayList disclosureArrayList = (ArrayList) disclosureList.get(i);
                addKeyToPolicyList(disclosureArrayList, (ArrayList) claimObj);
            }
        }
    }

    private static boolean isSampleListForClaim(ArrayList claimList) {
        if (CollectionUtils.isEmpty(claimList)) {
            return true;
        }
        Object claimObj = claimList.get(0);
        if (claimObj instanceof Map) {
            return false;
        }
        if (claimObj instanceof List) {
            return isSampleListForClaim((ArrayList) claimObj);
        }
        return true;
    }

    private static void addSelectSalt(
            Map<String, Object> disclosureMap,
            Map<String, Object> saltMap,
            Map<String, Object> claim,
            boolean isZkp
    ) {
        for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
            String disclosureKey = entry.getKey();
            Object value = entry.getValue();
            Object saltV = saltMap.get(disclosureKey);
            Object claimV = claim.get(disclosureKey);
            if (value == null) {
                throw new WeIdBaseException(ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL);
            } else if ((value instanceof Map) && (claimV instanceof Map)) {
                addSelectSalt((HashMap) value, (HashMap) saltV, (HashMap) claimV, isZkp);
            } else if (value instanceof List) {
                addSaltForList(
                        (ArrayList<Object>) value,
                        (ArrayList<Object>) saltV,
                        (ArrayList<Object>) claimV,
                        isZkp
                );
            } else {
                addHashToClaim(saltMap, claim, disclosureKey, value, saltV, claimV, isZkp);
            }
        }
    }

    private static void addHashToClaim(
            Map<String, Object> saltMap,
            Map<String, Object> claim,
            String disclosureKey,
            Object value,
            Object saltV,
            Object claimV,
            boolean isZkp
    ) {

        if (isZkp) {
            if ((value instanceof Map) || !(((Integer) value).equals(Integer.parseInt(DISCLOSED))
                    && claim.containsKey(disclosureKey))) {
                String hash =
                        CredentialPojoUtils.getFieldSaltHash(
                                String.valueOf(claimV),
                                String.valueOf(saltV)
                        );
                claim.put(disclosureKey, hash);
            }
        } else {

            if (((Integer) value).equals(Integer.parseInt(NOT_DISCLOSED))
                    && claim.containsKey(disclosureKey)) {
                saltMap.put(disclosureKey, NOT_DISCLOSED);
                String hash =
                        CredentialPojoUtils.getFieldSaltHash(
                                String.valueOf(claimV),
                                String.valueOf(saltV)
                        );
                claim.put(disclosureKey, hash);
            }
        }
    }

    private static void addSaltForList(
            List<Object> disclosures,
            List<Object> salt,
            List<Object> claim,
            boolean isZkp) {
        for (int i = 0; claim != null && i < disclosures.size(); i++) {
            Object disclosureObj = disclosures.get(i);
            Object claimObj = claim.get(i);
            Object saltObj = salt.get(i);
            if (disclosureObj instanceof Map) {
                addSaltForList((HashMap) disclosureObj, salt, claim, isZkp);
            } else if (disclosureObj instanceof List) {
                addSaltForList(
                        (ArrayList<Object>) disclosureObj,
                        (ArrayList<Object>) saltObj,
                        (ArrayList<Object>) claimObj,
                        isZkp
                );
            }
        }
    }

    private static void addSaltForList(
            Map<String, Object> disclosures,
            List<Object> salt,
            List<Object> claim,
            boolean isZkp
    ) {
        for (int i = 0; claim != null && i < claim.size(); i++) {
            Object claimObj = claim.get(i);
            Object saltObj = salt.get(i);
            addSelectSalt(disclosures, (HashMap) saltObj, (HashMap) claimObj, isZkp);
        }
    }

    //使用提供的签发者公钥验证Credential，前提是该Credential对应的CPT文件保存在本地，如果没有请先使用registerCpt将CPT保存至本地文件
    public ResponseData<Boolean> verify(
            String issuerPublicKey,
            CredentialPojo credential) {

        if (StringUtils.isEmpty(issuerPublicKey)) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_PUBLIC_KEY_NOT_EXISTS);
        }
        if (CredentialPojoUtils.isLiteCredential(credential)) {
            return verifyLiteCredential(credential, issuerPublicKey, null, null);
        }
        ErrorCode errorCode = verifyContent(credential, issuerPublicKey, credential.getCptId(), null, null);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<Boolean>(false, errorCode);
        }
        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    //使用提供的签发者的WeIdDocument来验证Credential，前提是该Credential对应的CPT文件保存在本地，如果没有请先使用registerCpt将CPT保存至本地文件
    public ResponseData<Boolean> verify(WeIdDocument weIdDocument, CredentialPojo credential) {

        if (credential == null) {
            logger.error("[verify] The input credential is invalid.");
            return new ResponseData<Boolean>(false, ErrorCode.ILLEGAL_INPUT);
        }

        if (isZkpCredential(credential)) {
            return verifyZkpCredential(credential);
        }

        String issuerId = credential.getIssuer();
        if (!StringUtils.equals(weIdDocument.getId(), issuerId)) {
            logger.error("[verify] The input issuer weid is not match the credential's.");
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_ISSUER_MISMATCH);
        }
        if (CredentialPojoUtils.isLiteCredential(credential)) {
            return verifyLiteCredential(credential, null, null, weIdDocument);
        }
        ErrorCode errorCode = verifyContent(credential, null, credential.getCptId(), null, weIdDocument);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[verify] credential verify failed. error message :{}", errorCode);
            return new ResponseData<Boolean>(false, errorCode);
        }
        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    //只校验credential和cpt是否吻合
    public ResponseData<Set<ValidationMessage>> checkCredentialWithCpt(
            CredentialPojo credential,
            Cpt cpt
    ) {
        try {
            if (credential == null || credential.getSalt() == null
                    || credential.getClaim() == null || credential.getType() == null) {
                String errorMsg = ErrorCode.ILLEGAL_INPUT.getCodeDesc() + ": credential";
                logger.error("[checkCredentialWithCpt] {}.", errorMsg);
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT.getCode(), errorMsg);
            }
            if (cpt == null || cpt.getCptJsonSchema() == null) {
                String errorMsg = ErrorCode.ILLEGAL_INPUT.getCodeDesc() + ": cpt";
                logger.error("[checkCredentialWithCpt] {}.", errorMsg);
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT.getCode(), errorMsg);
            }
            if (credential.getCptId().intValue() != cpt.getCptId().intValue()) {
                logger.error("[checkCredentialWithCpt] the cptId does not match.");
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_CPTID_NOTMATCH);
            }
            // zkp不支持检查
            if (credential.getCredentialType() == CredentialType.ZKP) {
                logger.error("[checkCredentialWithCpt] ZKP Credential DO NOT support.");
                return new ResponseData<>(null, ErrorCode.THIS_IS_UNSUPPORTED);
            } else if (credential.getCredentialType() == CredentialType.ORIGINAL) {
                // 如果不是lite1类型的，则判断是否为选择性披露类型
                boolean isSelectivelyDisclosed = CredentialPojoUtils.isSelectivelyDisclosed(
                        credential.getSalt());
                // 如果是选择性披露 则特殊处理
                if (isSelectivelyDisclosed) {
                    // 做特殊处理逻辑: remove不披露的字段 并判断是否为必选字段，如果是必选字段 并且为不披露则检查失败
                    credential = DataToolUtils.clone(credential);
                    removeDisclosedFiledMap(
                            credential.getClaim(),
                            credential.getSalt(),
                            cpt.getCptJsonSchema()
                    );
                }
            }

            String cptJsonSchema = DataToolUtils.serialize(cpt.getCptJsonSchema());
            // 验证cpt自身的合法性
            if (!DataToolUtils.isCptJsonSchemaValid(cptJsonSchema)) {
                logger.error("[checkCredentialWithCpt] the cpt invalid.");
                return new ResponseData<>(null, ErrorCode.CPT_JSON_SCHEMA_INVALID);
            }
            String claimStr = DataToolUtils.serialize(credential.getClaim());
            // 验证cpt与credential的匹配性
            Set<ValidationMessage>  checkRes = DataToolUtils.checkJsonVersusSchema(
                    claimStr, cptJsonSchema);
            if (checkRes.size() != 0) {
                logger.error(
                        "[checkCredentialWithCpt] check fail, ProcessingReport = {}.", checkRes);
                ResponseData<Set<ValidationMessage>> result = new ResponseData<>(
                        checkRes,
                        ErrorCode.CREDENTIAL_DOES_NOT_MATCHE_THE_CPT
                );
                return result;
            }
            return new ResponseData<>(checkRes, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[checkCredentialWithCpt] check has base exception.", e);
            return new ResponseData<>(null, e.getErrorCode().getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("[checkCredentialWithCpt] check has unknown exception.", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    //计算credential的整体hash，用于给credential存证
    public ResponseData<String> getCredentialPojoHash(CredentialPojo credentialPojo) {
        ErrorCode innerResponse = CredentialPojoUtils.isCredentialPojoValid(credentialPojo);
        if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
            logger.error("Create Evidence input format error!");
            return new ResponseData<>(StringUtils.EMPTY, innerResponse);
        }
        return new ResponseData<>(CredentialPojoUtils.getCredentialPojoHash(credentialPojo, null),
                ErrorCode.SUCCESS);
    }

    private static ResponseData<Boolean> verifyLiteCredential(
            CredentialPojo credential,
            String publicKey,
            String methodId,
            WeIdDocument weIdDocument) {
        // Lite Credential only contains limited areas (others truncated)
        if (credential.getCptId() == null || credential.getCptId().intValue() < 0) {
            return new ResponseData<>(false, ErrorCode.CPT_ID_ILLEGAL);
        }
        if (!WeIdUtils.isWeIdValid(credential.getIssuer())) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ISSUER_INVALID);
        }
        if (credential.getClaim() == null || credential.getClaim().size() == 0) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS);
        }
        if (credential.getProof() == null || StringUtils.isEmpty(credential.getSignature())) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_SIGNATURE_NOT_EXISTS);
        }
        String rawData = CredentialPojoUtils.getLiteCredentialThumbprintWithoutSig(credential);

        // Using provided public key to verify signature
        if (!StringUtils.isBlank(publicKey)) {
            boolean result;
            try {
                // For Lite CredentialPojo, we begin to use Secp256k1 verify to fit external type
                result = DataToolUtils.verifySignature(rawData, credential.getSignature(), new BigInteger(publicKey));
            } catch (Exception e) {
                logger.error("[verifyContent] verify signature fail.", e);
                return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_SIGNATURE_BROKEN);
            }
            if (!result) {
                return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_VERIFY_FAIL);
            }
            return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
        }

        if(weIdDocument != null) {
            ErrorCode verifyErrorCode = DataToolUtils.verifySignatureFromWeId(
                    rawData, credential.getSignature(), weIdDocument, methodId);
            if (verifyErrorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<Boolean>(false, verifyErrorCode);
            }
            return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
        }

        return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_VERIFY_FAIL);
    }

    private static ErrorCode verifyContent(
            CredentialPojo credential,
            String publicKey,
            Integer cptId,
            String methodId,
            WeIdDocument weIdDocument
    ) {
        ErrorCode errorCode;
        try {
            errorCode = verifyContentInner(credential, publicKey, cptId, methodId, weIdDocument);
        } catch (WeIdBaseException ex) {
            logger.error("[verifyContent] verify credential has exception.", ex);
            return ex.getErrorCode();
        }
        // System CPT business related check
        if (errorCode == ErrorCode.SUCCESS
                && CredentialPojoUtils.isSystemCptId(credential.getCptId())) {
            errorCode = verifySystemCptClaimInner(credential);
        }
        return errorCode;
    }

    private static ErrorCode verifySystemCptClaimInner(CredentialPojo credential) {
        if (credential.getCptId().intValue() == CredentialConstant.EMBEDDED_TIMESTAMP_CPT) {
            return verifyTimestampClaim(credential);
        }
        if (credential.getCptId().intValue() == CredentialConstant.AUTHORIZATION_CPT) {
            return verifyAuthClaim(credential);
        }
        return ErrorCode.SUCCESS;
    }

    private static ErrorCode verifyAuthClaim(CredentialPojo credential) {
        Cpt101 authInfo;
        try {
            authInfo = DataToolUtils.mapToObj(credential.getClaim(), Cpt101.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize authorization information.");
            return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
        }
        ErrorCode errorCode = verifyAuthInfo(authInfo);
        if (errorCode != ErrorCode.SUCCESS) {
            return errorCode;
        }
        // Extra check 1: cannot authorize other WeID's resources
        String issuerWeId = credential.getIssuer();
        if (!issuerWeId.equalsIgnoreCase(authInfo.getFromWeId())) {
            return ErrorCode.AUTHORIZATION_CANNOT_AUTHORIZE_OTHER_WEID_RESOURCE;
        }
        // TODO Extra check 2: check service url endpoint exposed or not?
        // Need getWeIdDocument() check
        return ErrorCode.SUCCESS;
    }

    private static ErrorCode verifyTimestampClaim(CredentialPojo credential) {
        Map<String, Object> claim = credential.getClaim();
        if (((String) claim.get("timestampAuthority"))
                .contains(TimestampUtils.WESIGN_AUTHORITY_NAME)) {
            String hashValue = (String) claim.get("claimHash");
            String authoritySignature = (String) claim.get("authoritySignature");
            Long timestamp = (long) claim.get("timestamp");
            ResponseData<Boolean> resp =
                    TimestampUtils.verifyWeSignTimestamp(hashValue, authoritySignature, timestamp);
            if (!resp.getResult()) {
                return ErrorCode.getTypeByErrorCode(resp.getErrorCode());
            }
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Verify the authorization info in an authorization token credential.
     *
     * @param authInfo the auth info in CPT101 format
     * @return success if valid, specific error codes otherwise
     */
    public static ErrorCode verifyAuthInfo(Cpt101 authInfo) {
        if (authInfo == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }

        String serviceUrl = authInfo.getServiceUrl();
        String resourceId = authInfo.getResourceId();
        Long duration = authInfo.getDuration();
        if (!CredentialUtils.isValidUuid(resourceId)) {
            logger.error("Resource ID illegal: is not a valid UUID.");
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (!DataToolUtils.isValidEndpointUrl(serviceUrl)) {
            logger.error("Service URL illegal.");
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (duration < 0) {
            logger.error("Auth token duration of validity illegal: already expired.");
            return ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL;
        }

        String fromWeId = authInfo.getFromWeId();
        String toWeId = authInfo.getToWeId();
        if (fromWeId.equalsIgnoreCase(toWeId)) {
            logger.error("FromWeId and ToWeId must be different.");
            return ErrorCode.AUTHORIZATION_FROM_TO_MUST_BE_DIFFERENT;
        }
        /*
        ResponseData<Boolean> existResp = getWeIdService().isWeIdExist(fromWeId);
        if (!existResp.getResult()) {
            logger.error("From WeID illegal: {}", existResp.getErrorMessage());
            return ErrorCode.getTypeByErrorCode(existResp.getErrorCode());
        }
        existResp = getWeIdService().isWeIdExist(toWeId);
        if (!existResp.getResult()) {
            logger.error("To WeID illegal: {}", existResp.getErrorMessage());
            return ErrorCode.getTypeByErrorCode(existResp.getErrorCode());
        }
        */
        return ErrorCode.SUCCESS;
    }

    private static ErrorCode verifyContentInner(
            CredentialPojo credential,
            String publicKey,
            Integer cptId,
            String methodId,
            WeIdDocument weIdDocument
    ) {
        ErrorCode checkResp = CredentialPojoUtils.isCredentialPojoValid(credential);
        if (ErrorCode.SUCCESS.getCode() != checkResp.getCode()) {
            return checkResp;
        }
        if (credential.getCptId() == CredentialConstant.CREDENTIAL_EMBEDDED_SIGNATURE_CPT
                .intValue()) {
            logger.error("Embedded Credential is obsoleted. Please use embedded Credential Pojo.");
            return ErrorCode.CPT_ID_ILLEGAL;
        }
        if (credential.getCptId() == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT
                .intValue() || credential.getCptId() == CredentialConstant.EMBEDDED_TIMESTAMP_CPT
                .intValue()) {
            // This is a multi-signed Credential. We firstly verify itself (i.e. external check)
            ErrorCode errorCode = verifySingleSignedCredential(
                    credential, publicKey, cptId, methodId, weIdDocument);
            if (errorCode != ErrorCode.SUCCESS) {
                return errorCode;
            }
            // Then, we verify its list members one-by-one
            List<Object> innerCredentialList;
            try {
                if (credential.getClaim().get("credentialList") instanceof String) {
                    // For selectively-disclosed credential, stop here. External check is enough.
                    return ErrorCode.SUCCESS;
                } else {
                    innerCredentialList = (ArrayList) credential.getClaim().get("credentialList");
                }
            } catch (Exception e) {
                logger.error("the credential claim data illegal.", e);
                return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
            }
            for (Object innerCredentialObject : innerCredentialList) {
                // PublicKey can only be used in the passed-external check, so pass-in null key
                try {
                    CredentialPojo innerCredential;
                    if (!(innerCredentialObject instanceof CredentialPojo)) {
                        Map<String, Object> map = (Map<String, Object>) innerCredentialObject;
                        innerCredential = DataToolUtils
                                .mapToObj(map, CredentialPojo.class);
                    } else {
                        innerCredential = (CredentialPojo) innerCredentialObject;
                    }
                    errorCode = verifyContentInner(
                            innerCredential, null, cptId, methodId, weIdDocument);
                    if (errorCode != ErrorCode.SUCCESS) {
                        return errorCode;
                    }
                } catch (Exception e) {
                    logger.error("Failed to convert credentialPojo to object.", e);
                    return ErrorCode.ILLEGAL_INPUT;
                }
            }
            return ErrorCode.SUCCESS;
        }
        return verifySingleSignedCredential(credential, publicKey, cptId, methodId, weIdDocument);
    }

    private static ErrorCode verifySingleSignedCredential(
            CredentialPojo credential,
            String publicKey,
            Integer cptId,
            String methodId,
            WeIdDocument weIdDocument
    ) {
        ErrorCode errorCode = verifyCptFormat(
                credential.getCptId(),
                credential.getClaim(),
                CredentialPojoUtils.isSelectivelyDisclosed(credential.getSalt())
        );
        if (ErrorCode.SUCCESS.getCode() != errorCode.getCode()) {
            return errorCode;
        }
        Map<String, Object> salt = credential.getSalt();
        String rawData;
        if (CredentialPojoUtils.isEmbeddedCredential(credential)) {
            List<Object> objList = (ArrayList<Object>) credential.getClaim().get("credentialList");
            List<CredentialPojo> credentialList = new ArrayList<>();
            try {
                for (Object obj : objList) {
                    if (obj instanceof CredentialPojo) {
                        credentialList.add((CredentialPojo) obj);
                    } else {
                        credentialList.add(DataToolUtils
                                .mapToObj((HashMap<String, Object>) obj, CredentialPojo.class));
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to convert credentialPojo: " + e.getMessage(), e);
                return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
            }
            rawData = CredentialPojoUtils.getEmbeddedCredentialThumbprintWithoutSig(credentialList);
        } else {
            rawData = CredentialPojoUtils
                    .getCredentialThumbprintWithoutSig(credential, salt, null);
        }
        String issuerWeid = credential.getIssuer();
        if (!StringUtils.isEmpty(publicKey)) {
            boolean result;
            try {
                result = DataToolUtils.verifySignature(rawData,
                        credential.getSignature(), new BigInteger(publicKey));

            } catch (Exception e) {
                logger.error("[verifyContent] verify signature fail.", e);
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            }
            if (!result) {
                return ErrorCode.CREDENTIAL_VERIFY_FAIL;
            }
            return ErrorCode.SUCCESS;
        } else if (weIdDocument != null) {
            errorCode = DataToolUtils.verifySignatureFromWeId(
                    rawData, credential.getSignature(), weIdDocument, methodId);
            return errorCode;
        }
        return ErrorCode.CREDENTIAL_VERIFY_FAIL;
    }

    private static ErrorCode verifyCptFormat(
            Integer cptId, Map<String, Object> claim,
            boolean isSelectivelyDisclosed
    ) {
        if (cptId == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT.intValue()) {
            if (!claim.containsKey("credentialList")) {
                return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
            } else {
                return ErrorCode.SUCCESS;
            }
        }
        if (cptId == CredentialConstant.EMBEDDED_TIMESTAMP_CPT.intValue()) {
            if (claim.containsKey("credentialList") && claim.containsKey("claimHash")
                    && claim.containsKey("timestampAuthority") && claim.containsKey("timestamp")
                    && claim.containsKey("authoritySignature")) {
                return ErrorCode.SUCCESS;
            } else {
                return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
            }
        }
        try {
            /*if (offline) {
                return ErrorCode.SUCCESS;
            }*/
            String claimStr = DataToolUtils.serialize(claim);
            //Cpt cpt = getCptService().queryCpt(cptId).getResult();
            Cpt cpt = weIdServiceConsole.queryCpt(cptId).getResult();
            if (cpt == null) {
                logger.error(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCodeDesc());
                return ErrorCode.CREDENTIAL_CPT_NOT_EXISTS;
            }
            //String cptJsonSchema = JsonUtil.objToJsonStr(cpt.getCptJsonSchema());
            String cptJsonSchema = DataToolUtils.serialize(cpt.getCptJsonSchema());

            if (!DataToolUtils.isCptJsonSchemaValid(cptJsonSchema)) {
                logger.error(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCodeDesc());
                return ErrorCode.CPT_JSON_SCHEMA_INVALID;
            }
            if (!isSelectivelyDisclosed) {
                Set<ValidationMessage>  checkRes = DataToolUtils.checkJsonVersusSchema(
                        claimStr, cptJsonSchema);
                if (checkRes.size() != 0) {
                    logger.error(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCodeDesc());
                    return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
                }
            }
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            logger.error(
                    "Generic error occurred during verify cpt format when verifyCredential: ", e);
            return ErrorCode.CREDENTIAL_ERROR;
        }
    }

    private static Boolean isZkpCredential(CredentialPojo credential) {

        List<String> types = credential.getType();
        for (String type : types) {
            if (StringUtils.equals(type, CredentialType.ZKP.getName())) {
                return true;
            }
        }
        return false;
    }

    private static ResponseData<Boolean> verifyZkpCredential(CredentialPojo credential) {

        Map<String, Object> proof = credential.getProof();
        String encodedVerificationRule = (String) proof
                .get(ParamKeyConstant.PROOF_ENCODEDVERIFICATIONRULE);
        String verificationRequest = (String) proof.get(ParamKeyConstant.PROOF_VERIFICATIONREQUEST);
        VerifierResult verifierResult =
                VerifierClient.verifyProof(encodedVerificationRule, verificationRequest);
        if (verifierResult.wedprErrorMessage == null) {
            return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
        }

        return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_VERIFY_FAIL);
    }

    // 移除不披露的字段
    private void removeDisclosedFiledMap(
            Map<String, Object> claim,
            Map<String, Object> salt,
            Map<String, Object> properties
    ) {
        ArrayList<String> requireds = (ArrayList<String>)properties.get("required");
        Map<String, Object> nextProperties = (Map<String, Object>)properties.get("properties");
        // 遍历claim
        for (Map.Entry<String, Object> entry : salt.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                // 说明下一层为map
                removeDisclosedFiledMap(
                        (Map<String, Object>) claim.get(key),
                        (Map<String, Object>) value,
                        (Map<String, Object>) nextProperties.get(key)
                );
            } else if (value instanceof List) {
                // 说明下一层是List
                removeDisclosedFiledList(
                        (ArrayList<Object>) claim.get(key),
                        (ArrayList<Object>) value,
                        (Map<String, Object>) nextProperties.get(key));
            } else {
                // 说明当前层为需要处理的层
                // 如果当前为不披露的key
                // 1. 如果必须的key则报错
                // 2. 否则移除此key
                if ("0".equals(value.toString())) {
                    // 说明是选择性披露
                    if (requireds != null && requireds.contains(key)) {
                        throw new WeIdBaseException(key + " is required and disclosed.");
                    } else {
                        claim.remove(key);
                    }
                }
            }
        }
    }

    private void removeDisclosedFiledList(
            ArrayList<Object> claims,
            ArrayList<Object> salts,
            Map<String, Object> properties
    ) {
        for (int i = 0; i < claims.size(); i++) {
            Object claim = claims.get(i);
            if (claim instanceof Map) {
                removeDisclosedFiledMap(
                        (Map<String, Object>) claim,
                        (Map<String, Object>) salts.get(i),
                        (Map<String, Object>) properties.get("items")
                );
            } else {
                removeDisclosedFiledList(
                        (ArrayList<Object>) claim,
                        (ArrayList<Object>) salts.get(i),
                        (Map<String, Object>) properties.get("items")
                );
            }
        }
    }

}
