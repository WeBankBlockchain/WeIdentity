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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;

import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * The Class CredentialUtils.
 *
 * @author chaoxinhu 2019.1
 */
public final class CredentialUtils {

    /**
     * Concat all fields of Credential info, without Signature. This should be invoked when
     * calculating Credential Signature. Return null if credential format is illegal.
     *
     * @param arg target Credential object
     * @return Hash value in String.
     */
    public static String getCredentialFields(Credential arg, Map<String, Object> disclosures) {
        String metaData = concatCredentialMetadata(arg);
        if (StringUtils.isEmpty(metaData)) {
            return StringUtils.EMPTY;
        }
        String claimHash = getClaimHash(arg, disclosures);
        String rawData = metaData + WeIdConstant.PIPELINE + claimHash;
        return rawData;
    }

    private static String getClaimHash(Credential credential, Map<String, Object> disclosures) {

        Map<String, Object> claim = credential.getClaim();
        Map<String, Object> claimHashMap = new HashMap<String, Object>(claim);

        for (Map.Entry<String, Object> entry : disclosures.entrySet()) {
            if (CredentialFieldDisclosureValue.DISCLOSED.getStatus().equals(entry.getValue())) {
                claimHashMap.put(
                    entry.getKey(),
                    getFieldHash(claimHashMap.get(entry.getKey()))
                );
            }
        }

        List<Map.Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>(
            claimHashMap.entrySet()
        );
        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {

            @Override
            public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        StringBuffer hash = new StringBuffer();
        for (Map.Entry<String, Object> en : list) {
            hash.append(en.getKey()).append(en.getValue());
        }
        return hash.toString();
    }

    /**
     * convert a field to hash.
     *
     * @param field which will be converted to hash.
     * @return hash value.
     */
    public static String getFieldHash(Object field) {
        return HashUtils.sha3(String.valueOf(field));
    }

    /**
     * Concat all fields of Credential info, with signature. This should be invoked when calculating
     * Credential Evidence and currently it does not allow selective disclosure. Return null if
     * credential format is illegal.
     *
     * @param arg target Credential object
     * @return Hash value in String.
     */
    public static String getFullCredentialFields(Credential arg) {
        String metaData = concatCredentialMetadata(arg);
        if (StringUtils.isEmpty(metaData)) {
            return StringUtils.EMPTY;
        }
        String rawData = metaData
            + WeIdConstant.PIPELINE
            + JsonUtil.objToJsonStr(arg.getClaim())
            + WeIdConstant.PIPELINE
            + arg.getSignature();
        return rawData;
    }

    /**
     * Concat metadata fields of Credential info. Return null if credential format is illegal.
     *
     * @param arg target Credential object
     * @return Hash value in String.
     */
    public static String concatCredentialMetadata(Credential arg) {
        if (arg == null
            || arg.getCptId() == null
            || arg.getIssuranceDate() == null
            || arg.getExpirationDate() == null) {
            return StringUtils.EMPTY;
        }
        return arg.getContext()
            + WeIdConstant.PIPELINE
            + arg.getId()
            + WeIdConstant.PIPELINE
            + Integer.toString(arg.getCptId())
            + WeIdConstant.PIPELINE
            + arg.getIssuer()
            + WeIdConstant.PIPELINE
            + arg.getIssuranceDate().toString()
            + WeIdConstant.PIPELINE
            + arg.getExpirationDate().toString();
    }

    /**
     * Get default Credential Context String.
     *
     * @return Context value in String.
     */
    public static String getDefaultCredentialContext() {
        return WeIdConstant.DEFAULT_CERTIFICATE_CONTEXT;
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
    public static String getCredentialHash(Credential arg) {
        String rawData = getFullCredentialFields(arg);
        if (StringUtils.isEmpty(rawData)) {
            return StringUtils.EMPTY;
        }
        return HashUtils.sha3(rawData);
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
        byte[] uuidBytes = mergedId.getBytes();
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
    public static ResponseData<Boolean> isCreateCredentialArgsValid(
        CreateCredentialArgs args) {
        if (args == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (args.getCptId() == null || args.getCptId().intValue() < 0) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_CPT_NOT_EXISTS);
        }
        if (!WeIdUtils.isWeIdValid(args.getIssuer())) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ISSUER_INVALID);
        }
        Long expirationDate = args.getExpirationDate();
        if (expirationDate == null
            || expirationDate.longValue() < 0
            || expirationDate.longValue() == 0) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL);
        }
        if (args.getClaim() == null || args.getClaim().isEmpty()) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS);
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Check the given Credential validity based on its input params.
     *
     * @param args Credential
     * @return true if yes, false otherwise
     */
    public static ResponseData<Boolean> isCredentialValid(Credential args) {
        if (args == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        CreateCredentialArgs createCredentialArgs = extractCredentialMetadata(args);
        ResponseData<Boolean> metadataResponseData =
            isCreateCredentialArgsValid(createCredentialArgs);
        if (!metadataResponseData.getResult()) {
            return metadataResponseData;
        }
        ResponseData<Boolean> contentResponseData = isCredentialContentValid(args);
        if (!contentResponseData.getResult()) {
            return contentResponseData;
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Check the given Credential content fields validity excluding metadata, based on its input.
     *
     * @param args Credential
     * @return true if yes, false otherwise
     */
    public static ResponseData<Boolean> isCredentialContentValid(Credential args) {
        String credentialId = args.getId();
        if (StringUtils.isEmpty(credentialId) || !CredentialUtils.isValidUuid(credentialId)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ID_NOT_EXISTS);
        }
        String context = args.getContext();
        if (StringUtils.isEmpty(context)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS);
        }
        Long issuranceDate = args.getIssuranceDate();
        if (issuranceDate == null) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL);
        }
        if (issuranceDate.longValue() > args.getExpirationDate().longValue()) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EXPIRED);
        }
        String signature = args.getSignature();
        if (StringUtils.isEmpty(signature) || !SignatureUtils.isValidBase64String(signature)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_SIGNATURE_BROKEN);
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Check the given CreateEvidenceArgs validity based on its input params.
     *
     * @param credential the given credential
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence address. Return empty string if failed due to any reason.
     */
    public static ResponseData<Boolean> isCreateEvidenceArgsValid(Credential credential,
        WeIdPrivateKey weIdPrivateKey) {
        if (credential == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS);
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }
}
