package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.EngineResultData;

/**
 * @author tonychen 2019年6月25日
 *
 */
public interface AuthorityIssuerServiceEngine {

	public EngineResultData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args);
	
	public EngineResultData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args);
	
	public EngineResultData<Boolean> isAuthorityIssuer(String address);
	
	public EngineResultData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId);
	
	public List<String> getAuthorityIssuerAddressList(Integer index, Integer num);
	
	public EngineResultData<Boolean> removeIssuer(String issuerType, String issuerAddress);
	
	public EngineResultData<Boolean> isSpecificTypeIssuer(String issuerType, String address);
	
	public EngineResultData<List<String>> getSpecificTypeIssuerList(String issuerType,
	        Integer index,
	        Integer num);
	
	public EngineResultData<Boolean>  registerIssuerType(String issuerType);
	
	public EngineResultData<Boolean> addIssuer(String issuerType, String issuerAddress);
}
