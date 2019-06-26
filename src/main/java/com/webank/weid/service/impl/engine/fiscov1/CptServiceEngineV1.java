package com.webank.weid.service.impl.engine.fiscov1;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.CollectionUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.CptController;
import com.webank.weid.contract.v1.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class CptServiceEngineV1 implements CptServiceEngine {
	
	 private static final Logger logger = LoggerFactory.getLogger(CptServiceEngineV1.class);

	 private static String cptControllerAddress;
	 
	 private static CptController cptController;

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#updateCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
	 */
	@Override
	public EngineResultData<CptBaseInfo> updateCpt(int cptId, String address, String cptJsonSchemaNew,
			RsvSignature rsvSignature) {
		
		StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
	            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
	        );

		TransactionReceipt receipt;
		try {
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
			return resolveRegisterCptEvents(receipt);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new EngineResultData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
		}
		
	}

	/**
     * Verify Register CPT related events.
     *
     * @param transactionReceipt the TransactionReceipt
     * @return the ErrorCode
     */
    public static EngineResultData<CptBaseInfo> resolveRegisterCptEvents(
        TransactionReceipt transactionReceipt) {
        List<RegisterCptRetLogEventResponse> event = CptController.getRegisterCptRetLogEvents(
            transactionReceipt
        );

        if (CollectionUtils.isEmpty(event)) {
            logger.error("[registerCpt] event is empty");
            return new EngineResultData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
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
    public static EngineResultData<CptBaseInfo> getResultByResolveEvent(
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
            return new EngineResultData<>(null, ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX, info);
        }

        if (DataToolUtils.uint256ToInt(retCode) == ErrorCode.CPT_ALREADY_EXIST.getCode()) {
            logger.error("[getResultByResolveEvent] cpt already exists on chain. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new EngineResultData<>(null, ErrorCode.CPT_ALREADY_EXIST, info);
        }

        if (DataToolUtils.uint256ToInt(retCode) == ErrorCode.CPT_NO_PERMISSION.getCode()) {
            logger.error("[getResultByResolveEvent] no permission. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new EngineResultData<>(null, ErrorCode.CPT_NO_PERMISSION, info);
        }

        // register and update
        if (DataToolUtils.uint256ToInt(retCode)
            == ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode()) {
            logger.error("[getResultByResolveEvent] publisher does not exist. cptId:{}",
                DataToolUtils.uint256ToInt(cptId));
            return new EngineResultData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST, info);
        }

        // update
        if (DataToolUtils.uint256ToInt(retCode)
            == ErrorCode.CPT_NOT_EXISTS.getCode()) {
            logger.error("[getResultByResolveEvent] cpt id : {} does not exist.",
                DataToolUtils.uint256ToInt(cptId));
            return new EngineResultData<>(null, ErrorCode.CPT_NOT_EXISTS, info);
        }

        CptBaseInfo result = new CptBaseInfo();
        result.setCptId(DataToolUtils.uint256ToInt(cptId));
        result.setCptVersion(DataToolUtils.int256ToInt(cptVersion));

        return new EngineResultData<>(result, ErrorCode.SUCCESS, info);
    }
	
	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
	 */
	@Override
	public EngineResultData<CptBaseInfo> registerCpt(int cptId, String address, String cptJsonSchemaNew,
			RsvSignature rsvSignature) {
		StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
	            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
	        );

//	        reloadContract(weIdPrivateKey.getPrivateKey());
	            // the case to update a CPT. Requires a valid CPT ID
//	        	engine.
		TransactionReceipt receipt;
		try {
			
	                // the case to register a CPT with a pre-set CPT ID
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
	            
			return resolveRegisterCptEvents(receipt);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new EngineResultData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
		}
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
	 */
	@Override
	public EngineResultData<CptBaseInfo> registerCpt(String address, String cptJsonSchemaNew,
			RsvSignature rsvSignature) {
		StaticArray<Bytes32> bytes32Array = DataToolUtils.stringArrayToBytes32StaticArray(
	            new String[WeIdConstant.CPT_STRING_ARRAY_LENGTH]
	        );

//	        reloadContract(weIdPrivateKey.getPrivateKey());
	            // the case to update a CPT. Requires a valid CPT ID
//	        	engine.
		TransactionReceipt receipt;
		try {
			
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
	            
			return resolveRegisterCptEvents(receipt);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new EngineResultData<CptBaseInfo>(null, ErrorCode.UNKNOW_ERROR);
		}
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#queryCpt(int)
	 */
	@Override
	public EngineResultData<Cpt> queryCpt(int cptId) {
		List<Type> typeList = cptController
                .queryCpt(DataToolUtils.intToUint256(cptId))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);

            if (typeList == null || typeList.isEmpty()) {
                logger.error("Query cpt id : {} does not exist, result is null.", cptId);
                return new EngineResultData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }

            if (WeIdConstant.EMPTY_ADDRESS.equals(((Address) typeList.get(0)).toString())) {
                logger.error("Query cpt id : {} does not exist.", cptId);
                return new EngineResultData<>(null, ErrorCode.CPT_NOT_EXISTS);
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

            String[] jsonSchemaArray =
                DataToolUtils.bytes32DynamicArrayToStringArrayWithoutTrim(
                    (DynamicArray<Bytes32>) typeList.get(3)
                );
            StringBuffer jsonSchema = new StringBuffer();
            for (int i = 0; i < jsonSchemaArray.length; i++) {
                jsonSchema.append(jsonSchemaArray[i]);
            }

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
            return new EngineResultData<Cpt>(cpt, ErrorCode.SUCCESS); 
	}


}
