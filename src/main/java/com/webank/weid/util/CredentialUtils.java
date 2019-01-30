/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;

/**
 * The Class CredentialUtils.
 *
 * @author chaoxinhu 2018.10
 */
public final class CredentialUtils {

    /**
     * Concat all fields of Credential info.
     *
     * @param arg target Credential object
     * @return Hash value in String.
     */
    public static String getCredentialFields(Credential arg, Map<String, Object> disclosures) {
        if (arg == null
            || arg.getCptId() == null
            || arg.getIssuranceDate() == null
            || arg.getExpirationDate() == null) {
            return StringUtils.EMPTY;
        }

        String claimHash = getClaimHash(arg, disclosures);

        String rawData =
            arg.getContext()
                + WeIdConstant.PIPELINE
                + arg.getId()
                + WeIdConstant.PIPELINE
                + Integer.toString(arg.getCptId())
                + WeIdConstant.PIPELINE
                + arg.getIssuer()
                + WeIdConstant.PIPELINE
                + arg.getIssuranceDate().toString()
                + WeIdConstant.PIPELINE
                + arg.getExpirationDate().toString()
                + WeIdConstant.PIPELINE
                + claimHash;
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
     * @param field which will be converted to hash.
     * @return hash value.
     */
    public static String getFieldHash(Object field) {
        return HashUtils.sha3(String.valueOf(field));
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
}
