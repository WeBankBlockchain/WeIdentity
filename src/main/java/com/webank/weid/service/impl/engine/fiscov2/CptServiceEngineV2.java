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

package com.webank.weid.service.impl.engine.fiscov2;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.IssuerClient;
import com.webank.wedpr.selectivedisclosure.IssuerResult;
import com.webank.wedpr.selectivedisclosure.proto.AttributeTemplate;
import com.webank.wedpr.selectivedisclosure.proto.AttributeTemplate.Builder;
import com.webank.wedpr.selectivedisclosure.proto.TemplatePublicKey;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.abi.EventEncoder;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.DefaultBlockParameterNumber;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v2.CptController;
import com.webank.weid.contract.v2.CptController.CredentialTemplateEventResponse;
import com.webank.weid.contract.v2.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.contract.v2.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * CptServiceEngine calls cpt contract which runs on FISCO BCOS 2.0.
 *
 * @author tonychen 2019年6月25日
 */
public class CptServiceEngineV2 extends BaseEngine implements CptServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceEngineV2.class);
    private static final String CREDENTIAL_TEMPLATE_EVENT = EventEncoder
        .encode(CptController.CREDENTIALTEMPLATE_EVENT);
    private static CptController cptController;
    private static Persistence dataDriver = new MysqlDriver();

    static {

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

            ResponseData<CptBaseInfo> response = processUpdateEventLog(cptController,
                transactionReceipt);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return response;
            }
            ErrorCode errorCode = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCode.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[registerCpt]register cpt failed, error code is {}", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.getTypeByErrorCode(code));
            }
            return response;
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

            ResponseData<CptBaseInfo> response = processRegisterEventLog(cptController,
                transactionReceipt);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return response;
            }
            ErrorCode errorCode = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCode.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[registerCpt]register cpt failed, error code is {}", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.getTypeByErrorCode(code));
            }
            return response;
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

            //
            ResponseData<CptBaseInfo> response = processRegisterEventLog(cptController,
                transactionReceipt);
            if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                return response;
            }
            Integer cptId = response.getResult().getCptId();
            ErrorCode errorCode = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCode.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[registerCpt]register cpt failed, error code is {}", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.getTypeByErrorCode(code));
            }
            return response;
        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed. exception message: ", e);
            return new ResponseData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ErrorCode processTemplate(Integer cptId, String cptJsonSchemaNew) {

        List<String> attributeList;
        try {
            attributeList = JsonUtil.extractCptProperties(cptJsonSchemaNew);

            IssuerResult issuerResult = IssuerClient.makeCredentialTemplate(attributeList);
            CredentialTemplateEntity template = issuerResult.credentialTemplateEntity;
            String templateSecretKey = issuerResult.templateSecretKey;
            ResponseData<Integer> resp =
                dataDriver.save(
                    DataDriverConstant.DOMAIN_ISSUER_TEMPLATE_SECRET,
                    String.valueOf(cptId),
                    templateSecretKey);
            if (resp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[processTemplate] save credential template to db failed.");
                throw new DatabaseException("database error!");
            }
            TransactionReceipt receipt = cptController.putCredentialTemplate(
                new BigInteger(String.valueOf(cptId)),
                template.getPublicKey().getCredentialPublicKey().getBytes(),
                template.getCredentialKeyCorrectnessProof().getBytes()).send();
            if (!StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.error("[processTemplate] put credential template to blockchain failed.");
                return ErrorCode.CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR;
            }
        } catch (Exception e) {
            logger.error("[processTemplate] process credential template failed.");
            return ErrorCode.CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * process UpdateEventLog.
     *
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
     *
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

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.CptServiceEngine#queryCredentialTemplate(
     * java.lang.Integer)
     */
    @Override
    public ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId) {

        int blockNum = 0;
        try {
            blockNum = cptController
                .getCredentialTemplateBlock(new BigInteger(String.valueOf(cptId))).send()
                .intValue();
        } catch (Exception e1) {
            logger.error(
                "[queryCredentialTemplate] get block number for cpt : {} failed.",
                cptId);
            return new ResponseData<CredentialTemplateEntity>(null, ErrorCode.UNKNOW_ERROR);
        }
        if (blockNum == 0) {
            logger.error(
                "[queryCredentialTemplate] no credential template found for cpt : {}.",
                cptId);
            return new ResponseData<CredentialTemplateEntity>(null, ErrorCode.BASE_ERROR);
        }
        BcosBlock bcosBlock = null;
        try {
            bcosBlock = ((Web3j) getWeb3j())
                .getBlockByNumber(new DefaultBlockParameterNumber(blockNum), true).send();
        } catch (IOException e) {
            logger.error(
                "[queryCredentialTemplate]get block by number :{} failed. Error message:{}",
                blockNum,
                e);
        }
        if (bcosBlock == null) {
            logger.info(
                "[queryCredentialTemplate]:get block by number :{} . latestBlock is null",
                blockNum);
            return new ResponseData<CredentialTemplateEntity>(null, ErrorCode.BASE_ERROR);
        }

        List<Transaction> transList = bcosBlock.getBlock().getTransactions().stream()
            .map(transactionResult -> (Transaction) transactionResult.get())
            .collect(Collectors.toList());

        CredentialTemplateEntity credentialTemplateStorage = new CredentialTemplateEntity();
        try {
            for (Transaction transaction : transList) {
                String transHash = transaction.getHash();

                BcosTransactionReceipt rec1 = ((Web3j) getWeb3j()).getTransactionReceipt(transHash)
                    .send();
                TransactionReceipt receipt = rec1.getTransactionReceipt().get();
                List<Log> logs = rec1.getResult().getLogs();
                for (Log log : logs) {

                    if (StringUtils.equals(log.getTopics().get(0), CREDENTIAL_TEMPLATE_EVENT)) {
                        List<CredentialTemplateEventResponse> event = cptController
                            .getCredentialTemplateEvents(receipt);
                        CredentialTemplateEventResponse eventResponse = event.get(0);
                        byte[] proof = eventResponse.credentialProof;
                        byte[] credentialPubKey = eventResponse.credentialPublicKey;
                        credentialTemplateStorage
                            .setCredentialKeyCorrectnessProof(DataToolUtils.byteToString(proof));
                        TemplatePublicKey pubKey = TemplatePublicKey.newBuilder()
                            .setCredentialPublicKey(DataToolUtils.byteToString(credentialPubKey))
                            .build();
                        credentialTemplateStorage.setPublicKey(pubKey);
                        break;
                    }
                }
            }
            ResponseData<Cpt> resp = this.queryCpt(cptId);
            Cpt cpt = resp.getResult();

            Map<String, Object> cptInfo = cpt.getCptJsonSchema();
            List<String> attrList;
            attrList = JsonUtil.extractCptProperties(cptInfo);
            Builder builder = AttributeTemplate.newBuilder();
            for (String attr : attrList) {
                builder.addAttributeKey(attr);
            }
            AttributeTemplate attributes = builder.build();

            credentialTemplateStorage.setCredentialSchema(attributes);
        } catch (Exception e) {
            return new ResponseData<CredentialTemplateEntity>(null, ErrorCode.UNKNOW_ERROR);
        }

        return new ResponseData<CredentialTemplateEntity>(credentialTemplateStorage,
            ErrorCode.SUCCESS);
    }
}
