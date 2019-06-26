package com.webank.weid.service.impl.proxy;

import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.RsvSignature;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class CptServiceProxy {

	
	public EngineResultData<CptBaseInfo>  updateCpt(
            int cptId,
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        ){
		return null;
	}
	
	public EngineResultData<CptBaseInfo>  registerCpt(
            int cptId,
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        ){
		return null;
	}
	
	public EngineResultData<CptBaseInfo>  registerCpt(
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        ){
		return null;
	}
}
