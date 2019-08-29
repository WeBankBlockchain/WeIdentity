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

package com.webank.weid.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialConstant.CredentialProofType;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.CredentialPojoUtils;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on Credential.
 *
 * @author tonychen 2019年4月17日
 */
public class CredentialPojoServiceImpl extends BaseService implements CredentialPojoService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialPojoServiceImpl.class);

    private static  WeIdService weIdService = new WeIdServiceImpl();

    private static CptService cptService = new CptServiceImpl();

    private static final String NOT_DISCLOSED =
        CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus().toString();

    private static final String DISCLOSED =
        CredentialFieldDisclosureValue.DISCLOSED.getStatus().toString();

    private static final String EXISTED =
        CredentialFieldDisclosureValue.EXISTED.getStatus().toString();

    /**
     * 验证生成器.
     *
     * @param map 传入的Map
     */
    public static void generateSalt(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                generateSalt((HashMap) value);
            } else if (value instanceof List) {
                boolean isMapOrList = generateSaltFromList((ArrayList<Object>) value);
                if (!isMapOrList) {
                    addSalt(entry);
                }
            } else {
                addSalt(entry);
            }
        }
    }

    private static void addSalt(Map.Entry<String, Object> entry) {
        String salt = DataToolUtils.getRandomSalt();
        entry.setValue(salt);
    }

    private static boolean generateSaltFromList(List<Object> objList) {
        List<Object> list = (List<Object>) objList;
        for (Object obj : list) {
            if (obj instanceof Map) {
                generateSalt((HashMap) obj);
            } else if (obj instanceof List) {
                boolean result = generateSaltFromList((ArrayList<Object>) obj);
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
                HashMap claimHashMap = (HashMap)claimV;
                if (!disclosureMap.containsKey(claimK)) {
                    disclosureMap.put(claimK, new HashMap());
                }
                HashMap disclosureHashMap = (HashMap)disclosureMap.get(claimK);
                addKeyToPolicy(disclosureHashMap, claimHashMap);
            } else if (claimV instanceof List) {
                ArrayList claimList = (ArrayList)claimV;
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
                    ArrayList disclosureList = (ArrayList)disclosureMap.get(claimK);
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
                HashMap disclosureHashMap = (HashMap)disclosureList.get(0);
                addKeyToPolicy(disclosureHashMap, (HashMap)claimObj);
                break;
            } else if (claimObj instanceof List) {
                Object disclosureObj = disclosureList.get(i);
                if (disclosureObj == null) {
                    disclosureList.add(new ArrayList());
                }
                ArrayList disclosureArrayList = (ArrayList)disclosureList.get(i);
                addKeyToPolicyList(disclosureArrayList, (ArrayList)claimObj);
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
            return isSampleListForClaim((ArrayList)claimObj);
        }
        return true;
    }
    
    private static void addSelectSalt(
        Map<String, Object> disclosureMap,
        Map<String, Object> saltMap,
        Map<String, Object> claim
    ) {
        for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
            String disclosureKey = entry.getKey();
            Object value = entry.getValue();
            Object saltV = saltMap.get(disclosureKey);
            Object claimV = claim.get(disclosureKey);
            if (value == null) {
                throw new WeIdBaseException(ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL);
            } else if (value instanceof Map) {
                addSelectSalt((HashMap) value, (HashMap) saltV, (HashMap) claimV);
            } else if (value instanceof List) {
                addSaltForList(
                    (ArrayList<Object>) value,
                    (ArrayList<Object>) saltV,
                    (ArrayList<Object>) claimV
                );
            } else {
                addHashToClaim(saltMap, claim, disclosureKey, value, saltV, claimV);
            }
        }
    }

    private static void addHashToClaim(
        Map<String, Object> saltMap,
        Map<String, Object> claim,
        String disclosureKey,
        Object value,
        Object saltV,
        Object claimV
    ) {
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

    private static void addSaltForList(List<Object> disclosures, List<Object> salt,
        List<Object> claim) {
        for (int i = 0; claim != null && i < disclosures.size(); i++) {
            Object disclosureObj = disclosures.get(i);
            Object claimObj = claim.get(i);
            Object saltObj = salt.get(i);
            if (disclosureObj instanceof Map) {
                addSaltForList((HashMap) disclosureObj, salt, claim);
            } else if (disclosureObj instanceof List) {
                addSaltForList(
                    (ArrayList<Object>) disclosureObj,
                    (ArrayList<Object>) saltObj,
                    (ArrayList<Object>) claimObj
                );
            }
        }
    }

    private static void addSaltForList(
        Map<String, Object> disclosures,
        List<Object> salt,
        List<Object> claim
    ) {
        for (int i = 0; claim != null && i < claim.size(); i++) {
            Object claimObj = claim.get(i);
            Object saltObj = salt.get(i);
            addSelectSalt(disclosures, (HashMap) saltObj, (HashMap) claimObj);
        }
    }

    private static ErrorCode verifyContent(CredentialPojo credential, String publicKey) {
        try {
            return verifyContent(credential, publicKey, null);
        } catch (WeIdBaseException ex) {
            logger.error("[verifyContent] verify credential has exception.", ex);
            return ex.getErrorCode();
        }
    }

    private static ErrorCode verifyContent(CredentialPojo credential,
        String publicKey, ClaimPolicy claimPolicy) {
        ErrorCode checkResp = CredentialPojoUtils.isCredentialPojoValid(credential);
        if (ErrorCode.SUCCESS.getCode() != checkResp.getCode()) {
            return checkResp;
        }
        if (credential.getCptId() == CredentialConstant.CREDENTIAL_EMBEDDED_SIGNATURE_CPT
            .intValue()) {
            return ErrorCode.CPT_ID_ILLEGAL;
        }
        if (credential.getCptId() == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT
            .intValue()) {
            // This is a multi-signed Credential. We firstly verify itself (i.e. external check)
            ErrorCode errorCode = verifySingleSignedCredential(credential, publicKey, null);
            if (errorCode != ErrorCode.SUCCESS) {
                if (!(credential.getCptId()
                    == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT.intValue()
                    && errorCode == ErrorCode.CREDENTIAL_CPT_NOT_EXISTS)) {
                    return errorCode;
                }
            }
            // Then, we verify its list members one-by-one
            List<CredentialPojo> innerCredentialList;
            try {
                if (credential.getClaim().get("credentialList") instanceof String) {
                    // For selectively-disclosed credential, stop here. External check is enough.
                    return ErrorCode.SUCCESS;
                } else {
                    innerCredentialList = (ArrayList) credential.getClaim().get("credentialList");
                }
            } catch (Exception e) {
                return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
            }
            for (CredentialPojo innerCredential : innerCredentialList) {
                // PublicKey can only be used in the passed-external check, so pass-in null key
                errorCode = verifyContent(innerCredential, null, null);
                if (errorCode != ErrorCode.SUCCESS) {
                    if (!(credential.getCptId()
                        == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT.intValue()
                        && errorCode == ErrorCode.CREDENTIAL_CPT_NOT_EXISTS)) {
                        return errorCode;
                    }
                }
            }
            return ErrorCode.SUCCESS;
        }
        return verifySingleSignedCredential(credential, publicKey, null);
    }

    private static ErrorCode verifySingleSignedCredential(CredentialPojo credential,
        String publicKey, ClaimPolicy claimPolicy) {
        ErrorCode errorCode = verifyCptFormat(
            credential.getCptId(),
            credential.getClaim(),
            CredentialPojoUtils.isSelectivelyDisclosed(credential.getSalt())
        );
        if (ErrorCode.SUCCESS.getCode() != errorCode.getCode()) {
            return errorCode;
        }
        Map<String, Object> salt = credential.getSalt();
        String rawData = CredentialPojoUtils
            .getCredentialThumbprintWithoutSig(credential, salt, null);
        String issuerWeid = credential.getIssuer();
        if (StringUtils.isEmpty(publicKey)) {
            // Fetch public key from chain
            ResponseData<WeIdDocument> innerResponseData =
                weIdService.getWeIdDocument(issuerWeid);
            if (innerResponseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "Error occurred when fetching WeIdentity DID document for: {}, msg: {}",
                    issuerWeid, innerResponseData.getErrorMessage());
                return ErrorCode.getTypeByErrorCode(innerResponseData.getErrorCode());
            } else {
                WeIdDocument weIdDocument = innerResponseData.getResult();
                return DataToolUtils
                    .verifySignatureFromWeId(rawData, credential.getSignature(), weIdDocument);
            }
        } else {
            boolean result;
            try {
                result = DataToolUtils
                    .verifySignature(
                        rawData,
                        credential.getSignature(),
                        new BigInteger(publicKey)
                    );
            } catch (Exception e) {
                logger.error("[verifyContent] verify signature fail.", e);
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            }
            if (!result) {
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            }
            return ErrorCode.SUCCESS;
        }
    }


    private static ErrorCode verifyCptFormat(Integer cptId, Map<String, Object> claim,
        boolean isSelectivelyDisclosed) {

        try {
            String claimStr = DataToolUtils.serialize(claim);
            Cpt cpt = cptService.queryCpt(cptId).getResult();
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
                if (!DataToolUtils.isValidateJsonVersusSchema(claimStr, cptJsonSchema)) {
                    logger.error(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCodeDesc());
                    return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
                }
            }
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            logger.error(
                "Generic error occurred during verify cpt format when verifyCredential: " + e);
            return ErrorCode.CREDENTIAL_ERROR;
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#createCredential(
     *          com.webank.weid.protocol.request.CreateCredentialPojoArgs
     *      )
     */
    @Override
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
            result.setId(UUID.randomUUID().toString());
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
            if (!WeIdUtils.validatePrivateKeyWeIdMatches(
                args.getWeIdAuthentication().getWeIdPrivateKey(),
                args.getIssuer())) {
                logger.error("Create Credential, private key does not match the current weid.");
                return new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            }
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
            Object claimObject = args.getClaim();
            String claimStr = null;
            if (!(claimObject instanceof String)) {
                claimStr = DataToolUtils.serialize(claimObject);
            } else {
                claimStr = (String) claimObject;
            }

            HashMap<String, Object> claimMap = DataToolUtils.deserialize(claimStr, HashMap.class);
            result.setClaim(claimMap);

            Map<String, Object> saltMap = DataToolUtils.clone(claimMap);
            generateSalt(saltMap);
            String rawData = CredentialPojoUtils
                .getCredentialThumbprintWithoutSig(result, saltMap, null);
            String privateKey = args.getWeIdAuthentication().getWeIdPrivateKey().getPrivateKey();

            String signature = DataToolUtils.sign(rawData, privateKey);

            result.putProofValue(ParamKeyConstant.PROOF_CREATED, result.getIssuanceDate());

            String weIdPublicKeyId = args.getWeIdAuthentication().getWeIdPublicKeyId();
            result.putProofValue(ParamKeyConstant.PROOF_CREATOR, weIdPublicKeyId);

            String proofType = CredentialProofType.ECDSA.getTypeName();
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

    /**
     * Add an extra signer and signature to a Credential. Multiple signatures will be appended in an
     * embedded manner.
     *
     * @param credentialList original credential list
     * @param callerAuth the passed-in privateKey and WeID bundle to sign
     * @return the modified CredentialWrapper
     */
    @Override
    public ResponseData<CredentialPojo> addSignature(
        List<CredentialPojo> credentialList,
        WeIdAuthentication callerAuth) {
        if (credentialList == null || credentialList.size() == 0
            || CredentialPojoUtils.isWeIdAuthenticationValid(callerAuth) != ErrorCode.SUCCESS) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        CredentialPojo result = new CredentialPojo();
        result.setCptId(CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT);
        result.setIssuanceDate(DateUtils.getNoMillisecondTimeStamp());
        result.setId(UUID.randomUUID().toString());
        result.setContext(CredentialUtils.getDefaultCredentialContext());
        Long expirationDate = 0L;
        for (CredentialPojo arg : credentialList) {
            if (arg.getExpirationDate() > expirationDate) {
                expirationDate = arg.getExpirationDate();
            }
        }
        Long newExpirationDate =
            DateUtils.convertToNoMillisecondTimeStamp(expirationDate);
        if (newExpirationDate == null) {
            logger.error("Create Credential Args illegal.");
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL);
        } else {
            result.setExpirationDate(newExpirationDate);
        }
        if (!WeIdUtils.validatePrivateKeyWeIdMatches(
            callerAuth.getWeIdPrivateKey(),
            callerAuth.getWeId())) {
            logger.error("Create Credential, private key does not match the current weid.");
            return new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
        }
        String privateKey = callerAuth.getWeIdPrivateKey().getPrivateKey();
        ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
        String keyWeId = WeIdUtils
            .convertAddressToWeId(new Address(Keys.getAddress(keyPair)).toString());
        result.setIssuer(keyWeId);
        result.addType(CredentialConstant.DEFAULT_CREDENTIAL_TYPE);

        // The claim will be the wrapper of the to-be-signed credentialpojos
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("credentialList", credentialList);
        result.setClaim(claim);

        Map<String, Object> saltMap = DataToolUtils.clone(claim);
        generateSalt(saltMap);
        String rawData = CredentialPojoUtils
            .getCredentialThumbprintWithoutSig(result, saltMap, null);
        String signature = DataToolUtils.sign(rawData, privateKey);

        result.putProofValue(ParamKeyConstant.PROOF_CREATED, result.getIssuanceDate());

        String weIdPublicKeyId = callerAuth.getWeIdPublicKeyId();
        result.putProofValue(ParamKeyConstant.PROOF_CREATOR, weIdPublicKeyId);

        String proofType = CredentialProofType.ECDSA.getTypeName();
        result.putProofValue(ParamKeyConstant.PROOF_TYPE, proofType);
        result.putProofValue(ParamKeyConstant.PROOF_SIGNATURE, signature);
        result.setSalt(saltMap);

        return new ResponseData<>(result, ErrorCode.SUCCESS);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#createSelectiveCredential(
     *          com.webank.weid.protocol.base.CredentialPojo,
     *          com.webank.weid.protocol.base.ClaimPolicy
     *      )
     */
    @Override
    public ResponseData<CredentialPojo> createSelectiveCredential(
        CredentialPojo credential,
        ClaimPolicy claimPolicy) {
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
            addSelectSalt(disclosureMap, saltMap, claim);
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

    /**
     * Get the full hash value of a CredentialPojo. All fields in the CredentialPojo will be
     * included. This method should be called when creating and verifying the Credential Evidence
     * and the result is selectively-disclosure irrelevant.
     *
     * @param credentialPojo the args
     * @return the Credential Hash value
     */
    @Override
    public ResponseData<String> getCredentialPojoHash(CredentialPojo credentialPojo) {
        ErrorCode innerResponse = CredentialPojoUtils.isCredentialPojoValid(credentialPojo);
        if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
            logger.error("Create Evidence input format error!");
            return new ResponseData<>(StringUtils.EMPTY, innerResponse);
        }
        return new ResponseData<>(CredentialPojoUtils.getCredentialPojoHash(credentialPojo, null),
            ErrorCode.SUCCESS);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#verify(
     *          java.lang.String,
     *          com.webank.weid.protocol.base.CredentialPojo
     *      )
     */
    @Override
    public ResponseData<Boolean> verify(String issuerWeId, CredentialPojo credential) {

        String issuerId = credential.getIssuer();
        if (!StringUtils.equals(issuerWeId, issuerId)) {
            logger.error("[verify] The input issuer weid is not match the credential's.");
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_ISSUER_MISMATCH);
        }
        ErrorCode errorCode = verifyContent(credential, null);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[verify] credential verify failed. error message :{}", errorCode);
            return new ResponseData<Boolean>(false, errorCode);
        }
        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#verify(
     *          com.webank.weid.protocol.base.CredentialPojo,
     *          com.webank.weid.protocol.base.WeIdPublicKey
     *      )
     */
    @Override
    public ResponseData<Boolean> verify(
        WeIdPublicKey issuerPublicKey,
        CredentialPojo credential) {

        String publicKey = issuerPublicKey.getPublicKey();
        if (StringUtils.isEmpty(publicKey)) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_PUBLIC_KEY_NOT_EXISTS);
        }
        ErrorCode errorCode = verifyContent(credential, publicKey);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<Boolean>(false, errorCode);
        }
        return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.CredentialPojoService#verify(
     *          java.lang.String,
     *          com.webank.weid.protocol.base.PresentationPolicyE,
     *           com.webank.weid.protocol.base.Challenge,
     *           com.webank.weid.protocol.base.PresentationE
     *       )
     */
    @Override
    public ResponseData<Boolean> verify(
        String presenterWeId,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        PresentationE presentationE) {

        ErrorCode errorCode =
            checkInputArgs(presenterWeId, presentationPolicyE, challenge, presentationE);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[verify] checkInputArgs fail.");
            return new ResponseData<Boolean>(false, errorCode);
        }

        //verify cptId of presentationE
        List<CredentialPojo> credentialList = presentationE.getVerifiableCredential();
        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ErrorCode verifyCptIdresult =
            this.verifyCptId(policyMap, credentialList);
        if (verifyCptIdresult.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[verify] verify cptId failed.");
            return new ResponseData<Boolean>(false, verifyCptIdresult);
        }
        try {
            for (CredentialPojo credential : credentialList) {
                //verify policy
                Integer cptId = credential.getCptId();
                ClaimPolicy claimPolicy = policyMap.get(cptId);
                if (claimPolicy != null) {
                    ErrorCode verifypolicyResult = this
                        .verifyPolicy(credential, claimPolicy, presenterWeId);
                    if (verifypolicyResult.getCode() != ErrorCode.SUCCESS.getCode()) {
                        logger.error("[verify] verify policy {} failed.", policyMap);
                        return new ResponseData<Boolean>(false, verifypolicyResult);
                    }
                }
                //verify credential
                ErrorCode verifyCredentialResult = verifyContent(credential, null);
                if (verifyCredentialResult.getCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error(
                        "[verify] verify credential {} failed.", credential);
                    return new ResponseData<Boolean>(false, verifyCredentialResult);
                }
            }
            return new ResponseData<Boolean>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error(
                "[verify] verify credential error.", e);
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ErrorCode checkInputArgs(
        String presenterWeId,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        PresentationE presentationE) {

        if (StringUtils.isBlank(presenterWeId)
            || challenge == null
            || StringUtils.isBlank(challenge.getNonce())
            || !CredentialPojoUtils.checkPresentationPolicyEValid(presentationPolicyE)) {
            logger.error("[verify] presentation verify failed, please check your input.");
            return ErrorCode.ILLEGAL_INPUT;
        }

        ErrorCode errorCode = CredentialPojoUtils.checkPresentationEValid(presentationE);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[verify] presentation verify failed, error message : {}",
                errorCode.getCodeDesc()
            );
            return errorCode;
        }

        //verify presenterWeId
        if (StringUtils.isNotBlank(challenge.getWeId())
            && !presenterWeId.equals(challenge.getWeId())) {
            logger.error("[verify] The input issuer weid is not match the presentian's.");
            return ErrorCode.CREDENTIAL_PRESENTERWEID_NOTMATCH;
        }

        //verify challenge
        if (!challenge.getNonce().equals(presentationE.getNonce())) {
            logger
                .error("[verify] The nonce of challenge is not matched with the presentationE's.");
            return ErrorCode.PRESENTATION_CHALLENGE_NONCE_MISMATCH;
        }

        //verify Signature of PresentationE
        WeIdDocument weIdDocument = weIdService.getWeIdDocument(presenterWeId).getResult();
        if (weIdDocument == null) {
            logger.error(
                "[verify]presentation verify failed, because the presenter weid :{} "
                    + "does not exist.",
                presenterWeId);
            return ErrorCode.WEID_DOES_NOT_EXIST;
        }
        String signature = presentationE.getSignature();
        errorCode =
            DataToolUtils
                .verifySignatureFromWeId(presentationE.toRawData(), signature, weIdDocument);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[verify] verify presentation signature failed, error message : {}.",
                errorCode.getCodeDesc()
            );
            return ErrorCode.PRESENTATION_SIGNATURE_MISMATCH;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyCptId(
        Map<Integer, ClaimPolicy> policyMap,
        List<CredentialPojo> credentialList) {

        if (policyMap.size() > credentialList.size()) {
            return ErrorCode.CREDENTIAL_CPTID_NOTMATCH;
        } else {
            List<Integer> cptIdList = credentialList.stream().map(
                cpwl -> cpwl.getCptId()).collect(Collectors.toList());
            if (cptIdList == null || cptIdList.isEmpty()) {
                return ErrorCode.CREDENTIAL_CPTID_NOTMATCH;
            }
            if (!cptIdList.containsAll(policyMap.keySet())) {
                return ErrorCode.CREDENTIAL_CPTID_NOTMATCH;
            }
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyDisclosureAndSalt(
        Map<String, Object> disclosureMap,
        Map<String, Object> saltMap) {

        for (String disclosureK : disclosureMap.keySet()) {
            Object disclosureV = disclosureMap.get(disclosureK);
            Object saltV = saltMap.get(disclosureK);
            if (disclosureV instanceof Map) {
                ErrorCode code = verifyDisclosureAndSalt((HashMap) disclosureV, (HashMap) saltV);
                if (code.getCode() != ErrorCode.SUCCESS.getCode()) {
                    return code;
                }
            } else if (disclosureV instanceof List) {
                ArrayList<Object> disclosurs = (ArrayList<Object>) disclosureV;
                ErrorCode code = verifyDisclosureAndSaltList(disclosurs, (ArrayList<Object>) saltV);
                if (code.getCode() != ErrorCode.SUCCESS.getCode()) {
                    return code;
                }
            } else {
                String disclosure = String.valueOf(disclosureV);
               
                if (saltV == null 
                    || (!disclosure.equals(NOT_DISCLOSED) && !disclosure.equals(DISCLOSED)
                        && !disclosure.equals(EXISTED))) {
                    logger.error(
                        "[verifyDisclosureAndSalt] policy disclosureValue {} illegal.",
                        disclosureMap
                    );
                    return ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL;
                }
                
                String salt = String.valueOf(saltV);
                if ((disclosure.equals(NOT_DISCLOSED) && salt.length() > 1)
                    || (disclosure.equals(NOT_DISCLOSED) && !salt.equals(NOT_DISCLOSED))) {
                    return ErrorCode.CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE;
                }

                if (disclosure.equals(DISCLOSED) && salt.length() <= 1) {
                    return ErrorCode.CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE;
                }
            }
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyDisclosureAndSaltList(
        List<Object> disclosureList,
        List<Object> saltList
    ) {
        for (int i = 0; i < disclosureList.size(); i++) {
            Object disclosure = disclosureList.get(i);
            Object saltV = saltList.get(i);
            if (disclosure instanceof Map) {
                ErrorCode code =
                    verifyDisclosureAndSaltList(
                        (HashMap) disclosure,
                        (ArrayList<Object>) saltList
                    );
                if (code.getCode() != ErrorCode.SUCCESS.getCode()) {
                    return code;
                }
            } else if (disclosure instanceof List) {
                ErrorCode code =
                    verifyDisclosureAndSaltList(
                        (ArrayList<Object>) disclosure,
                        (ArrayList<Object>) saltV
                    );
                if (code.getCode() != ErrorCode.SUCCESS.getCode()) {
                    return code;
                }
            }
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyDisclosureAndSaltList(
        Map<String, Object> disclosure,
        List<Object> saltList
    ) {
        for (int i = 0; i < saltList.size(); i++) {
            Object saltV = saltList.get(i);
            ErrorCode code = verifyDisclosureAndSalt((HashMap) disclosure, (HashMap) saltV);
            if (code.getCode() != ErrorCode.SUCCESS.getCode()) {
                return code;
            }
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyPolicy(CredentialPojo credentialPojo, ClaimPolicy claimPolicy,
        String presenterWeId) {
        Map<String, Object> saltMap = credentialPojo.getSalt();
        String disclosure = claimPolicy.getFieldsToBeDisclosed();
        Map<String, Object> disclosureMap = DataToolUtils.deserialize(disclosure, HashMap.class);

        Object idValue = disclosureMap.get("id");
        if (idValue != null) {
            Object weid = credentialPojo.getClaim().get("id");
            if (StringUtils.equals(String.valueOf(idValue), DISCLOSED)) {
                if (!StringUtils.equals(String.valueOf(weid), presenterWeId)) {
                    logger.error(
                        "[verifyPolicy] the presenter weid->{} of presentation does not "
                            + "match the credential's ->{}. ",
                        presenterWeId,
                        weid);
                    return ErrorCode.PRESENTATION_WEID_CREDENTIAL_WEID_MISMATCH;
                }
            } else if (StringUtils.equals(String.valueOf(idValue), EXISTED)
                && !credentialPojo.getClaim().containsKey("id")) {
                logger.error(
                    "[verifyPolicy] the presenter weid->{} of presentation does not "
                        + "match the credential's ->{}. ",
                    presenterWeId,
                    weid);
                return ErrorCode.PRESENTATION_CREDENTIAL_CLAIM_WEID_NOT_EXIST;
            }
        }
        return this.verifyDisclosureAndSalt(disclosureMap, saltMap);
    }


    @Override
    public ResponseData<PresentationE> createPresentation(
        List<CredentialPojo> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        WeIdAuthentication weIdAuthentication) {

        PresentationE presentation = new PresentationE();
        try {
            // 检查输入数据完整性
            ErrorCode errorCode =
                validateCreateArgs(
                    credentialList,
                    presentationPolicyE,
                    challenge,
                    weIdAuthentication
                );
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
            generatePresentationProof(challenge, weIdAuthentication, presentation);
            return new ResponseData<PresentationE>(presentation, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("create PresentationE error", e);
            return new ResponseData<PresentationE>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ErrorCode validateCreateArgs(
        List<CredentialPojo> credentialList,
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
        List<CredentialPojo> credentialList,
        PresentationPolicyE presentationPolicyE) {
        if (CollectionUtils.isEmpty(credentialList)) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (presentationPolicyE == null || presentationPolicyE.getPolicy() == null) {
            return ErrorCode.PRESENTATION_POLICY_INVALID;
        }
        if (!WeIdUtils.isWeIdValid(presentationPolicyE.getPolicyPublisherWeId())) {
            return ErrorCode.PRESENTATION_POLICY_PUBLISHER_WEID_INVALID;
        }
        ResponseData<Boolean> weIdRes = weIdService
            .isWeIdExist(presentationPolicyE.getPolicyPublisherWeId());
        if (ErrorCode.SUCCESS.getCode() != weIdRes.getErrorCode() || !weIdRes.getResult()) {
            return ErrorCode.PRESENTATION_POLICY_PUBLISHER_WEID_NOT_EXIST;
        }
        for (CredentialPojo credentialPojo : credentialList) {
            ErrorCode checkResp = CredentialPojoUtils.isCredentialPojoValid(credentialPojo);
            if (ErrorCode.SUCCESS.getCode() != checkResp.getCode()) {
                return checkResp;
            }
        }
        List<Integer> cptIdList = credentialList.stream().map(
            cpwl -> cpwl.getCptId()).collect(Collectors.toList());
        Set<Integer> claimPolicyCptSet = presentationPolicyE.getPolicy().keySet();
        if (!cptIdList.containsAll(claimPolicyCptSet)) {
            return ErrorCode.PRESENTATION_CREDENTIALLIST_MISMATCH_CLAIM_POLICY;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode processCredentialList(
        List<CredentialPojo> credentialList,
        PresentationPolicyE presentationPolicy,
        PresentationE presentation) {

        List<CredentialPojo> newCredentialList = new ArrayList<>();
        // 获取ClaimPolicyMap
        Map<Integer, ClaimPolicy> claimPolicyMap = presentationPolicy.getPolicy();
        // 遍历所有原始证书
        for (CredentialPojo credential : credentialList) {
            // 根据原始证书获取对应的 claimPolicy
            ClaimPolicy claimPolicy = claimPolicyMap.get(credential.getCptId());
            if (claimPolicy == null) {
                continue;
            }
            // 根据原始证书和claimPolicy去创建选择性披露凭证
            ResponseData<CredentialPojo> res =
                this.createSelectiveCredential(credential, claimPolicy);
            if (res.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return ErrorCode.getTypeByErrorCode(res.getErrorCode().intValue());
            }
            newCredentialList.add(res.getResult());
        }
        presentation.setVerifiableCredential(newCredentialList);
        return ErrorCode.SUCCESS;
    }

    private void generatePresentationProof(
        Challenge challenge,
        WeIdAuthentication weIdAuthentication,
        PresentationE presentation) {

        String proofType = CredentialProofType.ECDSA.getTypeName();
        presentation.putProofValue(ParamKeyConstant.PROOF_TYPE, proofType);

        Long proofCreated = DateUtils.getNoMillisecondTimeStamp();
        presentation.putProofValue(ParamKeyConstant.PROOF_CREATED, proofCreated);

        String weIdPublicKeyId = weIdAuthentication.getWeIdPublicKeyId();
        presentation.putProofValue(ParamKeyConstant.PROOF_VERIFICATION_METHOD, weIdPublicKeyId);
        presentation.putProofValue(ParamKeyConstant.PROOF_NONCE, challenge.getNonce());
        String signature =
            DataToolUtils.sign(
                presentation.toRawData(),
                weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
            );
        presentation.putProofValue(ParamKeyConstant.PROOF_SIGNATURE, signature);
    }
}
