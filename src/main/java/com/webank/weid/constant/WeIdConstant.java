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
        "\"@context\" : \"https://w3id.org/did/v1\",";

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
    public static final Integer CPT_LONG_ARRAY_LENGTH = 8;

    /**
     * The Constant WeIdentity DID String Array Length.
     */
    public static final Integer CPT_STRING_ARRAY_LENGTH = 8;

    /**
     * The Constant Authority Issuer contract array length.
     */
    public static final Integer AUTHORITY_ISSUER_ARRAY_LEGNTH = 16;

    /**
     * The default accumulator value.
     */
    public static final String DEFAULT_ACCUMULATOR_VALUE = "1";

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
     * The Constant separator character.
     */
    public static final String SEPARATOR = "/";

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
     * 0L.
     */
    public static final Long LONG_VALUE_ZERO = 0L;

    /**
     * Hex Prefix.
     */
    public static final String HEX_PREFIX = "0x";

    /**
     * UUID Separator.
     */
    public static final String UUID_SEPARATOR = "-";

    /**
     * UUID Pattern.
     */
    public static final String UUID_PATTERN =
        "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    /**
     * The transaction poll interval.
     */
    public static final Integer POLL_TRANSACTION_SLEEP_DURATION = 1500;

    /**
     * The transaction poll attempts (max).
     */
    public static final Integer POLL_TRANSACTION_ATTEMPTS = 5;

    /**
     * The additive block height.
     */
    public static final Integer ADDITIVE_BLOCK_HEIGHT = 500;

    /**
     * The big-enough block limit number.
     */
    public static final String BIG_BLOCK_LIMIT = "9999999999";

    /**
     * The Constant default Presentation type.
     */
    public static final String DEFAULT_PRESENTATION_TYPE = "VerifiablePresentation";

    /**
     * The default maximum authority issuer list size to fetch from blockchain.
     */
    public static final Integer MAX_AUTHORITY_ISSUER_LIST_SIZE = 50;

    /**
     * The Constant WeIdentity DID Event Attribute Change String Name.
     */
    public static final String WEID_EVENT_ATTRIBUTE_CHANGE = "WeIdAttributeChanged";

    /**
     * The FISCO-BCOS Address pattern.
     */
    public static final String FISCO_BCOS_ADDRESS_PATTERN = "0x[a-fA-f0-9]{40}";
}
