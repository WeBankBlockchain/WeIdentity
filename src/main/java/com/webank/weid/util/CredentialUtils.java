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

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;

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
    public static String getCredentialFields(Credential arg) {
        if (arg == null
            || arg.getCptId() == null
            || arg.getIssuranceDate() == null
            || arg.getExpirationDate() == null) {
            return StringUtils.EMPTY;
        }
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
                + arg.getClaim();
        return rawData;
    }

    /**
     * Craft a non-masquerade hash which contains all fields of Credential info.
     *
     * @param arg the arg
     * @return Hash value in String.
     */
    public static String getCredentialFields(VerifyCredentialArgs arg) {
        if (arg == null) {
            return StringUtils.EMPTY;
        }
        return getCredentialFields(extractCredentialResult(arg));
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
     * Extract Credential from VerifyCredentialArgs.
     *
     * @param arg the arg
     * @return Credential
     */
    public static Credential extractCredentialResult(VerifyCredentialArgs arg) {
        if (arg == null) {
            return null;
        }
        Credential credential = arg.getCredential();
        return credential;
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
