package com.webank.weid.service.impl.engine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v2.CptController;
import com.webank.weid.contract.v2.CptController.UpdateCptRetLogEventResponse;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class CptControllerV2 implements CptEngineController {
	
	 private static final Logger logger = LoggerFactory.getLogger(CptControllerV2.class);

	 private static String cptControllerAddress;
	 
	 private static CptController cptController;

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#updateCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
	 */
	@Override
	public EngineResultData<CptBaseInfo> updateCpt(int cptId, String address, String cptJsonSchemaNew,
			RsvSignature rsvSignature) {
		
		List<byte[]> byteArray = new ArrayList<>();
		TransactionReceipt transactionReceipt;
		try {
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
		
		
            List<UpdateCptRetLogEventResponse> event = cptController.getUpdateCptRetLogEvents(
                transactionReceipt
            );
            if (CollectionUtils.isEmpty(event)) {
                logger.error("[updateCpt] event is empty, cptId:{}.", cptId);
                return new EngineResultData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
            }

            return this.getResultByResolveEvent(
                transactionReceipt,
                event.get(0).retCode,
                event.get(0).cptId,
                event.get(0).cptVersion
            );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new EngineResultData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
		}
	}

	private EngineResultData<CptBaseInfo> getResultByResolveEvent(
	        TransactionReceipt receipt,
	        BigInteger retCode,
	        BigInteger cptId,
	        BigInteger cptVersion) {

	        TransactionInfo info = new TransactionInfo(receipt);

	        // register
	        if (retCode.intValue()
	            == ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX.getCode()) {
	            logger.error("[getResultByResolveEvent] cptId limited max value. cptId:{}", cptId);
	            return new EngineResultData<>(null, ErrorCode.CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX, info);
	        }

	        if (retCode.intValue() == ErrorCode.CPT_ALREADY_EXIST.getCode()) {
	            logger.error("[getResultByResolveEvent] cpt already exists on chain. cptId:{}",
	                cptId.intValue());
	            return new EngineResultData<>(null, ErrorCode.CPT_ALREADY_EXIST, info);
	        }

	        if (retCode.intValue() == ErrorCode.CPT_NO_PERMISSION.getCode()) {
	            logger.error("[getResultByResolveEvent] no permission. cptId:{}",
	                cptId.intValue());
	            return new EngineResultData<>(null, ErrorCode.CPT_NO_PERMISSION, info);
	        }

	        // register and update
	        if (retCode.intValue()
	            == ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode()) {
	            logger.error("[getResultByResolveEvent] publisher does not exist. cptId:{}", cptId);
	            return new EngineResultData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST, info);
	        }

	        // update
	        if (retCode.intValue()
	            == ErrorCode.CPT_NOT_EXISTS.getCode()) {
	            logger.error("[getResultByResolveEvent] cpt id : {} does not exist.", cptId);
	            return new EngineResultData<>(null, ErrorCode.CPT_NOT_EXISTS, info);
	        }

	        CptBaseInfo result = new CptBaseInfo();
	        result.setCptId(cptId.intValue());
	        result.setCptVersion(cptVersion.intValue());

	        EngineResultData<CptBaseInfo> responseData =
	            new EngineResultData<>(result, ErrorCode.SUCCESS, info);
	        return responseData;
	    }
	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(int, java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
	 */
	@Override
	public EngineResultData<CptBaseInfo> registerCpt(int cptId, String address, String cptJsonSchemaNew,
			RsvSignature rsvSignature) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.service.impl.engine.CptEngineController#registerCpt(java.lang.String, java.lang.String, com.webank.weid.protocol.response.RsvSignature)
	 */
	@Override
	public EngineResultData<CptBaseInfo> registerCpt(String address, String cptJsonSchemaNew,
			RsvSignature rsvSignature) {
		// TODO Auto-generated method stub
		return null;
	}


}
