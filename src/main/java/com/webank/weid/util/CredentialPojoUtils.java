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

package com.webank.weid.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;

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

    /**
     * Concat all fields of Credential info, without Signature, in Json format. This should be
     * invoked when calculating Credential Signature. Return null if credential format is illegal.
     * Note that: 1. Keys should be dict-ordered; 2. Claim should use standard getClaimHash() to
     * support selective disclosure; 3. Use compact output to avoid Json format confusion.
     *
     * @param credential target Credential object
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
            return StringUtils.EMPTY;
        }
    }

    /**
     * Concat all fields of Credential info, with signature. This should be invoked when calculating
     * Credential Evidence. Return null if credential format is illegal.
     *
     * @param credential target Credential object
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
            return StringUtils.EMPTY;
        }
    }

    /**
     * Get the claim hash. This is irrelevant to selective disclosure.
     *
     * @param credential Credential
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
        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            String key = entry.getKey();
            Object disclosureObj = null;
            if (disclosures != null) {
                disclosureObj = disclosures.get(key);
            }
            Object saltObj = salt.get(key);
            Object newClaimObj = claim.get(key);

            if (newClaimObj instanceof Map) {
                addSaltAndGetHash(
                    (HashMap) newClaimObj,
                    (HashMap) saltObj,
                    (HashMap) disclosureObj
                );
            } else {
                if (disclosureObj == null) {
                    if (!CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus().equals(saltObj)) {
                        claim.put(
                            key,
                            getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj))
                        );
                    }
                }
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
}
