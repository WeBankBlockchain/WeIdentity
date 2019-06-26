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

package com.webank.weid.service.impl.engine.fiscov2;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v2.CptController;
import com.webank.weid.contract.v2.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月25日
 */
public class CptServiceEngineV2 extends BaseEngine implements CptServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceEngineV2.class);

    private static String cptControllerAddress;

    private static CptController cptController;

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#updateCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> updateCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        List<byte[]> byteArray = new ArrayList<>();
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = cptController.registerCpt(
                address,
                DataToolUtils.listToListBigInteger(
                    DataToolUtils.getParamCreatedList(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                    WeIdConstant.CPT_LONG_ARRAY_LENGTH
                ),
                DataToolUtils.bytesArrayListToBytes32ArrayList(
                    byteArray,
                    WeIdConstant.CPT_STRING_ARRAY_LENGTH
                ),
                DataToolUtils.stringToByte32ArrayList(
                    cptJsonSchemaNew, WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH),
                rsvSignature.getV().getValue(),
                rsvSignature.getR().getValue(),
                rsvSignature.getS().getValue()
            ).send();

            List<UpdateCptRetLogEventResponse> event = cptController.getUpdateCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isEmpty(event)) {
                logger.error("[updateCpt] event is empty, cptId:{}.", cptId);
                return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
            }

            return this.getResultByResolveEvent(
                transactionReceipt,
                event.get(0).retCode,
                event.get(0).cptId,
                event.get(0).cptVersion
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
        }
    }

    private ResponseData<CptBaseInfo> getResultByResolveEvent(
        TransactionReceipt receipt,
        BigInteger retCode,
        BigInteger cptId,
        BigInteger cptVersion) {

        TransactionInfo info = new TransactionInfo(receipt);

        // register
        if (retCode.intValue()
            == ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX.getCode()) {
            logger.error("[getResultByResolveEvent] cptId limited max value. cptId:{}", cptId);
            return new ResponseData<>(null, ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX, info);
        }

        if (retCode.intValue() == ErrorCode.CPT_ALREADY_EXIST.getCode()) {
            logger.error("[getResultByResolveEvent] cpt already exists on chain. cptId:{}",
                cptId.intValue());
            return new ResponseData<>(null, ErrorCode.CPT_ALREADY_EXIST, info);
        }

        if (retCode.intValue() == ErrorCode.CPT_NO_PERMISSION.getCode()) {
            logger.error("[getResultByResolveEvent] no permission. cptId:{}",
                cptId.intValue());
            return new ResponseData<>(null, ErrorCode.CPT_NO_PERMISSION, info);
        }

        // register and update
        if (retCode.intValue()
            == ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode()) {
            logger.error("[getResultByResolveEvent] publisher does not exist. cptId:{}", cptId);
            return new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST, info);
        }

        // update
        if (retCode.intValue()
            == ErrorCode.CPT_NOT_EXISTS.getCode()) {
            logger.error("[getResultByResolveEvent] cpt id : {} does not exist.", cptId);
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS, info);
        }

        CptBaseInfo result = new CptBaseInfo();
        result.setCptId(cptId.intValue());
        result.setCptVersion(cptVersion.intValue());

        ResponseData<CptBaseInfo> responseData =
            new ResponseData<>(result, ErrorCode.SUCCESS, info);
        return responseData;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        List<byte[]> byteArray = new ArrayList<>();

        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = cptController.registerCpt(
                BigInteger.valueOf(cptId),
                address,
                DataToolUtils.listToListBigInteger(
                    DataToolUtils.getParamCreatedList(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                    WeIdConstant.CPT_LONG_ARRAY_LENGTH
                ),
                DataToolUtils.bytesArrayListToBytes32ArrayList(
                    byteArray,
                    WeIdConstant.CPT_STRING_ARRAY_LENGTH
                ),
                DataToolUtils.stringToByte32ArrayList(
                    cptJsonSchemaNew, WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH),
                rsvSignature.getV().getValue(),
                rsvSignature.getR().getValue(),
                rsvSignature.getS().getValue()
            ).send();

            List<UpdateCptRetLogEventResponse> event = cptController.getUpdateCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isEmpty(event)) {
                logger.error("[updateCpt] event is empty, cptId:{}.", cptId);
                return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
            }

            return this.getResultByResolveEvent(
                transactionReceipt,
                event.get(0).retCode,
                event.get(0).cptId,
                event.get(0).cptVersion
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        List<byte[]> byteArray = new ArrayList<>();

        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = cptController.registerCpt(
                address,
                DataToolUtils.listToListBigInteger(
                    DataToolUtils.getParamCreatedList(WeIdConstant.CPT_LONG_ARRAY_LENGTH),
                    WeIdConstant.CPT_LONG_ARRAY_LENGTH
                ),
                DataToolUtils.bytesArrayListToBytes32ArrayList(
                    byteArray,
                    WeIdConstant.CPT_STRING_ARRAY_LENGTH
                ),
                DataToolUtils.stringToByte32ArrayList(
                    cptJsonSchemaNew, WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH),
                rsvSignature.getV().getValue(),
                rsvSignature.getR().getValue(),
                rsvSignature.getS().getValue()
            ).send();

            List<UpdateCptRetLogEventResponse> event = cptController.getUpdateCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isEmpty(event)) {
                return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
            }

            return this.getResultByResolveEvent(
                transactionReceipt,
                event.get(0).retCode,
                event.get(0).cptId,
                event.get(0).cptVersion
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#queryCpt(int)
     */
    @Override
    public ResponseData<Cpt> queryCpt(int cptId) {

        Tuple7<String, List<BigInteger>, List<byte[]>, List<byte[]>,
            BigInteger, byte[], byte[]> valueList =
            cptController
                .queryCpt(new BigInteger(String.valueOf(cptId))).sendAsync()
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

        if (valueList == null) {
            logger.error("Query cpt id : {} does not exist, result is null.", cptId);
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
        }

        if (WeIdConstant.EMPTY_ADDRESS.equals(valueList.getValue1())) {
            logger.error("Query cpt id : {} does not exist.", cptId);
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
        }
        Cpt cpt = new Cpt();
        cpt.setCptId(cptId);
        cpt.setCptPublisher(
            WeIdUtils.convertAddressToWeId(valueList.getValue1())
        );

        List<BigInteger> longArray = valueList.getValue2();

        cpt.setCptVersion(longArray.get(0).intValue());
        cpt.setCreated(longArray.get(1).longValue());
        cpt.setUpdated(longArray.get(2).longValue());

        List<byte[]> jsonSchemaArray = valueList.getValue4();

        String jsonSchema = DataToolUtils.byte32ListToString(
            jsonSchemaArray, WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH);

        Map<String, Object> jsonSchemaMap = DataToolUtils
            .deserialize(jsonSchema.trim(), HashMap.class);
        cpt.setCptJsonSchema(jsonSchemaMap);

        int v = valueList.getValue5().intValue();
        byte[] r = valueList.getValue6();
        byte[] s = valueList.getValue7();
        Sign.SignatureData signatureData = DataToolUtils
            .rawSignatureDeserialization(v, r, s);
        String cptSignature =
            new String(
                DataToolUtils.base64Encode(
                    DataToolUtils.simpleSignatureSerialization(signatureData)),
                StandardCharsets.UTF_8
            );
        cpt.setCptSignature(cptSignature);

        ResponseData<Cpt> responseData = new ResponseData<Cpt>(cpt, ErrorCode.SUCCESS);
        return responseData;
    }


}
