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

package com.webank.weid.service.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.CptController;
import com.webank.weid.contract.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.contract.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.rpc.CptService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.DataTypetUtils;
import com.webank.weid.util.JsonSchemaValidatorUtils;
import com.webank.weid.util.SignatureUtils;
import com.webank.weid.util.WeIdUtils;

import com.google.common.base.Splitter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service implementation for operation on CPT (Claim Protocol Type).
 *
 * @author lingfenghe
 */
@Component
public class CptServiceImpl extends BaseService implements CptService {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceImpl.class);

    private static CptController cptController;
    private static String cptControllerAddress;

    /**
     * Instantiates a new cpt service impl.
     */
    public CptServiceImpl() {
        init();
    }

    private static void init() {
        ContractConfig config = context.getBean(ContractConfig.class);
        cptControllerAddress = config.getCptAddress();
        cptController = (CptController) getContractService(config.getCptAddress(),
            CptController.class);
    }

    private static void reloadContract(String privateKey) {
        cptController =
            (CptController) reloadContract(cptControllerAddress, privateKey, CptController.class);
    }

    /**
     * This is used to register a new CPT to the blockchain.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> registerCpt(RegisterCptArgs args) {
        ResponseData<CptBaseInfo> responseData = new ResponseData<CptBaseInfo>();

        try {
            responseData = validateRegisterCptArgs(args, responseData);
            if (responseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return responseData;
            }

            Address publisher = new Address(WeIdUtils.convertWeIdToAddress(args.getCptPublisher()));

            long[] longArray = new long[WeIdConstant.LONG_ARRAY_LENGTH];
            long created = System.currentTimeMillis();
            longArray[1] = created;
            StaticArray<Int256> intArray = DataTypetUtils.longArrayToInt256StaticArray(longArray);

            String[] stringArray = new String[WeIdConstant.STRING_ARRAY_LENGTH];
            StaticArray<Bytes32> bytes32Array = DataTypetUtils.stringArrayToBytes32StaticArray(
                stringArray
            );

            List<String> stringList = Splitter
                .fixedLength(WeIdConstant.BYTES32_FIXED_LENGTH)
                .splitToList(args.getCptJsonSchema());
            String[] jsonSchemaArray = new String[WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH];
            for (int i = 0; i < stringList.size(); i++) {
                jsonSchemaArray[i] = stringList.get(i);
            }
            StaticArray<Bytes32> jsonSchema =
                DataTypetUtils.stringArrayToBytes32StaticArray(jsonSchemaArray);

            RsvSignature rsvSignature =
                sign(args.getCptPublisher(), args.getCptJsonSchema(),
                    args.getCptPublisherPrivateKey());

            reloadContract(args.getCptPublisherPrivateKey().getPrivateKey());
            TransactionReceipt transactionReceipt = cptController.registerCpt(
                publisher,
                intArray,
                bytes32Array,
                jsonSchema,
                rsvSignature.getV(),
                rsvSignature.getR(),
                rsvSignature.getS()
            ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

            List<RegisterCptRetLogEventResponse> event = CptController.getRegisterCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isNotEmpty(event)) {
                if (DataTypetUtils.uint256ToInt(event.get(0).retCode)
                    == ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX.getCode()) {
                    responseData = new ResponseData<>(null,
                        ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX);
                } else if (DataTypetUtils.uint256ToInt(event.get(0).retCode)
                    == ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode()) {
                    responseData = new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST);
                } else {
                    CptBaseInfo result = new CptBaseInfo();
                    result.setCptId(DataTypetUtils.uint256ToInt(event.get(0).cptId));
                    result.setCptVersion(DataTypetUtils.int256ToInt(event.get(0).cptVersion));
                    responseData.setResult(result);
                }
            } else {
                responseData = new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
            }
        } catch (InterruptedException | ExecutionException e) {
            responseData = new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
            logger.error(
                "[CptServiceImpl] register cpt failed due to transaction execution error. ",
                e
            );
        } catch (TimeoutException e) {
            responseData = new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
            logger.error("[CptServiceImpl] register cpt failed due to transaction timeout. ", e);
        } catch (Exception e) {
            responseData = new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
            logger.error("[CptServiceImpl] register cpt failed due to unknown error. ", e);
        }

        return responseData;
    }

    /**
     * this is used to query cpt with the latest version which has been registered.
     *
     * @param cptId the cpt id
     * @return the response data
     */
    public ResponseData<Cpt> queryCpt(Integer cptId) {
        ResponseData<Cpt> responseData = new ResponseData<Cpt>();
        List<Type> typeList;

        try {
            if (cptId == null || cptId < 0) {
                responseData = new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
                return responseData;
            }

            typeList = cptController.queryCpt(DataTypetUtils.intToUint256(cptId)).get();

            if (typeList != null) {
                if (WeIdConstant.EMPTY_ADDRESS.equals(((Address) typeList.get(0)).toString())) {
                    responseData = new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
                    logger.error("Query cpt id : {} does not exist.", cptId);
                    return responseData;
                }
                Cpt cpt = new Cpt();
                cpt.setCptId(cptId);

                cpt.setCptPublisher(
                    WeIdUtils.convertAddressToWeId(((Address) typeList.get(0)).toString()));

                long[] longArray = DataTypetUtils.int256DynamicArrayToLongArray(
                    (DynamicArray<Int256>) typeList.get(1)
                );
                cpt.setCptVersion((int) longArray[0]);
                cpt.setCreated(longArray[1]);
                cpt.setUpdated(longArray[2]);

                String[] jsonSchemaArray =
                    DataTypetUtils.bytes32DynamicArrayToStringArrayWithoutTrim(
                        (DynamicArray<Bytes32>) typeList.get(3)
                    );
                String jsonSchema = StringUtils.EMPTY;
                for (int i = 0; i < jsonSchemaArray.length; i++) {
                    jsonSchema = jsonSchema + jsonSchemaArray[i];
                }
                cpt.setCptJsonSchema(jsonSchema.trim());

                int v = DataTypetUtils.uint8ToInt((Uint8) typeList.get(4));
                byte[] r = DataTypetUtils.bytes32ToBytesArray((Bytes32) typeList.get(5));
                byte[] s = DataTypetUtils.bytes32ToBytesArray((Bytes32) typeList.get(6));
                Sign.SignatureData signatureData = SignatureUtils
                    .rawSignatureDeserialization(v, r, s);
                String cptSignature =
                    new String(
                        SignatureUtils.base64Encode(
                            SignatureUtils.simpleSignatureSerialization(signatureData)));
                cpt.setCptSignature(cptSignature);

                responseData.setResult(cpt);
            }
        } catch (InterruptedException | ExecutionException e) {
            responseData = new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
            logger.error(
                "[CptServiceImpl] query cpt failed due to transaction execution error. ",
                e
            );
        } catch (Exception e) {
            responseData = new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
            logger.error("[CptServiceImpl] query cpt failed due to unknown error. ", e);
        }

        return responseData;
    }

    /**
     * This is used to update a CPT data which has been register.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> updateCpt(UpdateCptArgs args) {
        ResponseData<CptBaseInfo> responseData = new ResponseData<>();

        try {
            responseData = validateUpdateCptArgs(args, responseData);
            if (responseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return responseData;
            }

            Uint256 cptId = DataTypetUtils.intToUint256(args.getCptId());

            Address publisher = new Address(WeIdUtils.convertWeIdToAddress(args.getCptPublisher()));

            long[] longArray = new long[WeIdConstant.LONG_ARRAY_LENGTH];
            long updated = System.currentTimeMillis();
            longArray[2] = updated;
            StaticArray<Int256> intArray = DataTypetUtils.longArrayToInt256StaticArray(longArray);

            String[] stringArray = new String[WeIdConstant.STRING_ARRAY_LENGTH];
            StaticArray<Bytes32> bytes32Array =
                DataTypetUtils.stringArrayToBytes32StaticArray(stringArray);

            List<String> stringList = Splitter
                .fixedLength(WeIdConstant.BYTES32_FIXED_LENGTH)
                .splitToList(args.getCptJsonSchema());
            String[] jsonSchemaArray = new String[WeIdConstant.JSON_SCHEMA_ARRAY_LENGTH];
            for (int i = 0; i < stringList.size(); i++) {
                jsonSchemaArray[i] = stringList.get(i);
            }
            StaticArray<Bytes32> jsonSchema = DataTypetUtils.stringArrayToBytes32StaticArray(
                jsonSchemaArray
            );

            RsvSignature rsvSignature = sign(
                args.getCptPublisher(),
                args.getCptJsonSchema(),
                args.getCptPublisherPrivateKey()
            );

            reloadContract(args.getCptPublisherPrivateKey().getPrivateKey());
            TransactionReceipt transactionReceipt = cptController.updateCpt(
                cptId,
                publisher,
                intArray,
                bytes32Array,
                jsonSchema,
                rsvSignature.getV(),
                rsvSignature.getR(),
                rsvSignature.getS()
            ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

            List<UpdateCptRetLogEventResponse> event = CptController.getUpdateCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isNotEmpty(event)) {
                if (DataTypetUtils.uint256ToInt(event.get(0).retCode)
                    == ErrorCode.CPT_NOT_EXISTS.getCode()) {
                    responseData = new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
                    logger.error("Update cpt id : {} does not exist.", args.getCptId());
                } else if (DataTypetUtils.uint256ToInt(event.get(0).retCode)
                    == ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode()) {
                    responseData = new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST);
                } else {
                    CptBaseInfo result = new CptBaseInfo();
                    result.setCptId(DataTypetUtils.uint256ToInt(event.get(0).cptId));
                    result.setCptVersion(DataTypetUtils.int256ToInt(event.get(0).cptVersion));
                    responseData.setResult(result);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            responseData = new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
            logger.error(
                "[CptServiceImpl] update cpt failed due to transaction execution error. ",
                e
            );
        } catch (TimeoutException e) {
            responseData = new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
            logger.error("[CptServiceImpl] update cpt failed due to transaction timeout. ", e);
        } catch (Exception e) {
            responseData = new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
            logger.error("[CptServiceImpl] update cpt failed due to unkown error. ", e);
        }

        return responseData;
    }

    private RsvSignature sign(
        String cptPublisher, String jsonSchema, WeIdPrivateKey cptPublisherPrivateKey)
        throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(cptPublisher);
        sb.append(WeIdConstant.PIPELINE);
        sb.append(jsonSchema);
        SignatureData signatureData =
            SignatureUtils.signMessage(sb.toString(), cptPublisherPrivateKey.getPrivateKey());
        Uint8 v = DataTypetUtils.intToUnt8(Integer.valueOf(signatureData.getV()));
        Bytes32 r = DataTypetUtils.bytesArrayToBytes32(signatureData.getR());
        Bytes32 s = DataTypetUtils.bytesArrayToBytes32(signatureData.getS());

        RsvSignature rsvSignature = new RsvSignature();
        rsvSignature.setV(v);
        rsvSignature.setR(r);
        rsvSignature.setS(s);
        return rsvSignature;
    }

    private ResponseData<CptBaseInfo> validateRegisterCptArgs(
        RegisterCptArgs args, ResponseData<CptBaseInfo> responseData) throws Exception {

        if (args == null) {
            logger.error("input RegisterCptArgs is null");
            responseData = new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            return responseData;
        }

        if (!WeIdUtils.isWeIdValid(args.getCptPublisher())) {
            logger.error("Input cpt publisher : {} is invalid.", args.getCptPublisher());
            responseData = new ResponseData<>(null, ErrorCode.WEID_INVALID);
            return responseData;
        }

        if (!JsonSchemaValidatorUtils.isCptJsonSchemaValid(args.getCptJsonSchema())) {
            logger.error("Input cpt json schema : {} is invalid.", args.getCptJsonSchema());
            responseData = new ResponseData<>(null, ErrorCode.CPT_JSON_SCHEMA_INVALID);
            return responseData;
        }

        if (null == args.getCptPublisherPrivateKey()
            || StringUtils.isEmpty(args.getCptPublisherPrivateKey().getPrivateKey())) {
            logger.error(
                "Input cpt publisher private key : {} is in valid.",
                args.getCptPublisherPrivateKey()
            );
            responseData = new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_INVALID);
            return responseData;
        }

        if (!validatePrivateKeyWeIdMatches(args.getCptPublisherPrivateKey(),
            args.getCptPublisher())) {
            responseData = new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
        }

        return responseData;
    }

    private boolean validatePrivateKeyWeIdMatches(WeIdPrivateKey cptPublisherPrivateKey,
        String cptPublisher) {
        boolean isMatch = false;

        try {
            BigInteger publicKey = SignatureUtils
                .publicKeyFromPrivate(new BigInteger(cptPublisherPrivateKey.getPrivateKey()));
            String address1 = "0x" + Keys.getAddress(publicKey);
            String address2 = WeIdUtils.convertWeIdToAddress(cptPublisher);
            if (address1.equals(address2)) {
                isMatch = true;
            }
        } catch (Exception e) {
            logger.error("Validate private key We Id matches failed. Error message :{}", e);
            return isMatch;
        }

        return isMatch;
    }

    private ResponseData<CptBaseInfo> validateUpdateCptArgs(
        UpdateCptArgs args, ResponseData<CptBaseInfo> responseData) throws Exception {

        if (args == null) {
            logger.error("input UpdateCptArgs is null");
            responseData = new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            return responseData;
        }

        if (!WeIdUtils.isWeIdValid(args.getCptPublisher())) {
            logger.error("Input cpt publisher : {} is invalid.", args.getCptPublisher());
            responseData = new ResponseData<>(null, ErrorCode.WEID_INVALID);
            return responseData;
        }

        if (!JsonSchemaValidatorUtils.isCptJsonSchemaValid(args.getCptJsonSchema())) {
            logger.error("Input cpt json schema : {} is in valid.", args.getCptJsonSchema());
            responseData = new ResponseData<>(null, ErrorCode.CPT_JSON_SCHEMA_INVALID);
            return responseData;
        }

        if (null == args.getCptPublisherPrivateKey()
            || StringUtils.isEmpty(args.getCptPublisherPrivateKey().getPrivateKey())) {
            logger.error(
                "Input cpt publisher private key : {} is in valid.",
                args.getCptPublisherPrivateKey()
            );
            responseData = new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_INVALID);
            return responseData;
        }

        if (!validatePrivateKeyWeIdMatches(args.getCptPublisherPrivateKey(),
            args.getCptPublisher())) {
            responseData = new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
        }

        return responseData;
    }
}
