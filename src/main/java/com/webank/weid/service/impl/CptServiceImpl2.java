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

package com.webank.weid.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.CptController;
import com.webank.weid.contract.v1.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.rpc.CptService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementation for operation on CPT (Claim Protocol Type).
 *
 * @author lingfenghe
 */
public class CptServiceImpl2 extends BaseService implements CptService {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceImpl2.class);

    private static CptController cptController;
    private static String cptControllerAddress;
    
    private CptServiceEngine engine;

    /**
     * Instantiates a new cpt service impl.
     */
    public CptServiceImpl2() {
        init();
    }

    private static void init() {
        ContractConfig config = buildContractConfig();
        cptControllerAddress = config.getCptAddress();
        cptController = (CptController) getContractService(config.getCptAddress(),
            CptController.class);
    }

    private static void reloadContract(String privateKey) {
        cptController =
            (CptController) reloadContract(cptControllerAddress, privateKey, CptController.class);
    }

    /**
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param args the args
     * @param cptId the CPT ID
     * @return response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptStringArgs args, Integer cptId) {
        if (args == null || cptId == null || cptId <= 0) {
            logger.error(
                "[registerCpt1] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(args.getWeIdAuthentication());
            cptMapArgs.setCptJsonSchema(
                DataToolUtils.deserialize(args.getCptJsonSchema(), HashMap.class));
            return this.registerCpt(cptMapArgs, cptId);
        } catch (Exception e) {
            logger.error("[registerCpt1] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to register a new CPT to the blockchain.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptStringArgs args) {

        try {
            if (args == null) {
                logger.error(
                    "[registerCpt1]input CptStringArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }

            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(args.getWeIdAuthentication());
            cptMapArgs.setCptJsonSchema(
                DataToolUtils.deserialize(args.getCptJsonSchema(), HashMap.class));
            return this.registerCpt(cptMapArgs);
        } catch (Exception e) {
            logger.error("[registerCpt1] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param args the args
     * @param cptId the CPT ID
     * @return response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptMapArgs args, Integer cptId) {
        if (args == null || cptId == null || cptId <= 0) {
            logger.error("[registerCpt] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            ErrorCode errorCode =
                this.validateCptArgs(
                    args.getWeIdAuthentication(),
                    args.getCptJsonSchema()
                );
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null, errorCode);
            }

            TransactionReceipt transactionReceipt = this.getTransactionReceipt(
                args.getWeIdAuthentication(),
                args.getCptJsonSchema(),
                false,
                cptId
            );
            return TransactionUtils.resolveRegisterCptEvents(transactionReceipt);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(
                "[registerCpt] register cpt failed due to transaction execution error. ",
                e
            );
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("[registerCpt] register cpt failed due to transaction timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to register a new CPT to the blockchain.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptMapArgs args) {

        try {
            if (args == null) {
                logger.error("[registerCpt]input CptMapArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            ErrorCode validateResult =
                this.validateCptArgs(
                    args.getWeIdAuthentication(),
                    args.getCptJsonSchema()
                );

            if (validateResult.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null, validateResult);
            }

            TransactionReceipt transactionReceipt = this.getTransactionReceipt(
                args.getWeIdAuthentication(),
                args.getCptJsonSchema(),
                false,
                null
            );
            return TransactionUtils.resolveRegisterCptEvents(transactionReceipt);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(
                "[registerCpt] register cpt failed due to transaction execution error. ",
                e
            );
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("[registerCpt] register cpt failed due to transaction timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * this is used to query cpt with the latest version which has been registered.
     *
     * @param cptId the cpt id
     * @return the response data
     */
    public ResponseData<Cpt> queryCpt(Integer cptId) {

        try {
            if (cptId == null || cptId < 0) {
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }

            List<Type> typeList = cptController
                .queryCpt(DataToolUtils.intToUint256(cptId))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

            if (typeList == null || typeList.isEmpty()) {
                logger.error("Query cpt id : {} does not exist, result is null.", cptId);
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }

            if (WeIdConstant.EMPTY_ADDRESS.equals(((Address) typeList.get(0)).toString())) {
                logger.error("Query cpt id : {} does not exist.", cptId);
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }
            Cpt cpt = new Cpt();
            cpt.setCptId(cptId);
            cpt.setCptPublisher(
                WeIdUtils.convertAddressToWeId(((Address) typeList.get(0)).toString())
            );

            long[] longArray = DataToolUtils.int256DynamicArrayToLongArray(
                (DynamicArray<Int256>) typeList.get(1)
            );
            cpt.setCptVersion((int) longArray[0]);
            cpt.setCreated(longArray[1]);
            cpt.setUpdated(longArray[2]);

            String[] jsonSchemaArray =
                DataToolUtils.bytes32DynamicArrayToStringArrayWithoutTrim(
                    (DynamicArray<Bytes32>) typeList.get(3)
                );
            StringBuffer jsonSchema = new StringBuffer();
            for (int i = 0; i < jsonSchemaArray.length; i++) {
                jsonSchema.append(jsonSchemaArray[i]);
            }

            Map<String, Object> jsonSchemaMap = DataToolUtils
                .deserialize(jsonSchema.toString().trim(), HashMap.class);
            cpt.setCptJsonSchema(jsonSchemaMap);

            int v = DataToolUtils.uint8ToInt((Uint8) typeList.get(4));
            byte[] r = DataToolUtils.bytes32ToBytesArray((Bytes32) typeList.get(5));
            byte[] s = DataToolUtils.bytes32ToBytesArray((Bytes32) typeList.get(6));
            Sign.SignatureData signatureData = DataToolUtils
                .rawSignatureDeserialization(v, r, s);
            String cptSignature =
                new String(
                    DataToolUtils.base64Encode(
                        DataToolUtils.simpleSignatureSerialization(signatureData)
                    ),
                    StandardCharsets.UTF_8
                );
            cpt.setCptSignature(cptSignature);

            ResponseData<Cpt> responseData = new ResponseData<Cpt>(cpt, ErrorCode.SUCCESS);
            return responseData;
        } catch (InterruptedException | ExecutionException e) {
            logger.error(
                "[updateCpt] query cpt failed due to transaction execution error. ",
                e
            );
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("[updateCpt] query cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to update a CPT data which has been register.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> updateCpt(CptStringArgs args, Integer cptId) {

        try {
            if (args == null) {
                logger.error("[updateCpt1]input UpdateCptArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }

            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(args.getWeIdAuthentication());
            // cptMapArgs.setCptJsonSchema(
            //     (Map<String, Object>) JsonUtil.jsonStrToObj(
            //         new HashMap<String, Object>(),
            //         args.getCptJsonSchema())
            // );
            cptMapArgs.setCptJsonSchema(
                DataToolUtils.deserialize(args.getCptJsonSchema(), HashMap.class));
            return this.updateCpt(cptMapArgs, cptId);
        } catch (Exception e) {
            logger.error("[updateCpt1] update cpt failed due to unkown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to update a CPT data which has been register.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> updateCpt(CptMapArgs args, Integer cptId) {

        try {
            if (args == null) {
                logger.error("[updateCpt]input UpdateCptArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            if (cptId == null) {
                logger.error("[updateCpt]input cptId is null");
                return new ResponseData<>(null, ErrorCode.CPT_ID_NULL);
            }
            ErrorCode errorCode =
                this.validateCptArgs(
                    args.getWeIdAuthentication(),
                    args.getCptJsonSchema()
                );

            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null, errorCode);
            }

            TransactionReceipt transactionReceipt = this.getTransactionReceipt(
                args.getWeIdAuthentication(),
                args.getCptJsonSchema(),
                true,
                cptId
            );
            List<UpdateCptRetLogEventResponse> event = CptController.getUpdateCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isEmpty(event)) {
                logger.error("[updateCpt] event is empty, cptId:{}.", cptId);
                return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
            }
            return TransactionUtils.getResultByResolveEvent(
                event.get(0).retCode,
                event.get(0).cptId,
                event.get(0).cptVersion,
                transactionReceipt
            );
        } catch (InterruptedException | ExecutionException e) {
            logger.error(
                "[updateCpt2] update cpt failed due to transaction execution error. ",
                e
            );
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("[updateCpt] update cpt failed due to transaction timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (Exception e) {
            logger.error("[updateCpt] update cpt failed due to unkown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private TransactionReceipt getTransactionReceipt(
        WeIdAuthentication weIdAuthentication,
        Map<String, Object> cptJsonSchemaMap,
        Boolean isUpdate,
        Integer cptId) throws Exception {

        String weId = weIdAuthentication.getWeId();
        WeIdPrivateKey weIdPrivateKey = weIdAuthentication.getWeIdPrivateKey();
        String cptJsonSchemaNew = this.cptSchemaToString(cptJsonSchemaMap);
        RsvSignature rsvSignature = sign(
            weId,
            cptJsonSchemaNew,
            weIdPrivateKey);

        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

        reloadContract(weIdPrivateKey.getPrivateKey());
        if (isUpdate) {
            // the case to update a CPT. Requires a valid CPT ID
//        	engine.
            return cptController.updateCpt(
                DataToolUtils.intToUint256(cptId),
                new Address(WeIdUtils.convertWeIdToAddress(weId)),
                TransactionUtils.getParamUpdated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                bytes32Array,
                TransactionUtils.getParamJsonSchema(cptJsonSchemaNew),
                rsvSignature.getV(),
                rsvSignature.getR(),
                rsvSignature.getS()
            ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
        } else {
            if (cptId == null || cptId == 0) {
                // the case to register a CPT with an auto-generated CPT ID
//            	engine.registerCpt(args);
                return cptController.registerCpt(
                    new Address(WeIdUtils.convertWeIdToAddress(weId)),
                    TransactionUtils.getParamCreated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                    bytes32Array,
                    TransactionUtils.getParamJsonSchema(cptJsonSchemaNew),
                    rsvSignature.getV(),
                    rsvSignature.getR(),
                    rsvSignature.getS()
                ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            } else {
                // the case to register a CPT with a pre-set CPT ID
                return cptController.registerCpt(
                    DataToolUtils.intToUint256(cptId),
                    new Address(WeIdUtils.convertWeIdToAddress(weId)),
                    TransactionUtils.getParamCreated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                    bytes32Array,
                    TransactionUtils.getParamJsonSchema(cptJsonSchemaNew),
                    rsvSignature.getV(),
                    rsvSignature.getR(),
                    rsvSignature.getS()
                ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            }
        }
    }

    private RsvSignature sign(
        String cptPublisher,
        String jsonSchema,
        WeIdPrivateKey cptPublisherPrivateKey) {

        StringBuilder sb = new StringBuilder();
        sb.append(cptPublisher);
        sb.append(WeIdConstant.PIPELINE);
        sb.append(jsonSchema);
        SignatureData signatureData =
            DataToolUtils.signMessage(sb.toString(), cptPublisherPrivateKey.getPrivateKey());
        return DataToolUtils.convertSignatureDataToRsv(signatureData);
    }

    private ErrorCode validateCptArgs(
        WeIdAuthentication weIdAuthentication,
        Map<String, Object> cptJsonSchemaMap) throws Exception {

        if (weIdAuthentication == null) {
            logger.error("Input cpt weIdAuthentication is invalid.");
            return ErrorCode.WEID_AUTHORITY_INVALID;
        }

        String weId = weIdAuthentication.getWeId();
        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input cpt publisher : {} is invalid.", weId);
            return ErrorCode.WEID_INVALID;
        }

        if (cptJsonSchemaMap == null || cptJsonSchemaMap.isEmpty()) {
            logger.error("Input cpt json schema is invalid.");
            return ErrorCode.CPT_JSON_SCHEMA_INVALID;
        }
        //String cptJsonSchema = JsonUtil.objToJsonStr(cptJsonSchemaMap);
        String cptJsonSchema = DataToolUtils.serialize(cptJsonSchemaMap);
        if (!DataToolUtils.isCptJsonSchemaValid(cptJsonSchema)) {
            logger.error("Input cpt json schema : {} is invalid.", cptJsonSchemaMap);
            return ErrorCode.CPT_JSON_SCHEMA_INVALID;
        }

        WeIdPrivateKey weIdPrivateKey = weIdAuthentication.getWeIdPrivateKey();
        if (weIdPrivateKey == null
            || StringUtils.isEmpty(weIdPrivateKey.getPrivateKey())) {
            logger.error(
                "Input cpt publisher private key : {} is in valid.",
                weIdPrivateKey
            );
            return ErrorCode.WEID_PRIVATEKEY_INVALID;
        }

        if (!WeIdUtils.validatePrivateKeyWeIdMatches(weIdPrivateKey, weId)) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * create new cpt json schema.
     *
     * @param cptJsonSchema Map
     * @return String
     */
    private String cptSchemaToString(Map<String, Object> cptJsonSchema) throws Exception {

        Map<String, Object> cptJsonSchemaNew = new HashMap<String, Object>();
        cptJsonSchemaNew.put(JsonSchemaConstant.SCHEMA_KEY, JsonSchemaConstant.SCHEMA_VALUE);
        cptJsonSchemaNew.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_OBJECT);
        cptJsonSchemaNew.putAll(cptJsonSchema);
        return DataToolUtils.serialize(cptJsonSchemaNew);
    }


}
