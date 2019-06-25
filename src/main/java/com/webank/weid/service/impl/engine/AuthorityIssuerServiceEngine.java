package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.util.PropertyUtils;

/**
 * @author tonychen 2019年6月25日
 *
 */
public class AuthorityIssuerServiceEngine {
	
private static IssuerContractController controller;
	
	private static String weIdContractAddr;
	/**
	 * fisco bcos version, default 1.3.x
	 */
	private static String fiscoVersion = PropertyUtils.getProperty("fisco.version", "1.3");

	public AuthorityIssuerServiceEngine() {
		
		if(fiscoVersion.equals("1.3")) {
			controller = new AuthorityIssuerControllerV1();
		}
		else {
			controller = new AuthorityIssuerControllerV2();
		}
	}
	
public EngineResultData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args){
	return controller.addAuthorityIssuer(args);
}
	
	public EngineResultData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args){
		return controller.removeAuthorityIssuer(args);
	}
	
	public EngineResultData<Boolean> isAuthorityIssuer(String address){
		return controller.isAuthorityIssuer(address);
	}
	
	public EngineResultData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId){
		return controller.getAuthorityIssuerInfoNonAccValue(weId);
	}
	
	public List<String> getAuthorityIssuerAddressList(Integer index, Integer num){
		return controller.getAuthorityIssuerAddressList(index, num);
	}
	
	public EngineResultData<Boolean> removeIssuer(String issuerType, String issuerAddress){
		return controller.removeIssuer(issuerType, issuerAddress);
	}
	
	public EngineResultData<Boolean> isSpecificTypeIssuer(String issuerType, String address){
		return controller.isSpecificTypeIssuer(issuerType, address);
	}
	
	public EngineResultData<List<String>> getSpecificTypeIssuerList(String issuerType,
	        Integer index,
	        Integer num){
		return controller.getSpecificTypeIssuerList(issuerType, index, num);
	}
	
	public EngineResultData<Boolean>  registerIssuerType(String issuerType){
		return controller.registerIssuerType(issuerType);
	}
	
	public EngineResultData<Boolean> addIssuer(String issuerType, String issuerAddress){
		return controller.addIssuer(issuerType, issuerAddress);
	}
}
