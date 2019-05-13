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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoWrapper;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;

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
            Map<String, Object> credMap = JsonUtil.objToMap(credential);
            credMap.remove(ParamKeyConstant.CREDENTIAL_SIGNATURE);
            String claimHash = getClaimHash(credential, salt, disclosures);
            credMap.put(ParamKeyConstant.CLAIM, claimHash);
            return JsonUtil.mapToCompactJson(credMap);
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
    public static String getCredentialThumbprint(CredentialPojo credential,Map<String, Object> salt,
        Map<String, Object> disclosures) {
        try {
            Map<String, Object> credMap = JsonUtil.objToMap(credential);
            String claimHash = getClaimHash(credential, salt,disclosures);
            credMap.put(ParamKeyConstant.CLAIM, claimHash);
            return JsonUtil.mapToCompactJson(credMap);
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
    public static String getClaimHash(CredentialPojo credential, Map<String, Object> salt, Map<String, Object> disclosures) {

		Map<String, Object> claim = credential.getClaim();
		Map<String, Object> newClaim = DataToolUtils.clone((HashMap) claim);
        
		addSaltAndGetHash(newClaim, salt, disclosures);
		try {
			String jsonData = JsonUtil.mapToCompactJson(newClaim);
			return jsonData;
		} catch (Exception e) {
			logger.error("[getClaimHash] get claim hash failed. {}", e);
		}
		return StringUtils.EMPTY;
    }
    
    private static void addSaltAndGetHash(Map<String, Object> claim, Map<String, Object> salt,Map<String, Object> disclosures) {
    	    	
		for (Map.Entry<String, Object> entry : claim.entrySet()) {
			String key = entry.getKey();
			Object disclosureObj = null;
			if (disclosures != null) {
				disclosureObj = disclosures.get(key);
			}
			Object saltObj = salt.get(key);
			Object newClaimObj = claim.get(key);

			if (newClaimObj instanceof Map) {
				addSaltAndGetHash((HashMap) newClaimObj, (HashMap) saltObj, (HashMap) disclosureObj);
			} else {
			    if (disclosureObj == null) {
			        if (!CredentialFieldDisclosureValue.NOT_DISCLOSED.getStatus().equals(saltObj)) {
			            (claim).put(key,
	                            getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj)));
			        }
			    }
			}
		}
    }
    
    
    /**
     * convert a field to hash.
     *
     * @param field which will be converted to hash.
     * @return hash value.
     */
    public static String getFieldHash(Object field) {
        return DataToolUtils.sha3(String.valueOf(field));
    }

    /**
     * Get default Credential Context String.
     *
     * @return Context value in String.
     */
    public static String getDefaultCredentialContext() {
        return CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT;
    }

    /**
     * Extract GenerateCredentialArgs from Credential.
     *
     * @param arg the arg
     * @return GenerateCredentialArgs
     */
    public static CreateCredentialArgs extractCredentialMetadata(Credential arg) {
        if (arg == null) {
            return null;
        }
        CreateCredentialArgs generateCredentialArgs = new CreateCredentialArgs();
        generateCredentialArgs.setCptId(arg.getCptId());
        generateCredentialArgs.setIssuer(arg.getIssuer());
        generateCredentialArgs.setExpirationDate(arg.getExpirationDate());
        generateCredentialArgs.setClaim(arg.getClaim());
        return generateCredentialArgs;
    }

    /**
     * Create a full Credential Hash for a Credential based on all its fields. This should be
     * invoked when getting Credential Evidence. Please note: the result is a String with fixed
     * length 66 bytes including the first two bytes ("0x") and 64 bytes Hash value..
     *
     * @param arg the args
     * @return Hash in byte array
     */
    public static String getCredentialHash(CredentialPojo arg) {
        String rawData = getCredentialThumbprint(arg, null,null);
        if (StringUtils.isEmpty(rawData)) {
            return StringUtils.EMPTY;
        }
        return DataToolUtils.sha3(rawData);
    }

    /**
     * Convert a Credential ID to a Bytes32 object. The "-" connector will be removed.
     *
     * @param id the Credential id
     * @return a Bytes32 object
     */
    public static Bytes32 convertCredentialIdToBytes32(String id) {
		if (!isValidUuid(id)) {
            return new Bytes32(new byte[32]);
        }
        String mergedId = id.replaceAll(WeIdConstant.UUID_SEPARATOR, StringUtils.EMPTY);
        byte[] uuidBytes = mergedId.getBytes(StandardCharsets.UTF_8);
        return DataTypetUtils.bytesArrayToBytes32(uuidBytes);
    }

    /**
     * Check whether the given String is a valid UUID.
     *
     * @param id the Credential id
     * @return true if yes, false otherwise
     */
    public static boolean isValidUuid(String id) {
        Pattern p = Pattern.compile(WeIdConstant.UUID_PATTERN);
        return p.matcher(id).matches();
    }

    /**
     * Check the given CreateCredentialArgs validity based on its input params.
     *
     * @param args CreateCredentialArgs
     * @return true if yes, false otherwise
     */
    public static ErrorCode isCreateCredentialArgsValid(
        CreateCredentialArgs args) {
        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (args.getCptId() == null || args.getCptId().intValue() < 0) {
            return ErrorCode.CREDENTIAL_CPT_NOT_EXISTS;
        }
        if (!WeIdUtils.isWeIdValid(args.getIssuer())) {
            return ErrorCode.CREDENTIAL_ISSUER_INVALID;
        }
        Long expirationDate = args.getExpirationDate();
        if (expirationDate == null
            || expirationDate.longValue() < 0
            || expirationDate.longValue() == 0) {
            return ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL;
        }
        if (args.getClaim() == null || args.getClaim().isEmpty()) {
            return ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Check the given Credential validity based on its input params.
     *
     * @param args Credential
     * @return true if yes, false otherwise
     */
    public static ErrorCode isCredentialValid(Credential args) {
        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        CreateCredentialArgs createCredentialArgs = extractCredentialMetadata(args);
        ErrorCode metadataResponseData = isCreateCredentialArgsValid(createCredentialArgs);
        if (ErrorCode.SUCCESS.getCode() != metadataResponseData.getCode()) {
            return metadataResponseData;
        }
        ErrorCode contentResponseData = isCredentialContentValid(args);
        if (ErrorCode.SUCCESS.getCode() != contentResponseData.getCode()) {
            return contentResponseData;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Check the given Credential content fields validity excluding metadata, based on its input.
     *
     * @param args Credential
     * @return true if yes, false otherwise
     */
    public static ErrorCode isCredentialContentValid(Credential args) {
        String credentialId = args.getId();
        if (StringUtils.isEmpty(credentialId) || !CredentialPojoUtils.isValidUuid(credentialId)) {
            return ErrorCode.CREDENTIAL_ID_NOT_EXISTS;
        }
        String context = args.getContext();
        if (StringUtils.isEmpty(context)) {
            return ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS;
        }
        Long issuranceDate = args.getIssuranceDate();
        if (issuranceDate == null) {
            return ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL;
        }
        if (issuranceDate.longValue() > args.getExpirationDate().longValue()) {
            return ErrorCode.CREDENTIAL_EXPIRED;
        }
        String signature = args.getSignature();
        if (StringUtils.isEmpty(signature) || !DataToolUtils.isValidBase64String(signature)) {
            return ErrorCode.CREDENTIAL_SIGNATURE_BROKEN;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Check the given CreateEvidenceArgs validity based on its input params.
     *
     * @param credential the given credential
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence address. Return empty string if failed due to any reason.
     */
    public static ErrorCode isCreateEvidenceArgsValid(
        Credential credential,
        WeIdPrivateKey weIdPrivateKey) {
        if (credential == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS;
        }
        return ErrorCode.SUCCESS;
    }
    
    
	public static String getCredentialPojoHash(CredentialPojoWrapper credentialWrapper) {
		
		String rawData = getCredentialThumbprint(credentialWrapper.getCredentialPojo(), credentialWrapper.getSalt(),
				null);
		if (StringUtils.isEmpty(rawData)) {
			return StringUtils.EMPTY;
		}
		return DataToolUtils.sha3(rawData);
	}
	
    /**
     * Check the credential and proof of presentationE
     * 
     * @param presentationE
     * @return
     */
    public static ErrorCode checkPresentationEValid(PresentationE presentationE) {
        if(presentationE == null || presentationE.getCredentialList() == null 
                || presentationE.getCredentialList().isEmpty() 
                || presentationE.getProof() == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if(StringUtils.isEmpty(presentationE.getSignature())) {
            return ErrorCode.CREDENTIAL_SIGNATURE_NOT_EXISTS;
        }
        return ErrorCode.SUCCESS;
    }
	
    /**
     * Check the policy of PresentationPolicyE 
     * 
     * @param presentationPolicyE
     * @return
     */
    public static boolean checkPresentationPolicyEValid(PresentationPolicyE presentationPolicyE) {
        return (presentationPolicyE != null 
                && presentationPolicyE.getPolicy() != null 
                && presentationPolicyE.getPolicy().size() != 0);
    }
    
    
    public static String getFieldSaltHash(String field, String salt) {
    	return DataToolUtils.sha3(String.valueOf(field) + String.valueOf(salt));
    }
}
