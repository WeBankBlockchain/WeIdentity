package com.webank.weid.service.impl.proxy;

import java.util.List;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.service.impl.engine.AuthorityIssuerServiceEngine;
import com.webank.weid.service.impl.engine.fiscov1.AuthorityIssuerEngineV1;
import com.webank.weid.service.impl.engine.fiscov2.AuthorityIssuerEngineV2;
import com.webank.weid.util.PropertyUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class AuthorityIssuerServiceProxy {
	
private static AuthorityIssuerServiceEngine engine;
	
	private static String weIdContractAddr;
	/**
	 * fisco bcos version, default 1.3.x
	 */
	private static String fiscoVersion = PropertyUtils.getProperty("fisco.version", "1.3");

	public AuthorityIssuerServiceProxy() {
		
		if(fiscoVersion.equals("1.3")) {
			engine = new AuthorityIssuerEngineV1();
		}
		else {
			engine = new AuthorityIssuerEngineV2();
		}
	}
	
public EngineResultData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args){
	return engine.addAuthorityIssuer(args);
}
	
	public EngineResultData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args){
		return engine.removeAuthorityIssuer(args);
	}
	
	public EngineResultData<Boolean> isAuthorityIssuer(String address){
		return engine.isAuthorityIssuer(address);
	}
	
	public EngineResultData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId){
		return engine.getAuthorityIssuerInfoNonAccValue(weId);
	}
	
	public List<String> getAuthorityIssuerAddressList(Integer index, Integer num){
		return engine.getAuthorityIssuerAddressList(index, num);
	}
	
	public EngineResultData<Boolean> removeIssuer(String issuerType, String issuerAddress){
		return engine.removeIssuer(issuerType, issuerAddress);
	}
	
	public EngineResultData<Boolean> isSpecificTypeIssuer(String issuerType, String address){
		return engine.isSpecificTypeIssuer(issuerType, address);
	}
	
	public EngineResultData<List<String>> getSpecificTypeIssuerList(String issuerType,
	        Integer index,
	        Integer num){
		return engine.getSpecificTypeIssuerList(issuerType, index, num);
	}
	
	public EngineResultData<Boolean>  registerIssuerType(String issuerType){
		return engine.registerIssuerType(issuerType);
	}
	
	public EngineResultData<Boolean> addIssuer(String issuerType, String issuerAddress){
		return engine.addIssuer(issuerType, issuerAddress);
	}
}
