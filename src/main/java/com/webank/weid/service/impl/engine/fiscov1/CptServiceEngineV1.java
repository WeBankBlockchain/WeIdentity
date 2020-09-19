/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service.impl.engine.fiscov1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.IssuerClient;
import com.webank.wedpr.selectivedisclosure.IssuerResult;
import com.webank.wedpr.selectivedisclosure.proto.TemplatePublicKey;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.EventEncoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.DefaultBlockParameterNumber;
import org.bcos.web3j.protocol.core.methods.response.EthBlock;
import org.bcos.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.bcos.web3j.protocol.core.methods.response.Log;
import org.bcos.web3j.protocol.core.methods.response.Transaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.CptController;
import com.webank.weid.contract.v1.CptController.CredentialTemplateEventResponse;
import com.webank.weid.contract.v1.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.contract.v1.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.exception.ResolveAttributeException;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.suite.api.persistence.PersistenceFactory;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.api.persistence.params.PersistenceType;
import com.webank.weid.util.CredentialPojoUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * CptServiceEngine calls the authority issuer contract which runs on FISCO BCOS 1.3.x.
 *
 * @author tonychen 2019年6月25日
 */
public class CptServiceEngineV1 extends BaseEngine implements CptServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceEngineV1.class);

    private static CptController cptController;

    private static String CREDENTIALTEMPLATETOPIC;

    private static Persistence dataDriver;

    private static PersistenceType persistenceType;

    static {
        Event event = new Event(
            "CredentialTemplate",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(
                new TypeReference<Uint256>() {
                },
                new TypeReference<DynamicBytes>() {
                },
                new TypeReference<DynamicBytes>() {
                }));
        CREDENTIALTEMPLATETOPIC = EventEncoder.encode(event);
    }
    
    /**
     * 构造函数.
     */
    public CptServiceEngineV1() {
        if (cptController == null) {
            reload();
        }
    }

    /**
     * 重新加载静态合约对象.
     */
    public void reload() {
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
     * Verify Update CPT related events.
     *
     * @param transactionReceipt the TransactionReceipt
     * @return the ErrorCode
     */
    public static ResponseData<CptBaseInfo> resolveUpdateCptEvents(
        TransactionReceipt transactionReceipt) {
        List<UpdateCptRetLogEventResponse> event = CptController.getUpdateCptRetLogEvents(
            transactionReceipt
        );

        if (CollectionUtils.isEmpty(event)) {
            logger.error("[updateCpt] event is empty");
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

    private Persistence getDataDriver() {
        String type = PropertyUtils.getProperty("persistence_type");
        if (type.equals("mysql")) {
            persistenceType = PersistenceType.Mysql;
        } else if (type.equals("redis")) {
            persistenceType = PersistenceType.Redis;
        }
        if (dataDriver == null) {
            dataDriver = PersistenceFactory.build(persistenceType);
        }
        return dataDriver;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController
     * #updateCpt(int, java.lang.String, java.lang.String,
     * com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> updateCpt(int cptId, String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

        TransactionReceipt receipt;
        try {
            CptController cptController =
                reloadContract(fiscoConfig.getCptAddress(), privateKey, CptController.class);
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
            ResponseData<CptBaseInfo> response = resolveUpdateCptEvents(receipt);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return response;
            }
            ErrorCode errorCode = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCode.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[updateCpt] save credential template failed. errorcode:{} ", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_TIMEOUT);
            }
            return response;
        } catch (TimeoutException e) {
            logger.error("[updateCpt] transaction execute with timeout exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("[updateCpt] transaction execute with exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
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
        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

        TransactionReceipt receipt;
        try {
            CptController cptController =
                reloadContract(fiscoConfig.getCptAddress(), privateKey, CptController.class);
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

            ResponseData<CptBaseInfo> response = resolveRegisterCptEvents(receipt);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return response;
            }
            ErrorCode errorCode = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCode.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[updateCpt] save credential template failed. errorcode:{} ", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_TIMEOUT);
            }
            return response;
        } catch (TimeoutException e) {
            logger.error("[updateCpt] transaction execute with timeout exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("[updateCpt] transaction execute with exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
    }


    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptEngineController
     * #registerCpt(java.lang.String, java.lang.String,
     * com.webank.weid.protocol.response.RsvSignature)
     */
    @Override
    public ResponseData<CptBaseInfo> registerCpt(String address, String cptJsonSchemaNew,
        RsvSignature rsvSignature, String privateKey) {

        StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
        );

        TransactionReceipt receipt;
        try {
            CptController cptController =
                reloadContract(fiscoConfig.getCptAddress(), privateKey, CptController.class);
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

            ResponseData<CptBaseInfo> response = resolveRegisterCptEvents(receipt);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return response;
            }
            int cptId = response.getResult().getCptId();
            ErrorCode errorCode = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCode.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[updateCpt] save credential template failed. errorcode:{} ", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_TIMEOUT);
            }
            return response;
        } catch (TimeoutException e) {
            logger.error("[updateCpt] transaction execute with timeout exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("[updateCpt] transaction execute with exception. ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
    }

    private ErrorCode processTemplate(Integer cptId, String cptJsonSchemaNew) {

        if (!CredentialPojoUtils.isZkpCpt(cptJsonSchemaNew)) {
            return ErrorCode.SUCCESS;
        }
        List<String> attributeList;
        try {
            attributeList = JsonUtil.extractCptProperties(cptJsonSchemaNew);

            IssuerResult issuerResult = IssuerClient.makeCredentialTemplate(attributeList);
            CredentialTemplateEntity template = issuerResult.credentialTemplateEntity;
            String templateSecretKey = issuerResult.templateSecretKey;
            ResponseData<Integer> resp =
                this.getDataDriver().addOrUpdate(
                    DataDriverConstant.DOMAIN_ISSUER_TEMPLATE_SECRET,
                    String.valueOf(cptId),
                    templateSecretKey);
            if (resp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[processTemplate] save credential template to db failed.");
                throw new DatabaseException("database error!");
            }
            TransactionReceipt receipt = cptController.putCredentialTemplate(
                DataToolUtils.intToUint256(cptId),
                DataToolUtils
                    .stringToDynamicBytes(template.getPublicKey().getCredentialPublicKey()),
                DataToolUtils.stringToDynamicBytes(template.getCredentialKeyCorrectnessProof()))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("[processTemplate] process credential template failed.", e);
            return ErrorCode.CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR;
        }
        return ErrorCode.SUCCESS;
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

            String jsonSchema = DataToolUtils.bytes32DynamicArrayToStringWithoutTrim(
                (DynamicArray<Bytes32>) typeList.get(3));

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

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptServiceEngine#queryCredentialTemplate(
     * java.lang.Integer)
     */
    @Override
    public ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId) {

        CredentialTemplateEntity credentialTemplateStorage = new CredentialTemplateEntity();
        Future<Uint256> f = cptController
            .getCredentialTemplateBlock(DataToolUtils.intToUint256(cptId));
        EthBlock latestBlock = null;
        int blockNum = 0;
        try {
            blockNum = f.get().getValue().intValue();
            latestBlock =
                ((Web3j) getWeb3j())
                    .ethGetBlockByNumber(
                        new DefaultBlockParameterNumber(blockNum),
                        true
                    )
                    .send();
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.error(
                "[queryCredentialTemplate]:get block by number :{} failed. Exception message:{}",
                blockNum,
                e
            );
        }
        if (latestBlock == null) {
            logger.info(
                "[queryCredentialTemplate]:get block by number :{} . latestBlock is null",
                blockNum
            );
            return new ResponseData<CredentialTemplateEntity>(null, ErrorCode.UNKNOW_ERROR);
        }
        List<Transaction> transList =
            latestBlock
                .getBlock()
                .getTransactions()
                .stream()
                .map(transactionResult -> (Transaction) transactionResult.get())
                .collect(Collectors.toList());

        try {
            for (Transaction transaction : transList) {
                String transHash = transaction.getHash();

                EthGetTransactionReceipt rec1 = ((Web3j) getWeb3j())
                    .ethGetTransactionReceipt(transHash)
                    .send();
                TransactionReceipt receipt = rec1.getTransactionReceipt().get();
                List<Log> logs = rec1.getResult().getLogs();
                for (Log log : logs) {
                    String topic = log.getTopics().get(0);
                    if (StringUtils.equals(topic, CREDENTIALTEMPLATETOPIC)) {
                        List<CredentialTemplateEventResponse> events = CptController
                            .getCredentialTemplateEvents(receipt);
                        CredentialTemplateEventResponse eventResp = events.get(0);
                        String credentialProof = eventResp.credentialProof.getTypeAsString();
                        String pubKey = eventResp.credentialPublicKey.getTypeAsString();
                        credentialTemplateStorage.setCredentialKeyCorrectnessProof(credentialProof);
                        TemplatePublicKey publicKey = TemplatePublicKey.newBuilder()
                            .setCredentialPublicKey(pubKey).build();
                        credentialTemplateStorage.setPublicKey(publicKey);
                    }
                }
            }
        } catch (IOException | DataTypeCastException e) {
            logger.error(
                "[queryCredentialTemplate]: get TransactionReceipt by cpt :{} failed.",
                cptId,
                e
            );
            throw new ResolveAttributeException(
                ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
                ErrorCode.TRANSACTION_EXECUTE_ERROR.getCodeDesc());
        }
        return new ResponseData<CredentialTemplateEntity>(credentialTemplateStorage,
            ErrorCode.SUCCESS);
    }
}
