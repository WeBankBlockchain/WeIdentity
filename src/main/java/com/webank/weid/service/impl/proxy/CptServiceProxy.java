/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.service.impl.proxy;

import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.service.impl.engine.CptServiceEngine;
import com.webank.weid.service.impl.engine.fiscov1.CptServiceEngineV1;
import com.webank.weid.service.impl.engine.fiscov2.CptServiceEngineV2;
import com.webank.weid.util.PropertyUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class CptServiceProxy {

private static CptServiceEngine engine;
	
	private static String cptContractAddr;
	/**
	 * fisco bcos version, default 1.3.x
	 */
	private static String fiscoVersion = PropertyUtils.getProperty("fisco.version", "1.3");

	public CptServiceProxy() {
		
		if(fiscoVersion.equals("1.3")) {
			engine = new CptServiceEngineV1();
		}
		else {
			engine = new CptServiceEngineV2();
		}
	}
	
	public ResponseData<CptBaseInfo>  updateCpt(
            int cptId,
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        ){
		return engine.updateCpt(cptId, address, cptJsonSchemaNew, rsvSignature);
	}
	
	public ResponseData<CptBaseInfo>  registerCpt(
            int cptId,
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        ){
		return engine.registerCpt(cptId, address, cptJsonSchemaNew, rsvSignature);
	}
	
	public ResponseData<CptBaseInfo>  registerCpt(
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature
        ){
		return engine.registerCpt(address, cptJsonSchemaNew, rsvSignature);
	}
	
	public ResponseData<Cpt> queryCpt(int cptId){
		return engine.queryCpt(cptId);
	}
}
