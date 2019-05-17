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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.contract.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.contract.SpecificIssuerController;
import com.webank.weid.contract.SpecificIssuerController.SpecificIssuerRetLogEventResponse;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.DataTypetUtils;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on Authority Issuer.
 *
 * @author chaoxinhu 2018.10
 */
@Component
public class AuthorityIssuerServiceImpl extends BaseService implements AuthorityIssuerService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorityIssuerServiceImpl.class);

    private static AuthorityIssuerController authorityIssuerController;
    private static String authorityIssuerControllerAddress;
    private static SpecificIssuerController specificIssuerController;
    private static String specificIssuerControllerAddress;

    private WeIdService weIdService = new WeIdServiceImpl();

    /**
     * Instantiates a new authority issuer service impl.
     */
    public AuthorityIssuerServiceImpl() {
        init();
    }

    private static void init() {
        ContractConfig config = context.getBean(ContractConfig.class);
        authorityIssuerController =
            (AuthorityIssuerController)
                getContractService(config.getIssuerAddress(), AuthorityIssuerController.class);
        authorityIssuerControllerAddress = config.getIssuerAddress();
        specificIssuerController = (SpecificIssuerController) getContractService(
            config.getSpecificIssuerAddress(), SpecificIssuerController.class);
        specificIssuerControllerAddress = config.getSpecificIssuerAddress();
    }

    /**
     * Use the given private key to send the transaction to call the contract.
     *
     * @param privateKey the private key
     */
    private static void reloadAuthorityIssuerContract(String privateKey) {
        authorityIssuerController = (AuthorityIssuerController) reloadContract(
            authorityIssuerControllerAddress,
            privateKey,
            AuthorityIssuerController.class
        );
    }

    private static void reloadSpecificIssuerContract(String privateKey) {
        specificIssuerController = (SpecificIssuerController) reloadContract(
            specificIssuerControllerAddress,
            privateKey,
            SpecificIssuerController.class
        );
    }

    /**
     * Register a new Authority Issuer on Chain.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> registerAuthorityIssuer(RegisterAuthorityIssuerArgs args) {

        ErrorCode innerResponseData = checkRegisterAuthorityIssuerArgs(args);
        if (ErrorCode.SUCCESS.getCode() != innerResponseData.getCode()) {
            return new ResponseData<>(false, innerResponseData);
        }

        AuthorityIssuer authorityIssuer = args.getAuthorityIssuer();
        String weAddress = WeIdUtils.convertWeIdToAddress(authorityIssuer.getWeId());
        String[] stringAttributes = loadNameToStringAttributes(authorityIssuer.getName());
        long[] longAttributes = new long[16];
        Long createDate = System.currentTimeMillis();
        longAttributes[0] = createDate;
        Address addr = new Address(weAddress);
        try {
            DynamicBytes accValue = new DynamicBytes(authorityIssuer
                .getAccValue()
                .getBytes(StandardCharsets.UTF_8)
            );
            reloadAuthorityIssuerContract(args.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = authorityIssuerController.addAuthorityIssuer(
                addr,
                DataTypetUtils.stringArrayToBytes32StaticArray(stringAttributes),
                DataTypetUtils.longArrayToInt256StaticArray(longAttributes),
                accValue
            );
            TransactionReceipt receipt = future.get(
                WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT,
                TimeUnit.SECONDS
            );
            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(receipt);
            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(event,
                WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE);
            return new ResponseData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode);
        } catch (TimeoutException e) {
            logger.error("register authority issuer failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("register authority issuer failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("register authority issuer failed.", e);
        }
        return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
    }

    /**
     * Register a new Authority Issuer on Chain with preset transaction hex value. The inputParam is
     * a Json String, with two keys: WeIdentity DID and Name. Parameters will be ordered as
     * mentioned after validity check; then transactionHex will be sent to blockchain.
     *
     * @param transactionHex the transaction hex value
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<String> registerAuthorityIssuer(String transactionHex) {
        try {
            if (StringUtils.isEmpty(transactionHex)) {
                logger.error("AuthorityIssuer transaction error");
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
            TransactionReceipt transactionReceipt = TransactionUtils
                .sendTransaction(getWeb3j(), transactionHex);
            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(transactionReceipt);
            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(event,
                WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE);
            Boolean result = errorCode.getCode() == ErrorCode.SUCCESS.getCode();
            return new ResponseData<>(result.toString(), errorCode);
        } catch (Exception e) {
            logger.error("[registerAuthorityIssuer] register failed due to transaction error.", e);
        }
        return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_EXECUTE_ERROR);
    }

    /**
     * Remove a new Authority Issuer on Chain.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args) {

        ErrorCode innerResponseData = checkRemoveAuthorityIssuerArgs(args);
        if (ErrorCode.SUCCESS.getCode() != innerResponseData.getCode()) {
            return new ResponseData<>(false, innerResponseData);
        }

        String weId = args.getWeId();
        Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
        try {
            reloadAuthorityIssuerContract(args.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = authorityIssuerController
                .removeAuthorityIssuer(addr);
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(receipt);

            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            if (event != null) {
                ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(
                    event,
                    WeIdConstant.REMOVE_AUTHORITY_ISSUER_OPCODE
                );
                if (ErrorCode.SUCCESS.getCode() != errorCode.getCode()) {
                    return new ResponseData<>(false, errorCode);
                } else {
                    return new ResponseData<>(true, errorCode);
                }
            } else {
                logger.error("remove authority issuer failed, transcation event decoding failure.");
                return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }
        } catch (TimeoutException e) {
            logger.error("remove authority issuer failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("remove authority issuer failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("remove authority issuer failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    /**
     * Check whether the given weId is an authority issuer.
     *
     * @param weId the WeIdentity DID
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> isAuthorityIssuer(String weId) {
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();

        if (!WeIdUtils.isWeIdValid(weId)) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
        try {
            Future<Bool> future = authorityIssuerController.isAuthorityIssuer(addr);
            Boolean result =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS).getValue();
            responseData.setResult(result);
            if (result) {
                responseData.setErrorCode(ErrorCode.SUCCESS);
            } else {
                responseData.setErrorCode(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
            }
        } catch (TimeoutException e) {
            logger.error("check authority issuer id failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("check authority issuer id failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("check authority issuer id failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
        return responseData;
    }

    /**
     * Query the authority issuer information given weId.
     *
     * @param weId the WeIdentity DID
     * @return the AuthorityIssuer response data
     */
    @Override
    public ResponseData<AuthorityIssuer> queryAuthorityIssuerInfo(String weId) {
        ResponseData<AuthorityIssuer> responseData = new ResponseData<>();
        if (!WeIdUtils.isWeIdValid(weId)) {
            return new ResponseData<>(null, ErrorCode.WEID_INVALID);
        }
        Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
        try {
            List<Type> rawResult =
                authorityIssuerController
                    .getAuthorityIssuerInfoNonAccValue(addr)
                    .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            if (rawResult == null) {
                return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }

            DynamicArray<Bytes32> bytes32Attributes = (DynamicArray<Bytes32>) rawResult.get(0);
            DynamicArray<Int256> int256Attributes = (DynamicArray<Int256>) rawResult.get(1);

            AuthorityIssuer result = new AuthorityIssuer();
            result.setWeId(weId);
            String name = extractNameFromBytes32Attributes(bytes32Attributes.getValue());
            Long createDate = Long
                .valueOf(int256Attributes.getValue().get(0).getValue().longValue());
            if (StringUtils.isEmpty(name) && createDate.equals(WeIdConstant.LONG_VALUE_ZERO)) {
                return new ResponseData<>(
                    null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS
                );
            }
            result.setName(name);
            result.setCreated(createDate);
            // Accumulator Value is unable to load due to Solidity 0.4.4 restrictions - left blank.
            result.setAccValue("");
            responseData.setResult(result);
        } catch (TimeoutException e) {
            logger.error("query authority issuer failed due to system timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("query authority issuer failed due to transaction error. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("query authority issuer failed.", e);
            return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
        return responseData;
    }

    /**
     * Get all of the authority issuer.
     *
     * @param index start position
     * @param num number of returned authority issuer in this request
     * @return Execution result
     */
    @Override
    public ResponseData<List<AuthorityIssuer>> getAllAuthorityIssuerList(Integer index,
        Integer num) {
        ErrorCode errorCode = isStartEndPosValid(index, num);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(null, errorCode);
        }
        try {
            List<Address> addressList = authorityIssuerController
                .getAuthorityIssuerAddressList(new Uint256(index), new Uint256(num))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS)
                .getValue();
            List<AuthorityIssuer> authorityIssuerList = new ArrayList<>();
            for (Address address : addressList) {
                String weId = WeIdUtils.convertAddressToWeId(address.toString());
                ResponseData<AuthorityIssuer> innerResponseData
                    = this.queryAuthorityIssuerInfo(weId);
                if (innerResponseData.getResult() != null) {
                    authorityIssuerList.add(innerResponseData.getResult());
                }
            }
            return new ResponseData<>(authorityIssuerList, ErrorCode.SUCCESS);
        } catch (TimeoutException e) {
            logger.error("query authority issuer list failed due to system timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("query authority issuer list failed due to transaction error. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("query authority issuer list failed.", e);
            return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    /**
     * Register a new issuer type.
     *
     * @param callerAuth the caller
     * @param issuerType the specified issuer type
     * @return Execution result
     */
    public ResponseData<Boolean> registerIssuerType(
        WeIdAuthentication callerAuth,
        String issuerType
    ) {
        ErrorCode innerCode = isIssuerTypeValid(issuerType);
        if (innerCode != ErrorCode.SUCCESS) {
            return new ResponseData<>(false, innerCode);
        }
        try {
            reloadSpecificIssuerContract(callerAuth.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = specificIssuerController
                .registerIssuerType(DataTypetUtils.stringToBytes32(issuerType));
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            // pass-in empty address
            String emptyAddress = new Address(BigInteger.ZERO).toString();
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, true, emptyAddress);
            return new ResponseData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode);
        } catch (TimeoutException e) {
            logger.error("register issuer type failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("register issuer type failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("register issuer type failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }


    /**
     * Marked an issuer as the specified issuer type.
     *
     * @param callerAuth the caller who have the access to modify this list
     * @param issuerType the specified issuer type
     * @param targetIssuerWeId the weId of the issuer who will be marked as a specific issuer type
     * @return Execution result
     */
    public ResponseData<Boolean> addIssuerIntoIssuerType(
        WeIdAuthentication callerAuth,
        String issuerType,
        String targetIssuerWeId
    ) {
        ErrorCode innerCode = isSpecificTypeIssuerArgsValid(callerAuth, issuerType,
            targetIssuerWeId);
        if (innerCode != ErrorCode.SUCCESS) {
            return new ResponseData<>(false, innerCode);
        }
        try {
            reloadSpecificIssuerContract(callerAuth.getWeIdPrivateKey().getPrivateKey());
            String issuerAddress = WeIdUtils.convertWeIdToAddress(targetIssuerWeId);
            Future<TransactionReceipt> future = specificIssuerController
                .addIssuer(DataTypetUtils.stringToBytes32(issuerType), new Address(issuerAddress));
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, true, issuerAddress);
            return new ResponseData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode);
        } catch (TimeoutException e) {
            logger.error("add issuer into type failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("add issuer into type failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("add issuer into type failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    /**
     * Removed an issuer from the specified issuer list.
     *
     * @param callerAuth the caller who have the access to modify this list
     * @param issuerType the specified issuer type
     * @param targetIssuerWeId the weId of the issuer to be removed from a specific issuer list
     * @return Execution result
     */
    public ResponseData<Boolean> removeIssuerFromIssuerType(
        WeIdAuthentication callerAuth,
        String issuerType,
        String targetIssuerWeId
    ) {
        ErrorCode innerCode = isSpecificTypeIssuerArgsValid(callerAuth, issuerType,
            targetIssuerWeId);
        if (innerCode != ErrorCode.SUCCESS) {
            return new ResponseData<>(false, innerCode);
        }
        try {
            reloadSpecificIssuerContract(callerAuth.getWeIdPrivateKey().getPrivateKey());
            String issuerAddress = WeIdUtils.convertWeIdToAddress(targetIssuerWeId);
            Future<TransactionReceipt> future = specificIssuerController
                .removeIssuer(DataTypetUtils.stringToBytes32(issuerType),
                    new Address(issuerAddress));
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, false, issuerAddress);
            return new ResponseData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode);
        } catch (TimeoutException e) {
            logger.error("remove issuer from type failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("remove issuer from type failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("remove issuer from type failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    /**
     * Check if the given WeId is belonging to a specific issuer type.
     *
     * @param issuerType the issuer type
     * @param targetIssuerWeId the WeId
     * @return true if yes, false otherwise
     */
    public ResponseData<Boolean> isSpecificTypeIssuer(
        String issuerType,
        String targetIssuerWeId
    ) {
        ErrorCode errorCode = isIssuerTypeValid(issuerType);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(false, errorCode);
        }
        if (!weIdService.isWeIdExist(targetIssuerWeId).getResult()) {
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        try {
            Future<Bool> future = specificIssuerController
                .isSpecificTypeIssuer(DataTypetUtils.stringToBytes32(issuerType),
                    new Address(WeIdUtils.convertWeIdToAddress(targetIssuerWeId)));
            Boolean result =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS).getValue();
            if (!result) {
                return new ResponseData<>(result,
                    ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
            }
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        } catch (TimeoutException e) {
            logger.error("check issuer type failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("check issuer type failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("check issuer type failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    /**
     * Get all specific typed issuer in a list.
     *
     * @param issuerType the issuer type
     * @param index the start position index
     * @param num the number of issuers
     * @return the list
     */
    public ResponseData<List<String>> getAllSpecificTypeIssuerList(
        String issuerType,
        Integer index,
        Integer num
    ) {
        ErrorCode errorCode = isIssuerTypeValid(issuerType);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(null, errorCode);
        }
        errorCode = isStartEndPosValid(index, num);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(null, errorCode);
        }
        try {
            List<Address> addresses = specificIssuerController
                .getSpecificTypeIssuerList(DataTypetUtils.stringToBytes32(issuerType),
                    new Uint256(index), new Uint256(num))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS)
                .getValue();
            List<String> addressList = new ArrayList<>();
            for (Address addr : addresses) {
                if (!WeIdUtils.isEmptyAddress(addr)) {
                    addressList.add(addr.toString());
                }
            }
            return new ResponseData<>(addressList, ErrorCode.SUCCESS);
        } catch (TimeoutException e) {
            logger.error("get all specific issuers failed due to system timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("get all specific issuers failed due to transaction error. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("get all specific issuers failed.", e);
            return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    private ErrorCode isStartEndPosValid(Integer index, Integer num) {
        if (index == null || index < 0 || num == null || num <= 0
            || num > WeIdConstant.MAX_AUTHORITY_ISSUER_LIST_SIZE) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode isSpecificTypeIssuerArgsValid(
        WeIdAuthentication callerAuth,
        String issuerType,
        String targetIssuerWeId
    ) {
        if (!weIdService.isWeIdExist(targetIssuerWeId).getResult()) {
            return ErrorCode.WEID_DOES_NOT_EXIST;
        }
        ErrorCode errorCode = isCallerAuthValid(callerAuth);
        if (errorCode.getCode() == ErrorCode.SUCCESS.getCode()) {
            return isIssuerTypeValid(issuerType);
        }
        return errorCode;
    }

    private ErrorCode isCallerAuthValid(WeIdAuthentication callerAuth) {
        if (callerAuth == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (callerAuth.getWeIdPrivateKey() == null
            || StringUtils.isEmpty(callerAuth.getWeIdPrivateKey().getPrivateKey())) {
            return ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL;
        }
        if (!WeIdUtils.isWeIdValid(callerAuth.getWeId())) {
            return ErrorCode.WEID_INVALID;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode resolveSpecificIssuerEvents(
        TransactionReceipt transactionReceipt,
        boolean isRegister,
        String address) {
        List<SpecificIssuerRetLogEventResponse> eventList =
            SpecificIssuerController.getSpecificIssuerRetLogEvents(transactionReceipt);

        SpecificIssuerRetLogEventResponse event = eventList.get(0);
        if (event != null) {
            if (isRegister) {
                // this might be the register type, or the register specific issuer case
                if (event.operation.getValue().intValue()
                    != WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE
                    || !StringUtils.equalsIgnoreCase(event.addr.toString(), address)) {
                    return ErrorCode.TRANSACTION_EXECUTE_ERROR;
                }
            } else {
                // this is the remove specific issuer case
                if (event.operation.getValue().intValue()
                    != WeIdConstant.REMOVE_AUTHORITY_ISSUER_OPCODE
                    || !StringUtils.equalsIgnoreCase(event.addr.toString(), address)) {
                    return ErrorCode.TRANSACTION_EXECUTE_ERROR;
                }
            }
            Integer eventRetCode = event.retCode.getValue().intValue();
            return ErrorCode.getTypeByErrorCode(eventRetCode);
        } else {
            logger.error(
                "specific issuer type resolution failed due to event decoding failure.");
            return ErrorCode.UNKNOW_ERROR;
        }
    }

    private ErrorCode isIssuerTypeValid(String issuerType) {
        if (StringUtils.isEmpty(issuerType)) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (issuerType.length() > WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH) {
            return ErrorCode.SPECIFIC_ISSUER_TYPE_ILLEGAL;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode checkRegisterAuthorityIssuerArgs(
        RegisterAuthorityIssuerArgs args) {

        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        ErrorCode errorCode = checkAuthorityIssuerArgsValidity(
            args.getAuthorityIssuer()
        );

        if (ErrorCode.SUCCESS.getCode() != errorCode.getCode()) {
            logger.error("register authority issuer format error!");
            return errorCode;
        }
        if (args.getWeIdPrivateKey() == null
            || StringUtils.isEmpty(args.getWeIdPrivateKey().getPrivateKey())) {
            return ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL;
        }
        // Need an extra check for the existence of WeIdentity DID on chain, in Register Case.
        ResponseData<Boolean> innerResponseData = weIdService
            .isWeIdExist(args.getAuthorityIssuer().getWeId());
        if (!innerResponseData.getResult()) {
            return ErrorCode.WEID_INVALID;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode checkRemoveAuthorityIssuerArgs(RemoveAuthorityIssuerArgs args) {

        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (!WeIdUtils.isWeIdValid(args.getWeId())) {
            return ErrorCode.WEID_INVALID;
        }
        if (args.getWeIdPrivateKey() == null
            || StringUtils.isEmpty(args.getWeIdPrivateKey().getPrivateKey())) {
            return ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode checkAuthorityIssuerArgsValidity(AuthorityIssuer args) {

        if (args == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (!WeIdUtils.isWeIdValid(args.getWeId())) {
            return ErrorCode.WEID_INVALID;
        }
        String name = args.getName();
        if (!isValidAuthorityIssuerName(name)) {
            return ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL;
        }
        String accValue = args.getAccValue();
        try {
            BigInteger accValueBigInteger = new BigInteger(accValue);
            logger.info(args.getWeId() + " accValue is: " + accValueBigInteger.longValue());
        } catch (Exception e) {
            return ErrorCode.AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL;
        }

        return ErrorCode.SUCCESS;
    }

    private ErrorCode verifyAuthorityIssuerRelatedEvent(
        AuthorityIssuerRetLogEventResponse event,
        Integer opcode) {

        if (event.addr == null || event.operation == null || event.retCode == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        Integer eventOpcode = event.operation.getValue().intValue();
        if (eventOpcode.equals(opcode)) {
            Integer eventRetCode = event.retCode.getValue().intValue();
            return ErrorCode.getTypeByErrorCode(eventRetCode);
        } else {
            return ErrorCode.AUTHORITY_ISSUER_OPCODE_MISMATCH;
        }

    }

    private boolean isValidAuthorityIssuerName(String name) {
        return !StringUtils.isEmpty(name)
            && name.length() < WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH
            && !StringUtils.isWhitespace(name)
            && StringUtils.isAsciiPrintable(name);
    }

    private String[] loadNameToStringAttributes(String name) {
        String[] nameArray = new String[WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH];
        nameArray[0] = name;
        return nameArray;
    }

    private String extractNameFromBytes32Attributes(List<Bytes32> bytes32Array) {
        StringBuffer name = new StringBuffer();
        int maxLength = WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH / 32;
        for (int i = 0; i < maxLength; i++) {
            name.append(DataTypetUtils.bytes32ToString(bytes32Array.get(i)));
        }
        return name.toString();
    }
}
