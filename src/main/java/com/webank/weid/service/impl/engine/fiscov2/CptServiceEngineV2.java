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
import org.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v2.CptController;
import com.webank.weid.contract.v2.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.contract.v2.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * CptServiceEngine calls cpt contract which runs on FISCO BCOS 2.0.
 * @author tonychen 2019年6月25日
 */
public class CptServiceEngineV2 extends BaseEngine implements CptServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceEngineV2.class);


    private static CptController cptController;

    /**
     * constructor.
     */
    public CptServiceEngineV2() {
        if (cptController == null) {
            cptController = getContractService(fiscoConfig.getCptAddress(), CptController.class);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController
     * #updateCpt(int, java.lang.String, java.lang.String,
     * com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> updateCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        List<byte[]> byteArray = new ArrayList<>();
        TransactionReceipt transactionReceipt;
        try {
            CptController cptController =
                reloadContract(fiscoConfig.getCptAddress(), privateKey, CptController.class);
            transactionReceipt = cptController.updateCpt(
                BigInteger.valueOf(Long.valueOf(cptId)),
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

            return processUpdateEventLog(cptController, transactionReceipt);
        } catch (Exception e) {
            logger.error("[updateCpt] cptId limited max value. cptId:{}", cptId);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }


    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController
     * #registerCpt(int, java.lang.String, java.lang.String,
     * com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        List<byte[]> byteArray = new ArrayList<>();

        TransactionReceipt transactionReceipt;
        try {
            CptController cptController =
                reloadContract(fiscoConfig.getCptAddress(), privateKey, CptController.class);
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

            return processRegisterEventLog(cptController, transactionReceipt);
        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed. exception message: ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController
     * #registerCpt(java.lang.String, java.lang.String,
     * com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey) {

        List<byte[]> byteArray = new ArrayList<>();
        TransactionReceipt transactionReceipt;
        try {
            CptController cptController =
                reloadContract(fiscoConfig.getCptAddress(), privateKey, CptController.class);
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

            return processRegisterEventLog(cptController, transactionReceipt);
        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed. exception message: ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * process UpdateEventLog.
     * @param cptController cpt contract object
     * @param transactionReceipt transactionReceipt
     * @return result
     */
    private ResponseData<CptBaseInfo> processUpdateEventLog(
        CptController cptController,
        TransactionReceipt transactionReceipt) {
        List<UpdateCptRetLogEventResponse> event = cptController.getUpdateCptRetLogEvents(
            transactionReceipt
        );
        if (CollectionUtils.isEmpty(event)) {
            return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
        }

        return TransactionUtils.getResultByResolveEvent(
            event.get(0).retCode,
            event.get(0).cptId,
            event.get(0).cptVersion,
            transactionReceipt
        );
    }
    
    /**
     * process RegisterEventLog.
     * @param cptController cpt contract object
     * @param transactionReceipt transactionReceipt
     * @return result
     */
    private ResponseData<CptBaseInfo> processRegisterEventLog(
        CptController cptController,
        TransactionReceipt transactionReceipt) {
        List<RegisterCptRetLogEventResponse> event = cptController.getRegisterCptRetLogEvents(
            transactionReceipt
        );
        if (CollectionUtils.isEmpty(event)) {
            return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
        }

        return TransactionUtils.getResultByResolveEvent(
            event.get(0).retCode,
            event.get(0).cptId,
            event.get(0).cptVersion,
            transactionReceipt
        );
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController#queryCpt(int)
     */
    @Override
    public ResponseData<Cpt> queryCpt(int cptId) {

        try {
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
        } catch (Exception e) {
            logger.error("[queryCpt] query Cpt failed. exception message: ", e);
            return new ResponseData<Cpt>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
    }

}
