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

package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.util.PropertyUtils;

/**
 * @author tonychen 2019年6月21日
 *
 */

public class WeIdServiceEngine {
	
	private static WeIdController controller;
	
	private static String weIdContractAddr;
	/**
	 * fisco bcos version, default 1.3.x
	 */
	private static String fiscoVersion = PropertyUtils.getProperty("fisco.version", "1.3");

	public WeIdServiceEngine() {
		
		if(fiscoVersion.equals("1.3")) {
			controller = new WeIdController1();
		}
		else {
			controller = new WeIdController2();
		}
	}
	
	public EngineResultData<CreateWeIdDataResult> createWeId() {
		
		return controller.createWeId();
	}
	
	public EngineResultData<Boolean> isWeIdExist(String weId) {
		
		return controller.isWeIdExist(weId);
	}
	
	public EngineResultData<String> createWeId(CreateWeIdArgs createWeIdArgs){
		return controller.createWeId(createWeIdArgs);
	}
	
	public EngineResultData<Boolean> setPublicKey(SetPublicKeyArgs setPublicKeyArgs){
		return controller.setPublicKey(setPublicKeyArgs);
	}
	
	public EngineResultData<Boolean> setService(SetServiceArgs setServiceArgs){
		return controller.setService(setServiceArgs);
	}
	
	public EngineResultData<Boolean> setAuthentication(SetAuthenticationArgs setAuthenticationArgs){
		return controller.setAuthentication(setAuthenticationArgs);
	}
	
	public EngineResultData<WeIdDocument> getWeIdDocument(String weId){
		return controller.getWeIdDocument(weId);
	}
}
