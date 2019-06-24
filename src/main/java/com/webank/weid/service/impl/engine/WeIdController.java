package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.EngineResultData;

/**
 * @author tonychen 2019年6月21日
 * @param <T>
 *
 */
public interface WeIdController{

	EngineResultData<CreateWeIdDataResult> createWeId();
	
	EngineResultData<String> createWeId(CreateWeIdArgs createWeIdArgs);
	
	EngineResultData<Boolean> isWeIdExist(String weId);
	
	EngineResultData<Boolean> setPublicKey(SetPublicKeyArgs setPublicKeyArgs);
	
	EngineResultData<Boolean> setService(SetServiceArgs setServiceArgs);
	
	EngineResultData<Boolean> setAuthentication(SetAuthenticationArgs setAuthenticationArgs);
	
	EngineResultData<WeIdDocument> getWeIdDocument(String weId);
}
