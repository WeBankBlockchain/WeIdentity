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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.service.BaseService;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.bcos.web3j.protocol.core.methods.response.EthSendTransaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.protocol.exceptions.TransactionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transaction related utility functions. This class handles specific Transaction tasks, including
 * sending raw transactions into blockchain, and parse the transaction receipts.
 *
 * @author chaoxinhu 2019.3
 */

public class TransactionUtils {

    private static final Logger logger = LoggerFactory.getLogger(TransactionUtils.class);

    /**
     * Send a transaction to blockchain through web3j instance using the transactionHex value.
     *
     * @param web3j the web3j instance to blockchain
     * @param transactionHex the transactionHex value
     * @return the transactionReceipt
     * @throws Exception the exception
     */
    public static TransactionReceipt sendTransaction(Web3j web3j, String transactionHex)
        throws Exception {
        if (web3j == null || StringUtils.isEmpty(transactionHex)) {
            return null;
        }
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(transactionHex)
            .sendAsync().get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
        if (ethSendTransaction.hasError()) {
            logger.error("Error processing transaction request: "
                + ethSendTransaction.getError().getMessage());
            return null;
        }
        Optional<TransactionReceipt> receiptOptional =
            getTransactionReceiptRequest(web3j, ethSendTransaction.getTransactionHash());
        int sumTime = 0;
        try {
            for (int i = 0; i < WeIdConstant.POLL_TRANSACTION_ATTEMPTS; i++) {
                if (!receiptOptional.isPresent()) {
                    Thread.sleep((long) WeIdConstant.POLL_TRANSACTION_SLEEP_DURATION);
                    sumTime += WeIdConstant.POLL_TRANSACTION_SLEEP_DURATION;
                    receiptOptional = getTransactionReceiptRequest(web3j,
                        ethSendTransaction.getTransactionHash());
                } else {
                    return receiptOptional.get();
                }
            }
        } catch (Exception e) {
            throw new TransactionTimeoutException("Transaction receipt was not generated after "
                + ((sumTime) / 1000
                + " seconds for transaction: " + ethSendTransaction));
        }
        return null;
    }

    /**
     * Check validity and build input params for createWeId (with attributes - public key)
     * function.
     *
     * @param inputParam the input param json
     * @return the StaticArray
     */
    public static List<Type> buildCreateWeIdInputParameters(String inputParam) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode inputParamNode = objectMapper.readTree(inputParam);
        JsonNode publicKeyNode = inputParamNode.get(ParamKeyConstant.PUBLIC_KEY);
        if (publicKeyNode == null) {
            return null;
        }
        String publicKey = publicKeyNode.textValue();
        String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
        String addr = WeIdUtils.convertWeIdToAddress(weId);
        if (!StringUtils.isNotBlank(publicKey) || !WeIdUtils.isValidAddress(addr)) {
            logger.error("[createWeId]: input parameter publickey is invalid.");
            return null;
        }
        // We do not check WeID existence in this case since it does not really affect the outcome.
        return Arrays.<Type>asList(
            new Address(addr),
            DataTypetUtils.stringToBytes32(WeIdConstant.WEID_DOC_CREATED),
            DataTypetUtils.stringToDynamicBytes(DateUtils.getCurrentTimeStampString()),
            DateUtils.getCurrentTimeStampInt256()
        );
    }

    /**
     * Check validity and build input params for registerAuthorityIssuer function.
     *
     * @param inputParam the input Param json
     * @return the StaticArray
     */
    public static List<Type> buildAuthorityIssuerInputParameters(String inputParam)
        throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode inputParamNode = objectMapper.readTree(inputParam);
        JsonNode weIdNode = inputParamNode.get(ParamKeyConstant.WEID);
        JsonNode nameNode = inputParamNode.get(ParamKeyConstant.AUTHORITY_ISSUER_NAME);
        if (weIdNode == null || nameNode == null) {
            return null;
        }
        String weId = weIdNode.textValue();
        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input cpt publisher : {} is invalid.", weId);
            return null;
        }
        String name = nameNode.textValue();
        return Arrays.<Type>asList(
            new Address(WeIdUtils.convertWeIdToAddress(weId)),
            getParamName(name),
            getParamCreated(WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH),
            getDefaultAccValue());
    }

    /**
     * Convert the authority issuer org name into the static array.
     *
     * @param name authorityIssuer name
     * @return the StaticArray
     */
    private static StaticArray<Bytes32> getParamName(String name) {
        String[] nameArray = new String[WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH];
        nameArray[0] = name;
        return DataTypetUtils.stringArrayToBytes32StaticArray(nameArray);
    }

    /**
     * Get the default accumulator value.
     *
     * @return the StaticArray
     */
    private static DynamicBytes getDefaultAccValue() {
        String defaultAccValue = WeIdConstant.DEFAULT_ACCUMULATOR_VALUE;
        DynamicBytes accValue = new DynamicBytes(defaultAccValue
            .getBytes(StandardCharsets.UTF_8)
        );
        return accValue;
    }

    /**
     * Check validity and build input params for registerCpt blockchain function.
     *
     * @param inputParam the input Param json
     * @return the StaticArray
     */
    public static List<Type> buildRegisterCptInputParameters(String inputParam) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode inputParamNode = objectMapper.readTree(inputParam);
        JsonNode weIdNode = inputParamNode.get(ParamKeyConstant.WEID);
        JsonNode cptJsonSchemaNode = inputParamNode.get(ParamKeyConstant.CPT_JSON_SCHEMA);
        JsonNode cptSignatureNode = inputParamNode.get(ParamKeyConstant.CPT_SIGNATURE);
        if (weIdNode == null || cptJsonSchemaNode == null || cptSignatureNode == null) {
            return null;
        }

        String weId = weIdNode.textValue();
        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input cpt publisher : {} is invalid.", weId);
            return null;
        }

        String cptJsonSchema = cptJsonSchemaNode.toString();
        String cptJsonSchemaNew = complementCptJsonSchema(cptJsonSchema);
        Map<String, Object> cptJsonSchemaMap = (HashMap<String, Object>) JsonUtil.jsonStrToObj(
            new HashMap<String, Object>(),
            cptJsonSchemaNew);
        if (cptJsonSchemaMap == null
            || cptJsonSchemaMap.isEmpty()
            || !DataToolUtils.isCptJsonSchemaValid(cptJsonSchemaNew)) {
            logger.error("Input cpt json schema : {} is invalid.", cptJsonSchemaNew);
            return null;
        }

        String cptSignature = cptSignatureNode.textValue();
        if (!DataToolUtils.isValidBase64String(cptSignature)) {
            logger.error("Input cpt signature invalid: {}", cptSignature);
            return null;
        }
        RsvSignature rsvSignature = DataToolUtils.convertSignatureDataToRsv(
        		DataToolUtils.convertBase64StringToSignatureData(cptSignature));

        StaticArray<Bytes32> bytes32Array = DataTypetUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );
        return Arrays.<Type>asList(
            new Address(WeIdUtils.convertWeIdToAddress(weId)),
            getParamCreated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
            bytes32Array,
            getParamJsonSchema(cptJsonSchemaNew),
            rsvSignature.getV(),
            rsvSignature.getR(),
            rsvSignature.getS());
    }

    /**
     * Complement the cpt json schema json string, enforcing keys to present: @schema and type.
     *
     * @param cptJsonSchemaOld the old cpt json schema json string
     * @return the new string
     */
    public static String complementCptJsonSchema(String cptJsonSchemaOld) {
        Map<String, Object> cptJsonSchemaMapOld = (Map<String, Object>) JsonUtil.jsonStrToObj(
            new HashMap<String, Object>(),
            cptJsonSchemaOld);
        Map<String, Object> cptJsonSchemaMapNew = new HashMap<>();
        cptJsonSchemaMapNew.put(JsonSchemaConstant.SCHEMA_KEY, JsonSchemaConstant.SCHEMA_VALUE);
        cptJsonSchemaMapNew
            .put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATE_TYPE_OBJECT);
        cptJsonSchemaMapNew.putAll(cptJsonSchemaMapOld);
        return JsonUtil.objToJsonStr(cptJsonSchemaMapNew);
    }

    /**
     * Get the current timestamp as the param "created". May be called elsewhere.
     *
     * @return the StaticArray
     */
    public static StaticArray<Int256> getParamCreated(int length) {
        long[] longArray = new long[length];
        long created = System.currentTimeMillis();
        longArray[0] = created;
        return DataTypetUtils.longArrayToInt256StaticArray(longArray);
    }

    /**
     * Get the cpt json schema as the param "cptJsonSchema".
     *
     * @param cptJsonSchema the cptJsonSchema String
     * @return the StaticArray
     */
    public static StaticArray<Bytes32> getParamJsonSchema(String cptJsonSchema) {

        List<String> stringList = Splitter
            .fixedLength(WeIdConstant.BYTES32_FIXED_LENGTH)
            .splitToList(cptJsonSchema);
        String[] jsonSchemaArray = new String[WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH];
        for (int i = 0; i < stringList.size(); i++) {
            jsonSchemaArray[i] = stringList.get(i);
        }
        return DataTypetUtils.stringArrayToBytes32StaticArray(jsonSchemaArray);
    }

    /**
     * Get a TransactionReceipt request from a transaction Hash.
     *
     * @param web3j the web3j instance to blockchain
     * @param transactionHash the transactionHash value
     * @return the transactionReceipt wrapper
     * @throws Exception the exception
     */
    private static Optional<TransactionReceipt> getTransactionReceiptRequest(Web3j web3j,
        String transactionHash) throws Exception {
        EthGetTransactionReceipt transactionReceipt =
            web3j.ethGetTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            logger.error("Error processing transaction request: "
                + transactionReceipt.getError().getMessage());
            return Optional.empty();
        }
        return transactionReceipt.getTransactionReceipt();
    }

    /**
     * Get a random Nonce for a transaction.
     *
     * @return nonce in BigInt.
     */
    public static BigInteger getNonce() {
        Random r = new SecureRandom();
        return new BigInteger(250, r);
    }

    /**
     * Get a default blocklimit for a transaction.
     *
     * @return blocklimit in BigInt.
     */
    public static BigInteger getBlockLimit() {
        try {
            return BaseService.getWeb3j().ethBlockNumber().send().getBlockNumber()
                .add(new BigInteger(String.valueOf(WeIdConstant.ADDITIVE_BLOCK_HEIGHT)));
        } catch (Exception e) {
            //Send a large enough block limit number
            return new BigInteger(WeIdConstant.BIG_BLOCK_LIMIT);
        }
    }
}
