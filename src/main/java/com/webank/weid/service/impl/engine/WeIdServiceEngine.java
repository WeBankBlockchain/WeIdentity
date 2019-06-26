package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.EngineResultData;

/**
 * @author tonychen 2019年6月21日
 * @param <T>
 *
 */
public interface WeIdServiceEngine{

	
	EngineResultData<CreateWeIdDataResult> createWeId(String weId, String publicKey,
	        String privateKey);
	
	
	EngineResultData<Boolean> setAttribute(String weAddress, String attributeKey,
	        String value);
	
	
	EngineResultData<Boolean> isWeIdExist(String weId);
	
	
	EngineResultData<WeIdDocument> getWeIdDocument(String weId);
}
