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

package com.webank.weid.constant;

/**
 * The Class CredentialConstant.
 *
 * @author chaoxinhu
 */

public final class CredentialConstant {

    /**
     * The Constant default Credential Context.
     */
    public static final String DEFAULT_CREDENTIAL_CONTEXT =
        "https://www.w3.org/2018/credentials/v1";
    /**
     * The Constant default Credential Context field name.
     */
    public static final String CREDENTIAL_CONTEXT_FIELD = "\"context\"";
    /**
     * The Constant default Credential Context field name in Credential Json String.
     */
    public static final String CREDENTIAL_CONTEXT_PORTABLE_JSON_FIELD = "\"@context\"";
}
