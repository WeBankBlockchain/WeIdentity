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

package com.webank.weid.service.impl.engine.fiscov1;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.CollectionUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.CptController;
import com.webank.weid.contract.v1.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月25日
 */
public class CptServiceEngineV1 extends BaseEngine implements CptServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceEngineV1.class);

    private static CptController cptController;
    
    public CptServiceEngineV1() {
    	cptController = getContractService(fiscoConfig.getCptAddress(), CptController.class);
    }

    /**
     * Verify Register CPT related events.
     *
     * @param transactionReceipt the TransactionReceipt
     * @return the ErrorCode
     */
    public static ResponseData<CptBaseInfo> resolveRegisterCptEvents(
        TransactionReceipt transactionReceipt) {
        List<RegisterCptRetLogEventResponse> event = CptController.getRegisterCptRetLogEvents(
            transactionReceipt
        );

        if (CollectionUtils.isEmpty(event)) {
            logger.error("[registerCpt] event is empty");
            return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
        }

        return getResultByResolveEvent(
            event.get(0).retCode,
            event.get(0).cptId,
            event.get(0).cptVersion,
            transactionReceipt
        );
    }

    /**
     * Resolve CPT Event.
     *
     * @param retCode the retCode
     * @param cptId the CptId
     * @param cptVersion the CptVersion
     * @param receipt the transactionReceipt
     * @return the result
     */
    public static ResponseData<CptBaseInfo> getResultByResolveEvent(
        Uint256 retCode,
        Uint256 cptId,
        Int256 cptVersion,
        TransactionReceipt receipt) {

        TransactionInfo info = new TransactionInfo(receipt);
        // register
        if (DataToolUtils.uint256ToInt(retCode)
            == ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX.getCode()) {
            logger.error("[getResultByResolveEvent] cptId limited max value. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new ResponseData<>(null, ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX, info);
        }

        if (DataToolUtils.uint256ToInt(retCode) == ErrorCode.CPT_ALREADY_EXIST.getCode()) {
            logger.error("[getResultByResolveEvent] cpt already exists on chain. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new ResponseData<>(null, ErrorCode.CPT_ALREADY_EXIST, info);
        }

        if (DataToolUtils.uint256ToInt(retCode) == ErrorCode.CPT_NO_PERMISSION.getCode()) {
            logger.error("[getResultByResolveEvent] no permission. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new ResponseData<>(null, ErrorCode.CPT_NO_PERMISSION, info);
        }

        // register and update
        if (DataToolUtils.uint256ToInt(retCode)
            == ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode()) {
            logger.error("[getResultByResolveEvent] publisher does not exist. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST, info);
        }

        // update
        if (DataToolUtils.uint256ToInt(retCode)
            == ErrorCode.CPT_NOT_EXISTS.getCode()) {
            logger.error("[getResultByResolveEvent] cpt id : {} does not exist.",
                DataToolUtils.uint256ToInt(cptId));
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS, info);
        }

        CptBaseInfo result = new CptBaseInfo();
        result.setCptId(DataToolUtils.uint256ToInt(cptId));
        result.setCptVersion(DataToolUtils.int256ToInt(cptVersion));

        return new ResponseData<>(result, ErrorCode.SUCCESS, info);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#updateCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> updateCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

        TransactionReceipt receipt;
        try {
            receipt = cptController.updateCpt(
                DataToolUtils.intToUint256(cptId),
                new Address(address),
                TransactionUtils.getParamUpdated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                bytes32Array,
                TransactionUtils.getParamJsonSchema(cptJsonSchemaNew),
                rsvSignature.getV(),
                rsvSignature.getR(),
                rsvSignature.getS()
            ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            return resolveRegisterCptEvents(receipt);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("[updateCpt] transaction execute with exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }

    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {
        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

//	        reloadContract(weIdPrivateKey.getPrivateKey());
        // the case to update a CPT. Requires a valid CPT ID
//	        	engine.
        TransactionReceipt receipt;
        try {

            // the case to register a CPT with a pre-set CPT ID
            receipt = cptController.registerCpt(
                DataToolUtils.intToUint256(cptId),
                new Address(address),
                TransactionUtils.getParamCreated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                bytes32Array,
                TransactionUtils.getParamJsonSchema(cptJsonSchemaNew),
                rsvSignature.getV(),
                rsvSignature.getR(),
                rsvSignature.getS()
            ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

            return resolveRegisterCptEvents(receipt);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
        	logger.error("[updateCpt] transaction execute with exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {
    	
        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

        TransactionReceipt receipt;
        try {
            // the case to register a CPT with a pre-set CPT ID
            receipt = cptController.registerCpt(
                new Address(address),
                TransactionUtils.getParamCreated(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                bytes32Array,
                TransactionUtils.getParamJsonSchema(cptJsonSchemaNew),
                rsvSignature.getV(),
                rsvSignature.getR(),
                rsvSignature.getS()
            ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

            return resolveRegisterCptEvents(receipt);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
        	logger.error("[updateCpt] transaction execute with exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#queryCpt(int)
     */
    @Override
    public ResponseData<Cpt> queryCpt(int cptId) {
        try {

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
            return new ResponseData<Cpt>(cpt, ErrorCode.SUCCESS);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("query cpt failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        }
    }


}
