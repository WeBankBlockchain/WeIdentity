package com.webank.weid.service.impl.engine;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.abi.EventEncoder;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.core.DefaultBlockParameterNumber;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ResolveEventLogStatus;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.constant.WeIdEventConstant;
import com.webank.weid.contract.v2.WeIdContract;
import com.webank.weid.contract.v2.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.exception.ResolveAttributeException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.ServiceProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.ResolveEventLogResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月21日
 *
 */
public class WeIdController2 implements WeIdController{

	private static final Logger logger = LoggerFactory.getLogger(WeIdController2.class);
	
	 /**
     * The topic map.
     */
    private static final HashMap<String, String> topicMap;
	
	/**
     * Block number for stopping parsing.
     */
    private static final int STOP_RESOLVE_BLOCK_NUMBER = 0;
    
	 /**
     * WeIdentity DID contract object, for calling weIdentity DID contract.
     */
    private static WeIdContract weIdContract;
    
    static {
        // initialize the event topic
        topicMap = new HashMap<String, String>();

        topicMap.put(
            EventEncoder.encode(WeIdContract.WEIDATTRIBUTECHANGED_EVENT),
            WeIdEventConstant.WEID_EVENT_ATTRIBUTE_CHANGE
        );
    }
    

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.WeIdController#isWeIdExist(java.lang.String)
	 */
	@Override
	public EngineResultData<Boolean> isWeIdExist(String weId) {
		 try {
	            boolean isExist = weIdContract
	                .isIdentityExist(WeIdUtils.convertWeIdToAddress(weId)).send().booleanValue();
	            return new EngineResultData<>(isExist, ErrorCode.SUCCESS);
	        } catch (Exception e) {
	            logger.error("[isWeIdExist] execute failed. Error message :{}", e);
	            return new EngineResultData<>(false, ErrorCode.UNKNOW_ERROR);
	        }
	}


	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.WeIdController#getWeIdDocument(java.lang.String)
	 */
	@Override
	public EngineResultData<WeIdDocument> getWeIdDocument(String weId) {
		 WeIdDocument result = new WeIdDocument();
	        result.setId(weId);
	        int latestBlockNumber = 0;
	        try {
	            String identityAddr = WeIdUtils.convertWeIdToAddress(weId);
	            latestBlockNumber = weIdContract
	                .getLatestRelatedBlock(identityAddr).send().intValue();
	            if (0 == latestBlockNumber) {
	                return new EngineResultData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
	            }

	            resolveTransaction(weId, latestBlockNumber, result);
//	            responseData.setResult(result);
	            return new EngineResultData<WeIdDocument>(result, ErrorCode.SUCCESS);
	        } catch (InterruptedException | ExecutionException e) {
	            logger.error("Set weId service failed. Error message :{}", e);
	            return new EngineResultData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
	        } catch (TimeoutException e) {
	            return new EngineResultData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
	        } catch (ResolveAttributeException e) {
	            logger.error("[getWeIdDocument]: resolveTransaction failed. "
	                    + "weId: {}, errorCode:{}",
	                weId,
	                e.getErrorCode(),
	                e);
	            return new EngineResultData<WeIdDocument>(result, ErrorCode.getTypeByErrorCode(e.getErrorCode()));
	        } catch (Exception e) {
	            logger.error("[getWeIdDocument]: exception.", e);
	            return new EngineResultData<>(null, ErrorCode.UNKNOW_ERROR);
	        }
	}

	   private static ResolveEventLogResult resolveAttributeEvent(
		        String weId,
		        TransactionReceipt receipt,
		        WeIdDocument result) {

		        List<WeIdAttributeChangedEventResponse> eventlog =
		            weIdContract.getWeIdAttributeChangedEvents(receipt);
		        ResolveEventLogResult response = new ResolveEventLogResult();

		        if (CollectionUtils.isEmpty(eventlog)) {
		            response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_EVENTLOG_NULL);
		            return response;
		        }

		        int previousBlock = 0;
		        for (WeIdAttributeChangedEventResponse res : eventlog) {
		            if (res.identity == null || res.updated == null || res.previousBlock == null) {
		                response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_RES_NULL);
		                return response;
		            }

		            String identity = res.identity.toString();
		            if (result.getUpdated() == null) {
		                long timeStamp = res.updated.longValue();
		                result.setUpdated(timeStamp);
		            }
		            String weAddress = WeIdUtils.convertWeIdToAddress(weId);
		            if (!StringUtils.equals(weAddress, identity)) {
		                response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_WEID_NOT_MATCH);
		                return response;
		            }

		            String key = new String(res.key);
		            String value = new String(res.value);
		            previousBlock = res.previousBlock.intValue();
		            buildupWeIdAttribute(key, value, weId, result);
		        }

		        response.setPreviousBlock(previousBlock);
		        response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_SUCCESS);
		        return response;
		    }

		    private static void buildupWeIdAttribute(
		        String key, String value, String weId, WeIdDocument result) {
		        if (StringUtils.startsWith(key, WeIdConstant.WEID_DOC_PUBLICKEY_PREFIX)) {
		            buildWeIdPublicKeys(value, weId, result);
		        } else if (StringUtils.startsWith(key, WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX)) {
		            buildWeIdPublicKeys(value, weId, result);
		            buildWeIdAuthentication(value, weId, result);
		        } else if (StringUtils.startsWith(key, WeIdConstant.WEID_DOC_SERVICE_PREFIX)) {
		            buildWeIdService(key, value, weId, result);
		        } else {
		            buildWeIdAttributeDefault(key, value, weId, result);
		        }
		    }

		    private static void buildWeIdPublicKeys(String value, String weId, WeIdDocument result) {

		        logger.info("method buildWeIdPublicKeys() parameter::value:{}, weId:{}, "
		            + "result:{}", value, weId, result);
		        List<PublicKeyProperty> pubkeyList = result.getPublicKey();
		        for (PublicKeyProperty pr : pubkeyList) {
		            if (StringUtils.contains(value, pr.getPublicKey())) {
		                return;
		            }
		        }
		        PublicKeyProperty pubKey = new PublicKeyProperty();
		        pubKey.setId(
		            new StringBuffer()
		                .append(weId)
		                .append("#keys-")
		                .append(result.getPublicKey().size())
		                .toString()
		        );
		        String[] publicKeyData = StringUtils.splitByWholeSeparator(value, "/");
		        if (publicKeyData != null && publicKeyData.length == 2) {
		            pubKey.setPublicKey(publicKeyData[0]);
		            String weAddress = publicKeyData[1];
		            String owner = WeIdUtils.convertAddressToWeId(weAddress);
		            pubKey.setOwner(owner);
		        }
		        result.getPublicKey().add(pubKey);
		    }

		    private static void buildWeIdAuthentication(String value, String weId, WeIdDocument result) {

		        logger.info("method buildWeIdAuthentication() parameter::value:{}, weId:{}, "
		            + "result:{}", value, weId, result);
		        AuthenticationProperty auth = new AuthenticationProperty();
		        List<PublicKeyProperty> keys = result.getPublicKey();
		        List<AuthenticationProperty> authList = result.getAuthentication();

		        for (PublicKeyProperty r : keys) {
		            if (StringUtils.contains(value, r.getPublicKey())) {
		                for (AuthenticationProperty ar : authList) {
		                    if (StringUtils.equals(ar.getPublicKey(), r.getId())) {
		                        return;
		                    }
		                }
		                auth.setPublicKey(r.getId());
		                result.getAuthentication().add(auth);
		            }
		        }
		    }

		    private static void buildWeIdService(String key, String value, String weId,
		        WeIdDocument result) {

		        logger.info("method buildWeIdService() parameter::key{}, value:{}, weId:{}, "
		            + "result:{}", key, value, weId, result);
		        String service = StringUtils.splitByWholeSeparator(key, "/")[2];
		        List<ServiceProperty> serviceList = result.getService();
		        for (ServiceProperty sr : serviceList) {
		            if (StringUtils.equals(service, sr.getType())) {
		                return;
		            }
		        }
		        ServiceProperty serviceResult = new ServiceProperty();
		        serviceResult.setType(service);
		        serviceResult.setServiceEndpoint(value);
		        result.getService().add(serviceResult);
		    }

		    private static void buildWeIdAttributeDefault(
		        String key, String value, String weId, WeIdDocument result) {

		        logger.info("method buildWeIdAttributeDefault() parameter::key{}, value:{}, weId:{}, "
		            + "result:{}", key, value, weId, result);
		        switch (key) {
		            case WeIdConstant.WEID_DOC_CREATED:
		                result.setCreated(Long.valueOf(value));
		                break;
		            default:
		                break;
		        }
		    }

		    private static ResolveEventLogResult resolveEventLog(
		        String weId, Log log, TransactionReceipt receipt, WeIdDocument result) {
		        String topic = log.getTopics().get(0);
		        String event = topicMap.get(topic);

		        if (StringUtils.isNotBlank(event)) {
		            switch (event) {
		                case WeIdConstant.WEID_EVENT_ATTRIBUTE_CHANGE:
		                    return resolveAttributeEvent(weId, receipt, result);
		                default:
		            }
		        }
		        ResolveEventLogResult response = new ResolveEventLogResult();
		        response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_EVENT_NULL);
		        return response;
		    }

		    private static void resolveTransaction(
		        String weId,
		        int blockNumber,
		        WeIdDocument result) {

		        int previousBlock = blockNumber;
		        while (previousBlock != STOP_RESOLVE_BLOCK_NUMBER) {
		            int currentBlockNumber = previousBlock;
		            BcosBlock latestBlock = null;
		            try {
		                latestBlock =
		                    getWeb3j()
		                        .getBlockByNumber(
		                            new DefaultBlockParameterNumber(currentBlockNumber),
		                            true
		                        )
		                        .send();
		            } catch (IOException e) {
		                logger.error(
		                    "[resolveTransaction]:get block by number :{} failed. Exception message:{}",
		                    currentBlockNumber,
		                    e
		                );
		            }
		            if (latestBlock == null) {
		                logger.info(
		                    "[resolveTransaction]:get block by number :{} . latestBlock is null",
		                    currentBlockNumber
		                );
		                return;
		            }
		            List<Transaction> transList =
		                latestBlock
		                    .getBlock()
		                    .getTransactions()
		                    .stream()
		                    .map(transactionResult -> (Transaction) transactionResult.get())
		                    .collect(Collectors.toList());

		            previousBlock = 0;
		            try {
		                for (Transaction transaction : transList) {
		                    String transHash = transaction.getHash();

		                    BcosTransactionReceipt rec1 = getWeb3j().getTransactionReceipt(transHash)
		                        .send();
		                    TransactionReceipt receipt = rec1.getTransactionReceipt().get();
		                    List<Log> logs = rec1.getResult().getLogs();
		                    for (Log log : logs) {
		                        ResolveEventLogResult returnValue =
		                            resolveEventLog(weId, log, receipt, result);
		                        if (returnValue.getResultStatus().equals(
		                            ResolveEventLogStatus.STATUS_SUCCESS)) {
		                            if (returnValue.getPreviousBlock() == currentBlockNumber) {
		                                continue;
		                            }
		                            previousBlock = returnValue.getPreviousBlock();
		                        }
		                    }
		                }
		            } catch (IOException | DataTypeCastException e) {
		                logger.error(
		                    "[resolveTransaction]: get TransactionReceipt by weId :{} failed.",
		                    weId,
		                    e
		                );
		                throw new ResolveAttributeException(
		                    ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
		                    ErrorCode.TRANSACTION_EXECUTE_ERROR.getCodeDesc());
		            }
		        }
		    }

			/* (non-Javadoc)
			 * @see com.webank.weid.service.impl.engine.WeIdController#createWeId(java.lang.String, java.lang.String, java.lang.String)
			 */
			@Override
			public EngineResultData<CreateWeIdDataResult> createWeId(String weId, String publicKey, String privateKey) {
				 String weAddress = WeIdUtils.convertWeIdToAddress(weId);
		            String auth = new StringBuffer()
		                .append(publicKey)
		                .append(WeIdConstant.SEPARATOR)
		                .append(weAddress)
		                .toString();
		            String created = DateUtils.getCurrentTimeStampString();
		            TransactionReceipt receipt;
					try {
						receipt = weIdContract.createWeId(
						    weAddress,
						    DataToolUtils.stringToByteArray(auth),
						    DataToolUtils.stringToByteArray(created),
						    BigInteger.valueOf(DateUtils.getCurrentTimeStamp())
						).send();
					

		            TransactionInfo info = new TransactionInfo(receipt);
		            List<WeIdAttributeChangedEventResponse> response =
		                weIdContract.getWeIdAttributeChangedEvents(receipt);
		            if (CollectionUtils.isEmpty(response)) {
		                logger.error(
		                    "The input private key does not match the current weid, operation of "
		                        + "modifying weid is not allowed. weid is {}",
		                    weId
		                );
		                return new EngineResultData(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH, info);
		            }
		            return new EngineResultData(true, ErrorCode.SUCCESS, info);
					} catch (Exception e) {
						
						return new EngineResultData(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
					}
			}

			/* (non-Javadoc)
			 * @see com.webank.weid.service.impl.engine.WeIdController#setAttribute(java.lang.String, java.lang.String, java.lang.String)
			 */
			@Override
			public EngineResultData<Boolean> setAttribute(String weAddress, String attributeKey, String value) {
//				WeIdContract weIdContract = (WeIdContract) reloadContract(
//		                weIdContractAddress,
//		                privateKey,
//		                WeIdContract.class
//		            );
				try {
		            byte[] attrValue = value.getBytes();
		            BigInteger updated = BigInteger.valueOf(System.currentTimeMillis());
		            TransactionReceipt transactionReceipt =
		                weIdContract.setAttribute(
		                    weAddress,
		                    DataToolUtils.stringToByte32Array(attributeKey),
		                    attrValue,
		                    updated
		                ).send();

		            TransactionInfo info = new TransactionInfo(transactionReceipt);
		            List<WeIdAttributeChangedEventResponse> response =
		                weIdContract.getWeIdAttributeChangedEvents(transactionReceipt);
		            if (CollectionUtils.isNotEmpty(response)) {
		                return new EngineResultData<>(true, ErrorCode.SUCCESS, info);
		            } else {
		                return new EngineResultData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH,
		                    info);
		            }
				}catch (Exception e){
					return new EngineResultData<>(true, ErrorCode.SUCCESS);
				}
			}
}
