package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.RsvSignature;

/**
 * @author tonychen 2019年6月25日
 *
 */
public interface CptServiceEngine {


	EngineResultData<CptBaseInfo>  updateCpt(
            int cptId,
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        );
	
	EngineResultData<CptBaseInfo>  registerCpt(
            int cptId,
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        );
	
	EngineResultData<CptBaseInfo>  registerCpt(
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        );
	
	EngineResultData<Cpt>  queryCpt(
			int cptId
        );
}
