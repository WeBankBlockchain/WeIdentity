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

package com.webank.weid.constant;

import java.math.BigInteger;

/**
 * The Class WeIdConstant.
 *
 * @author tonychen
 */
public final class WeIdConstant {

    /**
     * The Constant WeIdentity DID Document Protocol Version.
     */
    public static final String WEID_DOC_PROTOCOL_VERSION =
        "\"@context\": \"https://weidentity.webank.com/did/v1\",";

    /**
     * The Constant GAS_PRICE.
     */
    public static final BigInteger GAS_PRICE = new BigInteger("99999999999");

    /**
     * The Constant GAS_LIMIT.
     */
    public static final BigInteger GAS_LIMIT = new BigInteger("9999999999999");

    /**
     * The Constant INIIIAL_VALUE.
     */
    public static final BigInteger INILITIAL_VALUE = new BigInteger("0");

    /**
     * The Constant WeIdentity DID String Prefix.
     */
    public static final String WEID_PREFIX = "did:weid:";

    /**
     * The Constant WeIdentity DID Document PublicKey Prefix.
     */
    public static final String WEID_DOC_PUBLICKEY_PREFIX = "/weId/pubkey";

    /**
     * The Constant WeIdentity DID Document Authentication Prefix.
     */
    public static final String WEID_DOC_AUTHENTICATE_PREFIX = "/weId/auth";

    /**
     * The Constant WeIdentity DID Document Service Prefix.
     */
    public static final String WEID_DOC_SERVICE_PREFIX = "/weId/service";

    /**
     * The Constant WeIdentity DID Document Create Date Attribute String Name.
     */
    public static final String WEID_DOC_CREATED = "created";

    /**
     * The Constant WeIdentity DID Long Array Length.
     */
    public static final Integer LONG_ARRAY_LENGTH = 8;

    /**
     * The Constant WeIdentity DID String Array Length.
     */
    public static final Integer STRING_ARRAY_LENGTH = 8;

    /**
     * The Constant WeIdentity DID Json Schema Array Length.
     */
    public static final Integer JSON_SCHEMA_ARRAY_LENGTH = 128;

    /**
     * The Constant WeIdentity DID Fixed Length for Bytes32.
     */
    public static final Integer BYTES32_FIXED_LENGTH = 32;

    /**
     * The Constant EMPTY_ADDRESS.
     */
    public static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";

    /**
     * The Constant WeIdentity DID Maximum Json Schema Array Length.
     */
    public static final Integer JSON_SCHEMA_MAX_LENGTH = 4096;

    /**
     * The Constant default timeout for getting transaction.
     */
    public static final Integer TRANSACTION_RECEIPT_TIMEOUT = 13;

    /**
     * The Constant pipeline character.
     */
    public static final String PIPELINE = "|";

    /**
     * The Constant default Certificate Context.
     */
    public static final String DEFAULT_CERTIFICATE_CONTEXT = "v1";

    /**
     * The Constant Max authority issuer name length in Chars.
     */
    public static final Integer MAX_AUTHORITY_ISSUER_NAME_LENGTH = 32;

    /**
     * The Constant ADD_AUTHORITY_ISSUER_OPCODE from contract layer.
     */
    public static final Integer ADD_AUTHORITY_ISSUER_OPCODE = 0;

    /**
     * The Constant REMOVE_AUTHORITY_ISSUER_OPCODE from contract layer.
     */
    public static final Integer REMOVE_AUTHORITY_ISSUER_OPCODE = 1;

    /**
     * UTF-8.
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 0L.
     */
    public static final Long LONG_VALUE_ZERO = 0L;
}
