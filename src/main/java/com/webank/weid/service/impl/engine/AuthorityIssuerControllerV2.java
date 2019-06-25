package com.webank.weid.service.impl.engine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v2.AuthorityIssuerController;
import com.webank.weid.contract.v2.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.contract.v2.SpecificIssuerController;
import com.webank.weid.contract.v2.SpecificIssuerController.SpecificIssuerRetLogEventResponse;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class AuthorityIssuerControllerV2 implements IssuerContractController {

	   private static final Logger logger = LoggerFactory.getLogger(AuthorityIssuerControllerV2.class);

	    private static AuthorityIssuerController authorityIssuerController;
	    private static String authorityIssuerControllerAddress;
	    private static SpecificIssuerController specificIssuerController;
	    private static String specificIssuerControllerAddress;
	    
	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#addAuthorityIssuer(com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs)
	 */
	@Override
	public EngineResultData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args) {
		 AuthorityIssuer authorityIssuer = args.getAuthorityIssuer();
	        String weAddress = WeIdUtils.convertWeIdToAddress(authorityIssuer.getWeId());
	        List<byte[]> stringAttributes = new ArrayList<byte[]>();
	        stringAttributes.add(authorityIssuer.getName().getBytes());
	        List<BigInteger> longAttributes = new ArrayList<>();
	        Long createDate = System.currentTimeMillis();
	        longAttributes.add(BigInteger.valueOf(createDate));
	        try {
//	            reloadAuthorityIssuerContract(args.getWeIdPrivateKey().getPrivateKey());
	            TransactionReceipt receipt = authorityIssuerController.addAuthorityIssuer(
	                weAddress,
	                DataToolUtils.bytesArrayListToBytes32ArrayList(
	                    stringAttributes,
	                    WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH
	                ),
	                DataToolUtils.listToListBigInteger(
	                    longAttributes,
	                    WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH
	                ),
	                authorityIssuer.getAccValue().getBytes()
	            ).send();
	            ErrorCode errorCode = resolveRegisterAuthorityIssuerEvents(receipt);
	            TransactionInfo info = new TransactionInfo(receipt);
	            if (errorCode.equals(ErrorCode.SUCCESS)) {
	                return new EngineResultData<>(Boolean.TRUE, ErrorCode.SUCCESS, info);
	            } else {
	                return new EngineResultData<>(Boolean.FALSE, errorCode, info);
	            }
	        } catch (Exception e) {
	            logger.error("register authority issuer failed.", e);
	            return new EngineResultData<>(Boolean.FALSE, ErrorCode.UNKNOW_ERROR);
	        }
	}
	
	  private ErrorCode resolveRegisterAuthorityIssuerEvents(
		        TransactionReceipt transactionReceipt) {
		        List<AuthorityIssuerRetLogEventResponse> eventList =
		            authorityIssuerController.getAuthorityIssuerRetLogEvents(transactionReceipt);

		        AuthorityIssuerRetLogEventResponse event = eventList.get(0);
		        if (event != null) {
		            ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(
		                event,
		                WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE
		            );
		            return errorCode;
		        } else {
		            logger.error(
		                "register authority issuer failed due to transcation event decoding failure.");
		            return ErrorCode.AUTHORITY_ISSUER_ERROR;
		        }
		    }
	  
	    private ErrorCode verifyAuthorityIssuerRelatedEvent(
	            AuthorityIssuerRetLogEventResponse event,
	            Integer opcode) {

	            if (event.addr == null || event.operation == null || event.retCode == null) {
	                return ErrorCode.ILLEGAL_INPUT;
	            }
	            Integer eventOpcode = event.operation.intValue();
	            if (eventOpcode.equals(opcode)) {
	                Integer eventRetCode = event.retCode.intValue();
	                return ErrorCode.getTypeByErrorCode(eventRetCode);
	            } else {
	                return ErrorCode.AUTHORITY_ISSUER_OPCODE_MISMATCH;
	            }

	        }

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#removeAuthorityIssuer(com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs)
	 */
	@Override
	public EngineResultData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args) {
		String weId = args.getWeId();
        try {
//            reloadAuthorityIssuerContract(args.getWeIdPrivateKey().getPrivateKey());
            TransactionReceipt receipt = authorityIssuerController
                .removeAuthorityIssuer(WeIdUtils.convertWeIdToAddress(weId)).send();
            List<AuthorityIssuerRetLogEventResponse> eventList =
                authorityIssuerController.getAuthorityIssuerRetLogEvents(receipt);

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
        } catch (Exception e) {
            logger.error("remove authority issuer failed.", e);
            return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
	}

    
	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#isAuthorityIssuer(java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> isAuthorityIssuer(String address) {
		EngineResultData<Boolean> resultData = new EngineResultData<Boolean>();
		 try {
	            Boolean result = authorityIssuerController.isAuthorityIssuer(
	            		address).send();
	            resultData.setResult(result);
	            if (result) {
	            	resultData.setErrorCode(ErrorCode.SUCCESS);
	            } else {
	            	resultData.setErrorCode(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
	            }
	            return resultData;
	        } catch (Exception e) {
	            logger.error("check authority issuer id failed.", e);
	            return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
	        }
		 
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#getAuthorityIssuerInfoNonAccValue(java.lang.String)
	 */
	@Override
	public EngineResultData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId) {
		EngineResultData<AuthorityIssuer> resultData = new EngineResultData<AuthorityIssuer>();
		try {
            Tuple2<List<byte[]>, List<BigInteger>> rawResult =
                authorityIssuerController.getAuthorityIssuerInfoNonAccValue(
                    WeIdUtils.convertWeIdToAddress(weId)).send();
            if (rawResult == null) {
                return new EngineResultData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }

            List<byte[]> bytes32Attributes = rawResult.getValue1();
            List<BigInteger> int256Attributes = rawResult.getValue2();

            AuthorityIssuer result = new AuthorityIssuer();
            result.setWeId(weId);
            String name = DataToolUtils.byte32ListToString(
                bytes32Attributes,
                WeIdConstant.AUTHORITY_ISSUER_ARRAY_LEGNTH
            );
            Long createDate = Long
                .valueOf(int256Attributes.get(0).longValue());
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
        } catch (Exception e) {
            logger.error("query authority issuer failed.", e);
            return new EngineResultData<>(null, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#getAuthorityIssuerAddressList(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<String> getAuthorityIssuerAddressList(Integer index, Integer num) {
		List<String> addressList = new ArrayList<>();
		try {
		addressList =
	                authorityIssuerController.getAuthorityIssuerAddressList(
	                		new BigInteger(index.toString()),
	                		new BigInteger(num.toString())
	                ).send();
		}catch(Exception e) {
			 logger.error("query authority issuer failed.", e);
		}
	                return addressList;
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#removeIssuer(java.lang.String, java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> removeIssuer(String issuerType, String issuerAddress) {
		 try {
	            TransactionReceipt receipt = specificIssuerController.removeIssuer(
	                DataToolUtils.stringToByte32Array(issuerType),
	                issuerAddress).send();

	            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, false, issuerAddress);
	            TransactionInfo info = new TransactionInfo(receipt);
	            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
	                errorCode, info);
	        } catch (Exception e) {
	            logger.error("remove issuer from type failed.", e);
	            return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
	        }
	}
	 private ErrorCode resolveSpecificIssuerEvents(
		        TransactionReceipt transactionReceipt,
		        boolean isRegister,
		        String address) {
		        List<SpecificIssuerRetLogEventResponse> eventList =
		            specificIssuerController.getSpecificIssuerRetLogEvents(transactionReceipt);

		        SpecificIssuerRetLogEventResponse event = eventList.get(0);
		        if (event != null) {
		            if (isRegister) {
		                // this might be the register type, or the register specific issuer case
		                if (event.operation.intValue()
		                    != WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE
		                    || !StringUtils.equalsIgnoreCase(event.addr.toString(), address)) {
		                    return ErrorCode.TRANSACTION_EXECUTE_ERROR;
		                }
		            } else {
		                // this is the remove specific issuer case
		                if (event.operation.intValue()
		                    != WeIdConstant.REMOVE_AUTHORITY_ISSUER_OPCODE
		                    || !StringUtils.equalsIgnoreCase(event.addr.toString(), address)) {
		                    return ErrorCode.TRANSACTION_EXECUTE_ERROR;
		                }
		            }
		            Integer eventRetCode = event.retCode.intValue();
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
            Boolean result = specificIssuerController.isSpecificTypeIssuer(
                DataToolUtils.stringToByte32Array(issuerType),
                address
            ).send();

            if (!result) {
                return new EngineResultData<>(result,
                    ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
            }
            return new EngineResultData<>(result, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("check issuer type failed.", e);
            return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.AuthorityIssuerController#getSpecificTypeIssuerList(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public EngineResultData<List<String>> getSpecificTypeIssuerList(String issuerType, Integer index, Integer num) {
		List<String> addresses = new ArrayList<>();
		EngineResultData<List<String>> result = new EngineResultData<List<String>>();
		try {
			
			addresses = specificIssuerController.getSpecificTypeIssuerList(
					DataToolUtils.stringToByte32Array(issuerType),
					new BigInteger(index.toString()),
					new BigInteger(num.toString())
					).send();
			return result;
		}catch(Exception e) {
			logger.error("check issuer type failed.", e);
			return new EngineResultData<List<String>>(null, ErrorCode.UNKNOW_ERROR);
		}
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.IssuerContractController#registerIssuerType(java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> registerIssuerType(String issuerType) {
		try {
            TransactionReceipt receipt = specificIssuerController
                .registerIssuerType(DataToolUtils.stringToByte32Array(issuerType)).send();

            // pass-in empty address
            String emptyAddress = new Address(BigInteger.ZERO).toString();
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, true, emptyAddress);
            TransactionInfo info = new TransactionInfo(receipt);
            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode, info);
        } catch (Exception e) {
            logger.error("register issuer type failed.", e);
            return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.IssuerContractController#addIssuer(java.lang.String, java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> addIssuer(String issuerType, String issuerAddress) {
		try {
		TransactionReceipt receipt = specificIssuerController.addIssuer(
                DataToolUtils.stringToByte32Array(issuerType),
                issuerAddress
            ).send();
            ErrorCode errorCode = resolveSpecificIssuerEvents(receipt, true, issuerAddress);
            TransactionInfo info = new TransactionInfo(receipt);
            return new EngineResultData<>(errorCode.getCode() == ErrorCode.SUCCESS.getCode(),
                errorCode, info);
        } catch (Exception e) {
            logger.error("add issuer into type failed.", e);
            return new EngineResultData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
	}

}
