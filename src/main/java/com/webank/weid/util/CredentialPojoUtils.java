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

package com.webank.weid.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialConstant.CredentialProofType;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;

/**
 * The Class CredentialUtils.
 *
 * @author chaoxinhu 2019.1
 */
public final class CredentialPojoUtils {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(CredentialPojoUtils.class);
    
    private static final String CLAIM_ID = "id";
    private static final String PROPERTIES = "properties";
    
    private static Integer NOT_DISCLOSED = 
        CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus();
    
    /**
     * Concat all fields of Credential info, without Signature, in Json format. This should be
     * invoked when calculating Credential Signature. Return null if credential format is illegal.
     * Note that: 1. Keys should be dict-ordered; 2. Claim should use standard getClaimHash() to
     * support selective disclosure; 3. Use compact output to avoid Json format confusion.
     *
     * @param credential target Credential object
     * @param salt Salt Map
     * @param disclosures Disclosure Map
     * @return Hash value in String.
     */
    public static String getCredentialThumbprintWithoutSig(
        CredentialPojo credential,
        Map<String, Object> salt,
        Map<String, Object> disclosures) {
        try {
            Map<String, Object> credMap = DataToolUtils.objToMap(credential);
            // Preserve the same behavior as in CredentialUtils - will merge later
            credMap.remove(ParamKeyConstant.PROOF);
            credMap.put(ParamKeyConstant.PROOF, null);
            String claimHash = getClaimHash(credential, salt, disclosures);
            credMap.put(ParamKeyConstant.CLAIM, claimHash);
            return DataToolUtils.mapToCompactJson(credMap);
        } catch (Exception e) {
            logger.error("get Credential Thumbprint WithoutSig error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Concat all fields of Credential info, with signature. This should be invoked when calculating
     * Credential Evidence. Return null if credential format is illegal.
     *
     * @param credential target Credential object
     * @param salt Salt Map
     * @param disclosures Disclosure Map
     * @return Hash value in String.
     */
    public static String getCredentialThumbprint(
        CredentialPojo credential,
        Map<String, Object> salt,
        Map<String, Object> disclosures
    ) {
        try {
            Map<String, Object> credMap = DataToolUtils.objToMap(credential);
            String claimHash = getClaimHash(credential, salt, disclosures);
            credMap.put(ParamKeyConstant.CLAIM, claimHash);
            return DataToolUtils.mapToCompactJson(credMap);
        } catch (Exception e) {
            logger.error("get Credential Thumbprint error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Get the claim hash. This is irrelevant to selective disclosure.
     *
     * @param credential Credential
     * @param salt Salt Map
     * @param disclosures Disclosure Map
     * @return the unique claim hash value
     */
    public static String getClaimHash(
        CredentialPojo credential,
        Map<String, Object> salt,
        Map<String, Object> disclosures
    ) {

        Map<String, Object> claim = credential.getClaim();
        Map<String, Object> newClaim = DataToolUtils.clone((HashMap) claim);
        addSaltAndGetHash(newClaim, salt, disclosures);
        try {
            String jsonData = DataToolUtils.mapToCompactJson(newClaim);
            return jsonData;
        } catch (Exception e) {
            logger.error("[getClaimHash] get claim hash failed. {}", e);
        }
        return StringUtils.EMPTY;
    }

    private static void addSaltAndGetHash(
        Map<String, Object> claim,
        Map<String, Object> salt,
        Map<String, Object> disclosures
    ) {
        for (Map.Entry<String, Object> entry : salt.entrySet()) {
            String key = entry.getKey();
            Object disclosureObj = null;
            if (disclosures != null) {
                disclosureObj = disclosures.get(key);
            }
            Object saltObj = salt.get(key);
            Object newClaimObj = claim.get(key);

            if (saltObj instanceof Map) {
                addSaltAndGetHash(
                    (HashMap) newClaimObj,
                    (HashMap) saltObj,
                    (HashMap) disclosureObj
                );
            } else if (saltObj instanceof List) {
                ArrayList<Object> disclosureObjList = null;
                if (disclosureObj != null) {
                    disclosureObjList = (ArrayList<Object>)disclosureObj;
                }
                addSaltAndGetHashForList(
                    (ArrayList<Object>)newClaimObj,
                    (ArrayList<Object>)saltObj,
                    disclosureObjList
                );
            } else {
                addSaltByDisclose(claim, key, disclosureObj, saltObj, newClaimObj);
            }
        }
    }

    private static void addSaltByDisclose(
        Map<String, Object> claim,
        String key,
        Object disclosureObj,
        Object saltObj,
        Object newClaimObj
    ) {
        if (disclosureObj == null) {
            if (!NOT_DISCLOSED.toString().equals(saltObj.toString())) {
                claim.put(
                    key,
                    getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj))
                );
            }
        } else if (NOT_DISCLOSED.toString().equals(disclosureObj.toString())) {           
            claim.put(
                key,
                getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj))
            );
        }
    }
    
    private static void addSaltAndGetHashForList(
        List<Object> claim,
        List<Object> salt,
        List<Object> disclosures
    ) {
        for (int i = 0; claim != null && i < claim.size(); i++) {
            Object obj = claim.get(i);
            Object saltObj = salt.get(i);
            if (obj instanceof Map) {
                Object disclosureObj = null;
                if (disclosures != null) {
                    disclosureObj = disclosures.get(0);
                }
                addSaltAndGetHash((HashMap)obj, (HashMap)saltObj, (HashMap)disclosureObj);
            } else if (obj instanceof List) {
                ArrayList<Object> disclosureObjList = null;
                if (disclosures != null) {
                    Object disclosureObj = disclosures.get(i);
                    if (disclosureObj != null) {
                        disclosureObjList = (ArrayList<Object>)disclosureObj;
                    }
                }
                addSaltAndGetHashForList(
                    (ArrayList<Object>)obj,
                    (ArrayList<Object>)saltObj,
                    disclosureObjList
                );
            }
        }
    }

    /**
     * Check the credential and proof of presentationE.
     *
     * @param presentationE the presentation
     * @return return the check code
     */
    public static ErrorCode checkPresentationEValid(PresentationE presentationE) {
        if (presentationE == null || presentationE.getVerifiableCredential() == null
            || presentationE.getVerifiableCredential().isEmpty()
            || presentationE.getProof() == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (StringUtils.isEmpty(presentationE.getSignature())) {
            return ErrorCode.CREDENTIAL_SIGNATURE_NOT_EXISTS;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Check the validity of a given policy of PresentationPolicyE.
     *
     * @param presentationPolicyE the presentationPolicyE
     * @return true if yes, false otherwise
     */
    public static boolean checkPresentationPolicyEValid(PresentationPolicyE presentationPolicyE) {
        return (presentationPolicyE != null
            && presentationPolicyE.getPolicy() != null
            && presentationPolicyE.getPolicy().size() != 0);
    }

    /**
     * Get per-field salted hash value.
     *
     * @param field the field value
     * @param salt the salt value
     * @return the hash value
     */
    public static String getFieldSaltHash(String field, String salt) {
        return DataToolUtils.sha3(String.valueOf(field) + String.valueOf(salt));
    }
    
    /**
     * remove credentialPojo not disclosure claimData with salt.
     * @param credentialPojo credentialPojo
     * @return claimData of remove not disclosure data
     */
    public static Map<String, Object> getDisclosuredClaimData(CredentialPojo credentialPojo) {
        if (credentialPojo == null 
            || !validClaimAndSaltForMap(credentialPojo.getClaim(), credentialPojo.getSalt())) {
            logger.error("getDisclosuredClaimData failed, credentialPojo is null or "
                + "claim and salt of credentialPojo not match ");
            return null;
        }
        Map<String, Object> claimMap = credentialPojo.getClaim(); 
        Map<String, Object> newMap = DataToolUtils.clone((HashMap<String, Object>)claimMap);
        Map<String, Object> saltMap = credentialPojo.getSalt();
        getDisclosureClaimData(saltMap, newMap);
        return newMap;
    }
    
    private static void getDisclosureClaimData(
        Map<String, Object> saltMap,
        Map<String, Object> claim
    ) {
        for (Map.Entry<String, Object> entry : saltMap.entrySet()) {
            String saltKey = entry.getKey();
            Object saltV = entry.getValue();
            Object claimV = claim.get(saltKey);
            if (saltV instanceof Map) {
                getDisclosureClaimData((HashMap) saltV, (HashMap) claimV);
            } else if (saltV instanceof List) { 
                getDisclosureClaimDataForList(
                    (ArrayList<Object>)saltV,
                    (ArrayList<Object>)claimV
                );
            } else {
                removeNotDisclosureData(claim, saltKey, saltV);
            }
        }
    }

    private static void removeNotDisclosureData(
        Map<String, Object> claim,
        String saltKey,
        Object saltV
    ) {
        if (!StringUtils.isBlank(saltV.toString()) 
            && (String.valueOf(saltV)).equals(NOT_DISCLOSED.toString())) {
            claim.remove(saltKey);
        }
    }
    
    private static void getDisclosureClaimDataForList(List<Object> salt, List<Object> claim) {
        for (int i = 0; claim != null && i < salt.size(); i++) {
            Object saltObj = salt.get(i);
            Object claimObj = claim.get(i);
            if (saltObj instanceof Map) {
                getDisclosureClaimData((HashMap)saltObj, (HashMap)claimObj);
            } else if (saltObj instanceof List) {
                getDisclosureClaimDataForList(
                    (ArrayList<Object>) saltObj,
                    (ArrayList<Object>) claimObj
                );
            }
        }
    }
    
    /**
     * valid claim and salt.
     * @param claim claimMap
     * @param salt saltMap
     * @return boolean
     */
    public static boolean validClaimAndSaltForMap(
        Map<String, Object> claim, 
        Map<String, Object> salt) {
        //检查是否为空
        if (claim == null || salt == null) {
            return false;
        }
        //检查每个map里的key个数是否相同
        Set<String> claimKeys = claim.keySet();
        Set<String> saltKeys = salt.keySet();
        if (claimKeys.size() != saltKeys.size()) {
            return false;
        }
        //检查key值是否一致
        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            String k = entry.getKey();
            Object claimV = entry.getValue();
            Object saltV = salt.get(k);
            if (!salt.containsKey(k)) {
                return false;
            }
            if (claimV instanceof Map) {
                //递归检查
                if (!validClaimAndSaltForMap((HashMap)claimV, (HashMap)saltV)) {
                    return false;
                }
            } else if (claimV instanceof List) {
                ArrayList<Object> claimValue = (ArrayList<Object>)claimV;
                ArrayList<Object> saltValue = (ArrayList<Object>)saltV;
                if (!validClaimAndSaltForList(claimValue, saltValue)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean validClaimAndSaltForList(
        List<Object> claimList,
        List<Object> saltList) {
        //检查是否为空
        if (claimList == null || saltList == null) {
            return false;
        }
        if (claimList.size() != saltList.size()) {
            return false;
        }
        for (int i = 0; i < claimList.size(); i++) {
            Object claimObj = claimList.get(i);
            Object saltObj = saltList.get(i);
            if (claimObj instanceof Map) {
                if (!(saltObj instanceof Map)) {
                    return false;
                }
                if (!validClaimAndSaltForMap((HashMap)claimObj, (HashMap)saltObj)) {
                    return false;
                }
            } else if (claimObj instanceof List) {
                if (!(saltObj instanceof List)) {
                    return false;
                }
                ArrayList<Object> claimObjV = (ArrayList<Object>)claimObj;
                ArrayList<Object> saltObjV = (ArrayList<Object>)saltObj;
                if (!validClaimAndSaltForList(claimObjV, saltObjV)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Check the given CreateCredentialPojoArgs validity based on its input params.
     *
     * @param args CreateCredentialPojoArgs
     * @return true if yes, false otherwise
     */
    public static ErrorCode isCreateCredentialPojoArgsValid(
        CreateCredentialPojoArgs args) {
        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (args.getCptId() == null || args.getCptId().intValue() < 0) {
            return ErrorCode.CREDENTIAL_CPT_NOT_EXISTS;
        }
        if (!WeIdUtils.isWeIdValid(args.getIssuer())) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        }               
        
        if (args.getClaim() == null) {
            return ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS;
        }
        ErrorCode errorCode = validDateExpired(args);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return errorCode;
        }
        return ErrorCode.SUCCESS;
    }
    
    private static ErrorCode validDateExpired(CreateCredentialPojoArgs args) {
        Long issuanceDate = args.getIssuanceDate();
        if (issuanceDate != null && issuanceDate <= 0) {
            return ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL;
        }
        Long expirationDate = args.getExpirationDate();
        if (expirationDate == null
            || expirationDate.longValue() < 0
            || expirationDate.longValue() == 0
            || (issuanceDate != null && expirationDate < issuanceDate)
            || (issuanceDate == null && DateUtils.isBeforeCurrentTime(expirationDate))) {
            return ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL;
        }
        if (issuanceDate != null) {
            args.setIssuanceDate(DateUtils.convertToNoMillisecondTimeStamp(issuanceDate));
        }
        args.setExpirationDate(DateUtils.convertToNoMillisecondTimeStamp(expirationDate));
        return ErrorCode.SUCCESS;        
    }
     
    /**
     * Check the given CredentialPojo validity based on its input params.
     *
     * @param args CredentialPojo
     * @return true if yes, false otherwise
     */
    public static ErrorCode isCredentialPojoValid(CredentialPojo args) {
        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        CreateCredentialPojoArgs createCredentialArgs = extractCredentialMetadata(args);
        ErrorCode metadataResponseData = isCreateCredentialPojoArgsValid(createCredentialArgs);
        if (ErrorCode.SUCCESS.getCode() != metadataResponseData.getCode()) {
            return metadataResponseData;
        }
        ErrorCode contentResponseData = isCredentialContentValid(args);
        if (ErrorCode.SUCCESS.getCode() != contentResponseData.getCode()) {
            return contentResponseData;
        }
        if (args.getIssuanceDate() != null) {
            args.setIssuanceDate(
                DateUtils.convertToNoMillisecondTimeStamp(args.getIssuanceDate()));
        }
        args.setExpirationDate(
            DateUtils.convertToNoMillisecondTimeStamp(args.getExpirationDate()));
        if (!validateContainIdKeyForClaim(args.getClaim())) {
            return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Extract GenerateCredentialPojoArgs from CredentialPojo.
     *
     * @param arg the arg
     * @return GenerateCredentialPojoArgs
     */
    public static CreateCredentialPojoArgs extractCredentialMetadata(CredentialPojo arg) {
        if (arg == null) {
            return null;
        }
        CreateCredentialPojoArgs generateCredentialArgs = new CreateCredentialPojoArgs();
        generateCredentialArgs.setCptId(arg.getCptId());
        generateCredentialArgs.setIssuer(arg.getIssuer());
        generateCredentialArgs.setExpirationDate(arg.getExpirationDate());
        generateCredentialArgs.setClaim(arg.getClaim());
        return generateCredentialArgs;
    }
    
    /**
     * Check the given CredentialPojo content fields validity excluding metadata, 
     * based on its input.
     *
     * @param args CredentialPojo
     * @return true if yes, false otherwise
     */
    public static ErrorCode isCredentialContentValid(CredentialPojo args) {
        String credentialId = args.getId();
        if (StringUtils.isEmpty(credentialId) || !CredentialUtils.isValidUuid(credentialId)) {
            return ErrorCode.CREDENTIAL_ID_NOT_EXISTS;
        }
        String context = args.getContext();
        if (StringUtils.isEmpty(context)) {
            return ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS;
        }
        Map<String, Object> proof = args.getProof();
        return isCredentialProofValid(proof);
    }

    private static ErrorCode isCredentialProofValid(Map<String, Object> proof) {
        if (proof == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        String type = String.valueOf(proof.get(ParamKeyConstant.PROOF_TYPE));
        if (!isCredentialProofTypeValid(type)) {
            return ErrorCode.CREDENTIAL_SIGNATURE_TYPE_ILLEGAL;
        }
        // Created is not obligatory
        Long created = Long.valueOf(String.valueOf(proof.get(ParamKeyConstant.PROOF_CREATED)));
        if (created.longValue() <= 0) {
            return ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL;
        }
        // Creator is not obligatory either
        String creator = String.valueOf(proof.get(ParamKeyConstant.PROOF_CREATOR));
        //if (!StringUtils.isEmpty(creator) && !WeIdUtils.isWeIdValid(creator)) {
        if (StringUtils.isEmpty(creator)) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        }
        // If the Proof type is ECDSA or other signature based scheme, check signature
        if (type.equalsIgnoreCase(CredentialProofType.ECDSA.getTypeName())) {
            String signature = String.valueOf(proof.get(ParamKeyConstant.CREDENTIAL_SIGNATURE));
            if (StringUtils.isEmpty(signature) || !DataToolUtils.isValidBase64String(signature)) {
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            }
        }
        return ErrorCode.SUCCESS;
    }
    
    private static boolean isCredentialProofTypeValid(String type) {
        // Proof type must be one of the pre-defined types.
        if (!StringUtils.isEmpty(type)) {
            for (CredentialProofType proofType : CredentialConstant.CredentialProofType.values()) {
                if (StringUtils.equalsIgnoreCase(type, proofType.getTypeName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * check if the given map contain correct id.
     * @param cptJsonSchema cptJsonSchema
     * @return boolean
     */
    public static boolean validateContainIdKeyForCpt(Map<String, Object> cptJsonSchema) {
        if (cptJsonSchema == null || cptJsonSchema.isEmpty()) {
            return false;
        }
        if (cptJsonSchema.containsKey(CLAIM_ID)) {   
            return true;
        } else if (cptJsonSchema.containsKey(PROPERTIES)) {
            HashMap<String, Object> propertiesMap = 
                (HashMap<String, Object>) cptJsonSchema.get(PROPERTIES);
            return propertiesMap.containsKey(CLAIM_ID);        
        } else {
            return false;
        }
    }
    
    /**
     * check if the given map contain correct id.
     * @param claimMap claimMap
     * @return boolean
     */
    public static boolean validateContainIdKeyForClaim(Map<String, Object> claimMap) {
        if (claimMap == null || claimMap.isEmpty()) {
            return false;
        }
        return claimMap.containsKey(CLAIM_ID);
    }
    
    /**
     * check if the given map contain correct id and the value is an WeId.
     * @param claimMap claimMap
     * @return boolean
     */
    public static boolean validateIdValueForClaim(Map<String, Object> claimMap) {
        if (!validateContainIdKeyForClaim(claimMap)) {
            return false;
        }
        String weId = (String)(claimMap.get(CLAIM_ID));
        return WeIdUtils.isWeIdValid(weId);
    }
}
