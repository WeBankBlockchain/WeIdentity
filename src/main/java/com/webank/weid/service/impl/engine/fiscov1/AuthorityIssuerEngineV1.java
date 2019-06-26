package com.webank.weid.service.impl.engine.fiscov1;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.CollectionUtils;
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

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.AuthorityIssuerController;
import com.webank.weid.contract.v1.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.contract.v1.SpecificIssuerController;
import com.webank.weid.contract.v1.SpecificIssuerController.SpecificIssuerRetLogEventResponse;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.AuthorityIssuerServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class AuthorityIssuerEngineV1 implements AuthorityIssuerServiceEngine {

	private static AuthorityIssuerController authorityIssuerController;
    private static String authorityIssuerControllerAddress;
    private static SpecificIssuerController specificIssuerController;
    private static String specificIssuerControllerAddress;
    private static final Logger logger = LoggerFactory.getLogger(AuthorityIssuerEngineV1.class);
    
	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#addAuthorityIssuer(com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs)
	 */
	@Override
	public EngineResultData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args) {
		
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
//            reloadAuthorityIssuerContract(args.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = authorityIssuerController.addAuthorityIssuer(
                addr,
                DataToolUtils.stringArrayToBytes32StaticArray(stringAttributes),
                DataToolUtils.longArrayToInt256StaticArray(longAttributes),
                accValue
            );
            TransactionReceipt receipt = future.get(
                WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT,
                TimeUnit.SECONDS
            );
            TransactionInfo info = new TransactionInfo(receipt);
            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(receipt);
            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(event,
                WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE);
            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode, info);
        } catch (TimeoutException e) {
            logger.error("register authority issuer failed due to system timeout. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("register authority issuer failed due to transaction error. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } 
	}
	
	  private String[] loadNameToStringAttributes(String name) {
	        String[] nameArray = new String[WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH];
	        nameArray[0] = name;
	        return nameArray;
	    }

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#removeAuthorityIssuer(com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs)
	 */
	@Override
	public EngineResultData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args) {
		String weId = args.getWeId();
        Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
        try {
//            reloadAuthorityIssuerContract(args.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = authorityIssuerController
                .removeAuthorityIssuer(addr);
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(receipt);

            TransactionInfo info = new TransactionInfo(receipt);
            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            if (event != null) {
                ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(
                    event,
                    WeIdConstant.REMOVE_AUTHORITY_ISSUER_OPCODE
                );
                if (ErrorCode.SUCCESS.getCode() != errorCode.getCode()) {
                    return new EngineResultData<>(false, errorCode, info);
                } else {
                    return new EngineResultData<>(true, errorCode, info);
                }
            } else {
                logger.error("remove authority issuer failed, transcation event decoding failure.");
                return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR, info);
            }
        } catch (TimeoutException e) {
            logger.error("remove authority issuer failed due to system timeout. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("remove authority issuer failed due to transaction error. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
	}
	
    /**
     * Verify Authority Issuer related events.
     *
     * @param event the Event
     * @param opcode the Opcode
     * @return the ErrorCode
     */
    private static ErrorCode verifyAuthorityIssuerRelatedEvent(
        AuthorityIssuerRetLogEventResponse event,
        Integer opcode) {
        if (event == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
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


	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#isAuthorityIssuer(java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> isAuthorityIssuer(String address) {
		EngineResultData<Boolean> resultData = new EngineResultData<Boolean>();
		Address addr = new Address(address);
        try {
            Future<Bool> future = authorityIssuerController.isAuthorityIssuer(addr);
            Boolean result =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS).getValue();
            resultData.setResult(result);
            if (result) {
            	resultData.setErrorCode(ErrorCode.SUCCESS);
            } else {
            	resultData.setErrorCode(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
            }
            return resultData;
        } catch (TimeoutException e) {
            logger.error("check authority issuer id failed due to system timeout. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("check authority issuer id failed due to transaction error. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } 
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#getAuthorityIssuerInfoNonAccValue(java.lang.String)
	 */
	@Override
	public EngineResultData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId) {
		
		EngineResultData<AuthorityIssuer>resultData = new EngineResultData<AuthorityIssuer>();
		Address addr = new Address(WeIdUtils.convertWeIdToAddress(weId));
		try {
            List<Type> rawResult =
                authorityIssuerController
                    .getAuthorityIssuerInfoNonAccValue(addr)
                    .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            if (rawResult == null) {
                return new EngineResultData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }

            DynamicArray<Bytes32> bytes32Attributes = (DynamicArray<Bytes32>) rawResult.get(0);
            DynamicArray<Int256> int256Attributes = (DynamicArray<Int256>) rawResult.get(1);

            AuthorityIssuer result = new AuthorityIssuer();
            result.setWeId(weId);
            String name = extractNameFromBytes32Attributes(bytes32Attributes.getValue());
            Long createDate = Long
                .valueOf(int256Attributes.getValue().get(0).getValue().longValue());
            if (StringUtils.isEmpty(name) && createDate.equals(WeIdConstant.LONG_VALUE_ZERO)) {
                return new EngineResultData<>(
                    null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS
                );
            }
            result.setName(name);
            result.setCreated(createDate);
            // Accumulator Value is unable to load due to Solidity 0.4.4 restrictions - left blank.
            result.setAccValue("");
            resultData.setResult(result);
            return resultData;
        } catch (TimeoutException e) {
            logger.error("query authority issuer failed due to system timeout. ", e);
            return new EngineResultData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("query authority issuer failed due to transaction error. ", e);
            return new EngineResultData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
	}
	
	   private String extractNameFromBytes32Attributes(List<Bytes32> bytes32Array) {
	        StringBuffer name = new StringBuffer();
	        int maxLength = WeIdConstant.MAX_AUTHORITY_ISSUER_NAME_LENGTH / 32;
	        for (int i = 0; i < maxLength; i++) {
	            name.append(DataToolUtils.bytes32ToString(bytes32Array.get(i)));
	        }
	        return name.toString();
	    }

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#getAuthorityIssuerAddressList(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<String> getAuthorityIssuerAddressList(Integer index, Integer num) {
		
		List<String>addrList = new ArrayList<>();
		try {
		List<Address> addressList = authorityIssuerController
                .getAuthorityIssuerAddressList(new Uint256(index), new Uint256(num))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS)
                .getValue();
		if(CollectionUtils.isNotEmpty(addressList)) {
			for(Address addr: addressList) {
				addrList.add(addr.getTypeAsString());
			}
		}
		}catch (TimeoutException e) {
            logger.error("query authority issuer list failed due to system timeout. ", e);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("query authority issuer list failed due to transaction error. ", e);
        }
		
		return addrList;
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#removeIssuer(java.lang.String, java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> removeIssuer(String issuerType, String issuerAddress) {
		try {
            Future<TransactionReceipt> future = specificIssuerController
                .removeIssuer(DataToolUtils.stringToBytes32(issuerType),
                    new Address(issuerAddress));
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            TransactionInfo info = new TransactionInfo(receipt);
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, false, issuerAddress);
            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode, info);
        } catch (TimeoutException e) {
            logger.error("remove issuer from type failed due to system timeout. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("remove issuer from type failed due to transaction error. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } 
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
	
	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#isSpecificTypeIssuer(java.lang.String, java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> isSpecificTypeIssuer(String issuerType, String address) {
		try {
            Future<Bool> future = specificIssuerController
                .isSpecificTypeIssuer(DataToolUtils.stringToBytes32(issuerType),
                    new Address(address));
            Boolean result =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS).getValue();
            if (!result) {
                return new EngineResultData<>(result,
                    ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
            }
            return new EngineResultData<>(result, ErrorCode.SUCCESS);
        } catch (TimeoutException e) {
            logger.error("check issuer type failed due to system timeout. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("check issuer type failed due to transaction error. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        }
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#getSpecificTypeIssuerList(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public EngineResultData<List<String>> getSpecificTypeIssuerList(String issuerType, Integer index, Integer num) {
		try {
            List<Address> addresses = specificIssuerController
                .getSpecificTypeIssuerList(DataToolUtils.stringToBytes32(issuerType),
                    new Uint256(index), new Uint256(num))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS)
                .getValue();
            List<String> addressList = new ArrayList<>();
            for (Address addr : addresses) {
                if (!WeIdUtils.isEmptyAddress(addr)) {
                    addressList.add(addr.toString());
                }
            }
            return new EngineResultData<>(addressList, ErrorCode.SUCCESS);
        } catch (TimeoutException e) {
            logger.error("get all specific issuers failed due to system timeout. ", e);
            return new EngineResultData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("get all specific issuers failed due to transaction error. ", e);
            return new EngineResultData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } 
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.IssuerContractController#registerIssuerType(java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> registerIssuerType(String issuerType) {
		try {
//            reloadSpecificIssuerContract(callerAuth.getWeIdPrivateKey().getPrivateKey());
            Future<TransactionReceipt> future = specificIssuerController
                .registerIssuerType(DataToolUtils.stringToBytes32(issuerType));
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            TransactionInfo info = new TransactionInfo(receipt);
            // pass-in empty address
            String emptyAddress = new Address(BigInteger.ZERO).toString();
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, true, emptyAddress);
            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode, info);
        } catch (TimeoutException e) {
            logger.error("register issuer type failed due to system timeout. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("register issuer type failed due to transaction error. ", e);
            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } 
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.IssuerContractController#addIssuer(java.lang.String, java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> addIssuer(String issuerType, String issuerAddress) {
		 try {
//	        	engine.add
//	            reloadSpecificIssuerContract(callerAuth.getWeIdPrivateKey().getPrivateKey());
//	            String issuerAddress = WeIdUtils.convertWeIdToAddress(targetIssuerWeId);
	            Future<TransactionReceipt> future = specificIssuerController
	                .addIssuer(DataToolUtils.stringToBytes32(issuerType), new Address(issuerAddress));
	            TransactionReceipt receipt =
	                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
	            TransactionInfo info = new TransactionInfo(receipt);
	            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, true, issuerAddress);
	            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
	                errorCode, info);
	        } catch (TimeoutException e) {
	            logger.error("add issuer into type failed due to system timeout. ", e);
	            return new EngineResultData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
	        } catch (InterruptedException | ExecutionException e) {
	            logger.error("add issuer into type failed due to transaction error. ", e);
	            return new EngineResultData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
	        }
	}

}
