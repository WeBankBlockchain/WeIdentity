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
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.webank.weid.service.impl.CredentialPojoServiceImpl.generateSalt;

import com.webank.weid.constant.CptType;
import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialConstant.CredentialProofType;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.CredentialType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
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
            credMap.put(ParamKeyConstant.CLAIM, getClaimHash(credential, salt, disclosures));
            return DataToolUtils.mapToCompactJson(credMap);
        } catch (Exception e) {
            logger.error("get Credential Thumbprint WithoutSig error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Concat all fields of lite Credential info, without Signature, in Json format. This should be
     * invoked when calculating Credential Signature. Return null if credential format is illegal.
     * Note that: 1. Keys should be dict-ordered; 2. Claim should use standard getLiteClaimHash();
     * 3. Use compact output to avoid Json format confusion.
     *
     * @param credential target Credential object
     * @return Hash value in String.
     */
    public static String getLiteCredentialThumbprintWithoutSig(
        CredentialPojo credential) {
        try {
            Map<String, Object> credMap = DataToolUtils.objToMap(credential);
            //credMap.remove(ParamKeyConstant.ISSUANCE_DATE);
            credMap.remove(ParamKeyConstant.CONTEXT);
            credMap.put(ParamKeyConstant.PROOF_TYPE, "lite1");
            // Preserve the same behavior as in CredentialUtils - will merge later
            credMap.remove(ParamKeyConstant.PROOF);
            credMap.put(ParamKeyConstant.CLAIM, getLiteClaimHash(credential));
            return DataToolUtils.mapToCompactJson(credMap);
        } catch (Exception e) {
            logger.error("get Credential Thumbprint WithoutSig error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Check if the two credentials are equal. Will traverse each field.
     *
     * @param credOld first credential
     * @param credNew second credential
     * @return true if yes, false otherwise
     */
    public static boolean isEqual(CredentialPojo credOld, CredentialPojo credNew) {
        if (credOld == null && credNew == null) {
            return true;
        }
        if (credOld == null || credNew == null) {
            return false;
        }
        return isProofContentEqual(credOld.getProof(), credNew.getProof())
            && credOld.getCptId().equals(credNew.getCptId())
            && credOld.getExpirationDate().equals(credNew.getExpirationDate())
            && credOld.getType().equals(credNew.getType())
            && credOld.getHash().equalsIgnoreCase(credNew.getHash())
            && credOld.getContext().equalsIgnoreCase(credNew.getContext())
            && credOld.getId().equalsIgnoreCase(credNew.getId())
            && credOld.getIssuanceDate().equals(credNew.getIssuanceDate())
            && credOld.getIssuer().equalsIgnoreCase(credNew.getIssuer());
    }

    private static boolean isProofContentEqual(Object a, Object b) {
        if (a instanceof Map && b instanceof Map) {
            Set<String> keySet = ((Map) a).keySet();
            Set<String> keySetb = ((Map) b).keySet();
            for (String key : keySet) {
                if (!keySetb.contains(key)) {
                    return false;
                }
            }
            for (String key : keySetb) {
                if (!keySet.contains(key)) {
                    return false;
                }
            }
            boolean equals = true;
            for (String key : keySet) {
                equals = isProofContentEqual(((Map) a).get(key), ((Map) b).get(key));
                if (!equals) {
                    return false;
                }
            }
        } else if (a instanceof String && b instanceof String) {
            return ((String) a).equalsIgnoreCase((String) b);
        } else if (a instanceof Number && b instanceof Number) {
            return ((Number) a).intValue() == ((Number) b).intValue();
        }
        return true;
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
    private static String getCredentialPojoRawDataWithProofWithoutSalt(
        CredentialPojo credential,
        Map<String, Object> salt,
        Map<String, Object> disclosures
    ) {
        try {
            Map<String, Object> credMap = DataToolUtils.objToMap(credential);
            // Replace the Claim value object with claim hash value to preserve immutability
            credMap.put(ParamKeyConstant.CLAIM, getClaimHash(credential, salt, disclosures));
            // Remove the whole Salt field to preserve immutability
            Map<String, Object> proof = (Map<String, Object>) credMap.get(ParamKeyConstant.PROOF);
            proof.remove(ParamKeyConstant.PROOF_SALT);
            proof.put(ParamKeyConstant.PROOF_SALT, null);
            credMap.remove(ParamKeyConstant.PROOF);
            credMap.put(ParamKeyConstant.PROOF, proof);
            return DataToolUtils.mapToCompactJson(credMap);
        } catch (Exception e) {
            logger.error("get Credential Thumbprint error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Create a full CredentialPojo Hash for a Credential based on all its fields, which is
     * resistant to selective disclosure.
     *
     * @param credentialPojo target Credential object
     * @param disclosures Disclosure Map
     * @return Hash value in String.
     */
    public static String getCredentialPojoHash(CredentialPojo credentialPojo,
        Map<String, Object> disclosures) {
        String rawData = getCredentialPojoRawDataWithProofWithoutSalt(
            credentialPojo,
            credentialPojo.getSalt(),
            disclosures);
        if (StringUtils.isEmpty(rawData)) {
            return StringUtils.EMPTY;
        }
        // System.out.println(rawData);
        return DataToolUtils.sha3(rawData);
    }

    /**
     * Create a full lite CredentialPojo Hash for a Credential based on all its fields.
     *
     * @param credentialPojo target Credential object
     * @return Hash value in String.
     */
    public static String getLiteCredentialPojoHash(CredentialPojo credentialPojo) {

        try {
            Map<String, Object> credMap = DataToolUtils.objToMap(credentialPojo);
            credMap.remove(ParamKeyConstant.CONTEXT);
            credMap.put(ParamKeyConstant.PROOF_TYPE, "lite1");
            credMap.remove(ParamKeyConstant.PROOF);
            String signature = credentialPojo.getSignature();
            credMap.put(ParamKeyConstant.PROOF, signature);
            credMap.put(ParamKeyConstant.CLAIM, getLiteClaimHash(credentialPojo));
            String rawData = DataToolUtils.mapToCompactJson(credMap);
            //System.out.println("LiteCredential's Pre-Hash for evidence: " + rawData);
            return DataToolUtils.sha3(rawData);
        } catch (Exception e) {
            logger.error("get Credential Thumbprint error.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Concat the credential list (embedded) into a selective disclosure resistant String.
     *
     * @param credentialList the credential list
     * @return the String
     */
    public static String getEmbeddedCredentialThumbprintWithoutSig(
        List<CredentialPojo> credentialList) {
        String result = StringUtils.EMPTY;
        // 1. sort against id
        Map<String, CredentialPojo> credMap = new HashMap<>();
        for (CredentialPojo credential : credentialList) {
            credMap.put(credential.getId(), credential);
        }
        Map<String, CredentialPojo> treeMap = new TreeMap<>(credMap);
        List<CredentialPojo> credList = new ArrayList<>();
        for (String id : treeMap.keySet()) {
            credList.add(treeMap.get(id));
        }
        // 2. do recursive compute
        for (CredentialPojo credential : credList) {
            if (!isEmbeddedCredential(credential)) {
                result += getCredentialPojoHash(credential, null);
            } else {
                List<Object> objList = (ArrayList<Object>) credential.getClaim()
                    .get("credentialList");
                List<CredentialPojo> newList = new ArrayList<>();
                try {
                    for (Object obj : objList) {
                        if (obj instanceof CredentialPojo) {
                            newList.add((CredentialPojo) obj);
                        } else {
                            newList.add(DataToolUtils
                                .mapToObj((HashMap<String, Object>) obj, CredentialPojo.class));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed to convert credentialPojo: " + e.getMessage());
                    return null;
                }
                result += getEmbeddedCredentialThumbprintWithoutSig(newList);
            }
        }
        return result;
    }

    /**
     * Check whether a Credential is an embedded credential. Embedded Credential does not support
     * Selective disclosure and its proof is empty.
     *
     * @param credential the credentialPojo
     * @return true if yes, false otherwise
     */
    public static boolean isEmbeddedCredential(CredentialPojo credential) {
        int cptId = credential.getCptId();
        return cptId == CredentialConstant.CREDENTIAL_EMBEDDED_SIGNATURE_CPT
            || cptId == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT
            || cptId == CredentialConstant.EMBEDDED_TIMESTAMP_CPT
            || cptId == CredentialConstant.TIMESTAMP_ENVELOP_CPT;
    }

    /**
     * Convert a credential to fully undisclosed.
     *
     * @param credential the credential
     * @return true if yes, false otherwise
     */
    public static boolean convertToFullyUndisclose(CredentialPojo credential) {
        return false;
    }

    /**
     * Check whether a CPT ID is system CPT ID.
     *
     * @param id CPT ID
     * @return true if yes, false otherwise
     */
    public static boolean isSystemCptId(Integer id) {
        int cptId = id.intValue();
        return cptId == CredentialConstant.CREDENTIAL_EMBEDDED_SIGNATURE_CPT
            || cptId == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT
            || cptId == CredentialConstant.EMBEDDED_TIMESTAMP_CPT
            || cptId == CredentialConstant.TIMESTAMP_ENVELOP_CPT
            || cptId == CredentialConstant.AUTHORIZATION_CPT
            || cptId == CredentialConstant.CHALLENGE_CPT
            || cptId == CredentialConstant.CHALLENGE_VERIFICATION_CPT
            || cptId == CredentialConstant.CLAIM_POLICY_CPT
            || cptId == CredentialConstant.SERVICE_ENDPOINT_CPT;
    }

    /**
     * Convert a fully-disclosed credentialPojo to fully non-disclosed. Multi-sign credentialPojo,
     * and partially disclosed credentialPojo are not allowed in this case.
     *
     * @param credential the credential
     * @return the fully non-disclosed credential
     */
    public static ClaimPolicy generateNonDisclosedPolicy(CredentialPojo credential) {
        if (credential.getCptId().intValue()
            == CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT) {
            logger.error("Cannot convert a multi-sign credential to non-disclosed - meaningless.");
            return null;
        }
        if (CredentialPojoUtils.isSelectivelyDisclosed(credential.getSalt())) {
            logger.error("Cannot do re-selectively disclose to a credential.");
            return null;
        }
        HashMap<String, Object> claimMap = (HashMap<String, Object>) credential.getClaim();
        Map<String, Object> policyMap = DataToolUtils.clone(claimMap);
        generateSalt(policyMap, 0);
        String policyStr = DataToolUtils.serialize(policyMap);
        ClaimPolicy policy = new ClaimPolicy();
        policy.setFieldsToBeDisclosed(policyStr);
        return policy;
    }

    /**
     * Check if the given CredentialPojo is selectively disclosed, or not.
     *
     * @param saltMap the saltMap
     * @return true if yes, false otherwise
     */
    public static boolean isSelectivelyDisclosed(Map<String, Object> saltMap) {
        if (saltMap == null) {
            return false;
        }
        for (Map.Entry<String, Object> entry : saltMap.entrySet()) {
            Object v = entry.getValue();
            if (v instanceof Map) {
                if (isSelectivelyDisclosed((HashMap) v)) {
                    return true;
                }
            } else if (v instanceof List) {
                if (isSelectivelyDisclosed((ArrayList<Object>) v)) {
                    return true;
                }
            }
            if (v == null) {
                throw new WeIdBaseException(ErrorCode.CREDENTIAL_SALT_ILLEGAL);
            }
            if ("0".equals(v.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the given CredentialPojo is selectively disclosed, or not.
     *
     * @param saltList the saltList
     * @return true if yes, false otherwise
     */
    public static boolean isSelectivelyDisclosed(List<Object> saltList) {
        if (saltList == null) {
            return false;
        }
        for (Object saltObj : saltList) {
            if (saltObj instanceof Map) {
                if (isSelectivelyDisclosed((HashMap) saltObj)) {
                    return true;
                }
            } else if (saltObj instanceof List) {
                if (isSelectivelyDisclosed((ArrayList<Object>) saltObj)) {
                    return true;
                }
            }
            if (saltObj == null) {
                throw new WeIdBaseException(ErrorCode.CREDENTIAL_SALT_ILLEGAL);
            }
            if ("0".equals(saltObj.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether a credential list contains any selectively disclosed credential.
     *
     * @param credentialList the credential list
     * @return true if yes, false otherwise
     */
    public static boolean isSelectivelyDisclosedCredentialList(
        List<CredentialPojo> credentialList) {
        boolean notFound = true;
        for (CredentialPojo credentialPojo : credentialList) {
            if (isEmbeddedCredential(credentialPojo)) {
                // recursive check for inner credential
                try {
                    notFound = (notFound & !(isSelectivelyDisclosedCredentialList(
                        (ArrayList<CredentialPojo>) credentialPojo.getClaim()
                            .get("credentialList"))));
                } catch (Exception e) {
                    return false;
                }
            } else {
                notFound = (notFound & !isSelectivelyDisclosed(credentialPojo.getSalt()));
            }
            if (!notFound) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the lite credential claim hash.
     *
     * @param credential Credential
     * @return the claimMap value
     */
    public static Map<String, Object> getLiteClaimHash(
        CredentialPojo credential) {

        Map<String, Object> claim = credential.getClaim();
        return DataToolUtils.clone((HashMap) claim);
    }

    /**
     * Get the claim hash. This is irrelevant to selective disclosure.
     *
     * @param credential Credential
     * @param salt Salt Map
     * @param disclosures Disclosure Map
     * @return the claimMap value
     */
    public static Map<String, Object> getClaimHash(
        CredentialPojo credential,
        Map<String, Object> salt,
        Map<String, Object> disclosures
    ) {

        Map<String, Object> claim = credential.getClaim();
        Map<String, Object> newClaim = DataToolUtils.clone((HashMap) claim);
        addSaltAndGetHash(newClaim, salt, disclosures);
        return newClaim;
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
                    disclosureObjList = (ArrayList<Object>) disclosureObj;
                }
                addSaltAndGetHashForList(
                    (ArrayList<Object>) newClaimObj,
                    (ArrayList<Object>) saltObj,
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
                addSaltAndGetHash((HashMap) obj, (HashMap) saltObj, (HashMap) disclosureObj);
            } else if (obj instanceof List) {
                ArrayList<Object> disclosureObjList = null;
                if (disclosures != null) {
                    Object disclosureObj = disclosures.get(i);
                    if (disclosureObj != null) {
                        disclosureObjList = (ArrayList<Object>) disclosureObj;
                    }
                }
                addSaltAndGetHashForList(
                    (ArrayList<Object>) obj,
                    (ArrayList<Object>) saltObj,
                    disclosureObjList
                );
            }
        }
    }

    /**
     * Set all the values in a map to be null while preserving its key structure recursively.
     *
     * @param map the map
     */
    public static void clearMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object mapObj = map.get(key);
            if (mapObj instanceof Map) {
                clearMap((HashMap<String, Object>) mapObj);
            } else if (mapObj instanceof List) {
                clearMapList((ArrayList<Object>) mapObj);
            } else {
                map.put(key, StringUtils.EMPTY);
            }
        }
    }

    private static void clearMapList(ArrayList<Object> listObj) {
        for (int i = 0; listObj != null && i < listObj.size(); i++) {
            Object obj = listObj.get(i);
            if (obj instanceof Map) {
                clearMap((HashMap<String, Object>) obj);
            } else if (obj instanceof List) {
                clearMapList((ArrayList<Object>) obj);
            } else {
                listObj.set(i, StringUtils.EMPTY);
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
     *
     * @param credentialPojo credentialPojo
     * @return claimData of remove not disclosure data
     */
    public static Map<String, Object> getDisclosedClaim(CredentialPojo credentialPojo) {
        if (credentialPojo == null
            || !validClaimAndSaltForMap(credentialPojo.getClaim(), credentialPojo.getSalt())) {
            logger.error("getDisclosuredClaimData failed, credentialPojo is null or "
                + "claim and salt of credentialPojo not match ");
            return null;
        }
        Map<String, Object> claimMap = credentialPojo.getClaim();
        Map<String, Object> newMap = DataToolUtils.clone((HashMap<String, Object>) claimMap);
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
                    (ArrayList<Object>) saltV,
                    (ArrayList<Object>) claimV
                );
            } else {
                removeNotDisclosureData(claim, saltKey, saltV);
            }
        }
    }

    /**
     * remove credentialPojo not disclosure claimData with salt.
     *
     * @param <T> any object
     * @param credentialPojo credentialPojo
     * @param presentationPolicyId the presentation Policy Id
     * @return policy CPT object
     */
    public static <T> T getDisclosedClaimPojo(
        CredentialPojo credentialPojo,
        String presentationPolicyId) {

        ErrorCode errorCode = isCredentialPojoValid(credentialPojo);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[getDisclosedClaimPojo]The input credential is not a valid credential! "
                    + "errorCode is {}",
                errorCode);
            return null;
        }
        if (StringUtils.isEmpty(presentationPolicyId)) {
            logger.error("[getDisclosedClaimPojo]The input presentation policy id is empty.");
            return null;
        }
        Integer cptId = credentialPojo.getCptId();
        String pojoClass = new StringBuffer()
            .append(ParamKeyConstant.POLICY_PACKAGE)
            .append(ParamKeyConstant.CPT)
            .append(cptId)
            .append(ParamKeyConstant.POLICY)
            .append(presentationPolicyId)
            .toString();

        Map<String, Object> claim = credentialPojo.getClaim();
        Object claimPojoData = null;
        try {
            claimPojoData = DataToolUtils.mapToObj(claim, Class.forName(pojoClass));
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error("[getDisclosedClaimPojo] Convert claim to POJO failed, Error msg:", e);
        } catch (Exception e) {
            logger.error("[getDisclosedClaimPojo] Convert claim to POJO failed, Exception msg:", e);
        }
        return (T) claimPojoData;
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
                getDisclosureClaimData((HashMap) saltObj, (HashMap) claimObj);
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
     *
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
                if (!validClaimAndSaltForMap((HashMap) claimV, (HashMap) saltV)) {
                    return false;
                }
            } else if (claimV instanceof List) {
                ArrayList<Object> claimValue = (ArrayList<Object>) claimV;
                if (saltV instanceof ArrayList) {
                    ArrayList<Object> saltValue = (ArrayList<Object>) saltV;
                    if (!validClaimAndSaltForList(claimValue, saltValue)) {
                        return false;
                    }
                } else {
                    continue;
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
        for (int i = 0; i < claimList.size(); i++) {
            Object claimObj = claimList.get(i);
            Object saltObj = saltList.get(i);
            if (claimObj instanceof Map) {
                if (!(saltObj instanceof Map)) {
                    return false;
                }
                if (!validClaimAndSaltForMap((HashMap) claimObj, (HashMap) saltObj)) {
                    return false;
                }
            } else if (claimObj instanceof List) {
                if (!(saltObj instanceof List)) {
                    return false;
                }
                ArrayList<Object> claimObjV = (ArrayList<Object>) claimObj;
                ArrayList<Object> saltObjV = (ArrayList<Object>) saltObj;
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
            return ErrorCode.CPT_ID_ILLEGAL;
        }
        if (!WeIdUtils.isWeIdValid(args.getIssuer())) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        }

        if (args.getClaim() == null) {
            return ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS;
        }
        ErrorCode errorCode = validDateExpired(args.getIssuanceDate(), args.getExpirationDate());
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return errorCode;
        }
        if (args.getWeIdAuthentication() != null
            && !StringUtils.isEmpty(args.getWeIdAuthentication().getWeId())
            && !args.getWeIdAuthentication().getWeId().equalsIgnoreCase(args.getIssuer())) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        }
        return isWeIdAuthenticationValid(args.getWeIdAuthentication());
    }

    /**
     * Check WeIdAuthentication validity.
     *
     * @param callerAuth WeIdAuthentication
     * @return true if yes, false otherwise
     */
    public static ErrorCode isWeIdAuthenticationValid(WeIdAuthentication callerAuth) {
        if (callerAuth == null
            || callerAuth.getWeIdPrivateKey() == null
            || StringUtils.isBlank(callerAuth.getWeIdPrivateKey().getPrivateKey())
            || StringUtils.isBlank(callerAuth.getWeIdPublicKeyId())) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (!WeIdUtils.isWeIdValid(callerAuth.getWeId())) {
            return ErrorCode.WEID_INVALID;
        }
        return ErrorCode.SUCCESS;
    }

    private static ErrorCode validDateExpired(Long issuanceDate, Long expirationDate) {
        if (issuanceDate != null && issuanceDate <= 0) {
            return ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL;
        }
        if (expirationDate == null
            || expirationDate.longValue() < 0
            || expirationDate.longValue() == 0) {
            return ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL;
        }
        if (!DateUtils.isAfterCurrentTime(expirationDate)) {
            return ErrorCode.CREDENTIAL_EXPIRED;
        }
        if (issuanceDate != null && expirationDate < issuanceDate) {
            return ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL;
        }
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
        if (args.getCptId() == null || args.getCptId().intValue() < 0) {
            return ErrorCode.CPT_ID_ILLEGAL;
        }
        if (!WeIdUtils.isWeIdValid(args.getIssuer())) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        }
        if (args.getClaim() == null) {
            return ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS;
        }
        if (args.getIssuanceDate() == null) {
            return ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL;
        }
        ErrorCode errorCode = validDateExpired(args.getIssuanceDate(), args.getExpirationDate());
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return errorCode;
        }
        ErrorCode contentResponseData = isCredentialContentValid(args);
        if (ErrorCode.SUCCESS.getCode() != contentResponseData.getCode()) {
            return contentResponseData;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Check the given CredentialPojo content fields validity excluding metadata, based on its
     * input.
     *
     * @param args CredentialPojo
     * @return true if yes, false otherwise
     */
    private static ErrorCode isCredentialContentValid(CredentialPojo args) {
        String credentialId = args.getId();
        if (StringUtils.isEmpty(credentialId) || !CredentialUtils.isValidUuid(credentialId)) {
            return ErrorCode.CREDENTIAL_ID_NOT_EXISTS;
        }
        String context = args.getContext();
        if (StringUtils.isEmpty(context)) {
            return ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS;
        }
        if (CollectionUtils.isEmpty(args.getType())) {
            return ErrorCode.CREDENTIAL_TYPE_IS_NULL;
        }
        Map<String, Object> proof = args.getProof();
        return isCredentialProofValid(proof);
    }

    private static ErrorCode isCredentialProofValid(Map<String, Object> proof) {
        if (proof == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }

        String type = null;
        if (proof.get(ParamKeyConstant.PROOF_TYPE) == null) {
            return ErrorCode.CREDENTIAL_SIGNATURE_TYPE_ILLEGAL;
        } else {
            type = String.valueOf(proof.get(ParamKeyConstant.PROOF_TYPE));
            if (!isCredentialProofTypeValid(type)) {
                return ErrorCode.CREDENTIAL_SIGNATURE_TYPE_ILLEGAL;
            }
        }
        // Created is not obligatory
        if (proof.get(ParamKeyConstant.PROOF_CREATED) == null) {
            return ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL;
        } else {
            Long created = Long.valueOf(String.valueOf(proof.get(ParamKeyConstant.PROOF_CREATED)));
            if (created.longValue() <= 0) {
                return ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL;
            }
        }
        // Creator is not obligatory either
        if (proof.get(ParamKeyConstant.PROOF_CREATOR) == null) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        } else {
            String creator = String.valueOf(proof.get(ParamKeyConstant.PROOF_CREATOR));
            //if (!StringUtils.isEmpty(creator) && !WeIdUtils.isWeIdValid(creator)) {
            if (StringUtils.isEmpty(creator)) {
                return ErrorCode.CREDENTIAL_ISSUER_INVALID;
            }
        }
        // If the Proof type is ECDSA or other signature based scheme, check signature
        if (type.equalsIgnoreCase(CredentialProofType.ECDSA.getTypeName())) {
            if (proof.get(ParamKeyConstant.PROOF_SIGNATURE) == null) {
                return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
            } else {
                String signature = String.valueOf(proof.get(ParamKeyConstant.PROOF_SIGNATURE));
                if (StringUtils.isEmpty(signature)
                    || !DataToolUtils.isValidBase64String(signature)) {
                    return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
                }
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
     * check if the cpt can be used for zkp or not.
     *
     * @param cptJson the json schema of the CPT
     * @return true if can be used for zkp, otherwise fales
     */
    public static boolean isZkpCpt(String cptJson) {

        Map<String, Object> jsonSchemaMap = DataToolUtils
            .deserialize(cptJson.trim(), HashMap.class);
        if (jsonSchemaMap.containsKey(CredentialConstant.CPT_TYPE_KEY)) {
            String cptType = String.valueOf(jsonSchemaMap.get(CredentialConstant.CPT_TYPE_KEY));
            if (StringUtils.equals(cptType, CptType.ZKP.getName().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this CredentialPojo is Lite Credential.
     *
     * @param credential the credential
     * @return true if yes, false otherwise
     */
    public static boolean isLiteCredential(CredentialPojo credential) {

        List<String> types = credential.getType();
        if (types.contains(CredentialType.LITE1.getName())) {
            return true;
        }
        return false;
    }
}
