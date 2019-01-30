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
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.contract.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.DataTypetUtils;
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
    }

    /**
     * Use the cpt publisher's private key to send the transaction to call the contract.
     *
     * @param privateKey the private key
     */
    private static void reloadContract(String privateKey) {
        authorityIssuerController = (AuthorityIssuerController) reloadContract(
            authorityIssuerControllerAddress,
            privateKey,
            AuthorityIssuerController.class
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

        ResponseData<Boolean> innerResponseData = checkRegisterAuthorityIssuerArgs(args);
        if (!innerResponseData.getResult()) {
            return new ResponseData<>(
                false, innerResponseData.getErrorCode(), innerResponseData.getErrorMessage());
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
                .getBytes(WeIdConstant.UTF_8));
            reloadContract(args.getWeIdPrivateKey().getPrivateKey());
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
            if (event != null) {
                return verifyAuthorityIssuerRelatedEvent(
                    event,
                    addr,
                    WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE
                );
            } else {
                logger.error(
                    "register authority issuer failed due to transcation event decoding failure.");
                return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }
        } catch (TimeoutException e) {
            logger.error("register authority issuer failed due to system timeout. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("register authority issuer failed due to transaction error. ", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("register authority issuer failed.", e);
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
    }

    /**
     * Remove a new Authority Issuer on Chain.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args) {

        ResponseData<Boolean> innerResponseData = checkRemoveAuthorityIssuerArgs(args);
        if (!innerResponseData.getResult()) {
            return new ResponseData<>(
                false,
                innerResponseData.getErrorCode(),
                innerResponseData.getErrorMessage()
            );
        }

        String weId = args.getWeId();
        Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
        try {
            reloadContract(args.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = authorityIssuerController
                .removeAuthorityIssuer(addr);
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(receipt);

            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            if (event != null) {
                return verifyAuthorityIssuerRelatedEvent(
                    event,
                    addr,
                    WeIdConstant.REMOVE_AUTHORITY_ISSUER_OPCODE
                );
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
                responseData.setErrorCode(ErrorCode.SUCCESS.getCode());
                responseData.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
            } else {
                responseData
                    .setErrorCode(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode());
                responseData.setErrorMessage(
                    ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCodeDesc());
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
        ResponseData<AuthorityIssuer> responseData = new ResponseData<AuthorityIssuer>();

        if (!WeIdUtils.isWeIdValid(weId)) {
            return new ResponseData<AuthorityIssuer>(null, ErrorCode.WEID_INVALID);
        }
        Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
        try {
            List<Type> rawResult =
                authorityIssuerController
                    .getAuthorityIssuerInfoNonAccValue(addr)
                    .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            if (rawResult == null) {
                return new ResponseData<AuthorityIssuer>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }

            DynamicArray<Bytes32> bytes32Attributes = (DynamicArray<Bytes32>) rawResult.get(0);
            DynamicArray<Int256> int256Attributes = (DynamicArray<Int256>) rawResult.get(1);

            AuthorityIssuer result = new AuthorityIssuer();
            result.setWeId(weId);
            String name = extractNameFromBytes32Attributes(bytes32Attributes.getValue());
            Long createDate = Long
                .valueOf(int256Attributes.getValue().get(0).getValue().longValue());
            if (StringUtils.isEmpty(name) && createDate.equals(WeIdConstant.LONG_VALUE_ZERO)) {
                return new ResponseData<AuthorityIssuer>(
                    null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
            }
            result.setName(name);
            result.setCreated(createDate);
            // Accumulator Value is unable to load due to Solidity 0.4.4 restrictions - left blank.
            result.setAccValue("");
            responseData.setResult(result);
        } catch (TimeoutException e) {
            logger.error("query authority issuer failed due to system timeout. ", e);
            return new ResponseData<AuthorityIssuer>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("query authority issuer failed due to transaction error. ", e);
            return new ResponseData<AuthorityIssuer>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("query authority issuer failed.", e);
            return new ResponseData<AuthorityIssuer>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
        return responseData;
    }

    private ResponseData<Boolean> checkRegisterAuthorityIssuerArgs(
        RegisterAuthorityIssuerArgs args) {

        if (args == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        ResponseData<Boolean> checkResponse = checkAuthorityIssuerArgsValidity(
            args.getAuthorityIssuer()
        );
        if (!checkResponse.getResult()) {
            logger.error("register authority issuer format error!");
            return new ResponseData<>(
                false,
                checkResponse.getErrorCode(),
                checkResponse.getErrorMessage()
            );
        }
        if (args.getWeIdPrivateKey() == null
            || StringUtils.isEmpty(args.getWeIdPrivateKey().getPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL);
        }
        // Need an extra check for the existence of WeIdentity DID on chain, in Register Case.
        ResponseData<Boolean> innerResponseData = weIdService
            .isWeIdExist(args.getAuthorityIssuer().getWeId());
        if (!innerResponseData.getResult()) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        responseData.setResult(true);
        return responseData;
    }

    private ResponseData<Boolean> checkRemoveAuthorityIssuerArgs(RemoveAuthorityIssuerArgs args) {

        if (args == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isWeIdValid(args.getWeId())) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        if (args.getWeIdPrivateKey() == null
            || StringUtils.isEmpty(args.getWeIdPrivateKey().getPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL);
        }
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        responseData.setResult(true);
        return responseData;
    }

    private ResponseData<Boolean> checkAuthorityIssuerArgsValidity(AuthorityIssuer args) {

        if (args == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isWeIdValid(args.getWeId())) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        String name = args.getName();
        if (!isValidAuthorityIssuerName(name)) {
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL);
        }
        String accValue = args.getAccValue();
        try {
            BigInteger accValueBigInteger = new BigInteger(accValue);
            logger.info(args.getWeId() + " accValue is: " + accValueBigInteger.longValue());
        } catch (Exception e) {
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL);
        }
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        responseData.setResult(true);
        return responseData;
    }

    private ResponseData<Boolean> verifyAuthorityIssuerRelatedEvent(
        AuthorityIssuerRetLogEventResponse event,
        Address addr,
        Integer opcode) {

        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        if (event.addr == null || event.operation == null || event.retCode == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (event.addr.getValue().equals(addr.getValue())) {
            Integer eventOpcode = event.operation.getValue().intValue();
            if (eventOpcode.equals(opcode)) {
                Integer eventRetCode = event.retCode.getValue().intValue();
                if (eventRetCode.equals(ErrorCode.SUCCESS.getCode())) {
                    return new ResponseData<>(true, ErrorCode.SUCCESS);
                } else {
                    responseData.setResult(false);
                    responseData.setErrorCode(eventRetCode);
                    if (eventRetCode.equals(
                        ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCode())) {
                        responseData.setErrorMessage(
                            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCodeDesc());
                    } else if (eventRetCode.equals(
                        ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode())) {
                        responseData.setErrorMessage(
                            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCodeDesc());
                    } else if (eventRetCode.equals(
                        ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode())) {
                        responseData.setErrorMessage(
                            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCodeDesc());
                    } else {
                        responseData.setErrorCode(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode());
                        responseData
                            .setErrorMessage(ErrorCode.AUTHORITY_ISSUER_ERROR.getCodeDesc());
                    }
                    return responseData;
                }
            } else {
                return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_OPCODE_MISMATCH);
            }
        } else {
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ADDRESS_MISMATCH);
        }
    }

    private boolean isValidAuthorityIssuerName(String name) {
        return !StringUtils.isEmpty(name)
            && name.length() < WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH
            && !StringUtils.isWhitespace(name)
            && StringUtils.isAsciiPrintable(name);
    }

    private String[] loadNameToStringAttributes(String name) {
        String[] nameArray = new String[16];
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
